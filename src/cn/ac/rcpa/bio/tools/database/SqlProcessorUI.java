package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.database.RcpaDBFactory;
import cn.ac.rcpa.bio.database.RcpaDatabaseType;
import cn.ac.rcpa.bio.processor.AbstractFileProcessorUI;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaComboBox;
import cn.ac.rcpa.component.JRcpaTextField;
import cn.ac.rcpa.utils.SaveFileArgument;

public class SqlProcessorUI extends AbstractFileProcessorUI {
	private static final String RCPA_DB_TYPE_KEY = "RCPA_DB_TYPE";

	private static final String SQL_KEY = "SQL_STRING";

	private static String title = "Save SQL Result To File";

	JRcpaComboBox<RcpaDatabaseType> rcpaDbType = new JRcpaComboBox<RcpaDatabaseType>(
			RCPA_DB_TYPE_KEY, "Database Source", RcpaDatabaseType.values(),
			RcpaDatabaseType.ANNOTATION);

	JRcpaTextField sql = new JSelectSQLText(SQL_KEY, "SQL String", "", true);

	public SqlProcessorUI() {
		super(Constants.getSQHTitle(title, SqlProcessor.version),
				new SaveFileArgument("Save Result Text", "txt"));
		addComponent(rcpaDbType);
		addComponent(sql);
	}

	@Override
	protected int getPerfectHeight() {
		return 200;
	}

	protected String getSql() {
		return sql.getText();
	}

	protected RcpaDatabaseType getRcpaDatabaseType() {
		return rcpaDbType.getSelectedItem();
	}

	@Override
	protected IFileProcessor getProcessor() {
		return new SqlProcessor(RcpaDBFactory.getInstance().getConnection(
				getRcpaDatabaseType()), getSql());
	}

	public static void main(String[] args) {
		new SqlProcessorUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Database };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			SqlProcessorUI.main(new String[0]);
		}

		public String getVersion() {
			return SqlProcessor.version;
		}
	}

}
