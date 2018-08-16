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
package forestry.greenhouse;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import forestry.api.modules.ForestryModule;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.greenhouse.blocks.BlockClimatiserType;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import forestry.greenhouse.blocks.BlockRegistryGreenhouse;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.GREENHOUSE, name = "Greenhouse", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.greenhouse.description")
public class ModuleGreenhouse extends BlankForestryModule {

	@Nullable
	private static BlockRegistryGreenhouse blocks;

	public static BlockRegistryGreenhouse getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryGreenhouse();
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
		BlockRegistryGreenhouse greenBlocks = getBlocks();

		RecipeUtil.addShapelessRecipe("greenhouse_plain", coreItems.craftingMaterial.getCamouflagedPaneling(2),
			new ItemStack(greenBlocks.greenhouseBlock, 1, BlockGreenhouseType.PLAIN.ordinal()));

		RecipeUtil.addShapelessRecipe("greenhouse_control", coreItems.tubes.get(EnumElectronTube.GOLD, 2),
			new ItemStack(greenBlocks.greenhouseBlock, 1, BlockGreenhouseType.CONTROL.ordinal()));

		ItemStack gears = coreItems.ingotTin.copy();
		gears.setCount(12);
		RecipeUtil.addShapelessRecipe("greenhouse_gearbox", gears, new ItemStack(greenBlocks.greenhouseBlock, 1, BlockGreenhouseType.GEARBOX.ordinal()));

		RecipeUtil.addShapelessRecipe("greenhouse_hygro", coreItems.craftingMaterial.getCamouflagedPaneling(2),
			new ItemStack(greenBlocks.climatiserBlock, 1, BlockClimatiserType.HYGRO.ordinal()));

		RecipeUtil.addShapelessRecipe("greenhouse_heater", coreItems.tubes.get(EnumElectronTube.GOLD, 2),
			new ItemStack(greenBlocks.climatiserBlock, 1, BlockClimatiserType.HEATER.ordinal()));

		RecipeUtil.addShapelessRecipe("greenhouse_fan", coreItems.tubes.get(EnumElectronTube.TIN, 2),
			new ItemStack(greenBlocks.climatiserBlock, 1, BlockClimatiserType.FAN.ordinal()));

		RecipeUtil.addShapelessRecipe("greenhouse_dehumidifier", coreItems.tubes.get(EnumElectronTube.BLAZE, 2),
			new ItemStack(greenBlocks.climatiserBlock, 1, BlockClimatiserType.DEHUMIDIFIER.ordinal()));

		RecipeUtil.addShapelessRecipe("greenhouse_humidifier", coreItems.tubes.get(EnumElectronTube.LAPIS, 2),
			new ItemStack(greenBlocks.climatiserBlock, 1, BlockClimatiserType.HUMIDIFIER.ordinal()));

		RecipeUtil.addShapelessRecipe("greenhouse_window", new ItemStack(Blocks.GLASS, 7),
			greenBlocks.window);

		RecipeUtil.addShapelessRecipe("greenhouse_window_roof", new ItemStack(Blocks.GLASS, 7),
			greenBlocks.roofWindow);
	}

}
