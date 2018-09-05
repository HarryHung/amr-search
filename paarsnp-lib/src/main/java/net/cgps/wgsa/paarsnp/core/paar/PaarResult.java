package net.cgps.wgsa.paarsnp.core.paar;

import net.cgps.wgsa.paarsnp.core.lib.blast.BlastSearchStatistics;
import net.cgps.wgsa.paarsnp.core.lib.json.AbstractJsonnable;
import net.cgps.wgsa.paarsnp.core.lib.json.ResistanceSet;

import java.util.*;

/**
 * Document class for storing the P/A AR result for an assembly. AR genes come in sets, which are required to be complete (all found in the query assembly) in order to confer resistance.
 */
public class PaarResult extends AbstractJsonnable {

  private final Collection<String> paarElementIds;
  private final Collection<ResistanceSet> completeResistanceSets; // set ID -> resistance Gene
  private final Collection<ResistanceSet> partialResistanceSets; // set ID -> resistance Gene
  private final Map<String, List<BlastSearchStatistics>> blastMatches;

  @SuppressWarnings("unused")
  private PaarResult() {

    this(Collections.emptyList(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
  }

  public PaarResult(final Collection<ResistanceSet> completeResistanceSets, final Collection<ResistanceSet> partialResistanceSets, final Map<String, List<BlastSearchStatistics>> blastMatches, final Collection<String> paarElementIds) {

    this.completeResistanceSets = new ArrayList<>(completeResistanceSets);
    this.partialResistanceSets = new ArrayList<>(partialResistanceSets);
    this.blastMatches = new HashMap<>(blastMatches);
    this.paarElementIds = paarElementIds;
  }

  public static PaarResult buildEmpty() {
    return new PaarResult();
  }

  @SuppressWarnings("unused") // for json serialisation.
  public Map<String, List<BlastSearchStatistics>> getBlastMatches() {

    return this.blastMatches;
  }

  public Collection<ResistanceSet> getCompleteResistanceSets() {

    return this.completeResistanceSets;
  }

  @SuppressWarnings("unused") // for json serialisation.
  public Collection<ResistanceSet> getPartialResistanceSets() {

    return this.partialResistanceSets;
  }

  @SuppressWarnings("unused")
  public Collection<String> getPaarElementIds() {

    return this.paarElementIds;
  }
}
