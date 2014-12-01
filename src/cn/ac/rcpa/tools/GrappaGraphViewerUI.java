/*
 * Created on 2006-1-11
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.tools;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import att.grappa.Graph;
import att.grappa.GrappaAdapter;
import att.grappa.GrappaPanel;
import att.grappa.Parser;
import cn.ac.rcpa.Constants;
import cn.ac.rcpa.bio.AbstractUI;
import cn.ac.rcpa.bio.tools.solution.CommandType;
import cn.ac.rcpa.bio.tools.solution.IRcpaBioToolCommand;
import cn.ac.rcpa.component.JRcpaComponentProxy;
import cn.ac.rcpa.component.JRcpaFileField;
import cn.ac.rcpa.models.MessageType;
import cn.ac.rcpa.utils.OpenFileArgument;

public class GrappaGraphViewerUI extends AbstractUI {

	private JRcpaFileField grappaFile = new JRcpaFileField("GrappaFile",
			new OpenFileArgument("Grappa", "dot"), true);

	private JScrollPane grappaContainer;

	private JRcpaComponentProxy panelProxy;

	private static String title = "Grappa Graph Viewer";

	private static String version = "1.0.0";

	public GrappaGraphViewerUI() {
		super(Constants.getSQHTitle(title, version));
		addComponent(grappaFile);
		grappaContainer = new JScrollPane();
		grappaContainer.setMinimumSize(new Dimension(800, 600));
		grappaContainer.setPreferredSize(new Dimension(800, 600));
		panelProxy = new JRcpaComponentProxy(grappaContainer, 1.0);

		addComponent(panelProxy);
	}

	@Override
	protected void doRealGo() {
		File file = new File(grappaFile.getFilename());
		try {
			InputStream input = new FileInputStream(file);
			Parser program = new Parser(input);
			program.parse();
			Graph graph = program.getGraph();
			for (int i = grappaContainer.getComponentCount() - 1; i >= 0; i--) {
				Component comp = grappaContainer.getComponent(i);
				if (comp instanceof GrappaPanel) {
					grappaContainer.remove(grappaContainer.getComponent(i));
				}
			}

			GrappaPanel gp = new GrappaPanel(graph);
			gp.addGrappaListener(new GrappaAdapter());
			gp.setScaleToFit(false);
			grappaContainer.getViewport().add(gp);
			grappaContainer.getViewport().setScrollMode(
					JViewport.BACKINGSTORE_SCROLL_MODE);
			grappaContainer.setViewportView(gp);

			grappaContainer.invalidate();
			grappaContainer.repaint();
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
			showMessage(MessageType.ERROR_MESSAGE, "Exception : " + ex.getMessage());
		}
	}

	public static void main(String[] args) {
		new GrappaGraphViewerUI().showSelf();
	}

	public static class Command implements IRcpaBioToolCommand {
		public Command() {
		}

		public String[] getMenuNames() {
			return new String[] { CommandType.Report };
		}

		public String getCaption() {
			return title;
		}

		public void run() {
			main(new String[0]);
		}

		public String getVersion() {
			return version;
		}
	}

}
