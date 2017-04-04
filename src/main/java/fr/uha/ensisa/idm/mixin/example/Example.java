package fr.uha.ensisa.idm.mixin.example;

import fr.uha.ensisa.idm.mixin.sim.svg.SVGMixingMachine;

public class Example {

	public static void main(String[] args) {
		SVGMixingMachine main = new SVGMixingMachine();
		main.setAtInputCup(3, 10);
		main.open(0);
		main.move(-1);
		main.filt(1);
		main.suck(5);
		main.wait(1);
		main.suck(5);
		main.move(3);
		main.shut(0);
		main.filt(2);
		main.blow(10);
		main.wait(3);
		main.suck(10);
		main.filt(0);
		main.open(1);
		main.move(5);
		main.blow(10);
		main.move(-7);
		main.shut(1);
		main.suck(30);
		main.move(1);
		main.blow(30);
		main.move(-2);
	}
}
