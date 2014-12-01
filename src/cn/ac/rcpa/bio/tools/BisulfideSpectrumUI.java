package cn.ac.rcpa.bio.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.tools.impl.Peptide2BisulfideSpectrum;
import cn.ac.rcpa.bio.tools.impl.Peptide3BisulfideSpectrum;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.utils.GUIUtils;
import cn.ac.rcpa.utils.SpecialSwingFileFilter;

public class BisulfideSpectrumUI extends JFrame {
	private static String title = "Bisulfide Spectrum Builder";

	private static String version = "1.0.0";

	public BisulfideSpectrumUI() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BisulfideSpectrumUI frame = new BisulfideSpectrumUI();
		frame.setSize(600, 260);
		GUIUtils.setFrameDesktopCentre(frame);
		frame.setVisible(true);
	}

	private void jbInit() throws Exception {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		btnBuild.setText("build");
		btnBuild.addActionListener(new BisulfideSpectrumUI_btnBuild_actionAdapter(
				this));
		btnClose.addActionListener(new BisulfideSpectrumUI_btnClose_actionAdapter(
				this));
		btnResultFile.setText("Save result file to....");
		btnResultFile
				.addActionListener(new BisulfideSpectrumUI_btnDirectory_actionAdapter(
						this));
		pnlSave.setLayout(borderLayout1);
		txtResultFile.setText("");
		this.getContentPane().add(pnlButton, java.awt.BorderLayout.SOUTH);
		btnClose.setText("close");
		txtPeptides.setText("");
		pnlButton.add(btnBuild);
		pnlButton.add(btnClose);
		this.getContentPane().add(pnlPeptides, java.awt.BorderLayout.CENTER);
		pnlPeptides.getViewport().add(txtPeptides);
		this.getContentPane().add(pnlSave, java.awt.BorderLayout.NORTH);
		pnlSave.add(btnResultFile, java.awt.BorderLayout.WEST);
		pnlSave.add(txtResultFile, java.awt.BorderLayout.CENTER);
		this.setTitle(Constants.getSQHTitle(title, version));
	}

	JPanel pnlButton = new JPanel();

	JButton btnBuild = new JButton();

	JButton btnClose = new JButton();

	JScrollPane pnlPeptides = new JScrollPane();

	JTextArea txtPeptides = new JTextArea();

	JPanel pnlSave = new JPanel();

	JButton btnResultFile = new JButton();

	BorderLayout borderLayout1 = new BorderLayout();

	JTextField txtResultFile = new JTextField();

	public void btnClose_actionPerformed(ActionEvent e) {
		dispose();
	}

	public void btnBuild_actionPerformed(ActionEvent e) {
		txtResultFile.setText(txtResultFile.getText().trim());
		if (0 == txtResultFile.getText().length()) {
			JOptionPane.showMessageDialog(this, "Select the result file first!");
			return;
		}

		String text = txtPeptides.getText().trim();
		String[] peptides = text.split("\n*\\s*\n\\s*");
		if (peptides.length < 2) {
			JOptionPane.showMessageDialog(this,
					"Input at least two peptides containing amino acid 'C' first!");
			return;
		}
		for (int i = 0; i < peptides.length; i++) {
			peptides[i] = peptides[i].toUpperCase();
			if (-1 == peptides[i].indexOf('C')) {
				JOptionPane.showMessageDialog(this, "Peptide " + peptides[i]
						+ " doesn't contain amino acid 'C' !");
				return;
			}
		}

		if (peptides.length > 2
				&& (peptides[1].indexOf('C') == peptides[1].lastIndexOf('C'))) {
			JOptionPane.showMessageDialog(this, "Peptide " + peptides[1]
					+ " doesn't contain two 'C' amino acids !");
			return;
		}

		IBisulfideSpectrum bs;
		if (peptides.length == 2) {
			bs = new Peptide2BisulfideSpectrum();
		} else {
			bs = new Peptide3BisulfideSpectrum();
		}
		try {
			PrintStream ps = new PrintStream(new FileOutputStream(txtResultFile
					.getText()));
			try {
				bs.generate(ps, peptides);
			} finally {
				ps.close();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage());
			return;
		}
		JOptionPane.showMessageDialog(this, "Result file has been saved to "
				+ txtResultFile.getText(), "Congradulation",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void btnDirectory_actionPerformed(ActionEvent e) {
		File f = new File(txtResultFile.getText());
		JFileChooser filechooser = new JFileChooser();
		filechooser.setFileFilter(new SpecialSwingFileFilter("bisulfide",
				"Bisulfide Spectrum File", false));
		filechooser.setSelectedFile(f);
		filechooser.setDialogTitle("Select Bisulfide Spectrum File ...");
		if (filechooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (!filechooser.getSelectedFile().getAbsolutePath().endsWith(
					".bisulfide")) {
				txtResultFile.setText(filechooser.getSelectedFile().getAbsolutePath()
						+ ".bisulfide");
			} else {
				txtResultFile.setText(filechooser.getSelectedFile().getAbsolutePath());
			}
		}
	}
	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] {CommandType.Other};
		}

		public String getCaption() {
			return "Build Bisulfide Spectrum";
		}

		public void run() {
			BisulfideSpectrumUI.main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}
}

class BisulfideSpectrumUI_btnDirectory_actionAdapter implements ActionListener {
	private BisulfideSpectrumUI adaptee;

	BisulfideSpectrumUI_btnDirectory_actionAdapter(BisulfideSpectrumUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnDirectory_actionPerformed(e);
	}
}

class BisulfideSpectrumUI_btnBuild_actionAdapter implements ActionListener {
	private BisulfideSpectrumUI adaptee;

	BisulfideSpectrumUI_btnBuild_actionAdapter(BisulfideSpectrumUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnBuild_actionPerformed(e);
	}
}

class BisulfideSpectrumUI_btnClose_actionAdapter implements ActionListener {
	private BisulfideSpectrumUI adaptee;

	BisulfideSpectrumUI_btnClose_actionAdapter(BisulfideSpectrumUI adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.btnClose_actionPerformed(e);
	}
}
