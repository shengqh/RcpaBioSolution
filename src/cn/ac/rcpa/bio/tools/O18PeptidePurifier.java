package cn.ac.rcpa.bio.tools;

/**
 * <p>
 * Title: O18 Peptide Purifier
 * </p>
 * 
 * <p>
 * Description: A tool used to remove the peptides contain modified K/R in
 * internal
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: RCPA.SIBS.AC.CN
 * </p>
 * 
 * @author Sheng QuanHu (qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * @version 1.0
 */
public class O18PeptidePurifier {
	public O18PeptidePurifier() {
	}

	public boolean isO18Peptide(String matchSequence) {
		if (!isValidPeptide(matchSequence)) {
			return false;
		}

		char lastChar = matchSequence.charAt(matchSequence.length() - 1);
		char beforeChar = matchSequence.charAt(matchSequence.length() - 2);

		return isO18Aminoacid(lastChar)
				|| (isO18Aminoacid(beforeChar) && isModifiedChar(lastChar));
	}

	private boolean isO18Aminoacid(char aminoacid) {
		return aminoacid == 'K' || aminoacid == 'R';
	}

	private boolean isModifiedChar(char aChar) {
		return aChar < 'A' || aChar > 'Z';
	}

	public boolean isValidPeptide(String matchSequence) {
		if (matchSequence.length() < 2) {
			return false;
		}

		for (int i = 0; i < matchSequence.length() - 1; i++) {
			if (isO18Aminoacid(matchSequence.charAt(i))
					&& isModifiedChar(matchSequence.charAt(i + 1))
					&& (i != matchSequence.length() - 2)) {
				return false;
			}
		}
		return true;
	}
}
