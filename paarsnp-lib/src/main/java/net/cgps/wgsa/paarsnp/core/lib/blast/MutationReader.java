package net.cgps.wgsa.paarsnp.core.lib.blast;

import net.cgps.wgsa.paarsnp.core.lib.blast.ncbi.BlastOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Handles a BLAST XML format stream and parses out mutations.
 */
public class MutationReader implements Function<BlastOutput, Stream<MutationSearchMatch>> {

    private final Logger logger = LoggerFactory.getLogger(MutationReader.class);

    private static double calculatePid(final BigInteger hspIdentity, final BigInteger hspAlignLen) {

        return ((double) hspIdentity.intValue() / (double) hspAlignLen.intValue()) * 100;
    }

    /**
     * Returns a list of {@link MutationSearchMatch} objects, keyed by query sequence ID.
     */
    public Stream<MutationSearchMatch> apply(final BlastOutput blastOutput) {

        this.logger.debug("Mapping matches");

        // An "iteration" in blast speak is the result for a contig search (i.e. a single fasta record in a multi-fasta). So
        // a single sequence fasta will only have one iteration.
        return blastOutput.getBlastOutputIterations()
                .getIteration()
                .parallelStream()
                .flatMap(iteration -> iteration
                        .getIterationHits()
                        .getHit()
                        .stream()
                        .flatMap(hit -> hit
                                .getHitHsps()
                                .getHsp()
                                .stream()
                                .map(hsp -> {
                                    // Check if the match is reversed
                                    final boolean reversed = hsp.getHspHitFrom().intValue() > hsp.getHspHitTo().intValue();

                                    final MutationBuilder mutationBuilder = new MutationBuilder();

                                    // Extract the list of mutations
                                    final SequenceProcessingResult sequenceProcessingResult = new SequenceProcessor(hsp.getHspHseq(), hsp.getHspHitFrom().intValue(), reversed, hsp.getHspQseq(), hsp.getHspQueryFrom().intValue(), mutationBuilder).call();

                                    // Add the match w/ mutations to the collection.
                                    return new MutationSearchMatch(
                                            hit.getHitAccession(),
                                            hsp.getHspHitFrom().intValue(),
                                            hsp.getHspHitTo().intValue(),
                                            iteration.getIterationQueryDef(),
                                            hsp.getHspQueryFrom().intValue(),
                                            hsp.getHspQueryTo().intValue(),
                                            hsp.getHspQseq(),
                                            hsp.getHspHseq(),
                                            calculatePid(hsp.getHspIdentity(), hsp.getHspAlignLen()),
                                            hsp.getHspEvalue(),
                                            reversed,
                                            sequenceProcessingResult.getMutations(),
                                            hit.getHitLen().intValue()
                                    );
                                })
                        )
                );
    }
}
