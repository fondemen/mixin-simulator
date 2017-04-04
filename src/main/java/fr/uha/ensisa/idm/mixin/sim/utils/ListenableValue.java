package fr.uha.ensisa.idm.mixin.sim.utils;

import java.util.HashSet;
import java.util.Set;

public class ListenableValue<T> {
	protected final Set<ValueListener<T>> valueListeners;

	protected T value;
	
	public ListenableValue(T initialValue) {
		this.valueListeners = new HashSet<ValueListener<T>>();
		this.setValue(initialValue);
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		T oldValue = this.getValue();
		this.value = value;
		T newValue = this.getValue();
		//if (oldValue == null ? newValue != null : !oldValue.equals(newValue))
			this.fireValueChanged(oldValue, newValue);
	}
	
	public void addValueListener(ValueListener<T> listener) {
		this.valueListeners.add(listener);
	}
	
	public void removeValueListener(ValueListener<T> listener) {
		this.valueListeners.remove(listener);
	}
	
	protected void fireValueChanged(T oldValue, T newValue) {
		for (ValueListener<T> listener : this.valueListeners) {
			listener.valueChanged(this, oldValue, newValue);
		}
	}
}
