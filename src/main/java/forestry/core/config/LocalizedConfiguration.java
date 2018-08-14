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

import org.apache.commons.lang3.text.WordUtils;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import forestry.core.utils.Translator;

public class LocalizedConfiguration extends Configuration {

	public LocalizedConfiguration(File file, String configVersion) {
		super(file, configVersion);
	}

	public Configuration setCategoryLanguageKey(String categoryName) {
		String categoryKey = "for.config" + categoryName;
		return super.setCategoryLanguageKey(categoryName, categoryKey);
	}

	@Override
	public boolean getBoolean(String category, String name, boolean defaultValue, String comment) {
		Property prop = this.get(category, name, defaultValue);
		prop.setComment(comment + " [default: " + defaultValue + "]");
		return prop.getBoolean(defaultValue);
	}

	public boolean getBooleanLocalized(String category, String name, boolean defaultValue) {
		return getBooleanLocalizedFormatted(category, name, defaultValue, "");
	}

	public boolean getBooleanLocalizedFormatted(String category, String name, boolean defaultValue, Object... args) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = Translator.translateToLocalFormatted(commentKey, args);
		return getBoolean(name, category, defaultValue, comment, langKey);
	}

	public String getStringLocalized(String category, String name, String defaultValue) {
		return getStringLocalized(category, name, defaultValue, new String[0]);
	}

	public String getStringLocalized(String category, String name, String defaultValue, String[] validValues) {
		return getStringLocalizedFormatted(category, name, defaultValue, validValues, "");
	}

	public String getStringLocalizedFormatted(String category, String name, String defaultValue, String[] validValues, Object... args) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = Translator.translateToLocalFormatted(commentKey, args);

		Property prop = this.get(category, name, defaultValue);
		prop.setValidValues(validValues);
		prop.setLanguageKey(langKey);
		prop.setComment(comment + getValidOptions(prop));
		return prop.getString();
	}

	public <T extends Enum<T>> T getEnumLocalized(String category, String name, T defaultValue, T[] validEnumValues) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = "";
		if (Translator.canTranslateToLocal(commentKey)) {
			comment = Translator.translateToLocal(commentKey);
		}

		Property prop = this.get(category, name, defaultValue.name());

		String[] validValues = new String[validEnumValues.length];
		for (int i = 0; i < validEnumValues.length; i++) {
			T enumValue = validEnumValues[i];
			validValues[i] = enumValue.name();
		}

		prop.setValidValues(validValues);
		prop.setLanguageKey(langKey);
		prop.setComment(comment + " [default: " + defaultValue + "] [valid: " + Arrays.toString(prop.getValidValues()) + "]");
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
		if (Translator.canTranslateToLocal(commentKey)) {
			comment = Translator.translateToLocal(commentKey);
		}
		return super.getStringList(name, category, defaultValue, comment);
	}

	public String[] getStringListLocalized(String category, String name, String[] defaultValue, String[] validValues) {
		return getStringListLocalizedFormatted(category, name, defaultValue, validValues, "");
	}

	public String[] getStringListLocalizedFormatted(String category, String name, String[] defaultValue, String[] validValues, Object... args) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = Translator.translateToLocalFormatted(commentKey, args);

		Property prop = this.get(category, name, defaultValue);
		prop.setLanguageKey(langKey);
		prop.setValidValues(validValues);
		prop.setComment(comment + getValidOptions(prop));
		return prop.getStringList();
	}

	private String getValidOptions(Property prop) {
		String defautValue = prop.isList() ? Arrays.toString(prop.getDefaults()) : prop.getDefault();
		String ret = " [default: " + defautValue + "]";
		if (prop.getValidValues().length != 0) {
			ret += " [valid: " + Arrays.toString(prop.getValidValues()) + "]";
		}
		return ret;
	}

	public float getFloatLocalized(String category, String name, float defaultValue, float minValue, float maxValue) {
		return getFloatLocalizedFormatted(category, name, defaultValue, minValue, maxValue, "");
	}

	public float getFloatLocalizedFormatted(String category, String name, float defaultValue, float minValue, float maxValue, Object... args) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = Translator.translateToLocalFormatted(commentKey, args);
		return getFloat(name, category, defaultValue, minValue, maxValue, comment, langKey);
	}

	public int getIntLocalized(String category, String name, int defaultValue, int minValue, int maxValue) {
		return getIntLocalizedFormatted(category, name, defaultValue, minValue, maxValue, "");
	}

	public int getIntLocalizedFormatted(String category, String name, int defaultValue, int minValue, int maxValue, Object... args) {
		String langKey = "for.config." + category + '.' + name;
		String commentKey = langKey + '.' + "comment";
		String comment = Translator.translateToLocalFormatted(commentKey, args);
		return getInt(name, category, defaultValue, minValue, maxValue, comment, langKey);
	}

	public void addCategoryCommentLocalized(String category) {
		String langKey = "for.config." + category + '.' + "category_comment";
		setCategoryComment(category, WordUtils.wrap(Translator.translateToLocal(langKey), 100));
	}

}
