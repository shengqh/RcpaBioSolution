package cn.ac.rcpa.bio.tools.impl;

import cn.ac.rcpa.bio.tools.ISequenceValidator;

public class SequenceLengthValidator implements ISequenceValidator {
  private int minLength;

  private int maxLength;

  public SequenceLengthValidator(int minLength, int maxLength) {
    this.minLength = minLength;
    this.maxLength = maxLength;
  }

  public boolean accept(String seq) {
    return seq.length() >= minLength && seq.length() <= maxLength;
  }

  public String getType() {
    return "LengthRange=" + minLength + "--" + maxLength;
  }
}
