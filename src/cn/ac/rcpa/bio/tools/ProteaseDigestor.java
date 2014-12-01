package cn.ac.rcpa.bio.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.biojava.bio.BioException;
import org.biojava.bio.proteomics.Digest;
import org.biojava.bio.proteomics.Protease;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.impl.SimpleFeature;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.Location;
import org.biojava.utils.ChangeVetoException;

import cn.ac.rcpa.filter.IFilter;

public class ProteaseDigestor{
  private Digest bioJavaDigest;
  IFilter<String> validator;

  public ProteaseDigestor(Digest digest, IFilter<String> validator) {
    this.bioJavaDigest = digest;
    this.validator = validator;
  }

  public ProteaseDigestor(Protease protease, int maxMissedCleavages, IFilter<String> validator) {
    this.bioJavaDigest = new Digest();
    bioJavaDigest.setProtease(protease);
    bioJavaDigest.setMaxMissedCleavages(maxMissedCleavages);
    this.validator = validator;
  }

  public List<String> digest(Sequence sequence) throws
      IllegalSymbolException, BioException, ChangeVetoException {
    Sequence newSeq = ProteinTools.createProteinSequence(sequence.seqString(),"TEMP");

    digestSequence(newSeq);

    List<String> result = new ArrayList<String>();
    for (Iterator iter = newSeq.features(); iter.hasNext(); ) {
      SimpleFeature item = (SimpleFeature)iter.next();
      Location loc = item.getLocation();
      final String peptide = sequence.subStr(loc.getMin(), loc.getMax());
      if (validator == null || validator.accept(peptide)){
        result.add(peptide);
      }
    }

    return result;
  }

  private void digestSequence(Sequence sequence) throws
      ChangeVetoException, IllegalSymbolException, BioException {
    bioJavaDigest.setSequence(sequence);
    bioJavaDigest.addDigestFeatures();
  }
}
