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
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.farming.logic.FarmableGenericCrop;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

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
		int seedamount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");

		for (String magicSeedName : magicSeeds) {
			ItemStack magicSeed = GameRegistry.findItemStack(MagCrop, MagCrop + "_MagicSeeds" + magicSeedName, 1);
			Block magicSeedBlock = GameRegistry.findBlock(MagCrop, MagCrop + "_MagicCrop" + magicSeedName);
			if (magicSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{magicSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (magicSeed != null && magicSeedBlock != null && Config.isMagicalCropsSupportEnabled()) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(magicSeed, magicSeedBlock, 7));
			}
		}

		for (String elementSeedName : elementSeeds) {
			ItemStack elementSeed = GameRegistry.findItemStack(MagCrop, MagCrop + "_ElementSeeds" + elementSeedName, 1);
			Block elementSeedBlock = GameRegistry.findBlock(MagCrop, MagCrop + "_ElementCrop" + elementSeedName);
			if (elementSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{elementSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (elementSeed != null && elementSeedBlock != null && Config.isMagicalCropsSupportEnabled()) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(elementSeed, elementSeedBlock, 7));
			}
		}

		for (String modMagicSeedName : modMagicSeeds) {
			ItemStack modMagicSeed = GameRegistry.findItemStack(MagCrop, MagCrop + "_ModMagicSeeds" + modMagicSeedName, 1);
			Block modMagicSeedBlock = GameRegistry.findBlock(MagCrop, MagCrop + "_ModMagicCrop" + modMagicSeedName);
			if (modMagicSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{modMagicSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (modMagicSeed != null && modMagicSeedBlock != null && Config.isMagicalCropsSupportEnabled()) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(modMagicSeed, modMagicSeedBlock, 7));
			}
		}

		for (String soulSeedName : soulSeeds) {
			ItemStack soulSeed = GameRegistry.findItemStack(MagCrop, MagCrop + "_SoulSeeds" + soulSeedName, 1);
			Block soulSeedBlock = GameRegistry.findBlock(MagCrop, MagCrop + "_SoulCrop" + soulSeedName);
			if (soulSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{soulSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (soulSeed != null && soulSeedBlock != null && Config.isMagicalCropsSupportEnabled()) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(soulSeed, soulSeedBlock, 7));
			}
		}

		for (String potionSeedName : potionSeeds) {
			ItemStack potionSeed = GameRegistry.findItemStack(MagCrop, MagCrop + "_PotionSeeds" + potionSeedName, 1);
			Block potionSeedBlock = GameRegistry.findBlock(MagCrop, MagCrop + "_PotionCrop" + potionSeedName);
			if (potionSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{potionSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (potionSeed != null && potionSeedBlock != null && Config.isMagicalCropsSupportEnabled()) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(potionSeed, potionSeedBlock, 7));
			}
		}

		int juiceAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple");
		ItemStack produce = new ItemStack(GameRegistry.findItem(MagCrop, MagCrop + "_CropProduce"), 1, Defaults.WILDCARD);
        if (produce != null) {
            RecipeUtil.injectLeveledRecipe(produce, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
            RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{produce}, Fluids.JUICE.getFluid(juiceAmount));
        }
		for (String SeedName : seeds) {
			ItemStack Seed = GameRegistry.findItemStack(MagCrop, MagCrop + "_Seeds" + SeedName, 1);
			Block SeedBlock = GameRegistry.findBlock(MagCrop, MagCrop + "_Crop" + SeedName);
			if (Seed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{Seed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (Seed != null && SeedBlock != null && Config.isMagicalCropsSupportEnabled()) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(Seed, SeedBlock, 7));
			}
		}
	}
}
