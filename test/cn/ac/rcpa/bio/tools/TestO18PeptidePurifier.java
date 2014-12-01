package cn.ac.rcpa.bio.tools;

import junit.framework.TestCase;

public class TestO18PeptidePurifier extends TestCase {
  private O18PeptidePurifier o18PeptidePurifier = null;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    o18PeptidePurifier = new O18PeptidePurifier();
  }

  @Override
  protected void tearDown() throws Exception {
    o18PeptidePurifier = null;
    super.tearDown();
  }

  public void testIsO18Peptide() {
    assertTrue(o18PeptidePurifier.isO18Peptide("DNLKATDK"));
    assertTrue(o18PeptidePurifier.isO18Peptide("DNLKATDK*"));
    assertTrue(o18PeptidePurifier.isO18Peptide("DNLKATDR"));
    assertTrue(o18PeptidePurifier.isO18Peptide("DNLKATDR*"));

    //without K/R in C-terminal
    assertFalse(o18PeptidePurifier.isO18Peptide("DNLKATD"));

    //with modified K/R in internal
    assertFalse(o18PeptidePurifier.isO18Peptide("DNLK*ATDK"));
  }

  public void testIsValidPeptide() {
    //with/without modified k/r in C-terminal is valid
    assertTrue(o18PeptidePurifier.isValidPeptide("DNLKATDK"));
    assertTrue(o18PeptidePurifier.isValidPeptide("DNLKATDK*"));
    assertTrue(o18PeptidePurifier.isValidPeptide("DNLKATDR"));
    assertTrue(o18PeptidePurifier.isValidPeptide("DNLKATDR*"));
    assertTrue(o18PeptidePurifier.isValidPeptide("DNLKATD"));
    assertTrue(o18PeptidePurifier.isValidPeptide("DNLATD"));

    //length must large than 2
    assertFalse(o18PeptidePurifier.isValidPeptide(""));
    assertFalse(o18PeptidePurifier.isValidPeptide("D"));
    assertTrue(o18PeptidePurifier.isValidPeptide("DK"));

    //cannot contain modified k/r in internal
    assertFalse(o18PeptidePurifier.isValidPeptide("DNLK*ATDK"));
    assertFalse(o18PeptidePurifier.isValidPeptide("DNLR*ATDK"));
    assertFalse(o18PeptidePurifier.isValidPeptide("DNLK*ATD"));
    assertFalse(o18PeptidePurifier.isValidPeptide("DNLR*ATD"));
  }

}
