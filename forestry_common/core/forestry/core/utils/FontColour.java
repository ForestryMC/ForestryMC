/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.utils;

import java.io.InputStream;
import java.util.Properties;

import net.minecraft.client.resources.IResourceManager;

public class FontColour {

	//private static final ResourceLocation colourDefinitions = new ForestryResource("/config/forestry/colour.properties");

	private final Properties defaultMappings = new Properties();
	private final Properties mappings = new Properties();

	public FontColour(IResourceManager texturepack) {
		load(texturepack);
	}

	public synchronized int get(String key) {
		return Integer.parseInt(mappings.getProperty(key, defaultMappings.getProperty(key, "d67fff")), 16);
	}

	public void load(IResourceManager texturepack) {
		try {
			//InputStream fontStream = texturepack.func_110536_a(colourDefinitions).func_110527_b();
			InputStream defaultFontStream = FontColour.class.getResourceAsStream("/config/forestry/colour.properties");
			//mappings.load((fontStream == null) ? defaultFontStream : fontStream);
			mappings.load(defaultFontStream);
			defaultMappings.load(defaultFontStream);

			//if (fontStream != null)
			//	fontStream.close();
			defaultFontStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
