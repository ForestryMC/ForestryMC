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

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.farming.logic.FarmableGenericCrop;

@Plugin(pluginID = "MagicalCrops", name = "MagicalCrops", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.magicalcrops.description")
public class PluginMagicalCrops extends ForestryPlugin {

	private static final String MagCrop = "magicalcrops";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(MagCrop);
	}

	@Override
	public String getFailMessage() {
		return "Magical Crops not found";
	}

	@Override
	protected void registerRecipes() {

		ImmutableList<String> magicSeeds = ImmutableList.of(
				"Essence",
				"Coal",
				"Dye",
				"Redstone",
				"Glowstone",
				"Obsidian",
				"Iron",
				"Gold",
				"Lapis",
				"Ender",
				"Nether",
				"Experience",
				"Blaze",
				"Diamond",
				"Emerald"
		);

		ImmutableList<String> elementSeeds = ImmutableList.of(
				"Air",
				"Water",
				"Earth",
				"Fire"
		);

		ImmutableList<String> modMagicSeeds = ImmutableList.of(
				"Copper",
				"Tin",
				"Silver",
				"Lead",
				"Quartz",
				"Sapphire",
				"Ruby",
				"Peridot",
				"Aluminium",
				"Force",
				"Cobalt",
				"Ardite",
				"Nickel",
				"Platinum",
				"ThaumcraftShard",
				"Uranium",
				"Oil",
				"Rubber",
				"Vinteum",
				"BlueTopaz",
				"Chimerite",
				"Moonstone",
				"Sunstone",
				"Iridium",
				"Yellorite",
				"Osmium",
				"Manganese",
				"Sulfur",
				"Darkiron"
		);

		ImmutableList<String> soulSeeds = ImmutableList.of(
				"Cow",
				"Creeper",
				"Magma",
				"Skeleton",
				"Slime",
				"Spider",
				"Ghast",
				"Wither"
		);

		ImmutableList<String> potionSeeds = ImmutableList.of(
				"Fire",
				"Water",
				"Strength",
				"Regen",
				"Night",
				"Speed"
		);

		ImmutableList<String> seeds = ImmutableList.of(
				"Blackberry",
				"Blueberry",
				"Chili",
				"Cucumber",
				"Grape",
				"Raspberry",
				"Strawberry",
				"Sweetcorn",
				"Tomato",
				"SugarCane"
		);

		int seedAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");

		for (String magicSeedName : magicSeeds) {
			addRecipes("_MagicSeeds", "_MagicCrop", magicSeedName, seedAmount);
		}

		for (String elementSeedName : elementSeeds) {
			addRecipes("_ElementSeeds", "_ElementCrop", elementSeedName, seedAmount);
		}

		for (String modMagicSeedName : modMagicSeeds) {
			addRecipes("_ModMagicSeeds", "_ModMagicCrop", modMagicSeedName, seedAmount);
		}

		for (String soulSeedName : soulSeeds) {
			addRecipes("_SoulSeeds", "_SoulCrop", soulSeedName, seedAmount);
		}

		for (String potionSeedName : potionSeeds) {
			addRecipes("_PotionSeeds", "_PotionCrop", potionSeedName, seedAmount);
		}

		for (String seedName : seeds) {
			addRecipes("_Seeds", "_Crop", seedName, seedAmount);
		}

		Item cropProduce = GameRegistry.findItem(MagCrop, MagCrop + "_CropProduce");
		if (cropProduce != null) {
			int juiceAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple");
			ItemStack produce = new ItemStack(GameRegistry.findItem(MagCrop, MagCrop + "_CropProduce"), 1, Defaults.WILDCARD);
			RecipeUtil.injectLeveledRecipe(produce, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{produce}, Fluids.JUICE.getFluid(juiceAmount));
		}
	}

	private void addRecipes(String seedPrefix, String cropPrefix, String name, int fluidAmount) {
		ItemStack seed = new ItemStack(GameRegistry.findItem(MagCrop, MagCrop + seedPrefix + name), 1);
		Block crop = GameRegistry.findBlock(MagCrop, MagCrop + cropPrefix + name);
		if (seed != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{seed}, Fluids.SEEDOIL.getFluid(fluidAmount));
		}
		if (seed != null && crop != null && Config.isMagicalCropsSupportEnabled()) {
			Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(seed, crop, 7));
		}
	}
}
