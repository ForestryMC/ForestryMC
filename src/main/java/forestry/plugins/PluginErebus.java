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
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;

@Plugin(pluginID = "Erebus", name = "Erebus", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.erebus.description")
public class PluginErebus extends ForestryPlugin {

	private static final String EREBUS = "erebus";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(EREBUS);
	}

	@Override
	public String getFailMessage() {
		return "Erebus not found";
	}

	@Override
	protected void registerRecipes() {
		super.registerRecipes();
		Item materials = GameRegistry.findItem(EREBUS, "materials");
		ItemStack honeyDrip = new ItemStack(materials, 1, 21);
		if (PluginManager.Module.FACTORY.isEnabled()) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { honeyDrip },
					Fluids.HONEY.getFluid(Defaults.FLUID_PER_HONEY_DROP), ForestryItem.propolis.getItemStack(), 5);

		}
	}
}