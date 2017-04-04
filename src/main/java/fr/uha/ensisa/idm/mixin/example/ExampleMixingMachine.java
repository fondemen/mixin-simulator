package fr.uha.ensisa.idm.mixin.example;

import fr.uha.ensisa.idm.mixin.AbstractMixingMachine;
import fr.uha.ensisa.idm.mixin.sim.svg.SVGMixingMachine;

public class ExampleMixingMachine extends SVGMixingMachine {
	
	public void run() {
		open(0);
		move(-1);
		filt(1);
		suck(5);
		wait(1);
		suck(5);
		move(3);
		shut(0);
		filt(2);
		blow(10);
		wait(3);
		suck(10);
		filt(0);
		open(1);
		move(5);
		blow(10);
		move(-7);
		shut(1);
		suck(30);
		move(1);
		blow(30);
		move(-2);
	}

	public static void main(String[] args) {
		ExampleMixingMachine main = new ExampleMixingMachine();
		for (int i = 0; i < AbstractMixingMachine.DEFAULT_INPUT_CUPS; ++i) {
			main.setAtInputCup(i+1, AbstractMixingMachine.DEFAULT_CUP_MAX_FILL);
		}
		main.run();
	}
}
