package fr.uha.ensisa.idm.mixin.sim.svg;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import fr.uha.ensisa.idm.mixin.AbstractMixingMachine;
import fr.uha.ensisa.idm.mixin.MixingMachineListener;
import fr.uha.ensisa.idm.mixin.sim.utils.ColorUtil;

public class SVGMixingMachine extends AbstractMixingMachine {

	private static final Color CLEANING_COLOR = new Color(173, 216, 230); // lightblue or #ADD8E6
	
	private MixingMachineListener listener;
	private SVGMixingMachineDocument document;
	private JMixingMachineFrame frame;
	
	private Color syringeColor = null;
	private Color[] cupsColor;

	public SVGMixingMachine() {
		super();
		this.cupsColor = new Color[getInputCups() + 2 + getTempCups() + getOutputCups()];
		int i = 0;
		for (; i < getInputCups(); ++i) {
			this.cupsColor[i] = Color.getHSBColor(ColorUtil.generateHueForId(i), 1, 1);
		}
		this.cupsColor[i] = CLEANING_COLOR;
		this.setupFrame();
	}

	public SVGMixingMachine(int inputCups, int tempCups, int outputCups, double syringeCapacity,
			double inputCupCapacity, double tempCupCapacity, double outputCupCapacity) {
		super(inputCups, tempCups, outputCups, syringeCapacity, inputCupCapacity, tempCupCapacity, outputCupCapacity);
		this.setupFrame();
	}

	private void setupFrame() {

		try {
			this.document = new SVGMixingMachineDocument();
			this.frame = new JMixingMachineFrame(this.document);
			
			frame.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent e) {
			        System.exit(0);
			    }
			});
			frame.centerOnScreen();
			frame.setVisible(true);
			
			this.addListener(this.getListener());
			
			this.document.setSyringeAtCup(this.getSyringePosition());
		} catch (RuntimeException x) {
			throw x;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
	
	private MixingMachineListener getListener() {
		if (this.listener == null) {
			this.listener = new MixingMachineListener() {
				
				@Override
				public void syringeMoving(int currentCup, int destinationCup) {
						frame.showStatus("Moving to cup " + destinationCup);
						document.setSyringeAtCup(destinationCup);
				}
				
				@Override
				public void syringeMoved(int initialCup, int currentCup) {
					frame.showStatus("Moved to cup " + currentCup);
				}
				
				@Override
				public void suckingCup(double amount, int cup, double cupContentBefore, double cupContentAfter, double syringueFillBefore, double syringeContentAfter) {
					frame.showStatus("Sucking " + amount + " from cup " + cup);
					document.moveSyringeUp(false);
				}
				
				@Override
				public void suckedCup(double amount, int cup, double cupContentBefore, double cupContentAfter, double syringeContentBefore, double syringueFillAfter) {
					frame.showStatus("Sucked " + amount + " from cup " + cup);
					syringeColor = ColorUtil.mix(syringeColor, syringeContentBefore, cupsColor[cup-1], cupContentBefore-cupContentAfter);
					document.fillSyringe(syringueFillAfter / getSyringeCapacity(), cup, cupContentAfter / getCupCapacity(cup), ColorUtil.colorToHtmlString(syringeColor), ColorUtil.colorToHtmlString(cupsColor[cup-1]));
					document.moveSyringeUp(true);
				}
				
				@Override
				public void openingRightShutter() {
					frame.showStatus("Opening right shutter");
					document.openRightShutter();					
				}
				
				@Override
				public void openingLeftShutter() {
					frame.showStatus("Opening left shutter");
					document.openLeftShutter();
				}
				
				@Override
				public void openedRightShutter() {
					frame.showStatus("Opened right shutter");
				}
				
				@Override
				public void openedLeftShutter() {
					frame.showStatus("Opened right shutter");
				}
				
				@Override
				public void filterSetting(int filter) {
					frame.showStatus("Setting filter " + filter);
					document.setFilterA(filter == 1);
					document.setFilterB(filter == 2);
				}
				
				@Override
				public void filterSet(int filter) {
					frame.showStatus("Set filter " + filter);
				}
				
				@Override
				public void closingRightShutter() {
					frame.showStatus("Closing right shutter");
					document.closeRightShutter();
				}
				
				@Override
				public void closingLeftShutter() {
					frame.showStatus("Closing left shutter");
					document.closeLeftShutter();
				}
				
				@Override
				public void closedRightShutter() {
					frame.showStatus("Closed right shutter");
				}
				
				@Override
				public void closedLeftShutter() {
					frame.showStatus("Closed left shutter");
				}
				
				@Override
				public void blowingCup(double amount, int cup, double cupContentBefore, double cupContentAfter, double syringueFillBefore, double syringeContenteAfter) {
					frame.showStatus("Blowing " + amount + " to cup " + cup);
					document.moveSyringeUp(false);
				}
				
				@Override
				public void blownCup(double amount, int cup, double cupContentBefore, double cupContentAfter, double syringeContenteBefore, double syringueFillAfter) {
					cupsColor[cup-1] = cupsColor[cup-1] == null ? syringeColor : ColorUtil.mix(syringeColor, cupContentAfter-cupContentBefore, cupsColor[cup-1], cupContentBefore);
					
					document.fillSyringe(syringueFillAfter / getSyringeCapacity(), cup, cupContentAfter / getCupCapacity(cup), ColorUtil.colorToHtmlString(syringeColor), ColorUtil.colorToHtmlString(cupsColor[cup-1]));
					frame.showStatus("Blown " + amount + " to cup " + cup);
					document.moveSyringeUp(true);
				}

				@Override
				public void waiting(int time) {
					frame.showStatus("Waiting " + time + " s");
				}

				@Override
				public void waited(int time) {
					frame.showStatus("Waited " + time + " s");
				}
			};
		}
		return this.listener;
	}

	@Override
	public void setAtInputCup(int cup, double quantity) {
		try {
			super.setAtInputCup(cup, quantity);
			assert this.cupsColor[cup-1] != null; // Initialized
			this.document.setCupFill(cup, getCupFilll(cup)/getCupCapacity(cup), ColorUtil.colorToHtmlString(this.cupsColor[cup-1]));
		} catch (RuntimeException x) {
			this.frame.showStatus("ERROR while setting " + quantity + " to cup " + quantity);
			this.document.boom(x.getMessage());
			throw x;
		}
	}

	@Override
	public void move(int offset) {
		try {
			super.move(offset);
		} catch (RuntimeException x) {
			this.frame.showStatus("ERROR while moving " + offset);
			this.document.boom(x.getMessage());
			throw x;
		}
	}

	@Override
	public double scan() {
		try {
			return super.scan();
		} catch (RuntimeException x) {
			this.frame.showStatus("ERROR while scanning");
			this.document.boom(x.getMessage());
			throw x;
		}
	}

	@Override
	public void filt(int filter) {
		try {
			super.filt(filter);
		} catch (RuntimeException x) {
			this.frame.showStatus("ERROR while selecting filter " + filter);
			this.document.boom(x.getMessage());
			throw x;
		}
	}

	@Override
	public void blow(double amount) {
		try {
			super.blow(amount);
		} catch (RuntimeException x) {
			this.frame.showStatus("ERROR while blowing " + amount);
			this.document.boom(x.getMessage());
			throw x;
		}
	}

	@Override
	public void suck(double amount) {
		try {
			super.suck(amount);
		} catch (RuntimeException x) {
			this.frame.showStatus("ERROR while sucking " + amount);
			this.document.boom(x.getMessage());
			throw x;
		}
	}

	@Override
	public void open(int shutter) {
		try {
			super.open(shutter);
		} catch (RuntimeException x) {
			this.frame.showStatus("ERROR while opening " + shutter);
			this.document.boom(x.getMessage());
			throw x;
		}
	}

	@Override
	public void shut(int shutter) {
		try {
			super.shut(shutter);
		} catch (RuntimeException x) {
			this.frame.showStatus("ERROR while shutting " + shutter);
			this.document.boom(x.getMessage());
			throw x;
		}
	}

	@Override
	public void wait(int time) {
		try {
			super.wait(time);
		} catch (RuntimeException x) {
			this.frame.showStatus("ERROR while waiting " + time);
			this.document.boom(x.getMessage());
			throw x;
		}
	}
	
}
