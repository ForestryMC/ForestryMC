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
package forestry.plugins;

import net.minecraftforge.fml.common.Optional;

import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.proxy.Proxies;

import exterminatorJeff.undergroundBiomes.api.UBAPIHook;
import exterminatorJeff.undergroundBiomes.api.UBOreTexturizer;

@Plugin(pluginID = "UndergroundBiomes", name = "UndergroundBiomes", author = "mezz", url = Defaults.URL, unlocalizedDescription = "for.plugin.undergroundBiomes.description")
public class PluginUndergroundBiomes extends ForestryPlugin {

	public boolean isAvailable() {
		return Proxies.common.isModLoaded("UndergroundBiomes");
	}

	public String getFailMessage() {
		return "UndergroundBiomes is not found.";
	}

	@Optional.Method(modid = "UndergroundBiomes")
	@Override
	protected void preInit() {
		registerBlock(0, "apatite");
		registerBlock(1, "copper");
		registerBlock(2, "tin");
	}

	@Optional.Method(modid = "UndergroundBiomes")
	private void registerBlock(int meta, String textureName) {
		try {
			String blockName = ForestryBlock.resources.getItemStack(1, meta).getUnlocalizedName();
			UBAPIHook.ubAPIHook.ubOreTexturizer.requestUBOreSetup(ForestryBlock.resources.block(), meta, "forestry:ores/" + textureName + "_overlay", blockName);
		} catch (UBOreTexturizer.BlocksAreAlreadySet exception) {
			Proxies.log.severe(exception.toString());
		} catch (Throwable throwable) {
			Proxies.log.severe("Underground Biomes crashed.");
		}
	}
}
