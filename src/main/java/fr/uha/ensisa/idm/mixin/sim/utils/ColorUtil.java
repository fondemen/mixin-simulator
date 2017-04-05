package fr.uha.ensisa.idm.mixin.sim.utils;

import java.awt.Color;

public class ColorUtil {
	
	public static Color mix(Color c1, double quantity1, Color c2, double quantity2) {
		if (c1 == null || quantity1 <= 0) return c2;
		if (c2 == null || quantity2 <= 0) return c1;
		
		float r = (float) ((c1.getRed() * quantity1 / 255.0f + c2.getRed() * quantity2 / 255.0f) / (quantity1 + quantity2));
		assert r >= 0 && r <= 1;
		float g = (float) ((c1.getGreen() * quantity1 / 255.0f + c2.getGreen() * quantity2 / 255.0f) / (quantity1 + quantity2));
		assert g >= 0 && g<= 1;
		float b = (float) ((c1.getBlue() * quantity1 / 255.0f + c2.getBlue() * quantity2 / 255.0f) / (quantity1 + quantity2));
		assert b >= 0 && b<= 1;
		
		return new Color(r, g, b);
	}

	public static String colorToHtmlString(Color c) {
		if (c == null) return null;
		
		String rsStr = Integer.toHexString(c.getRed()).toUpperCase();
		while (rsStr.length() < 2)
			rsStr = "0" + rsStr;
		String gsStr = Integer.toHexString(c.getGreen()).toUpperCase();
		while (gsStr.length() < 2)
			gsStr = "0" + gsStr;
		String bsStr = Integer.toHexString(c.getBlue()).toUpperCase();
		while (bsStr.length() < 2)
			bsStr = "0" + bsStr;
		return "#" + rsStr + gsStr + bsStr;
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
