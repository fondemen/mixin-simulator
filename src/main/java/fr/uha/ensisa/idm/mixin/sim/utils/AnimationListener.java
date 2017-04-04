package fr.uha.ensisa.idm.mixin.sim.utils;

public interface AnimationListener<T extends Number> extends ValueListener<T> {
	void animationStarted(T initialValue, T finalValue);
	void animationEnded(T initialValue, T finalValue);
	void animationCancelled(T initialValue, T finalValue, T actualValue);
}
