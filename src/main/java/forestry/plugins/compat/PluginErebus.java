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
package forestry.plugins.compat;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.items.EnumPropolis;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginApiculture;
import forestry.plugins.PluginManager;

@Plugin(pluginID = "Erebus", name = "Erebus", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.erebus.description")
public class PluginErebus extends ForestryPlugin {

	private static final String EREBUS = "erebus";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(EREBUS);
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
		FluidStack honeyOutput = Fluids.HONEY.getFluid(Constants.FLUID_PER_HONEY_DROP);
		if (PluginManager.Module.FACTORY.isEnabled()) {
			ItemStack remnants = null;
			if (PluginManager.Module.APICULTURE.isEnabled()) {
				remnants = PluginApiculture.items.propolis.get(EnumPropolis.NORMAL, 1);
			}
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{honeyDrip}, honeyOutput, remnants, 5);
		}
	}
}
