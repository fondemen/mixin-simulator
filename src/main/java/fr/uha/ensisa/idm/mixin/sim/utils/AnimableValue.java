package fr.uha.ensisa.idm.mixin.sim.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnimableValue<T extends Number> extends ListenableValue<T> {
	protected final Set<AnimationListener<T>> animationListeners;

	public AnimableValue(T initialValue) {
		super(initialValue);
		this.animationListeners = new HashSet<AnimationListener<T>>();
	}

	@SuppressWarnings("unchecked")
	//Really ugly method... Can't figure out how to fix this !
	public static <T extends Number> T add(T lhs, T rhs) {
		if (Double.class.isInstance(lhs) || Double.class.isInstance(rhs))
			return (T) Double.valueOf(lhs.doubleValue() + rhs.doubleValue());
		else if (Float.class.isInstance(lhs) || Float.class.isInstance(rhs))
			return (T) Float.valueOf(lhs.floatValue() + rhs.floatValue());
		else if (Long.class.isInstance(lhs) || Long.class.isInstance(rhs))
			return (T) Long.valueOf(lhs.longValue() + rhs.longValue());
		else if (Integer.class.isInstance(lhs) || Integer.class.isInstance(rhs))
			return (T) Integer.valueOf(lhs.intValue() + rhs.intValue());
		else if (Short.class.isInstance(lhs) || Short.class.isInstance(rhs))
			return (T) Double.valueOf(lhs.doubleValue() + rhs.doubleValue());
		else if (Byte.class.isInstance(lhs) || Byte.class.isInstance(rhs))
			return (T) Byte.valueOf((byte) (lhs.byteValue() + rhs.byteValue()));
		else
			throw new IllegalStateException("Unsupported operation: " + lhs.getClass().getName() + ' ' + lhs + " + " + rhs.getClass().getName() + ' ' + rhs);
	}

	@SuppressWarnings("unchecked")
	//Really ugly method... Can't figure out how to fix this !
	public static <T extends Number> T sub(T lhs, T rhs) {
		if (Double.class.isInstance(lhs) || Double.class.isInstance(rhs))
			return (T) Double.valueOf(lhs.doubleValue() - rhs.doubleValue());
		else if (Float.class.isInstance(lhs) || Float.class.isInstance(rhs))
			return (T) Float.valueOf(lhs.floatValue() - rhs.floatValue());
		else if (Long.class.isInstance(lhs) || Long.class.isInstance(rhs))
			return (T) Long.valueOf(lhs.longValue() - rhs.longValue());
		else if (Integer.class.isInstance(lhs) || Integer.class.isInstance(rhs))
			return (T) Integer.valueOf(lhs.intValue() - rhs.intValue());
		else if (Short.class.isInstance(lhs) || Short.class.isInstance(rhs))
			return (T) Double.valueOf(lhs.doubleValue() - rhs.doubleValue());
		else if (Byte.class.isInstance(lhs) || Byte.class.isInstance(rhs))
			return (T) Byte.valueOf((byte) (lhs.byteValue() - rhs.byteValue()));
		else
			throw new IllegalStateException("Unsupported operation: " + lhs.getClass().getName() + ' ' + lhs + " - " + rhs.getClass().getName() + ' ' + rhs);
	}

	@SuppressWarnings("unchecked")
	//Really ugly method... Can't figure out how to fix this !
	public static <T extends Number> T mul(T lhs, T rhs) {
		if (Double.class.isInstance(lhs) || Double.class.isInstance(rhs))
			return (T) Double.valueOf(lhs.doubleValue() * rhs.doubleValue());
		else if (Float.class.isInstance(lhs) || Float.class.isInstance(rhs))
			return (T) Float.valueOf(lhs.floatValue() * rhs.floatValue());
		else if (Long.class.isInstance(lhs) || Long.class.isInstance(rhs))
			return (T) Long.valueOf(lhs.longValue() * rhs.longValue());
		else if (Integer.class.isInstance(lhs) || Integer.class.isInstance(rhs))
			return (T) Integer.valueOf(lhs.intValue() * rhs.intValue());
		else if (Short.class.isInstance(lhs) || Short.class.isInstance(rhs))
			return (T) Double.valueOf(lhs.doubleValue() * rhs.doubleValue());
		else if (Byte.class.isInstance(lhs) || Byte.class.isInstance(rhs))
			return (T) Byte.valueOf((byte) (lhs.byteValue() * rhs.byteValue()));
		else
			throw new IllegalStateException("Unsupported operation: " + lhs.getClass().getName() + ' ' + lhs + " * " + rhs.getClass().getName() + ' ' + rhs);
	}

	@SuppressWarnings("unchecked")
	//Really ugly method... Can't figure out how to fix this !
	public static <T extends Number> T div(T lhs, T rhs) {
		if (Double.class.isInstance(lhs) || Double.class.isInstance(rhs))
			return (T) Double.valueOf(lhs.doubleValue() / rhs.doubleValue());
		else if (Float.class.isInstance(lhs) || Float.class.isInstance(rhs))
			return (T) Float.valueOf(lhs.floatValue() / rhs.floatValue());
		else if (Long.class.isInstance(lhs) || Long.class.isInstance(rhs))
			return (T) Long.valueOf(lhs.longValue() / rhs.longValue());
		else if (Integer.class.isInstance(lhs) || Integer.class.isInstance(rhs))
			return (T) Integer.valueOf(lhs.intValue() / rhs.intValue());
		else if (Short.class.isInstance(lhs) || Short.class.isInstance(rhs))
			return (T) Double.valueOf(lhs.doubleValue() / rhs.doubleValue());
		else if (Byte.class.isInstance(lhs) || Byte.class.isInstance(rhs))
			return (T) Byte.valueOf((byte) (lhs.byteValue() / rhs.byteValue()));
		else
			throw new IllegalStateException("Unsupported operation: " + lhs.getClass().getName() + ' ' + lhs + " / " + rhs.getClass().getName() + ' ' + rhs);
	}
	
	@SuppressWarnings("unchecked")
	protected void checkSupported(T value) {
		List<Class> suppportedTypes = Arrays.asList(new Class [] {Double.class, Float.class, Long.class, Integer.class, Short.class, Byte.class});
		for (Class c : suppportedTypes) {
			if (c.isInstance(value))
				return;
		}
		throw new IllegalArgumentException("Unsupported type " + value.getClass());
	}
	
	@SuppressWarnings("unchecked")
	protected T toT(Number n) {
		if (Byte.class.isInstance(this.getValue()))
			return (T) Byte.valueOf(n.byteValue());
		else if (Short.class.isInstance(this.getValue()))
			return (T) Short.valueOf(n.shortValue());
		else if (Integer.class.isInstance(this.getValue()))
			return (T) Integer.valueOf(n.intValue());
		else if (Long.class.isInstance(this.getValue()))
			return (T) Long.valueOf(n.longValue());
		else if (Float.class.isInstance(this.getValue()))
			return (T) Float.valueOf(n.floatValue());
		else if (Double.class.isInstance(this.getValue()))
			return (T) Double.valueOf(n.doubleValue());
		else
			throw new IllegalStateException("Unsupported type: " + this.getValue().getClass().getName());
	}
	
	public synchronized void setValue(T value) {
		this.checkSupported(value);
		super.setValue(value);
	}
	
	public void addAnimationListener(AnimationListener<T> listener) {
		super.addValueListener(listener);
		this.animationListeners.add(listener);
	}
	
	public void removeAnimationListener(AnimationListener<T> listener) {
		super.removeValueListener(listener);
		this.animationListeners.remove(listener);
	}
	
	protected void fireAnimationStarted(T initialValue, T finalValue) {
		for (AnimationListener<T> l : this.animationListeners) {
			l.animationStarted(initialValue, finalValue);
		}
	}
	
	protected void fireAnimationEnded(T initialValue, T finalValue) {
		for (AnimationListener<T> l : this.animationListeners) {
			l.animationEnded(initialValue, finalValue);
		}
	}
	
	protected void fireAnimationInterrupted(T initialValue, T finalValue, T actualValue) {
		for (AnimationListener<T> l : this.animationListeners) {
			l.animationCancelled(initialValue, finalValue, actualValue);
		}
	}
	
	protected class Animator implements Runnable {
		private boolean interruptionRequest = false;
		private final long initialTime;
		private final T finalValue;
		private final int msPerUnit;
		private final int fps;
		
		public Animator(T finalValue, int msPerUnit, int fps) {
			this.finalValue = finalValue;
			this.msPerUnit = msPerUnit;
			this.fps = fps;
			this.initialTime = new Date().getTime();
		}
		
		protected boolean shouldInterrupt() {
			return this.interruptionRequest;
		}
		
		public void interrupt() {
			this.interruptionRequest = true;
		}
		
		public T getFinalValue() {
			return this.finalValue;
		}
		
		public void run() {
			try {
				setRunningAnimator(this);
				T initialValue = getValue();
				fireAnimationStarted(initialValue, finalValue);
				T diff = sub(finalValue, initialValue);
				T val = initialValue;
				long current;
				long totalTime = (long)Math.abs((double)this.msPerUnit * diff.doubleValue());
				boolean stop = false;
				do {
					if (fps > 0) {
						Thread.sleep(1000/fps);
						current = new Date().getTime() - initialTime;
					} else {
						Thread.sleep(totalTime);
						current = totalTime;
					}
					stop = shouldInterrupt();
					if (! stop) {
						if (current >= totalTime) {
							setValue(finalValue);
							fireAnimationEnded(initialValue, finalValue);
							return;
						} else {
							double ratio = ((double)current) / ((double)totalTime);
							T ratioT = toT(ratio); 
							T interm = mul(diff, ratioT);
							val = add(initialValue, interm);
							setValue(val);
						}
					}
				} while (! stop);
				fireAnimationInterrupted(initialValue, finalValue, val);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				synchronized (AnimableValue.this) {
					unsetRunningAnimator(this);
					AnimableValue.this.notify();
				}
			}
		}
	}
	
	private Animator runningAnimator = null;
	
	protected synchronized Animator getRunningAnimator() {
		return this.runningAnimator;
	}
	
	protected synchronized void setRunningAnimator(Animator animator) {
		Animator atRun = this.getRunningAnimator();
		if (atRun != null) {
			atRun.interrupt();
			int counter = 0;
			do {
				try {
					this.wait(50);
					atRun = this.getRunningAnimator();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					counter++;
					if (counter > 1000)
						throw new IllegalStateException("Cannot establish animation.");
				}
			} while (atRun != null);
		}
		this.runningAnimator = animator;
	}
	
	protected synchronized void unsetRunningAnimator(Animator animator) {
		assert this.runningAnimator == animator;
		this.runningAnimator = null;
	}

	public synchronized void goTo(final T finalValue, final int msPerUnit, final int fps) {
		if (this.getValue().equals(finalValue)) {
			new Thread() {
				public void run() {
					fireAnimationEnded(getValue(), finalValue);
				}
			}.start();
			return;
		}
		Thread newAnimation = new Thread(new Animator(finalValue, msPerUnit, fps));
		newAnimation.setDaemon(true);
		newAnimation.start();
	}

	@Override
	public void addValueListener(ValueListener<T> listener) {
		if (listener instanceof AnimationListener)
			this.addAnimationListener((AnimationListener<T>)listener);
		else
			super.addValueListener(listener);
	}

	@Override
	public void removeValueListener(ValueListener<T> listener) {
		if (listener instanceof AnimationListener)
			this.removeAnimationListener((AnimationListener<T>)listener);
		else
			super.removeValueListener(listener);
	}
	
	public synchronized void cancelAnimation() {
		Animator animator = this.getRunningAnimator();
		if (animator != null)
			animator.interrupt();
	}
}
