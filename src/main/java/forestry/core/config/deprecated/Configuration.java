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
package forestry.core.config.deprecated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

import net.minecraftforge.common.config.Configuration.UnicodeInputStreamReader;

import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;

@Deprecated
public class Configuration {

	private final TreeMap<String, ArrayList<Property>> categorized = new TreeMap<>();

	public Property get(String key, String category, boolean defaultVal) {

		Property existing = getExisting(key, category);
		if (existing != null) {
			return existing;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, Boolean.toString(defaultVal));
		categorized.get(category).add(property);
		return property;
	}

	public Property get(String key, String category, int defaultVal) {

		Property existing = getExisting(key, category);
		if (existing != null) {
			return existing;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, Integer.toString(defaultVal));
		categorized.get(category).add(property);
		return property;
	}

	public Property get(String key, String category, float defaultVal) {

		Property existing = getExisting(key, category);
		if (existing != null) {
			return existing;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, Float.toString(defaultVal));
		categorized.get(category).add(property);
		return property;
	}

	public Property get(String key, String category, String defaultVal) {

		Property existing = getExisting(key, category);
		if (existing != null) {
			return existing;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, defaultVal);
		categorized.get(category).add(property);
		return property;
	}

	public void set(String key, String category, boolean val) {
		Property existing = getExisting(key, category);
		if (existing != null) {
			existing.value = Boolean.toString(val);
			return;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, Boolean.toString(val));
		categorized.get(category).add(property);

	}

	public void set(String key, String category, String val) {
		Property existing = getExisting(key, category);
		if (existing != null) {
			existing.value = val;
			return;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, val);
		categorized.get(category).add(property);

	}

	public void set(String key, String category, int val) {
		Property existing = getExisting(key, category);
		if (existing != null) {
			existing.value = Integer.toString(val);
			return;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, Integer.toString(val));
		categorized.get(category).add(property);

	}

	public void set(String key, String category, float val) {
		Property existing = getExisting(key, category);
		if (existing != null) {
			existing.value = Float.toString(val);
			return;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, Float.toString(val));
		categorized.get(category).add(property);

	}

	public Property getExisting(String key, String category) {
		if (!categorized.containsKey(category)) {
			loadCategory(category);
		}

		for (Property property : categorized.get(category)) {
			if (property.key.equals(key)) {
				return property;
			}
		}

		return null;
	}

	public File getCategoryFile(String category) {
		return new File(Proxies.common.getForestryRoot(), "config/" + Constants.MOD.toLowerCase(Locale.ENGLISH) + "/" + category + ".conf");
	}

	public void loadCategory(String category) {

		File file = getCategoryFile(category);

		categorized.remove(category);
		categorized.put(category, new ArrayList<Property>());

		try {

			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}

			if (!file.exists()) {
				return;
			}

			if (!file.canRead()) {
				return;
			}

			UnicodeInputStreamReader filein = new UnicodeInputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader reader = new BufferedReader(filein);

			String lastComment = null;
			String line;

			while (true) {
				line = reader.readLine();

				if (line == null) {
					break;
				}

				if (line.startsWith("#")) {
					if (line.length() > 3) {
						lastComment = line.substring(2, line.length() - 1);
					}
					continue;
				}

				if (!line.contains("=")) {
					continue;
				}

				String[] tokens = line.split("=");
				Property property;
				if (tokens.length > 1) {
					property = new Property(tokens[0], tokens[1].trim());
				} else {
					property = new Property(tokens[0], "");
				}

				if (lastComment != null) {
					property.comment = lastComment;
					lastComment = null;
				}
				categorized.get(category).add(property);
			}

			reader.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
