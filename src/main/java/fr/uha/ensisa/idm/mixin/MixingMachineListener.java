package fr.uha.ensisa.idm.mixin;

public interface MixingMachineListener {

	void syringeMoving(int currentCup, int destinationCup);
	void syringeMoved(int initialCup, int currentCup);
	void filterSetting(int filter);
	void filterSet(int filter);
	void suckingCup(double amount, int cup, double cupContentBefore, double cupContentAfter, double syringueFillBefore, double syringeContentAfter);
	void suckedCup(double amount, int cup, double cupContentBefore, double cupContentAfter, double syringeContentBefire, double syringueFillAfter);
	void blowingCup(double amount, int cup, double cupContentBefore, double cupContentAfter, double syringueFillBefore, double syringeContenteAfter);
	void blownCup(double amount, int cup, double cupContentBefore, double cupContentAfter, double syringeContenteBefore, double syringueFillAfter);
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
