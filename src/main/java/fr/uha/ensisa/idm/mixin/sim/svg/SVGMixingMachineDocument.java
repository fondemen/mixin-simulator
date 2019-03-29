package fr.uha.ensisa.idm.mixin.sim.svg;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDefsElement;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGMetadataElement;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGRectElement;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGTextPositioningElement;

import fr.uha.ensisa.idm.mixin.AbstractMixingMachine;
import fr.uha.ensisa.idm.mixin.sim.utils.AnimableValue;
import fr.uha.ensisa.idm.mixin.sim.utils.AnimationListener;
import fr.uha.ensisa.idm.mixin.sim.utils.ListenableValue;

public class SVGMixingMachineDocument extends AbstractMixingMachine {

	private static final double CUP_WIDTH = 29;

	private static SVGDocument load(String name) throws IOException {
		int idx = name.lastIndexOf('/');
		String baseName = idx < 0 ? name : name.substring(idx + 1);
		if (baseName.indexOf('.') < 0) {
			name += ".svg";
		}

		URI uri;
		try {
			uri = SVGMixingMachineDocument.class.getClassLoader().getResource(name).toURI();
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
		System.out.println(name + " -> " + uri);

		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		return f.createSVGDocument(uri.toString());
	}

	private final int inputCups, tempCups, outputCups;
	private SVGDocument doc;
	private SVGDocumentUpdator updator;
	private SVGDefsElement defs;
	private SVGElement syringeElt, filterAbk, filterBbk, syringeBt, cleanDrain, leftShutter, leftShutterClose, rightShutter,
			rightShutterClose, boom, boomText;
	private SVGElement[] cups, cupsBt;
	private SVGRectElement syringeBk;
	private SVGRectElement[] cupsBk;
	private double needleHeight;
	private double syringePosition;
	private double syringeHeight;
	private double [] cupsHeight;

	public SVGMixingMachineDocument() throws IOException {
		this(DEFAULT_INPUT_CUPS, DEFAULT_TEMP_CUPS, DEFAULT_OUTPUT_CUPS, DEFAULT_SYRINGE_MAX_FILL, DEFAULT_CUP_MAX_FILL,
				DEFAULT_CUP_MAX_FILL, DEFAULT_CUP_MAX_FILL);
	}

	public SVGMixingMachineDocument(int inputCups, int tempCups, int outputCups, double syringeCapacity,
			double inputCupCapacity, double tempCupCapacity, double outputCupCapacity) throws IOException {
		this.inputCups = inputCups;
		this.tempCups = tempCups;
		this.outputCups = outputCups;

		this.doc = (SVGDocument) SVGDOMImplementation.getDOMImplementation()
				.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

		// Copying attributes from machine.svg
		SVGSVGElement machineRoot = load("machine").getRootElement();
		NamedNodeMap machineAtts = machineRoot.getAttributes();
		for (int i = 0; i < machineAtts.getLength(); ++i) {
			Attr att = (Attr) machineAtts.item(i);
			this.doc.getRootElement().setAttributeNodeNS(att);
		}

		this.defs = (SVGDefsElement) this.doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "defs");
		this.doc.getRootElement().appendChild(this.defs);

		this.loadSVGLeftShutter();
		this.loadSVGRightShutter();
		this.loadSVGSyringe();
		this.loadSVGCleanDrain();
		this.loadBoom();

		this.doc.getRootElement().appendChild(this.leftShutter);
		this.doc.getRootElement().appendChild(this.rightShutter);
		this.doc.getRootElement().appendChild(this.syringeElt);
		this.doc.getRootElement().appendChild(this.cleanDrain);

		this.cups = new SVGElement[inputCups + 2 + tempCups + outputCups];
		this.cupsBk = new SVGRectElement[inputCups + 2 + tempCups + outputCups];
		this.cupsBt = new SVGElement[inputCups + 2 + tempCups + outputCups];
		this.cupsHeight = new double[inputCups + 2 + tempCups + outputCups];
		int i = 0;
		for (; i < this.cups.length; ++i) {
			this.loadSVGCup(i);
			if (this.cups[i] != null) {
				this.doc.getRootElement().appendChild(this.cups[i]);
			}
		}

	}
	
	public void boom(final String message) {
		this.doUpdate(new Runnable() {
			
			@Override
			public void run() {
				doc.getRootElement().appendChild(boom);
				SVGElement elt = boomText, loc = boomText;
				while (! (loc instanceof SVGLocatable)) loc = (SVGElement) loc.getParentNode();
				double docWidth = doc.getRootElement().getBBox().getWidth();
				float width;
				StringTokenizer st = new StringTokenizer(message, " ");
				StringBuffer msg = new StringBuffer();
				while(st.hasMoreTokens()) {
					String next = " " + st.nextToken();
					elt.setTextContent(msg.toString() + next);
					SVGRect bBox = ((SVGLocatable)loc).getBBox();
					width = bBox == null ? 0 : bBox.getWidth();
					if (width < docWidth) {
						msg.append(next);
					} else {
						elt.setTextContent(msg.toString());
						SVGElement newElt = (SVGElement) elt.cloneNode(true);
						elt.getParentNode().appendChild(newElt);
						newElt.setAttributeNS(null, "y", Double.toString(35 + Double.parseDouble(elt.getAttributeNS(null, "y"))));
						elt = newElt;
						elt.setTextContent(next);
						msg = new StringBuffer();
						msg.append(next);
					}
				}
			}
		});
	}

	public void setSyringeAtCup(int cup) {
		animate(this.syringePosition, cup, (long)(500*Math.abs(cup-this.syringePosition)), value -> {
			setSyringeAtCupInt(value.doubleValue(), 1);
			return null;
		});
	}

	public void moveSyringeUp(boolean up) {
		animate(up ? 0.0d : 1.0d, up ? 1.0d : 0.0d, 500, value -> {
			setSyringeAtCupInt(this.syringePosition, value);
			return null;
		});
	}

	public void fillSyringe(double syringeRatio, int cup, double cupRatio, String syringeColor, String cupColor) {
		
		if (syringeColor != null && this.syringeBt != null && syringeRatio > 0) {
			this.doUpdate(new Runnable() {
				
				@Override
				public void run() {
					((SVGStylable)syringeBt).getStyle().setProperty("fill", syringeColor, "");
				}
			}); 
		}
		
		if (cupColor != null && this.cupsBt[cup-1] != null && cupRatio > 0) {
			this.doUpdate(new Runnable() {
				
				@Override
				public void run() {
					((SVGStylable)cupsBt[cup-1]).getStyle().setProperty("fill", cupColor, "");
				}
			}); 
		}
		
		float currentHeight = this.syringeBk.getBBox().getHeight();
		float newHeight = (float) (syringeRatio * this.syringeHeight);
		
		doUpdate(new Runnable() {
			
			@Override
			public void run() {
				if (syringeColor != null) ((SVGStylable)syringeBk).getStyle().setProperty("fill", syringeColor, "");
				if (cupColor != null && cupsBk[cup-1] != null) ((SVGStylable)cupsBk[cup-1]).getStyle().setProperty("fill", cupColor, "");
			}
		});
		
		long time = (long)(2000*Math.abs(newHeight-currentHeight)/getSyringeCapacity());
		
		if (this.cupsBk[cup-1] != null) {
			
			float currentCupHeight = this.cupsBk[cup-1].getBBox().getHeight();
			float newCupHeight = (float) (cupRatio * this.cupsHeight[cup-1]);
			
			animate(currentCupHeight, newCupHeight, time, value -> {
				this.cupsBk[cup-1].setAttributeNS(null, "height", Double.toString(value));
				return null;
			}, false);
		}
		
		animate(currentHeight, newHeight, time, value -> {
			this.syringeBk.setAttributeNS(null, "height", Double.toString(value));
			return null;
		});
		
		if (this.syringeBt != null && syringeRatio == 0) {
			this.doUpdate(new Runnable() {
				
				@Override
				public void run() {
					((SVGStylable)syringeBt).getStyle().setProperty("fill", "white", "");
				}
			}); 
		}
		
		if (this.cupsBt[cup-1] != null && cupRatio == 0) {
			this.doUpdate(new Runnable() {
				
				@Override
				public void run() {
					((SVGStylable)cupsBt[cup-1]).getStyle().setProperty("fill", "white", "");
				}
			}); 
		}
	}

	public void setCupFill(final int cup, double ratio, String color) {
		
		if (this.cupsBk[cup-1] == null) return;
		
		float newHeight = (float) (ratio * this.cupsHeight[cup-1]);
		doUpdate(new Runnable() {
			
			@Override
			public void run() {
				
				if (cupsBt[cup-1] != null) {
					((SVGStylable)cupsBt[cup-1]).getStyle().setProperty("fill", ratio == 0 ? "white" : color, "");
				}
				
				((SVGStylable)cupsBk[cup-1]).getStyle().setProperty("fill", color, "");
				cupsBk[cup-1].setAttributeNS(null, "height", Double.toString(newHeight));
			}
		});
	}

	public void setFilterA(boolean active) {
		this.doUpdate(new Runnable() {

			@Override
			public void run() {
				((SVGStylable)filterAbk).getStyle().setProperty("fill", active ? "green" : "white", "");
				//setStyleAtt(filterAbk, "fill", active ? "green" : "white");
			}
		});
	}

	public void setFilterB(boolean active) {
		this.doUpdate(new Runnable() {

			@Override
			public void run() {
				((SVGStylable)filterBbk).getStyle().setProperty("fill", active ? "green" : "white", "");
				//setStyleAtt(filterBbk, "fill", active ? "green" : "white");
			}
		});
	}

	public void openLeftShutter() {
		shutterAction(this.leftShutterClose, true);
	}

	public void closeLeftShutter() {
		shutterAction(this.leftShutterClose, false);
	}

	public void openRightShutter() {
		shutterAction(this.rightShutterClose, true);
	}

	public void closeRightShutter() {
		shutterAction(this.rightShutterClose, false);
	}

	protected void shutterAction(final SVGElement shutter, final boolean open) {
		final double height = ((SVGLocatable) shutter).getBBox().getHeight();
		final double width = ((SVGLocatable) shutter).getBBox().getWidth();
		final double x = ((SVGLocatable) shutter).getBBox().getX();
		final double y = ((SVGLocatable) shutter).getBBox().getY();
		
		animate(open ? 0.0d : height, open ? height : 0.0d, 1000, value -> {
			shutter.setAttributeNS(null, "transform", "translate(0, " + value + ")");
			return null;
		});
//		animate(open ? 0.0d : -90.0d, open ? -90.0d : 0.0d, 1000, value -> {
//			shutter.setAttributeNS(null, "transform", "rotate(" + value +  ", " + (x+width) +", " + (y+height) + ")");
//			return null;
//		});
	}
	
	protected <T extends Number> void animate(T from, T to, long speed, final Function<T, Void> fun) {
		animate(from, to, speed, fun, true);
	}
	
	protected <T extends Number> void animate(T from, T to, long speed, final Function<T, Void> fun, final boolean wait) {

		final AnimableValue<T> animation = new AnimableValue<T>(from);
		animation.addAnimationListener(new AnimationListener<T>() {

			@Override
			public void valueChanged(ListenableValue<T> value, T oldValue, final T newValue) {
				doUpdate(new Runnable() {

					@Override
					public void run() {
						fun.apply(newValue);
					}
				});
			}

			@Override
			public void animationStarted(T initialValue, T finalValue) {
			}

			@Override
			public void animationEnded(T initialValue, T finalValue) {
				if (wait) {
					synchronized (animation) {
						animation.notifyAll();
					}
				}
			}

			@Override
			public void animationCancelled(T initialValue, T finalValue, T actualValue) {
				if (wait) {
					synchronized (animation) {
						animation.notifyAll();
					}
				}
			}
		});
		int s = (int) (speed / Math.abs(AnimableValue.sub(to, from).doubleValue()));
		synchronized (animation) {
			animation.goTo(to, s, 24);
			if (wait) {
				try {
					animation.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setSyringeAtCupInt(double cup, double up) {
		this.syringeElt.setAttributeNS(null, "transform",
				"translate(" + Double.toString((CUP_WIDTH * (cup - 2)) + 15 + 1.8) + ", " + Double.toString(39 - (up * needleHeight)) + ")");
		this.syringePosition = cup;
	}
	
	protected void loadSVGSyringe() throws IOException {
		if (this.syringeElt == null) {
			this.syringeElt = toGroup(load("syringe").getRootElement());
			this.syringeBk = (SVGRectElement)getElementWithId(this.syringeElt, "syringebk");
			this.syringeBt = getElementWithId(this.syringeElt, "syringebt");
			this.filterAbk = getElementWithId(this.syringeElt, "filterAbk");
			this.filterBbk = getElementWithId(this.syringeElt, "filterBbk");
			String height = this.syringeElt.getAttributeNS(null, "height");
			try {
				this.needleHeight = Double.parseDouble(height) / 4;
			} catch (Exception x) {
				this.needleHeight = 20;
			}
			this.syringeHeight = Double.parseDouble(this.syringeBk.getAttributeNS(null, "height"));
			this.syringeBk.setAttributeNS(null, "height", "0.0");
			//((SVGStylable)this.syringeBt).getStyle().setProperty("fill", "white", ""); // no as document is not rendered yet
			setStyleAtt(syringeBt, "fill", "white");
			setSyringeAtCupInt(inputCups + 2, 1);
		}
	}

	protected void loadSVGLeftShutter() throws IOException {
		if (this.leftShutter == null) {
			this.leftShutter = toGroup(load("leftShutter").getRootElement());
			this.leftShutter.setAttributeNS(null, "transform",
					"translate(" + Double.toString((CUP_WIDTH * inputCups) - 5) + ", 0)");
			this.leftShutterClose = getElementWithId(this.leftShutter, "lclose");
		}
	}

	protected void loadSVGRightShutter() throws IOException {
		if (this.rightShutter == null) {
			this.rightShutter = toGroup(load("rightShutter").getRootElement());
			this.rightShutter.setAttributeNS(null, "transform",
					"translate(" + Double.toString((CUP_WIDTH * (inputCups + 2 + tempCups)) - 5) + ", 0)");
			this.rightShutterClose = getElementWithId(this.rightShutter, "rclose");
		}
	}

	protected void loadSVGCleanDrain() throws IOException {
		if (this.cleanDrain == null) {
			this.cleanDrain = toGroup(load("cleandrain").getRootElement());
			this.cleanDrain.setAttributeNS(null, "transform",
					"translate(" + Double.toString(CUP_WIDTH * inputCups - 85.5) + ", 98)");
		}
	}

	protected void loadSVGCup(int cup) throws IOException {
		if (cup == inputCups || cup == inputCups + 1) {
			return;
		}
		if (this.cups[cup] == null) {
			this.cups[cup] = toGroup(load("cup").getRootElement());
			this.cups[cup].setAttributeNS(null, "transform", "translate(" + Double.toString(CUP_WIDTH * cup) + ", 98)");
			SVGTextPositioningElement text = (SVGTextPositioningElement) getElementWithId(this.cups[cup], "text");
			text.setTextContent(Integer.toString(cup + 1));
			this.cupsBk[cup] = (SVGRectElement) getElementWithId(this.cups[cup], "back");
			this.cupsBk[cup].setId("cup_" + cup + "_bk");
			this.cupsBt[cup] = getElementWithId(this.cups[cup], "bottom");
			this.cupsBt[cup].setId("cup_" + cup + "_bt");
			this.cupsHeight[cup] = Double.parseDouble(this.cupsBk[cup].getAttributeNS(null, "height"));
			this.cupsBk[cup].setAttributeNS(null, "height", "0.0");
		}
	}
	
	protected void loadBoom() throws IOException {
		if (this.boom == null) {
			SVGSVGElement boomRoot = load("boom").getRootElement();
			this.boom = toGroup(boomRoot);
			double width = Double.parseDouble(boomRoot.getAttributeNS(null, "width"));
			double height = Double.parseDouble(boomRoot.getAttributeNS(null, "height"));
			double docWidth = Double.parseDouble(this.doc.getRootElement().getAttributeNS(null, "width"));
			double docHeight = Double.parseDouble(this.doc.getRootElement().getAttributeNS(null, "height"));
			this.boom.setAttributeNS(null, "transform", "scale(" + Double.toString(docWidth / width) + "," + (docHeight / height) + ")");
			this.boomText = getElementWithId(this.boom, "message");
		}
	}

	protected void setStyleAtt(SVGElement elt, String prop, String value) {
		String style = elt.getAttributeNS(null, "style");
		if (style == null) {
			elt.setAttributeNS(null, "style", prop + ": " + value + ";");
			return;
		}
		Pattern p = Pattern.compile("\\s*" + prop + "\\s*:\\s*[^;]+;");
		Matcher m = p.matcher(style);
		if (m.find()) {
			style = m.replaceFirst(prop + ":" + value + ";");
		} else {
			style = prop + ":" + value + ";" + style;
		}
		elt.setAttributeNS(null, "style", style);
	}

	protected SVGElement getElementWithId(SVGElement e, String id) {
		if (e == null) {
			return null;
		}
		if (e.getNodeType() != 1) {
			return null;
		}
		if (id.equals(e.getId())) {
			return e;
		}
		NodeList children = e.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child instanceof SVGElement) {
				SVGElement ret = getElementWithId((SVGElement) child, id);
				if (ret != null) {
					return ret;
				}
			}
		}
		return null;
	}

	protected SVGGElement toGroup(SVGSVGElement root) {
		SVGGElement ret = (SVGGElement) this.doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "g");
		NodeList childs = root.getChildNodes();
		for (int i = 0; i < childs.getLength(); ++i) {
			Node n = childs.item(i);
			if (n instanceof SVGElement) {
				if (n instanceof SVGDefsElement) {
					NodeList defs = n.getChildNodes();
					for (int di = 0; di < defs.getLength(); ++di) {
						this.defs.appendChild(this.doc.importNode(defs.item(di), true));
					}
				} else if (n instanceof SVGMetadataElement) {
				} else {
					SVGGElement elt = (SVGGElement) this.doc.importNode(n, true);
					ret.appendChild(elt);
				}
			}
		}
		return ret;
	}

	public SVGDocument getDocument() {
		return this.doc;
	}

	public void setUpdator(SVGDocumentUpdator updator) {
		this.updator = updator;
	}

	protected void doUpdate(Runnable updateAction) {
		if (this.updator != null)
			this.updator.doUpdate(updateAction);
	}

}
