package fr.uha.ensisa.idm.mixin;

public interface ListenableMixingMachine extends MixingMachine {
	public void addListener(MixingMachineListener listener);
	public void removeListener(MixingMachineListener listener);
}
