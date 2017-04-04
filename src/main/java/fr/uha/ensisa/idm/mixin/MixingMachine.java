package fr.uha.ensisa.idm.mixin;

public interface MixingMachine {

	void move(int offset); // Moves needle 'offset' positions (- = L, + = R)

	void filt(int filter); // Applies 'filter' (0 = no filter, 1 = A, 2 = B)

	double scan(); // Scans amount (mls) in the cup beneath the needle

	void suck(double amount); // Extracts 'amount' mls from the cup

	void blow(double amount); // Injects 'amount' mls into the cup

	void open(int shutter); // Opens shutter 'shutter' (0 = L, 1 = R)

	void shut(int shutter); // Closes shutter 'shutter' (0 = L, 1 = R)

	void wait(int time); // Waits 'time' seconds (+/- 2 seconds)
}