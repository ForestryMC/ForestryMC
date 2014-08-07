/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.utils;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

import forestry.core.proxy.Proxies;

/**
 * Simple mod localization class.
 * 
 * @author Jimeo Wan / modified for Forestry by SirSengir
 * @license Public domain
 */
public class Localization {

	public static Localization instance = new Localization();

	private static final String DEFAULT_LANGUAGE = "en_US";

	private String loadedLanguage = null;
	private Properties defaultMappings = new Properties();
	private Properties mappings = new Properties();
	private LinkedList<String> modules = new LinkedList<String>();

	/**
	 * Loads the mod's localization files. All language files must be stored in "[modname]/lang/", in .properties files. (ex: for the mod 'invtweaks', the
	 * french translation is in: "invtweaks/lang/fr_FR.properties")
	 * 
	 * @param modName
	 *            The mod name
	 */
	public Localization() {
		addLocalization("/lang/forestry/core/");
	}

	public void addLocalization(String path) {
		modules.add(path);
		load(path, DEFAULT_LANGUAGE);
	}

	private void load(String newLanguage) {
		defaultMappings.clear();
		mappings.clear();

		for (String path : this.modules)
			load(path, newLanguage);
	}

	private void load(String path, String newLanguage) {

		Properties modMappings = new Properties();

		try {
			InputStream langStream = Localization.class.getResourceAsStream(path + newLanguage + ".properties");
			InputStream defaultLangStream = Localization.class.getResourceAsStream(path + DEFAULT_LANGUAGE + ".properties");
			modMappings.load((langStream == null) ? defaultLangStream : langStream);
			mappings.putAll(modMappings);

			modMappings.clear();
			modMappings.load(defaultLangStream);
			defaultMappings.putAll(modMappings);

			if (langStream != null)
				langStream.close();
			defaultLangStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadedLanguage = newLanguage;
	}

	/**
	 * Get a string for the given key, in the currently active translation.
	 * 
	 * @param key
	 * @return
	 */
	public synchronized String get(String key) {
		String currentLanguage = getCurrentLanguage();
		if (currentLanguage == null || !currentLanguage.equals(loadedLanguage))
			load(currentLanguage);

		return mappings.getProperty(key, defaultMappings.getProperty(key, key));
	}

	public boolean hasMapping(String key) {
		return mappings.containsKey(key) || defaultMappings.containsKey(key);
	}

	private static String getCurrentLanguage() {
		return Proxies.common.getCurrentLanguage();
	}

}
