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
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.apiculture.FlowerManager;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.farming.FarmRegistry;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.BIOMES_O_PLENTY, name = "BiomesOPlenty", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.module.biomesoplenty.description")
public class PluginBiomesOPlenty extends CompatPlugin {

	public PluginBiomesOPlenty() {
		super("BiomesOPlenty", "biomesoplenty");
	}

	@Override
	public void doInit() {
		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			addFlowers();
		}
		if (ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
			addFarmCrops();
		}
	}

	@Override
	public void registerRecipes() {
		int amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		if (ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
			addSqueezerRecipes();
		}
	}

	private void addFarmCrops() {
		FarmRegistry registry = FarmRegistry.getInstance();

		//		for (int i = 0; i < 3; i++) {
		//			ItemStack sapling = getItemStack("sapling_" + i);
		//			if (sapling != null) {
		//				registry.registerFarmables(ForestryFarmIdentifier.ARBOREAL, new FarmableSapling(sapling, new ItemStack[0]));
		//				//TODO - check windfall
		//			}
		//		}
		//TODO - https://github.com/Glitchfiend/BiomesOPlenty/issues/1337
	}

	private void addSqueezerRecipes() {
		ItemStack mulch = new ItemStack(ModuleCore.getItems().mulch);
		FluidStack juice = Fluids.JUICE.getFluid(200);
		if (juice == null) {
			return;
		}
		String[] fruits = {"persommon", "berries", "peach", "pear"};

		for (String fruit : fruits) {
			ItemStack fruitStack = getItemStack(fruit);
			if (fruitStack != null) {
				RecipeManagers.squeezerManager.addRecipe(10, fruitStack, juice, mulch, 20);
			}
		}
	}


	@SuppressWarnings("deprecation")
	private void addFlowers() {
		Block flower_0 = getBlock("flower_0");
		IFlowerRegistry registry = FlowerManager.flowerRegistry;
		if (flower_0 != null) {
			registry.registerPlantableFlower(flower_0.getStateFromMeta(0), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //clover
			registry.registerPlantableFlower(flower_0.getStateFromMeta(1), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow, FlowerManager.FlowerTypeMushrooms);    //swampflower
			registry.registerPlantableFlower(flower_0.getStateFromMeta(2), 1.0, FlowerManager.FlowerTypeNether);    //deathbloom
			registry.registerPlantableFlower(flower_0.getStateFromMeta(3), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //GlowFlower
			registry.registerPlantableFlower(flower_0.getStateFromMeta(4), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //Blue Hydrangea
			registry.registerPlantableFlower(flower_0.getStateFromMeta(5), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow, FlowerManager.FlowerTypeJungle);        //Orange Cosmos
			registry.registerPlantableFlower(flower_0.getStateFromMeta(6), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //Pink Daffodil
			registry.registerPlantableFlower(flower_0.getStateFromMeta(7), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //WildFlower
			registry.registerPlantableFlower(flower_0.getStateFromMeta(8), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //Violet
			registry.registerPlantableFlower(flower_0.getStateFromMeta(9), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        // White Anemone
			registry.registerPlantableFlower(flower_0.getStateFromMeta(10), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //EnderLotus (does not actually spawn in the end)
			registry.registerPlantableFlower(flower_0.getStateFromMeta(11), 1.0, FlowerManager.FlowerTypeCacti);        //Bromeliad
			registry.registerPlantableFlower(flower_0.getStateFromMeta(12), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //wilted lily
			registry.registerPlantableFlower(flower_0.getStateFromMeta(13), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //pink hibiscus
			registry.registerPlantableFlower(flower_0.getStateFromMeta(14), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //lily of the valley
			registry.registerPlantableFlower(flower_0.getStateFromMeta(15), 1.0, FlowerManager.FlowerTypeNether);    //burning blossom
		}

		Block flower_1 = getBlock("flower_1");
		if (flower_1 != null) {
			for (int i = 0; i < 6; i++) {
				registry.registerPlantableFlower(flower_1.getStateFromMeta(i), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
			}

		}

		Block mushroom = getBlock("mushroom");
		if (mushroom != null) {
			for (int i = 0; i < 6; i++) {
				registry.registerPlantableFlower(mushroom.getStateFromMeta(i), 1.0, FlowerManager.FlowerTypeMushrooms);
			}
			registry.registerPlantableFlower(mushroom.getStateFromMeta(3), 1.0, FlowerManager.FlowerTypeNether);    //glowshroom
		}

		Block plant_1 = getBlock("plant_1");
		if (plant_1 != null) {
			registry.registerPlantableFlower(plant_1.getStateFromMeta(6), 1.0, FlowerManager.FlowerTypeCacti);    //tiny cactus
		}
	}

	@Override
	public void registerBackpackItems() {
		// most blocks are covered by the oreDictionary
		addBlocksToBackpack(BackpackManager.DIGGER_UID,
				"grass",
				"dirt",
				"sandstone",
				"dried_sand",
				"mud",
				"ash_block");
	}

}
