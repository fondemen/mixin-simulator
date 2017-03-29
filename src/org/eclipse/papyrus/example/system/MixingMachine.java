package org.eclipse.papyrus.example.system;

import org.eclipse.papyrus.example.utils.MixingMachineUtils;
import org.eclipse.papyrus.example.utils.MixingMachineUtils.FilterKind;
import org.eclipse.papyrus.example.utils.MixingMachineUtils.ShutterKind;

public class MixingMachine {

	static public void main(String[] args) {

		MixingMachineUtils.filter(FilterKind.A);
		MixingMachineUtils.shut(ShutterKind.Left);
		MixingMachineUtils.move(2);
		MixingMachineUtils.suck(Double.valueOf(3));
		MixingMachineUtils.move(1);
		MixingMachineUtils.blow(Double.valueOf(3));
		MixingMachineUtils.open(ShutterKind.Left);

	}
}
