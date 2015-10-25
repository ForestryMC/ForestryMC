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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;

@Plugin(pluginID = "BC6|Transport", name = "BuildCraft 6 Transport", author = "mezz", url = Defaults.URL, unlocalizedDescription = "for.plugin.buildcraft6.description")
public class PluginBuildCraftTransport extends ForestryPlugin {

	private static final String BCT = "BuildCraft|Transport";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(BCT);
	}

	@Override
	public String getFailMessage() {
		return "BuildCraft|Transport not found";
	}

	@Override
	protected void registerRecipes() {
		Item beeswax = ForestryItem.beeswax.item();
		if (beeswax != null) {
			Item pipeWaterproof = GameRegistry.findItem(BCT, "pipeWaterproof");
			if (pipeWaterproof != null) {
				Proxies.common.addShapelessRecipe(new ItemStack(pipeWaterproof), beeswax);
			} else {
				Proxies.log.fine("No BuildCraft pipe waterproof found.");
			}
		}
	}
}
