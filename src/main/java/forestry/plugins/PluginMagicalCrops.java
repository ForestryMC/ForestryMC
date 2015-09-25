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

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.GameMode;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.RecipeUtil;
import forestry.farming.logic.FarmableGenericCrop;

@Plugin(pluginID = "MagicalCrops", name = "MagicalCrops", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.magicalcrops.description")
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
	protected void postInit() {

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

		ImmutableList<String> betaSeeds = ImmutableList.of(
				"Air",
				"Aluminium",
				"Alumite",
				"Ardite",
				"Blaze",
				"Blizz",
				"Bronze",
				"CertusQuartz",
				"Chicken",
				"Coal",
				"Cobalt",
				"Copper",
				"Cow",
				"Creeper",
				"Diamond",
				"Dye",
				"Earth",
				"Electrum",
				"Emerald",
				"Enderium",
				"Enderman",
				"Experience",
				"Fire",
				"Fluix",
				"Ghast",
				"Glowstone",
				"Gold",
				"Invar",
				"Iron",
				"Lapis",
				"Lead",
				"Lumium",
				"Manasteel",
				"Manyullyn",
				"Minicio",
				"Nature",
				"Nether",
				"Nickel",
				"Obsidian",
				"Osmium",
				"Peridot",
				"Pig",
				"Platinum",
				"Quartz",
				"Redstone",
				"Rubber",
				"Ruby",
				"Saltpeter",
				"Sapphire",
				"Sheep",
				"Signalum",
				"Silver",
				"Skeleton",
				"Slime",
				"Spider",
				"Steel",
				"Sulfur",
				"Terrasteel",
				"Tin",
				"Water",
				"Wither",
				"Yellorite"
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
		for (String seedName : betaSeeds) {
			addBetaRecipes("Seeds", "Crop", seedName);
		}

		Item cropProduce = GameRegistry.findItem(MagCrop, MagCrop + "_CropProduce");
		if (cropProduce != null) {
			int juiceAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple");
			ItemStack produce = new ItemStack(GameRegistry.findItem(MagCrop, MagCrop + "_CropProduce"), 1, Constants.WILDCARD);
			RecipeUtil.addFermenterRecipes(produce, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{produce}, Fluids.JUICE.getFluid(juiceAmount));
		}
	}

	private static void addRecipes(String seedPrefix, String cropPrefix, String name, int fluidAmount) {
		ItemStack seed = GameRegistry.findItemStack(MagCrop, MagCrop + seedPrefix + name, 1);
		Block crop = GameRegistry.findBlock(MagCrop, MagCrop + cropPrefix + name);
		if (seed != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{seed}, Fluids.SEEDOIL.getFluid(fluidAmount));
		}
		if (seed != null && crop != null && Config.isMagicalCropsSupportEnabled()) {
			Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(seed, crop, 7));
		}
	}

	private static void addBetaRecipes(String seedSuffix, String cropSuffix, String name) {
		ItemStack seed = GameRegistry.findItemStack(MagCrop, MagCrop + "_" + name + seedSuffix, 1);
		Block crop = GameRegistry.findBlock(MagCrop, MagCrop + "_" + name + cropSuffix);
		if (seed != null && crop != null && Config.isMagicalCropsSupportEnabled()) {
			Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(seed, crop, 7));
		}
	}
}
