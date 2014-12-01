package cn.ac.rcpa.bio.tools.database;

import cn.ac.rcpa.component.JRcpaTextField;

public class JSelectSQLText
    extends JRcpaTextField {
  public JSelectSQLText(String key, String title, String value,
                        boolean required) {
    super(key, title, value, required);
  }

  @Override
  public void validate() throws IllegalAccessException {
    super.validate();

    if (getText().length() != 0 && !getText().toLowerCase().startsWith("select")) {
      throw new IllegalAccessException(
          "SQL string must be a query string beginning with select!");
    }
  }
}
