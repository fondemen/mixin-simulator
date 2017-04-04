package org.eclipse.papyrus.example.utils;

import fr.uha.ensisa.idm.mixin.AbstractMixingMachine;
import fr.uha.ensisa.idm.mixin.sim.svg.SVGMixingMachine;

/**
 * For legacy concern...
 * @deprecated prefer using {@link SVGMixingMachine}
 *
 */
public class MixingMachineUtils {
	
	private static SVGMixingMachine machine;
	
	static {
		machine = new SVGMixingMachine();
		for (int i = 0; i < AbstractMixingMachine.DEFAULT_INPUT_CUPS; ++i) {
			machine.setAtInputCup(i+1, AbstractMixingMachine.DEFAULT_CUP_MAX_FILL);
		}
	}

	/**
	 * Internal enum to define the current action
	 */
	private static enum ActionKind {
		Suck, Blow, None
	}

	/**
	 * Enumeration for the two shutter : Left and Right shutters
	 */
	public static enum ShutterKind {
		Left(0), Right(1);
		
		public final int kind;
		
		ShutterKind(int kind) {
			this.kind = kind;
		}
	};

	/**
	 * Enumeration for the three filters available : A, B or None
	 */
	public static enum FilterKind {
		A(1), B(2), NONE(0);
		
		public final int kind;
		
		FilterKind(int kind) {
			this.kind = kind;
		}
	};

	/**
	 * Opens the shutter specified
	 * 
	 * @param shutter
	 *        The shutter to open
	 */
	public static void open(ShutterKind shutter) {
		machine.open(shutter.kind);
	}

	/**
	 * Shuts the shutter specified
	 * 
	 * @param shutter
	 *        The shutter to shut
	 */
	public static void shut(ShutterKind shutter) {
		machine.shut(shutter.kind);
	}

	/**
	 * Suck a certain amount
	 * 
	 * @param amount
	 *        The amount to suck
	 */
	public static void suck(Double amount) {
		machine.suck(amount);
	}

	/**
	 * Blow a certain amount
	 * 
	 * @param amount
	 *        The amount to blow
	 */
	public static void blow(Double amount) {
		machine.blow(amount);
	}

	/**
	 * Specify the filter to apply on the needle
	 * 
	 * @param filter
	 *        The filter to apply
	 */
	public static void filter(FilterKind filter) {
		machine.filt(filter.kind);
	}

	/**
	 * Move the needle to another position
	 * 
	 * @param offset
	 *        The offset to apply for the move
	 */
	public static void move(Integer offset) {
		machine.move(offset);
	}
}
