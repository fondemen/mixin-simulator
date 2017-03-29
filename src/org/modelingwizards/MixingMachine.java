package org.modelingwizards;

import org.eclipse.papyrus.example.utils.MixingMachineUtils;
import org.eclipse.papyrus.example.utils.MixingMachineUtils.ShutterKind;
import org.eclipse.papyrus.example.utils.MixingMachineUtils.FilterKind;

public class MixingMachine {
	public static void main(String[] args) {
		MixingMachineUtils.shut(ShutterKind.Left);
		{
			MixingMachineUtils.move(-2);
			MixingMachineUtils.filter(FilterKind.A);
			MixingMachineUtils.suck((double) 3);
			{
				MixingMachineUtils.move(4);
				MixingMachineUtils.blow((double) 3);
			}
		}
		{
			MixingMachineUtils.move(-1);
			MixingMachineUtils.filter(FilterKind.NONE);
			MixingMachineUtils.suck((double) 3);
			{
				MixingMachineUtils.move(4);
				MixingMachineUtils.blow((double) 1);
			}
			{
				MixingMachineUtils.move(1);
				MixingMachineUtils.blow((double) 2);
			}
		}
		MixingMachineUtils.open(ShutterKind.Left);
	}

}
