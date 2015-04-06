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

import net.minecraft.block.Block;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.farming.Farmables;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.farming.logic.FarmableBasicAgricraft;

@Plugin(pluginID = "AgriCraft", name = "AgriCraft", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.agricraft.description")
public class PluginAgriCraft extends ForestryPlugin {

	private static final String AgriCraft = "AgriCraft";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(AgriCraft);
	}

	@Override
	public String getFailMessage() {
		return "AgriCraft";
	}

	@Override
	protected void registerRecipes() {

		Block cropBlock = GameRegistry.findBlock(AgriCraft, "crops");
		if (cropBlock != null) {
			Farmables.farmables.get("farmOrchard").add(new FarmableBasicAgricraft(cropBlock, 7));
		}
	}

}

