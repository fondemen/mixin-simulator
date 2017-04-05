package fr.uha.ensisa.idm.mixin.example;

import fr.uha.ensisa.idm.mixin.sim.svg.SVGMixingMachine;

public class ExampleMixingMachine extends SVGMixingMachine {
	
	public void run() {
		open(0);
		move(-1);
		filt(1);
		double i = scan();
		suck(i/3);
		move(3);
		blow(i/3);
		move(-4);
		double j = scan();
		suck(j/3);
		move(4);
		blow(j/3);
		move(-5);
		double k = scan();
		suck(k/3);
		move(5);
		shut(0);
		filt(2);
		blow(k/3);
		wait(3);
		suck((i+j+k)/3);
		filt(0);
		open(1);
		move(5);
		blow((i+j+k)/3);
		move(-7);
		shut(1);
		suck(30);
		move(1);
		blow(30);
		// move(-2); // triggers an exception: shutter is closed
	}

	public static void main(String[] args) {
		ExampleMixingMachine main = new ExampleMixingMachine();
		for (int i = 1; i <= main.getInputCups(); ++i) {
			main.setAtInputCup(i, main.getCupCapacity(i));
		}
		main.run();
	}
}
