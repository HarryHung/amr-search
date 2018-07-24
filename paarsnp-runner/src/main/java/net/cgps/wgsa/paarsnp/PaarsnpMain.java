package net.cgps.wgsa.paarsnp;

import ch.qos.logback.classic.Level;
import net.cgps.wgsa.paarsnp.core.Constants;
import net.cgps.wgsa.paarsnp.core.lib.blast.JsonFileException;
import net.cgps.wgsa.paarsnp.core.lib.json.AbstractJsonnable;
import net.cgps.wgsa.paarsnp.core.lib.json.AntimicrobialAgentLibrary;
import net.cgps.wgsa.paarsnp.core.paar.json.PaarLibrary;
import net.cgps.wgsa.paarsnp.core.snpar.json.SnparLibrary;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PaarsnpMain {

  private final Logger logger = LoggerFactory.getLogger(PaarsnpMain.class);

  public static void main(final String[] args) {

    // Initialise the options parser
    final Options options = PaarsnpMain.myOptions();
    final CommandLineParser parser = new DefaultParser();

    if (args.length == 0) {
      final HelpFormatter formatter = new HelpFormatter();
      formatter.setWidth(200);
      formatter.printHelp("java -jar paarsnp.jar <options>", options);
      System.exit(1);
    }

//    try {
    final CommandLine commandLine;
    try {
      commandLine = parser.parse(options, args);
    } catch (final ParseException e) {
      throw new RuntimeException(e);
    }

    final ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(Level.valueOf(commandLine.getOptionValue('l', "INFO")));

    // Resolve the file path.
    final Path input = Paths.get(commandLine.getOptionValue('i'));

    final Collection<Path> fastas;
    final Path workingDirectory;

    if (Files.exists(input, LinkOption.NOFOLLOW_LINKS)) {

      if (Files.isRegularFile(input)) {
        workingDirectory = input.toAbsolutePath().getParent();
        fastas = Collections.singletonList(input);
        rootLogger.debug("Processing one file", input);
      } else {
        fastas = new ArrayList<>(10000);
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(
            input,
            entry -> entry.toString().endsWith(".fna") || entry.toString().endsWith(".fa") || entry.toString().endsWith(".fasta"))) {
          stream.forEach(fastas::add);
        } catch (final IOException e) {
          rootLogger.error("Failed to read input FASTA {}", input.toAbsolutePath().toString());
          throw new RuntimeException(e);
        }
        rootLogger.debug("Processing {} files from \"{}\".", fastas.size(), input.toAbsolutePath().toString());
        workingDirectory = input;
      }
    } else {
      throw new RuntimeException("Can't find input file or directory " + input.toAbsolutePath().toString());
    }

    String databasePath = commandLine.getOptionValue('d', "databases");

    // Little shim for running in Docker.
    if (!Files.exists(Paths.get(databasePath))) {
      databasePath = "/paarsnp/" + databasePath;
    }

    new PaarsnpMain().run(commandLine.getOptionValue('s'), fastas, workingDirectory, commandLine.hasOption('o'), databasePath);
    System.exit(0);
  }

  private static Options myOptions() {

    // Required
    final Option speciesOption = Option.builder("s").longOpt("species").hasArg().argName("NCBI taxonomy numeric code").desc("Required: NCBI taxonomy numberic code for query species. e.g. 1280 for Staph. aureus").required().build();
    // Optional
    final Option assemblyListOption = Option.builder("i").longOpt("input").hasArg().argName("Assembly file(s)").desc("If a directory is provided then all FASTAs (.fna, .fa, .fasta) are searched.").build();
    final Option resourceDirectoryOption = Option.builder("d").longOpt("database-directory").hasArg().argName("Database directory").desc("Location of the BLAST databases and resources for .").build();
    final Option logLevel = Option.builder("l").longOpt("log-level").hasArg().argName("Logging level").desc("INFO, DEBUG etc").build();
    final Option outputOption = Option.builder("o").longOpt("outfile").argName("Create output file").desc("Use this flag if you want the result written to STDOUT rather than file.").build();

    final Options options = new Options();
    options.addOption(assemblyListOption)
        .addOption(speciesOption)
        .addOption(resourceDirectoryOption)
        .addOption(outputOption)
        .addOption(logLevel);

    return options;
  }

  private void run(final String speciesId, final Collection<Path> assemblyFiles, final Path workingDirectory, final boolean isToStdout, final String resourceDirectory) {

    final PaarLibrary paarLibrary;
    final SnparLibrary snparLibrary;
    final AntimicrobialAgentLibrary agentLibrary;

    try {
      paarLibrary = AbstractJsonnable.fromJsonFile(Paths.get(resourceDirectory, speciesId + Constants.PAAR_APPEND + Constants.JSON_APPEND).toFile(), PaarLibrary.class);
      snparLibrary = AbstractJsonnable.fromJsonFile(Paths.get(resourceDirectory, speciesId + Constants.SNPAR_APPEND + Constants.JSON_APPEND).toFile(), SnparLibrary.class);
      agentLibrary = AbstractJsonnable.fromJsonFile(Paths.get(resourceDirectory, speciesId + Constants.AGENT_FILE_APPEND).toFile(), AntimicrobialAgentLibrary.class);
    } catch (final JsonFileException e) {
      throw new RuntimeException(e);
    }

    final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    final PaarsnpRunner paarsnpRunner = new PaarsnpRunner(speciesId, paarLibrary, snparLibrary, agentLibrary.getAgents(), resourceDirectory, executorService);
    final Consumer<PaarsnpResult> resultWriter = this.getWriter(isToStdout, workingDirectory);

    // Run paarsnp on each assembly file.
    assemblyFiles
        .parallelStream()
        .peek(assemblyFile -> this.logger.info("{}", assemblyFile.toString()))
        .map(paarsnpRunner)
        .peek(paarsnpResult -> this.logger.debug("{}", paarsnpResult.toPrettyJson()))
        .forEach(resultWriter);
  }

  private Consumer<PaarsnpResult> getWriter(final boolean isToStdout, final Path workingDirectory) {

    if (isToStdout) {
      return paarsnpResult -> {
        try (final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out))) {
          bufferedWriter.append(paarsnpResult.toJson());
          bufferedWriter.newLine();
        } catch (final IOException e) {
          throw new RuntimeException(e);
        }
      };
    } else {
      return paarsnpResult -> {
        final Path outFile = Paths.get(workingDirectory.toString(), paarsnpResult.getAssemblyId() + "_paarsnp.jsn");

        this.logger.info("Writing {}", outFile.toAbsolutePath().toString());

        try (final BufferedWriter writer = Files.newBufferedWriter(outFile)) {
          writer.write(paarsnpResult.toPrettyJson());
        } catch (IOException e) {
          this.logger.error("Failed to write output for {}", paarsnpResult.getAssemblyId());
          throw new RuntimeException(e);
        }
      };
    }
  }
}
