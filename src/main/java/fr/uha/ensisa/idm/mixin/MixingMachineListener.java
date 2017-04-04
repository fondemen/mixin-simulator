package fr.uha.ensisa.idm.mixin;

public interface MixingMachineListener {

	void syringeMoving(int currentCup, int destinationCup);
	void syringeMoved(int initialCup, int currentCup);
	void filterSetting(int filter);
	void filterSet(int filter);
	void suckingCup(double amount, int cup, double cupContentBefore, double syringueFillBefore);
	void suckedCup(double amount, int cup, double cupContentAfter, double syringueFillAfter);
	void blowingCup(double amount, int cup, double cupContentBefore, double syringueFillBefore);
	void blowedCup(double amount, int cup, double cupContentAfter, double syringueFillAfter);
	void openingLeftShutter();
	void openedLeftShutter();
	void openingRightShutter();
	void openedRightShutter();
	void closingLeftShutter();
	void closedLeftShutter();
	void closingRightShutter();
	void closedRightShutter();
	void waiting(int time);
	void waited(int time);
}
