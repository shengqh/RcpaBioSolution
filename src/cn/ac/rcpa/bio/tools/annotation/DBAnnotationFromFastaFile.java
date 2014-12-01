package cn.ac.rcpa.bio.tools.annotation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import cn.ac.rcpa.bio.database.AccessNumberFastaParser;
import cn.ac.rcpa.bio.database.DatabaseShortLabelMap;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.bio.database.ebi.protein.ProteinEntryQueryFactory;
import cn.ac.rcpa.bio.database.ebi.protein.entry.ProteinEntry;
import cn.ac.rcpa.bio.database.link.DatabaseLink;
import cn.ac.rcpa.bio.database.link.DatabaseLinkSet;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.utils.RcpaStringUtils;

public class DBAnnotationFromFastaFile implements IFileProcessor {
	public static String version = "1.0.0";

	private DatabaseLinkSet dblinkset;

	private SequenceDatabaseType dbType;

	public String getResultFile() {
		return resultFile;
	}

	private String resultFile;

	public DBAnnotationFromFastaFile(SequenceDatabaseType dbType) {
		this.dbType = dbType;
		final String dblinkFile = "config/DatabaseLink.xml";
		try {
			dblinkset = DatabaseLinkSet.unmarshal(new FileReader(dblinkFile));
		} catch (Exception ex) {
			throw new IllegalStateException("Cannot read from " + dblinkFile + " : "
					+ ex.getMessage());
		}
	}

	private void showEntries(Map<String, ProteinEntry> entryMap,
			String resultFilename) throws IOException {
		final PrintWriter pw = new PrintWriter(new FileWriter(resultFilename));

		String search = getSearchUrl(dbType.toString());

		pw.print("<pre>\n");
		final File curDir = new File(resultFilename).getParentFile();
		for (String identity : entryMap.keySet()) {
			final ProteinEntry entry = entryMap.get(identity);
			final String individualEntryFilename = identity + ".html";

			pw.println("<a href=\"" + individualEntryFilename
					+ "\" target=\"_blank\">" + identity + "</a> "
					+ (entry.getDescription() == null ? "" : entry.getDescription()));

			final File individualEntryFile = new File(curDir, individualEntryFilename);

			writeProteinEntry(search, entry, individualEntryFile);
		}
		pw.println("</pre>");
		pw.close();

	}

	private String getAcFromEntryName(String entryName) {
		int ipos = entryName.indexOf('.');
		if (ipos != -1) {
			return entryName.substring(0, ipos);
		}

		return entryName;
	}

	private void writeProteinEntry(String search, ProteinEntry entry,
			File individualEntryFile) throws IndexOutOfBoundsException, IOException {
		final PrintWriter annopw = new PrintWriter(new FileWriter(
				individualEntryFile));
		writeHeader(entry, annopw);
		writeAC(search, entry, annopw);
		writeDT(entry, annopw);
		writeDE(entry, annopw);
		writeGE(entry, annopw);
		writeOG(entry, annopw);
		writeOC(entry, annopw);
		writeOX(entry, annopw);
		writeReference(entry, annopw);
		writeFreeComments(entry, annopw);
		writeDBReference(entry, annopw);
		writeKeyword(entry, annopw);
		writeFeatureTable(entry, annopw);
		writeSequence(entry, annopw);
		writeBottom(annopw);
		annopw.close();
	}

	private void writeBottom(PrintWriter annopw) {
		annopw.println("</pre>");
	}

	private void writeSequence(ProteinEntry entry, PrintWriter annopw) {
		annopw.print("SQ   SEQUENCE " + entry.getSequence_length() + " AA;");
		if (entry.getMw() != 0) {
			annopw.print(" " + entry.getMw());
		}
		if (entry.getCrc() != null) {
			annopw.print(" " + entry.getCrc());
		}
		annopw.println();
		annopw.print(RcpaStringUtils.warpString(entry.getSequence(), 60));
	}

	private void writeFeatureTable(ProteinEntry entry, PrintWriter annopw)
			throws IndexOutOfBoundsException {
		if (entry.getFeature_tableCount() != 0) {
			for (int k = 0; k < entry.getFeature_tableCount(); k++) {
				annopw.print("FT   " + entry.getFeature_table(k).getKey_name() + " "
						+ entry.getFeature_table(k).getSequence_from() + " "
						+ entry.getFeature_table(k).getSequence_to());
				if (entry.getFeature_table(k).getFt_description() != null) {
					annopw.print(" " + entry.getFeature_table(k).getFt_description());
				}
				annopw.println();
			}
		}
	}

	private void writeKeyword(ProteinEntry entry, PrintWriter annopw) {
		if (entry.getKeyword() != null) {
			annopw.println("KW   " + entry.getKeyword());
		}
	}

	private void writeDBReference(ProteinEntry entry, PrintWriter annopw)
			throws IndexOutOfBoundsException {
		if (entry.getDb_referenceCount() != 0) {
			for (int k = 0; k < entry.getDb_referenceCount(); k++) {
				annopw.print("DR   " + entry.getDb_reference(k).getDb() + "; ");

				String searchUrl = getSearchUrl(entry.getDb_reference(k).getDb());
				if (searchUrl == "") {
					annopw.print(entry.getDb_reference(k).getPrimary_identifier() + "; ");
				} else {
					annopw.print(getUrl(searchUrl, entry.getDb_reference(k)
							.getPrimary_identifier())
							+ "; ");
				}
				if (entry.getDb_reference(k).getSecondary_identifier() != null) {
					annopw.print(entry.getDb_reference(k).getSecondary_identifier()
							+ "; ");
				}
				if (entry.getDb_reference(k).getTertiary_identifier() != null) {
					annopw.print(entry.getDb_reference(k).getTertiary_identifier() + ".");
				}
				annopw.println();
			}
		}
	}

	private void writeFreeComments(ProteinEntry entry, PrintWriter annopw)
			throws IndexOutOfBoundsException {
		if (entry.getFree_commentCount() != 0) {
			for (int k = 0; k < entry.getFree_commentCount(); k++) {
				annopw.println("CC   -!-" + entry.getFree_comment(k).getCc_topic()
						+ ": " + entry.getFree_comment(k).getCc_details() + ".");
			}
		}
	}

	private void writeReference(ProteinEntry entry, PrintWriter annopw)
			throws IndexOutOfBoundsException {
		if (entry.getReferenceCount() != 0) {
			for (int k = 0; k < entry.getReferenceCount(); k++) {
				annopw.print("RN   " + entry.getReference(k).getNum() + "\n");
				if (entry.getReference(k).getPosition() != null) {
					annopw.println("RP   " + entry.getReference(k).getPosition());
				}
				if (entry.getReference(k).getComment() != null) {
					annopw.println("RC   " + entry.getReference(k).getComment());
				}
				annopw.print("RX   ");
				if (entry.getReference(k).getMedline_num() != 0) {
					annopw.print("MEDLINE=" + entry.getReference(k).getMedline_num());
				}
				if (entry.getReference(k).getPubmed_num() != 0) {
					annopw.print("PubMed=" + entry.getReference(k).getPubmed_num());
				}
				if (entry.getReference(k).getDoi_num() != null) {
					annopw.print("DOI=" + entry.getReference(k).getDoi_num());
				}
				annopw.println();
				if (entry.getReference(k).getAuthor() != null) {
					annopw.println("RA   " + entry.getReference(k).getAuthor());
				}
				if (entry.getReference(k).getTitle() != null) {
					annopw.println("RT   " + entry.getReference(k).getTitle());
				}
				if (entry.getReference(k).getLocation() != null) {
					annopw.println("RL   " + entry.getReference(k).getLocation());
				}
			}
		}
	}

	private void writeOX(ProteinEntry entry, PrintWriter annopw) {
		annopw.println("OX   " + entry.getTaxonomy_id());
	}

	private void writeOC(ProteinEntry entry, PrintWriter annopw) {
		annopw.println("OC   " + entry.getOrganism_classification());
	}

	private void writeOG(ProteinEntry entry, PrintWriter annopw) {
		annopw.println("OS   " + entry.getOrganism_species());
		if (entry.getOrganelle() != null) {
			annopw.println("OG   " + entry.getOrganelle());
		}
	}

	private void writeGE(ProteinEntry entry, PrintWriter annopw) {
		if (entry.getGene_name() != null) {
			annopw.println("GN   " + entry.getGene_name());
		}
	}

	private void writeDE(ProteinEntry entry, PrintWriter annopw) {
		if (entry.getDescription() != null) {
			annopw.println("DE   " + entry.getDescription());
		}
	}

	private void writeDT(ProteinEntry entry, PrintWriter annopw) {
		annopw.println("DT   " + entry.getCreate());
		annopw.println("DT   " + entry.getSequence_update());
		if (entry.getAnnotation_update() != null) {
			annopw.println("DT   " + entry.getAnnotation_update());
		}
	}

	private String getUrl(String searchUrl, String acNumber) {
		return "<a href=\""
				+ searchUrl.replaceAll("\\$\\{ac\\}", getAcFromEntryName(acNumber))
				+ "\" target=\"_blank\">" + acNumber + "</a>";
	}

	private void writeAC(String search, ProteinEntry entry, PrintWriter annopw)
			throws IndexOutOfBoundsException {
		annopw.println("ID   " + getUrl(search, entry.getEntry_name()) + " "
				+ entry.getData_class() + "; " + entry.getMolecule_type() + "; "
				+ entry.getSequence_length() + "AA.");
		annopw.print("AC   ");
		for (int k = 0; k < entry.getAc_numberCount(); k++) {
			annopw.print(getUrl(search, entry.getAc_number(k)) + ";");
		}
		annopw.println();
	}

	private void writeHeader(ProteinEntry entry, PrintWriter annopw) {
		annopw.println("<pre>");
	}

	private String getSearchUrl(String referenceDbType) {
		final String shortlabel = DatabaseShortLabelMap.getInstance()
				.getShortLabel(referenceDbType).toLowerCase();
		final DatabaseLink[] dblinks = dblinkset.getDatabaseLink();
		for (int j = 0; j < dblinkset.getDatabaseLinkCount(); j++) {
			if (dblinks[j].getShortlabel().equals(shortlabel)) {
				return dblinks[j].getSearch_url();
			}
		}
		return "";
	}

	private String buildAnnotationFiles(String fastaFilename,
			Map<String, ProteinEntry> entryMap, SequenceDatabaseType type)
			throws FileNotFoundException, IOException, ValidationException,
			MarshalException {
		File dir = new File(fastaFilename + ".Annotations");
		while (dir.exists() && !dir.isDirectory()) {
			dir = new File(dir.getAbsolutePath() + ".1");
		}
		if (!dir.exists()) {
			dir.mkdir();
		}

		final String result = new File(dir, "index.html").getAbsolutePath();
		showEntries(entryMap, result);
		return result;
	}

	public List<String> process(String originFile) throws Exception {
		final File fastaFile = new File(originFile);
		final String[] identities = AccessNumberFastaParser.getNames(fastaFile
				.getAbsolutePath(), dbType);

		final Map<String, ProteinEntry> entryMap = ProteinEntryQueryFactory.create(
				dbType).getEntries(identities);

		resultFile = buildAnnotationFiles(fastaFile.getAbsolutePath(), entryMap,
				dbType);
		return Arrays.asList(new String[] { resultFile });
	}

	public static void main(String[] args) throws Exception {
		new DBAnnotationFromFastaFile(SequenceDatabaseType.IPI)
				.process("data/testGetEntriesXMLFromFasta.fasta");
	}

}
