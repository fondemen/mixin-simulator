package fr.uha.ensisa.idm.mixin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class AbstractMixingMachineListenerTest {

	private AbstractMixingMachine sut;
	private MixingMachineListener listener;
	private InOrder inOrder;

	@Before
	public void setup() {
		this.sut = new AbstractMixingMachine();
		this.listener = mock(MixingMachineListener.class);
		
		this.inOrder = inOrder(listener);
		
		sut.addListener(listener);
	}
	
	@After
	public void checkNoMoreInteractions() {
		verifyNoMoreInteractions(listener);
	}
	
	@Test(expected=None.class)
	public void addNullListener() {
		sut.addListener(null);
	}
	
	@Test(expected=None.class)
	public void removeNullListener() {
		sut.removeListener(null);
	}
	
	@Test
	public void listenLeftShutter() {
		verifyZeroInteractions(listener);
		
		sut.open(0);

		inOrder.verify(listener).openingLeftShutter();
		inOrder.verify(listener).openedLeftShutter();
		verifyNoMoreInteractions(listener);
		
		sut.shut(0);

		inOrder.verify(listener).closingLeftShutter();
		inOrder.verify(listener).closedLeftShutter();
		verifyNoMoreInteractions(listener);
	}
	
	@Test
	public void removedListener() {
		sut.removeListener(listener);
		verifyZeroInteractions(listener);
		sut.open(0);
		verifyZeroInteractions(listener);
	}
	
	@Test
	public void twoListeners() {
		MixingMachineListener listener2 = mock(MixingMachineListener.class);
		sut.addListener(listener2);
		InOrder inOrder2 = inOrder(listener2);

		sut.open(0);
		inOrder.verify(listener).openingLeftShutter();
		inOrder.verify(listener).openedLeftShutter();
		inOrder2.verify(listener2).openingLeftShutter();
		inOrder2.verify(listener2).openedLeftShutter();
		verifyNoMoreInteractions(listener2);
	}
	
	@Test
	public void listenRightShutter() {
		verifyZeroInteractions(listener);
		
		sut.open(1);

		inOrder.verify(listener).openingRightShutter();
		inOrder.verify(listener).openedRightShutter();
		
		sut.shut(1);

		inOrder.verify(listener).closingRightShutter();
		inOrder.verify(listener).closedRightShutter();
		verifyNoMoreInteractions(listener);
	}
	
	@Test
	public void listenFilter() {
		sut.filt(0);

		inOrder.verify(listener).filterSetting(0);
		inOrder.verify(listener).filterSet(0);
		verifyNoMoreInteractions(listener);
		
		sut.filt(1);

		inOrder.verify(listener).filterSetting(1);
		inOrder.verify(listener).filterSet(1);
		verifyNoMoreInteractions(listener);
		
		sut.filt(2);

		inOrder.verify(listener).filterSetting(2);
		inOrder.verify(listener).filterSet(2);
		verifyNoMoreInteractions(listener);
	}
	
	@Test
	public void listenMove() {
		verifyZeroInteractions(listener);
		
		sut.move(1);

		inOrder.verify(listener).syringeMoving(5, 6);
		inOrder.verify(listener).syringeMoved(5, 6);
	}
	
	@Test
	public void movingBeforeActualMove() {
		final int[] pos = new int[1];
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				pos[0] = sut.getSyringePosition();
				return null;
			}
		}).when(listener).syringeMoving(5, 6);
		
		sut.move(1);
		
		assertEquals(5, pos[0]);

		inOrder.verify(listener).syringeMoving(5, 6);
		inOrder.verify(listener).syringeMoved(5, 6);
	}
	
	@Test
	public void movedAfterActualMove() {
		final int[] pos = new int[1];
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				pos[0] = sut.getSyringePosition();
				return null;
			}
		}).when(listener).syringeMoved(5, 6);
		
		sut.move(1);
		
		assertEquals(6, pos[0]);

		inOrder.verify(listener).syringeMoving(5, 6);
		inOrder.verify(listener).syringeMoved(5, 6);
	}
	
	@Test
	public void notMoving() {
		sut.move(0);
		
		verifyZeroInteractions(listener);
	}
}
