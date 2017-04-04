package fr.uha.ensisa.idm.mixin.sim.utils;

public interface ValueListener<T> {
	void valueChanged(ListenableValue<T> value, T oldValue, T newValue);
}
