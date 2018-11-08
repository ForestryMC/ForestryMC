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
package forestry.energy;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.SidedProxy;

import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.recipes.RecipeUtil;
import forestry.energy.blocks.BlockRegistryEnergy;
import forestry.energy.proxy.ProxyEnergy;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.ENERGY, name = "Energy", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.energy.description")
public class ModuleEnergy extends BlankForestryModule {

	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.energy.proxy.ProxyEnergyClient", serverSide = "forestry.energy.proxy.ProxyEnergy")
	public static ProxyEnergy proxy;

	@Nullable
	public static BlockRegistryEnergy blocks;

	public static BlockRegistryEnergy getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryEnergy();
	}

	@Override
	public void doInit() {
		BlockRegistryEnergy blocks = getBlocks();
		blocks.peatEngine.init();
		blocks.biogasEngine.init();

		if (ForestryAPI.activeMode.getBooleanSetting("energy.engine.clockwork")) {
			blocks.clockworkEngine.init();
		}
	}

	@Override
	public void registerRecipes() {
		BlockRegistryEnergy blocks = getBlocks();

		RecipeUtil.addRecipe("peat_engine", new ItemStack(blocks.peatEngine),
			"###",
			" X ",
			"YVY",
			'#', "ingotCopper",
			'X', "blockGlass",
			'Y', "gearCopper",
			'V', Blocks.PISTON);

		RecipeUtil.addRecipe("biogas_engine", new ItemStack(blocks.biogasEngine),
			"###",
			" X ",
			"YVY",
			'#', "ingotBronze",
			'X', "blockGlass",
			'Y', "gearBronze",
			'V', Blocks.PISTON);

		if (ForestryAPI.activeMode.getBooleanSetting("energy.engine.clockwork")) {
			RecipeUtil.addRecipe("clockwork_engine", new ItemStack(blocks.clockworkEngine),
				"###",
				" X ",
				"ZVY",
				'#', "plankWood",
				'X', "blockGlass",
				'Y', Items.CLOCK,
				'Z', "gearCopper",
				'V', Blocks.PISTON);
		}
	}
}
