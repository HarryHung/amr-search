package net.cgps.wgsa.paarsnp.core.formats;

import java.util.Collection;

public class ResistanceMutationMatch {

  private final Variant resistanceMutation;
  private final Collection<Mutation> causalMutations;

  @SuppressWarnings("unused")
  private ResistanceMutationMatch() {
    this(null, null);
  }

  public ResistanceMutationMatch(final Variant resistanceMutation, final Collection<Mutation> causalMutations) {

    this.resistanceMutation = resistanceMutation;
    this.causalMutations = causalMutations;
  }

  public Variant getResistanceMutation() {
    return this.resistanceMutation;
  }

  public Collection<Mutation> getCausalMutations() {
    return this.causalMutations;
  }
}
