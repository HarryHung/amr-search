package net.cgps.wgsa.paarsnp.core;

import net.cgps.wgsa.paarsnp.core.lib.json.AbstractJsonnable;
import net.cgps.wgsa.paarsnp.core.lib.json.AntimicrobialAgent;
import net.cgps.wgsa.paarsnp.core.paar.json.Paar;
import net.cgps.wgsa.paarsnp.core.snpar.json.Snpar;

import java.util.Collections;
import java.util.List;

public class PaarsnpLibrary extends AbstractJsonnable {

  private final String label;
  private final List<AntimicrobialAgent> antimicrobials;
  private final Paar paar;
  private final Snpar snpar;

  @SuppressWarnings("unused")
  private PaarsnpLibrary() {
    this("", Collections.emptyList(), null, null);
  }

  public PaarsnpLibrary(final String label, final List<AntimicrobialAgent> antimicrobials, final Paar paar, final Snpar snpar) {
    this.label = label;
    this.antimicrobials = antimicrobials;
    this.paar = paar;
    this.snpar = snpar;
  }

  public Paar getPaar() {
    return this.paar;
  }

  public Snpar getSnpar() {
    return this.snpar;
  }

  public String getLabel() {
    return this.label;
  }

  public List<AntimicrobialAgent> getAntimicrobials() {
    return this.antimicrobials;
  }
}
