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
package forestry.core.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import net.minecraftforge.common.config.Configuration.UnicodeInputStreamReader;

import forestry.core.proxy.Proxies;

public class Configuration {

	private String newLine;
	private ArrayList<String> purge = new ArrayList<String>();
	private TreeMap<String, ArrayList<Property>> categorized = new TreeMap<String, ArrayList<Property>>();

	public Configuration() {
		newLine = System.getProperty("line.separator");
	}

	public void addPurge(String key) {
		purge.add(key);
	}
	
	public Property get(String key, String category, boolean defaultVal) {

		Property existing = getExisting(key, category);
		if (existing != null)
			return existing;

		// Create new property since none exists yet.
		Property property = new Property(key, new Boolean(defaultVal).toString());
		categorized.get(category).add(property);
		return property;
	}

	public Property get(String key, String category, int defaultVal) {

		Property existing = getExisting(key, category);
		if (existing != null)
			return existing;

		// Create new property since none exists yet.
		Property property = new Property(key, new Integer(defaultVal).toString());
		categorized.get(category).add(property);
		return property;
	}

	public Property get(String key, String category, float defaultVal) {

		Property existing = getExisting(key, category);
		if (existing != null)
			return existing;

		// Create new property since none exists yet.
		Property property = new Property(key, new Float(defaultVal).toString());
		categorized.get(category).add(property);
		return property;
	}

	public Property get(String key, String category, String defaultVal) {

		Property existing = getExisting(key, category);
		if (existing != null)
			return existing;

		// Create new property since none exists yet.
		Property property = new Property(key, defaultVal);
		categorized.get(category).add(property);
		return property;
	}

	public void set(String key, String category, boolean val) {
		Property existing = getExisting(key, category);
		if (existing != null) {
			existing.Value = new Boolean(val).toString();
			return;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, new Boolean(val).toString());
		categorized.get(category).add(property);

	}

	public void set(String key, String category, String val) {
		Property existing = getExisting(key, category);
		if (existing != null) {
			existing.Value = val;
			return;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, val);
		categorized.get(category).add(property);

	}

	public void set(String key, String category, int val) {
		Property existing = getExisting(key, category);
		if (existing != null) {
			existing.Value = new Integer(val).toString();
			return;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, new Integer(val).toString());
		categorized.get(category).add(property);

	}

	public void set(String key, String category, float val) {
		Property existing = getExisting(key, category);
		if (existing != null) {
			existing.Value = new Float(val).toString();
			return;
		}

		// Create new property since none exists yet.
		Property property = new Property(key, new Float(val).toString());
		categorized.get(category).add(property);

	}

	public Property getExisting(String key, String category) {
		if (!categorized.containsKey(category))
			loadCategory(category);

		for (Property property : categorized.get(category))
			if (property.Key.equals(key))
				return property;

		return null;
	}

	public File getCategoryFile(String category) {
		return new File(Proxies.common.getForestryRoot(), "config/" + Defaults.MOD.toLowerCase(Locale.ENGLISH) + "/" + category + ".conf");
	}

	public void loadCategory(String category) {

		File file = getCategoryFile(category);

		categorized.remove(category);
		categorized.put(category, new ArrayList<Property>());

		try {

			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();

			if (!file.exists())
				return;

			if (!file.canRead())
				return;

			UnicodeInputStreamReader filein = new UnicodeInputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader reader = new BufferedReader(filein);

			String lastComment = null;
			String line;

			while (true) {
				line = reader.readLine();

				if (line == null)
					break;

				if (line.startsWith("#")) {
					if (line.length() > 3)
						lastComment = line.substring(2, line.length() - 1);
					continue;
				}

				if (!line.contains("="))
					continue;

				String[] tokens = line.split("=");
				Property property;
				if (tokens.length > 1)
					property = new Property(tokens[0], tokens[1].trim());
				else
					property = new Property(tokens[0], "");

				if (lastComment != null) {
					property.Comment = lastComment;
					lastComment = null;
				}
				categorized.get(category).add(property);
			}

			reader.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void save() {
		Iterator<Map.Entry<String, ArrayList<Property>>> it = categorized.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<Property>> entry = it.next();
			saveFile(getCategoryFile(entry.getKey()), entry.getValue());
		}
	}

	private void saveFile(File file, ArrayList<Property> properties) {

		try {

			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();

			if (!file.exists() && !file.createNewFile())
				return;

			if (!file.canWrite())
				return;

			FileOutputStream fileout = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileout, "UTF-8"));

			writer.write("# " + Defaults.MOD + newLine + "# " + Version.getVersion() + newLine);

			writer.write("#" + newLine + "# Config files:" + newLine);
			writer.write("# base.conf\t\t-\t Contains Forge configuration for block and item ids" + newLine);
			writer.write("# common.conf\t\t-\t Contains all options common to Forestry" + newLine);
			writer.write("# apiculture.conf\t-\t Contains all options for bee breeding" + newLine);
			writer.write("# backpacks.conf\t-\t Contains custom configurations for backpacks" + newLine);
			writer.write("# pipes.conf\t\t-\t Configures item id for the apiarist's pipe" + newLine);
			writer.write("# gamemodes/\t\t-\t Configures available gamemodes");
			TreeMap<String, ArrayList<Property>> subsectioned = getSubsectioned(properties);

			Iterator<Map.Entry<String, ArrayList<Property>>> it = subsectioned.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, ArrayList<Property>> entry = it.next();
				writer.write(newLine + newLine + "#####################" + newLine + "# " + entry.getKey().toUpperCase() + newLine + "#####################"
						+ newLine);

				for (Property property : entry.getValue()) {
					if(purge.contains(property.Key))
						continue;
					if (property.Comment != null)
						writer.write("# " + property.Comment + newLine);
					writer.write(property.Key + "=" + property.Value + newLine);
				}
			}

			writer.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private TreeMap<String, ArrayList<Property>> getSubsectioned(ArrayList<Property> properties) {
		TreeMap<String, ArrayList<Property>> subsectioned = new TreeMap<String, ArrayList<Property>>();

		for (Property property : properties) {
			String subsection = property.Key.split("\\.")[0];
			if (!subsectioned.containsKey(subsection))
				subsectioned.put(subsection, new ArrayList<Property>());
			subsectioned.get(subsection).add(property);
		}

		return subsectioned;
	}

}
