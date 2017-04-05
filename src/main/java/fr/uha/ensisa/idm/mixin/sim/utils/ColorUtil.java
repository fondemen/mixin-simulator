package fr.uha.ensisa.idm.mixin.sim.utils;

import java.awt.Color;
import java.util.Arrays;

public class ColorUtil {
	
	public static Color mix(Color c1, double quantity1, Color c2, double quantity2) {
		if (c1 == null || quantity1 <= 0) return c2;
		if (c2 == null || quantity2 <= 0) return c1;
		
		double[] rgb = Arrays.asList(new int[]{c1.getRed(), c2.getRed()}, new int[]{c1.getGreen(), c2.getGreen()}, new int[]{c1.getBlue(), c2.getBlue()}).stream()
				.mapToDouble(comp -> ((comp[0] * quantity1 + comp[1] * quantity2) / (255.0f * (quantity1 + quantity2))))
				.toArray();
		
		return new Color((float)rgb[0], (float)rgb[1], (float)rgb[2]);
	}

	public static String colorToHtmlString(Color c) {
		if (c == null) return null;
		
		return "#" + Arrays.asList(c.getRed(), c.getGreen(), c.getBlue()).stream().map(comp -> String.format("%02X", comp)).reduce((s1, s2) -> s1 + s2).get();
	}

	public static float generateHueForId(int id) {
		return id == 0 ? 0.0f : getFromInterval(id - 1, 0, 1);
	}

	public static float getFromInterval(int id, float min, float max) {
		float avg = (min + max) / 2;
		if (id <= 0 || min == max) {
			return avg;
		} else {
			if (id % 2 == 0) {
				return getFromInterval((id - 2) / 2, min, avg);
			} else {
				return getFromInterval((id - 1) / 2, avg, max);
			}
		}
	}
}
