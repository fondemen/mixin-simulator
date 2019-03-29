package fr.uha.ensisa.idm.mixin.sim.svg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.apache.batik.swing.JSVGScrollPane;
import org.w3c.dom.svg.SVGSVGElement;

public class JMixingMachineFrame extends JFrame {

	private static final long serialVersionUID = 3126880986535354651L;

	public static void main(String [] args) throws IOException {
		SVGMixingMachineDocument doc = new SVGMixingMachineDocument();
		final JMixingMachineFrame frame = new JMixingMachineFrame(doc);
		
		frame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        System.exit(0);
		    }
		});
		frame.centerOnScreen();
		frame.setVisible(true);
	}
	
	
	protected final JMixingMachinePanel liftPanel;
	protected JLabel status;
	
	public JMixingMachineFrame(SVGMixingMachineDocument doc) {
		super("SVG Mixin");
		this.liftPanel = new JMixingMachinePanel(doc);
		this.status = new JLabel("", JLabel.LEFT);
		
		this.getContentPane().setLayout(new BorderLayout());
		
		JSVGScrollPane scroll = new JSVGScrollPane(this.liftPanel);
		this.getContentPane().add(scroll, BorderLayout.CENTER);
		
		this.getContentPane().add(this.status, BorderLayout.SOUTH);
		
		this.showStatus("Mixin machine loaded.");
		
		this.pack();
	}
	
	public void centerOnScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}

		this.setLocation( (screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}
	
	public JMixingMachinePanel getPanel() {
		return this.liftPanel;
	}
	
	public SVGMixingMachineDocument getLiftDocument() {
		return this.getPanel().getDocument();
	}
	
	public void showStatus(String status) {
		this.status.setText(status);
	}
}
