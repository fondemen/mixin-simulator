package fr.uha.ensisa.idm.mixin.example;

import fr.uha.ensisa.idm.mixin.sim.svg.SVGMixingMachine;

public class ExampleMixingMachine extends SVGMixingMachine {
	
	public int run() {
		
		double i,j;
		open(0);
		move(-4);
		if(scan()!=9) return -1;
		suck(9);move(5);blow(9);
		move(-2);suck(30);move(1);blow(30);
		move(-3);
		if(scan()<6) return -1;
		i=scan();suck(i);move(5);shut(0);blow(i);
		move(-3);suck(30);move(1);blow(30);
		move(1);filt(1);suck(3);move(3);filt(0);blow(3);
		move(-5);suck(30);move(1);blow(30);
		move(2);filt(2);suck(2);
		move(2);filt(0);blow(2);
		wait(8);
		move(-5);suck(30);move(1);blow(30);
		move(1);filt(1);suck(3);move(3);filt(0);blow(3);
		move(-5);suck(30);move(1);blow(30);
		move(2);filt(2);suck(2);
		move(2);filt(0);blow(2);
		wait(8);
		move(-5);suck(30);move(1);blow(30);
		move(1);filt(1);suck(3);move(3);filt(0);blow(3);
		move(-5);suck(30);move(1);blow(30);
		move(2);filt(2);suck(2);
		move(2);filt(0);blow(2);
		wait(8);
		move(-5);suck(30);move(1);blow(30);
		move(2);i=scan();suck(i);move(3);
		if(i<=10) {blow(i);j=i;}
		else {blow(10);j=10;}
		move(-1);blow(i-j);
		wait(7);
		move(-5);suck(30);move(1);blow(30);
		move(4);suck(3+2+3+2+3+2+i-j);move(1);blow(3+2+3+2+3+2+i-j);
		wait(12);
		suck(3+2+3+2+3+2+i);open(1);move(1);blow(3+2+3+2+3+2+i);
		move(-7);shut(1);suck(30);move(1);blow(30);
		return 0;
	}

	public static void main(String[] args) {
		ExampleMixingMachine main = new ExampleMixingMachine();
		for (int i = 1; i <= main.getInputCups(); ++i) {
			main.setAtInputCup(i, 9);
		}
		main.run();
	}
}
