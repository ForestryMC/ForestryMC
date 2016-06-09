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

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.items.EnumPropolis;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ModUtil;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginApiculture;
import forestry.plugins.PluginManager;

@Plugin(pluginID = "MinefactoryReloaded", name = "MineFactoryReloaded", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.minefactoryreloaded.description")
public class PluginMineFactoryReloaded extends ForestryPlugin {

	private static final String MFR = "MineFactoryReloaded";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(MFR);
	}

	@Override
	public String getFailMessage() {
		return "MineFactoryReloaded not found";
	}

	@Override
	protected void registerRecipes() {
		Block rubbersapling = GameRegistry.findBlock(MFR, "rubberwood.sapling");
		Item saplingItem = GameRegistry.findItem(MFR, "rubberwood.sapling");
		ItemStack saplingStack = new ItemStack(saplingItem, 1, 0);
		ItemStack rubberRaw = GameRegistry.findItemStack(MFR,"rubber.raw", 1);
		GameRegistry.findItemStack(MFR,"rubber.raw", 1);

		if (PluginManager.Module.APICULTURE.isEnabled() && !PluginManager.Module.INDUSTRIALCRAFT.isEnabled()) {
			if (rubberRaw != null) {
				ItemRegistryApiculture beeItems = PluginApiculture.items;
				RecipeManagers.centrifugeManager.addRecipe(20, beeItems.propolis.get(EnumPropolis.NORMAL, 1), ImmutableMap.of(rubberRaw, 1.0f));
			}
		}
		if (rubbersapling != null && rubberRaw != null) {
			RecipeUtil.addFermenterRecipes(saplingStack, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
			String saplingName = GameData.getItemRegistry().getNameForObject(saplingItem);
			FMLInterModComms.sendMessage(Constants.MOD, "add-farmable-sapling", String.format("farmArboreal@%s.-1", saplingName));
		}
	}

}
