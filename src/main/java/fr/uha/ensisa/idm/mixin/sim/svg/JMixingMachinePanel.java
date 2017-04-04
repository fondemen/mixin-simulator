package fr.uha.ensisa.idm.mixin.sim.svg;

import java.awt.Container;

import javax.swing.JFrame;

import org.apache.batik.swing.JSVGCanvas;

public class JMixingMachinePanel extends JSVGCanvas {

	protected final SVGMixingMachineDocument document;

	public JMixingMachinePanel(SVGMixingMachineDocument doc) {
		this.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
		this.setDocument(doc.getDocument());
		this.document = doc;
		this.waitForSVGDocument();
		doc.setUpdator(new BatikRunnableQueueDocumentUpdator(this));
	}

	public JFrame getFrame() {
		Container p = this;
		do {
			p = p.getParent();
		} while ((p != null) && (!(p instanceof JFrame)));
		return (JFrame) p;
	}

	public SVGMixingMachineDocument getDocument() {
		return this.document;
	}

	private void waitForSVGDocument() {
		while (this.getUpdateManager() == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
}
