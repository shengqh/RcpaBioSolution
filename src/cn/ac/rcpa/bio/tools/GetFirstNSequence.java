package cn.ac.rcpa.bio.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import cn.ac.rcpa.bio.database.AccessNumberParserFactory;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.IAccessNumberParser;
import cn.ac.rcpa.bio.utils.SequenceUtils;

public class GetFirstNSequence {
	public GetFirstNSequence() {
	}

	public static void getSequence(File fromDatabase, File toDatabase, int size,
			SequenceDatabaseType dbType) throws FileNotFoundException, BioException,
			NoSuchElementException, IOException {
		SequenceIterator seqI = SequenceUtils.readFastaProtein(new BufferedReader(
				new FileReader(fromDatabase)));
		IAccessNumberParser parser = AccessNumberParserFactory.getParser(dbType);
		PrintWriter pw = new PrintWriter(new FileWriter(toDatabase));

		int icount = 0;
		while (seqI.hasNext()) {
			Sequence seq = seqI.nextSequence();
			String name = parser.getValue(seq.getName());
			String description = ((String) seq.getAnnotation().getProperty(
					"description")).trim();
			if (description.startsWith("Tax_Id=")) {
				int ipos = description.indexOf(" ");
				if (ipos == -1) {
					description = "Hypothetical protein";
				} else {
					description = description.substring(ipos).trim();
				}
			}
			description = description.replace(' ', '_');
			pw.println(">" + name + "_" + description);
			pw.println(seq.seqString());
			icount++;
			if (icount >= size) {
				break;
			}
		}

		pw.close();
	}

	public static void main(String[] args) throws Exception {
		getSequence(
				new File(
						"F:\\Science\\Data\\HPPP\\LCQ_LTQ_2.2.2.LCQ_Bound.noredundant.unduplicated.fasta"),
				new File("F:\\Science\\Data\\HPPP\\lcq_bound_positive.fasta"), 1000,
				SequenceDatabaseType.IPI);
	}
}
