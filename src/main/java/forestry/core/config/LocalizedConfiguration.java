/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.config;

import java.io.File;
import java.util.Arrays;

import net.minecraft.util.StatCollector;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class LocalizedConfiguration extends Configuration {

	public LocalizedConfiguration(File file, String configVersion) {
		super(file, configVersion);
	}

	public Configuration setCategoryLanguageKey(String categoryName) {
		String categoryKey = "for.config" + categoryName;
		return super.setCategoryLanguageKey(categoryName, categoryKey);
	}

	public boolean getBoolean(String category, String name, boolean defaultValue, String comment) {
		Property prop = this.get(category, name, defaultValue);
		prop.comment = comment + " [default: " + defaultValue + "]";
		return prop.getBoolean(defaultValue);
	}

	public boolean getBooleanLocalized(String category, String name, boolean defaultValue) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = "";
		if (StatCollector.canTranslate(commentKey)) {
			comment = StatCollector.translateToLocal(commentKey);
		}
		return getBoolean(name, category, defaultValue, comment, langKey);
	}

	public String getStringLocalized(String category, String name, String defaultValue) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = "";
		if (StatCollector.canTranslate(commentKey)) {
			comment = StatCollector.translateToLocal(commentKey);
		}
		return getString(name, category, defaultValue, comment, langKey);
	}

	public String getStringLocalized(String category, String name, String defaultValue, String[] validValues) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = "";
		if (StatCollector.canTranslate(commentKey)) {
			comment = StatCollector.translateToLocal(commentKey);
		}

		Property prop = this.get(category, name, defaultValue);
		prop.setValidValues(validValues);
		prop.setLanguageKey(langKey);
		prop.comment = comment + " [default: " + defaultValue + "] [valid: " + Arrays.toString(prop.getValidValues()) + "]";
		return prop.getString();
	}

	public <T extends Enum<T>> T getEnumLocalized(String category, String name, T defaultValue, T[] validEnumValues) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = "";
		if (StatCollector.canTranslate(commentKey)) {
			comment = StatCollector.translateToLocal(commentKey);
		}

		Property prop = this.get(category, name, defaultValue.name());

		String[] validValues = new String[validEnumValues.length];
		for (int i = 0; i < validEnumValues.length; i++) {
			T enumValue = validEnumValues[i];
			validValues[i] = enumValue.name();
		}

		prop.setValidValues(validValues);
		prop.setLanguageKey(langKey);
		prop.comment = comment + " [default: " + defaultValue + "] [valid: " + Arrays.toString(prop.getValidValues()) + "]";
		String stringValue = prop.getString();

		T enumValue = defaultValue;
		for (int i = 0; i < validValues.length; i++) {
			if (stringValue.equals(validValues[i])) {
				enumValue = validEnumValues[i];
			}
		}

		return enumValue;
	}

	public String[] getStringListLocalized(String category, String name, String[] defaultValue) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = "";
		if (StatCollector.canTranslate(commentKey)) {
			comment = StatCollector.translateToLocal(commentKey);
		}
		return super.getStringList(name, category, defaultValue, comment);
	}

	public String[] getStringListLocalized(String category, String name, String[] defaultValue, String[] validValues) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = "";
		if (StatCollector.canTranslate(commentKey)) {
			comment = StatCollector.translateToLocal(commentKey);
		}

		Property prop = this.get(category, name, defaultValue);
		prop.setLanguageKey(langKey);
		prop.setValidValues(validValues);
		prop.comment = comment + " [default: " + Arrays.toString(defaultValue) + "] [valid: " + Arrays.toString(prop.getValidValues()) + "]";
		return prop.getStringList();
	}

	public float getFloatLocalized(String category, String name, float defaultValue, float minValue, float maxValue) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = "";
		if (StatCollector.canTranslate(commentKey)) {
			comment = StatCollector.translateToLocal(commentKey);
		}
		return getFloat(name, category, defaultValue, minValue, maxValue, comment, langKey);
	}

	public int getIntLocalized(String category, String name, int defaultValue, int minValue, int maxValue) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = "";
		if (StatCollector.canTranslate(commentKey)) {
			comment = StatCollector.translateToLocal(commentKey);
		}
		return getInt(name, category, defaultValue, minValue, maxValue, comment, langKey);
	}

}
