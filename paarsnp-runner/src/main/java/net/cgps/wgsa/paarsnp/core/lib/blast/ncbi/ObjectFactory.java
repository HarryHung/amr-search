//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.07.29 at 08:18:25 PM BST
//


package net.cgps.wgsa.paarsnp.core.lib.blast.ncbi;


import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the ncbi package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived messageinterfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and net.wgst.server.api.model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {


  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ncbi
   */
  public ObjectFactory() {

  }

  /**
   * Create an instance of {@link Hit }
   */
  public Hit createHit() {

    return new Hit();
  }

  /**
   * Create an instance of {@link Iteration }
   */
  public Iteration createIteration() {

    return new Iteration();
  }

  /**
   * Create an instance of {@link BlastOutput }
   */
  public BlastOutput createBlastOutput() {

    return new BlastOutput();
  }

  /**
   * Create an instance of {@link Parameters }
   */
  public Parameters createParameters() {

    return new Parameters();
  }

  /**
   * Create an instance of {@link Hit.HitHsps }
   */
  public Hit.HitHsps createHitHitHsps() {

    return new Hit.HitHsps();
  }

  /**
   * Create an instance of {@link Statistics }
   */
  public Statistics createStatistics() {

    return new Statistics();
  }

  /**
   * Create an instance of {@link Iteration.IterationHits }
   */
  public Iteration.IterationHits createIterationIterationHits() {

    return new Iteration.IterationHits();
  }

  /**
   * Create an instance of {@link Iteration.IterationStat }
   */
  public Iteration.IterationStat createIterationIterationStat() {

    return new Iteration.IterationStat();
  }

  /**
   * Create an instance of {@link Hsp }
   */
  public Hsp createHsp() {

    return new Hsp();
  }

  /**
   * Create an instance of {@link BlastOutput.BlastOutputParam }
   */
  public BlastOutput.BlastOutputParam createBlastOutputBlastOutputParam() {

    return new BlastOutput.BlastOutputParam();
  }

  /**
   * Create an instance of {@link BlastOutput.BlastOutputIterations }
   */
  public BlastOutput.BlastOutputIterations createBlastOutputBlastOutputIterations() {

    return new BlastOutput.BlastOutputIterations();
  }

  /**
   * Create an instance of {@link BlastOutput.BlastOutputMbstat }
   */
  public BlastOutput.BlastOutputMbstat createBlastOutputBlastOutputMbstat() {

    return new BlastOutput.BlastOutputMbstat();
  }

}
