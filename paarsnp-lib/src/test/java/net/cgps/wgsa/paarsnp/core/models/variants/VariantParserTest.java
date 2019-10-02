package net.cgps.wgsa.paarsnp.core.models.variants;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class VariantParserTest {

  private final Logger logger = LoggerFactory.getLogger(VariantParserTest.class);

  @Test
  public void applyDnaSnp() {
    final String test1 = "t67a";

    this.logger.info("Testing {}", test1);

    Assert.assertTrue(this.compare(this.generator('t', 'a', 67), new VariantParser().apply(test1)));
  }

  @Test
  public void applyAminoAcidMutation() {
    final String test1 = "E67I";

    this.logger.info("Testing {}", test1);

    Assert.assertTrue(this.compare(this.generator('E', 'I', 67), new VariantParser().apply(test1)));
  }

  @Test
  public void applyAAInsert() {
    final String test1 = "ins67I";

    this.logger.info("Testing {}", test1);

    Assert.assertTrue(this.compare(this.generator('-', 'I', 67), new VariantParser().apply(test1)));
  }

  @Test
  public void applyAAInsertAlternativeForm() {
    final String test1 = "-67I";

    this.logger.info("Testing {}", test1);

    Assert.assertTrue(this.compare(this.generator('-', 'I', 67), new VariantParser().apply(test1)));
  }

  @Test
  public void applyMultiAAInsert() {
    final String test1 = "ins67IE";

    this.logger.info("Testing {}", test1);

    Assert.assertTrue(this.compare(this.generator("--", "IE", 67), new VariantParser().apply(test1)));
  }

  private boolean compare(final Map.Entry<Integer, Map.Entry<String, String>> reference, final Map.Entry<Integer, Map.Entry<String, String>> test) {
    return reference.getKey().equals(test.getKey())
        && reference.getValue().getKey().equals(test.getValue().getKey())
        && reference.getValue().getValue().equals(test.getValue().getValue());
  }

  @Test
  public void applyAADeletion() {
    final String test1 = "E67del";

    this.logger.info("Testing {}", test1);

    Assert.assertTrue(this.compare(this.generator('E', '-', 67), new VariantParser().apply(test1)));
  }

  @Test
  public void applyAADeletionAlternativeForm() {
    final String test1 = "E67-";

    this.logger.info("Testing {}", test1);

    Assert.assertTrue(this.compare(this.generator('E', '-', 67), new VariantParser().apply(test1)));
  }

  private Map.Entry<Integer, Map.Entry<String, String>> generator(final String a, final String b, final Integer position) {
    return new ImmutablePair<>(position, new ImmutablePair<>(a, b));
  }

  @Test
  public void applyMultiAAInsertAlternativeForm() {
    final String test1 = "-67IE";

    this.logger.info("Testing {}", test1);

    Assert.assertTrue(this.compare(this.generator("--", "IE", 67), new VariantParser().apply(test1)));
  }

  @Test
  public void applyPromoterSnp() {
    final String test1 = "t-15a";

    this.logger.info("Testing {}", test1);

    final Map.Entry<Integer, Map.Entry<String, String>> parsed = new VariantParser().apply(test1);

    Assert.assertTrue(this.compare(this.generator('t', 'a', -15), parsed));
  }

  private Map.Entry<Integer, Map.Entry<String, String>> generator(final char a, final char b, final Integer position) {
    return this.generator(Character.toString(a), Character.toString(b), position);
  }

  @Test
  public void applyPromoterInsert() {
    final String test1 = "--15a";

    this.logger.info("Testing {}", test1);

    Assert.assertTrue(this.compare(this.generator('-', 'a', -15), new VariantParser().apply(test1)));
  }

  @Test
  public void applyPromoterInsertAlternativeForm() {
    final String test1 = "ins-15a";

    this.logger.info("Testing {}", test1);

    Assert.assertTrue(this.compare(this.generator('-', 'a', -15), new VariantParser().apply(test1)));
  }

}