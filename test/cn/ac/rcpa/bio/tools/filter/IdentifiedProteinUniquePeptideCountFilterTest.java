package cn.ac.rcpa.bio.tools.filter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import cn.ac.rcpa.bio.database.SequenceDatabaseType;
import cn.ac.rcpa.utils.RcpaFileUtils;

public class IdentifiedProteinUniquePeptideCountFilterTest extends TestCase {
	private IdentifiedProteinUniquePeptideCountFilter converter = new IdentifiedProteinUniquePeptideCountFilter(
			SequenceDatabaseType.IPI, 2);

	public void testConvert() throws Exception {
		String originFile = "data/summary/data.proteins";
		Collection<String> actualReturn = converter.process(originFile);
		try {
			String[] ids = RcpaFileUtils.readFile(actualReturn.iterator().next());
			List<String> expectIds = Arrays.asList(new String[] { "IPI00030363",
					"IPI00218896", "IPI00440499", "IPI00465343", "IPI00218897",
					"IPI00473031", "IPI00218899", "IPI00180890", "IPI00062003",
					"IPI00006934", "IPI00395782" });
			assertEquals(expectIds, Arrays.asList(ids));
		} finally {
			for (String filename : actualReturn) {
				new File(filename).delete();
			}
		}
	}

}
