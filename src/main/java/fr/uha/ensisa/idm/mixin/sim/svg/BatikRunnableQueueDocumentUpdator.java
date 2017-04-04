package fr.uha.ensisa.idm.mixin.sim.svg;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.RunnableQueue;

public class BatikRunnableQueueDocumentUpdator implements SVGDocumentUpdator {
	protected final RunnableQueue queue;

	public BatikRunnableQueueDocumentUpdator(RunnableQueue queue) {
		this.queue = queue;
	}

	public BatikRunnableQueueDocumentUpdator(JSVGCanvas canvas) {
		this(canvas.getUpdateManager().getUpdateRunnableQueue());
	}
	
	public void doUpdate(Runnable updateAction) {
		this.queue.invokeLater(updateAction);
	}

}
