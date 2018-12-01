package fr.uha.ensisa.idm.mixin;

import static fr.uha.ensisa.idm.mixin.AbstractMixingMachine.DEFAULT_CUP_MAX_FILL;
import static fr.uha.ensisa.idm.mixin.AbstractMixingMachine.DEFAULT_INPUT_CUPS;
import static fr.uha.ensisa.idm.mixin.AbstractMixingMachine.DEFAULT_SYRINGE_MAX_FILL;
import static fr.uha.ensisa.idm.mixin.AbstractMixingMachine.DEFAULT_TEMP_CUPS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;

public class AbstractMixingMachineTest {
	
	private AbstractMixingMachine sut;

	@Before
	public void setup() {
		this.sut = new AbstractMixingMachine();
	}

	@Test
	public void negativeInputCup() {
		for (int i = -5; i < 0; i++) {
			try {
				sut.setAtInputCup(i, 10);
				fail("Bad input cup accepted: " + i);
			} catch (Exception x) {}
		}
	}

	@Test
	public void tooLargeInputCup() {
		for (int i = DEFAULT_INPUT_CUPS+1; i < DEFAULT_INPUT_CUPS+5; i++) {
			try {
				sut.setAtInputCup(i, 10);
				fail("Bad input cup accepted: " + i);
			} catch (Exception x) {}
		}
	}
	
	@Test(expected=Exception.class)
	public void tooMuchSetAtInputCup() {
		sut.setAtInputCup(1, DEFAULT_CUP_MAX_FILL+1);
	}
	
	@Test
	public void setAllAtInput() {
		sut.setAtInputCup(DEFAULT_INPUT_CUPS, DEFAULT_CUP_MAX_FILL);
		assertEquals(DEFAULT_CUP_MAX_FILL, sut.getContentAtCup(DEFAULT_INPUT_CUPS), 0.0001);
	}
	
	@Test
	public void setHalfAtInput() {
		sut.setAtInputCup(DEFAULT_INPUT_CUPS, DEFAULT_CUP_MAX_FILL/2);
		assertEquals(DEFAULT_CUP_MAX_FILL/2, sut.getContentAtCup(DEFAULT_INPUT_CUPS), 0.0001);
	}
	
	@Test
	public void setNothingAtInput() {
		sut.setAtInputCup(DEFAULT_INPUT_CUPS, 0);
		assertEquals(0, sut.getContentAtCup(DEFAULT_INPUT_CUPS), 0.0001);
	}
	
	@Test(expected=Exception.class)
	public void moveThroughLeftShutter() {
		sut.move(-DEFAULT_INPUT_CUPS);
	}
	
	@Test(expected=Exception.class)
	public void openUnknownShutter() {
		sut.open(-1);
	}
	
	@Test(expected=Exception.class)
	public void openUnknownShutter2() {
		sut.open(2);
	}
	
	@Test(expected=Exception.class)
	public void closeUnknownShutter() {
		sut.shut(-1);
	}
	
	@Test(expected=Exception.class)
	public void closeUnknownShutter2() {
		sut.shut(2);
	}
	
	@Test
	public void movingToFirstInputCup() {
		final double quantity = DEFAULT_CUP_MAX_FILL/3;
		sut.setAtInputCup(1, quantity);
		sut.open(0);
		sut.move(-DEFAULT_INPUT_CUPS-1);
		assertEquals(quantity, sut.scan(), 0.0001);
	}
	
	@Test
	public void movingToLastInputCup() {
		final double quantity = DEFAULT_CUP_MAX_FILL/4;
		sut.setAtInputCup(DEFAULT_INPUT_CUPS, quantity);
		sut.open(0);
		sut.move(-2);
		assertEquals(quantity, sut.scan(), 0.0001);
	}
	
	@Test(expected=Exception.class)
	public void moveBackThroughLeftShutter() {
		sut.open(0);
		sut.move(-2);
		sut.shut(0);
		sut.move(1);
	}
	
	@Test(expected=Exception.class)
	public void moveTooFarLeft() {
		sut.open(0);
		sut.move(-DEFAULT_INPUT_CUPS-2);
	}
	
	@Test(expected=Exception.class)
	public void moveReallyTooFarLeft() {
		sut.open(0);
		sut.move(-(2*DEFAULT_INPUT_CUPS));
	}
	
	@Test(expected=Exception.class)
	public void moveThroughRightShutter() {
		sut.move(DEFAULT_TEMP_CUPS+2);
	}
	
	@Test(expected=Exception.class)
	public void moveTooFarRight() {
		sut.open(1);
		sut.move(DEFAULT_TEMP_CUPS+2+1);
	}
	
	@Test(expected=Exception.class)
	public void moveReallyTooFarRight() {
		sut.open(1);
		sut.move(2*DEFAULT_TEMP_CUPS+2+1);
	}
	
	@Test
	public void moveInputToOutput() {
		final double quantity = DEFAULT_CUP_MAX_FILL*2/5;
		sut.setAtInputCup(DEFAULT_INPUT_CUPS, quantity);
		sut.open(0);
		sut.move(-2);
		sut.suck(quantity);
		sut.open(1);
		sut.move(2+DEFAULT_TEMP_CUPS+1);
		sut.blow(quantity);
		assertEquals(quantity, sut.getAtOutputCup(1), 0.0001);
	}
	
	@Test(expected=Exception.class)
	public void suckingMoreThanAvailable() {
		sut.setAtInputCup(1, DEFAULT_CUP_MAX_FILL/10);
		sut.open(0);
		sut.move(-DEFAULT_INPUT_CUPS);
		sut.suck(DEFAULT_CUP_MAX_FILL/5);
	}
	
	@Test(expected=None.class)
	public void cleaningSyringeTriggersNoOverflow() {
		double sucked = 0;
		while (sucked < DEFAULT_CUP_MAX_FILL) {
			sut.move(-1);
			sut.suck(DEFAULT_SYRINGE_MAX_FILL);
			sut.move(1);
			sut.blow(DEFAULT_SYRINGE_MAX_FILL);
			sut.move(-1);
			sucked+=DEFAULT_SYRINGE_MAX_FILL;
		}
	}
	
	@Test(expected=Exception.class)
	public void blowingToCleaningFluid() {
		sut.suck(DEFAULT_SYRINGE_MAX_FILL);
		sut.blow(DEFAULT_SYRINGE_MAX_FILL);
	}
	
	@Test(expected=Exception.class)
	public void suckingFromDrain() {
		sut.move(1);
		sut.suck(DEFAULT_SYRINGE_MAX_FILL);
	}
	
	@Test(expected=None.class)
	public void syringeBelowOverflow() {
		int turns = (int)Math.floor(DEFAULT_SYRINGE_MAX_FILL/DEFAULT_CUP_MAX_FILL);
		sut.open(0);
		sut.move(-DEFAULT_INPUT_CUPS-1);
		for (int i = 0; i < turns; ++i) {
			sut.setAtInputCup(1, DEFAULT_CUP_MAX_FILL);
			sut.suck(DEFAULT_CUP_MAX_FILL);
		}
	}
	
	@Test(expected=Exception.class)
	public void syringeOverflow() {
		int turns = 1+(int)Math.floor(DEFAULT_SYRINGE_MAX_FILL/DEFAULT_CUP_MAX_FILL);
		sut.open(0);
		sut.move(-DEFAULT_INPUT_CUPS);
		for (int i = 0; i < turns; ++i) {
			sut.setAtInputCup(1, DEFAULT_CUP_MAX_FILL);
			sut.suck(DEFAULT_CUP_MAX_FILL);
		}
	}
	
	@Test(expected=None.class)
	public void cupBelowOverflow() {
		double blown = 0;
		sut.open(0);
		sut.move(-1);
		double toSuck = Math.min(DEFAULT_SYRINGE_MAX_FILL, DEFAULT_CUP_MAX_FILL);
		while (blown < DEFAULT_CUP_MAX_FILL) {
			double q = Math.min(toSuck, DEFAULT_CUP_MAX_FILL-blown); 
			sut.setAtInputCup(DEFAULT_INPUT_CUPS, toSuck);
			sut.suck(q);
			sut.move(3); // first temp cup
			sut.blow(q);
			sut.move(-3);
			blown += q;
		}
	}
	
	@Test(expected=Exception.class)
	public void cupOverflow() {
		double blown = 0;
		sut.open(0);
		sut.move(-1);
		double toSuck = Math.min(DEFAULT_SYRINGE_MAX_FILL, DEFAULT_CUP_MAX_FILL);
		while (blown <= DEFAULT_CUP_MAX_FILL) {
			sut.setAtInputCup(DEFAULT_INPUT_CUPS, DEFAULT_SYRINGE_MAX_FILL);
			sut.suck(toSuck);
			sut.move(3); // first temp cup
			sut.blow(toSuck);
			sut.move(-3);
			blown += toSuck;
		}
	}
	
	@Test(expected=Exception.class)
	public void blowingMoreThanInSyringe() {
		sut.setAtInputCup(DEFAULT_INPUT_CUPS, 1);
		sut.open(0);
		sut.move(-1);
		sut.suck(1);
		sut.move(2); // drain
		sut.blow(2);
	}
	
	@Test(expected=Exception.class)
	public void badFilter() {
		sut.filt(-1);
	}
	
	@Test(expected=Exception.class)
	public void badFilter2() {
		sut.filt(3);
	}
	
	@Test
	public void waiting() {
		long start = System.currentTimeMillis();
		sut.wait(2);
		long diff = System.currentTimeMillis()-start;
		assertTrue(diff > 2000);
	}
	
}
