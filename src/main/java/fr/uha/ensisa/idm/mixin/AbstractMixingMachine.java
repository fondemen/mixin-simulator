package fr.uha.ensisa.idm.mixin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class AbstractMixingMachine implements ListenableMixingMachine {

	public static int DEFAULT_INPUT_CUPS = 3;
	public static int DEFAULT_TEMP_CUPS = 5;
	public static int DEFAULT_OUTPUT_CUPS = 1;
	public static double DEFAULT_SYRINGE_MAX_FILL = 30;
	public static double DEFAULT_CUP_MAX_FILL = 18;

	public static Set<Integer> AVAILABLE_FILTERS = Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(0, 1, 2)));
	public static Set<Integer> AVAILABLE_SHUTTERS = Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(0, 1)));
	
	private final int inputCups, tempCups, outputCups;
	
	private int filter = 0;
	private double syringeFill = 0;
	private final double [] cupFills, cupCapacities;
	private final double syringeCapacity;
	private int syringeAtCup;
	private final int leftShutterPosition, rightShutterPosition, cleaningFluidPosition, drainPosition;
	private boolean leftShutterOpen = false, rightShutterOpen = false;
	
	private Set<MixingMachineListener> listeners = new HashSet<MixingMachineListener>();
	
	public AbstractMixingMachine() {
		this(DEFAULT_INPUT_CUPS, DEFAULT_TEMP_CUPS, DEFAULT_OUTPUT_CUPS, DEFAULT_SYRINGE_MAX_FILL, DEFAULT_CUP_MAX_FILL, DEFAULT_CUP_MAX_FILL, DEFAULT_CUP_MAX_FILL);
	}
	
	public AbstractMixingMachine(int inputCups, int tempCups, int outputCups, double syringeCapacity, double inputCupCapacity, double tempCupCapacity, double outputCupCapacity) {
		this.inputCups = inputCups;
		this.tempCups = tempCups;
		this.outputCups = outputCups;
		
		this.syringeCapacity = syringeCapacity;
		
		this.cupFills = new double[inputCups+2+tempCups+outputCups];
		Arrays.fill(cupFills, 0.0d);
		
		this.cupCapacities = new double[inputCups+2+tempCups+outputCups];
		int i = 0;
		for (; i < inputCups ; i++) {
			this.cupCapacities[i] = inputCupCapacity;
		}
		for (; i < inputCups+2; i++) {
			this.cupCapacities[i] = Integer.MAX_VALUE;
		}
		for (; i < inputCups+2+tempCups; i++) {
			this.cupCapacities[i] = tempCupCapacity;
		}
		for (; i < inputCups+2+tempCups+outputCups; i++) {
			this.cupCapacities[i] = outputCupCapacity;
		}
		
		this.leftShutterPosition = inputCups;
		this.rightShutterPosition = inputCups+2+tempCups;
		this.cleaningFluidPosition = inputCups+1;
		this.drainPosition = inputCups+2;
		
		this.syringeAtCup = inputCups+2;
	}
	
	protected int getInputCups() {
		return this.inputCups;
	}

	protected int getTempCups() {
		return tempCups;
	}

	protected int getOutputCups() {
		return outputCups;
	}

	protected double getSyringeCapacity() {
		return syringeCapacity;
	}

	protected double getCupCapacity(int cup) {
		return cupCapacities[cup-1];
	}
	
	protected double getSyringeFill() {
		return this.syringeFill;
	}

	protected double getCupFilll(int cup) {
		return cupFills[cup-1];
	}

	public void addListener(MixingMachineListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}

	public void removeListener(MixingMachineListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}
	
	public void setAtInputCup(int cup, double quantity) {
		if (cup <= 0 || cup > this.leftShutterPosition) {
			throw new IllegalArgumentException("Invalid input cup: " + cup + " ; input cup number start at 1 and end at " + this.leftShutterPosition);
		}
		if (quantity > this.cupCapacities[cup-1]) {
			throw new IllegalArgumentException("Cannot set " + quantity + " at input cup " + cup + " that accepts at most " + this.cupCapacities[cup-1]);
		}
		this.cupFills[cup-1] = quantity;
	}
	
	public double getAtOutputCup(int cup) {
		if (cup <= 0 || cup > (this.cupCapacities.length-this.rightShutterPosition)) {
			throw new IllegalArgumentException("Invalid output cup: " + cup + " ; input cup number start at 1 and end at " + (this.cupCapacities.length-this.rightShutterPosition));
		}
		return this.cupFills[cup+this.rightShutterPosition-1];
	}
	
	protected double getContentAtCup(int cup) {
		if (cup > this.cupFills.length) {
			throw new IllegalArgumentException("No such cup position: " + cup);
		}
		if (cup == this.cleaningFluidPosition) {
			throw new IllegalArgumentException("Cannot check cleaning fluid cup content");
		}
		if (cup == this.drainPosition) {
			throw new IllegalArgumentException("Cannot check drain cup content");
		}
		return this.cupFills[cup-1];
	}
	
	protected int getSyringePosition() {
		return this.syringeAtCup;
	}
	
	public void move(int offset) {
		if (offset == 0) return;
		int dest = this.syringeAtCup + offset;
		if (dest <= 0 || dest > cupFills.length) {
			throw new IllegalArgumentException("Cannot move syringe from cup " + this.syringeAtCup + " to " + Math.abs(offset) + " position/s at the " + (offset > 0 ? "right" : "left"));
		}

		// crossing left shutter ?
		if (!this.leftShutterOpen && (
				(this.syringeAtCup > this.leftShutterPosition && dest <= this.leftShutterPosition) ||
				(this.syringeAtCup <= this.leftShutterPosition && dest > this.leftShutterPosition)) ) {
			throw new IllegalStateException("Syringe crossing left shutter while closed");
		}
		// crossing right shutter ?
		if (!this.rightShutterOpen && (
				(this.syringeAtCup > this.rightShutterPosition && dest <= this.rightShutterPosition) ||
				(this.syringeAtCup <= this.rightShutterPosition && dest > this.rightShutterPosition))) {
			throw new IllegalStateException("Syringe crossing right shutter while closed");
		}
		
		int from = this.syringeAtCup;
		this.listeners.forEach(l -> l.syringeMoving(this.syringeAtCup, dest));
		this.syringeAtCup = dest;
		this.listeners.forEach(l -> l.syringeMoved(from, this.syringeAtCup));
	}

	public void filt(int filter) {
		if (!AVAILABLE_FILTERS.contains(filter)) {
			throw new IllegalArgumentException("Invalid filter : " + filter);
		}
		this.listeners.forEach(l -> l.filterSetting(filter));
		this.filter = filter;
		this.listeners.forEach(l -> l.filterSet(filter));
	}

	public double scan() {
		return this.getContentAtCup(this.syringeAtCup);
	}

	public void suck(double amount) {
		if (this.syringeFill+amount > this.syringeCapacity) {
			throw new IllegalArgumentException("Syringe overflow : cannot add " + amount + " to syringe already filled by " + this.syringeFill + " while its capacity is " + this.syringeCapacity);
		}
		
		boolean fromCleaningFluid = this.syringeAtCup == this.cleaningFluidPosition;
		if (!fromCleaningFluid) {
			double cupContent = this.getContentAtCup(this.syringeAtCup);
			if (cupContent < amount) {
				throw new IllegalArgumentException("Not enough product in cup " + this.syringeAtCup + ": requested " + amount + " while " + cupContent + " is available");
			}
		}

		double cupBefore = fromCleaningFluid ? -1 : this.getContentAtCup(this.syringeAtCup);
		double cupAfter = fromCleaningFluid ? -1 : cupBefore - amount;
		double syringeBefore = this.syringeFill;
		double syringeAfter = this.syringeFill + amount;
		
		this.listeners.forEach(l -> l.suckingCup(amount, this.syringeAtCup, cupBefore, cupAfter, syringeBefore, syringeAfter));
		if (!fromCleaningFluid) this.cupFills[this.syringeAtCup-1] -= amount;
		this.syringeFill += amount;
		this.listeners.forEach(l -> l.suckedCup(amount, this.syringeAtCup, cupBefore, cupAfter, syringeBefore, syringeAfter));
	}

	public void blow(double amount) {
		if (this.syringeFill < amount) {
			throw new IllegalArgumentException("Cannot blow " + amount + " while only " + this.syringeFill + " is available in syringe");
		}
		
		boolean toDrain = this.syringeAtCup == this.drainPosition;
		if (! toDrain) {
			double cupContent = this.getContentAtCup(this.syringeAtCup);
			if (cupContent+amount > this.cupCapacities[this.syringeAtCup-1]) {
				throw new IllegalArgumentException("Not enough capacity in cup " + this.syringeAtCup + ": requested to put " + amount + " while " + cupContent + " is already there and max capacity is " + this.cupCapacities[this.syringeAtCup]);
			}
		}

		double cupBefore = toDrain ? -1 : this.getContentAtCup(this.syringeAtCup);
		double cupAfter = toDrain ? -1 : cupBefore + amount;
		double syringeBefore = this.syringeFill;
		double syringeAfter = this.syringeFill - amount;

		this.listeners.forEach(l -> l.blowingCup(amount, this.syringeAtCup, cupBefore, cupAfter, syringeBefore, syringeAfter));
		if (!toDrain) this.cupFills[this.syringeAtCup-1] += amount;
		this.syringeFill -= amount;
		this.listeners.forEach(l -> l.blownCup(amount, this.syringeAtCup, cupBefore, cupAfter, syringeBefore, syringeAfter));
	}

	public void open(int shutter) {
		if (!AVAILABLE_SHUTTERS.contains(shutter)) {
			throw new IllegalArgumentException("Invalid shutter identifier: " + shutter);
		}
		switch (shutter) {
		case 0:
			this.listeners.forEach(l -> l.openingLeftShutter());
			this.leftShutterOpen = true;
			this.listeners.forEach(l -> l.openedLeftShutter());
			break;
		case 1:
			this.listeners.forEach(l -> l.openingRightShutter());
			this.rightShutterOpen = true;
			this.listeners.forEach(l -> l.openedRightShutter());
			break;

		default:
			assert false;
		}
	}

	public void shut(int shutter) {
		if (!AVAILABLE_SHUTTERS.contains(shutter)) {
			throw new IllegalArgumentException("Invalid shutter identifier: " + shutter);
		}
		switch (shutter) {
		case 0:
			this.listeners.forEach(l -> l.closingLeftShutter());
			this.leftShutterOpen = false;
			this.listeners.forEach(l -> l.closedLeftShutter());
			break;
		case 1:
			this.listeners.forEach(l -> l.closingRightShutter());
			this.rightShutterOpen = false;
			this.listeners.forEach(l -> l.closedRightShutter());
			break;

		default:
			assert false;
		}
	}

	public void wait(int time) {
		try {
			this.listeners.forEach(l -> l.waiting(time));
			Thread.sleep(time * 1000);
			this.listeners.forEach(l -> l.waited(time));
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}
