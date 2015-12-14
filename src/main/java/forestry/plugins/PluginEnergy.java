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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import cpw.mods.fml.common.SidedProxy;

import forestry.api.core.ForestryAPI;
import forestry.core.config.Constants;
import forestry.core.recipes.RecipeUtil;
import forestry.energy.blocks.BlockEngineType;
import forestry.energy.blocks.BlockRegistryEnergy;
import forestry.energy.proxy.ProxyEnergy;
import forestry.energy.tiles.EngineDefinition;

@Plugin(pluginID = "Energy", name = "Energy", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.energy.description")
public class PluginEnergy extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.energy.proxy.ProxyEnergyClient", serverSide = "forestry.energy.proxy.ProxyEnergy")
	public static ProxyEnergy proxy;

	public static BlockRegistryEnergy blocks;

	@Override
	protected void registerItemsAndBlocks() {
		blocks = new BlockRegistryEnergy();
	}

	@Override
	public void preInit() {
		blocks.engine.addDefinitions(
				new EngineDefinition(BlockEngineType.PEAT),
				new EngineDefinition(BlockEngineType.BIOGAS)
		);

		if (ForestryAPI.activeMode.getBooleanSetting("energy.engine.clockwork")) {
			blocks.engine.addDefinition(new EngineDefinition(BlockEngineType.CLOCKWORK));
		}
	}

	@Override
	public void doInit() {
		blocks.engine.init();
	}

	@Override
	protected void registerRecipes() {
		super.registerRecipes();

		RecipeUtil.addRecipe(blocks.engine.get(BlockEngineType.PEAT),
				"###",
				" X ",
				"YVY",
				'#', "ingotCopper",
				'X', "blockGlass",
				'Y', "gearCopper",
				'V', Blocks.piston);

		RecipeUtil.addRecipe(blocks.engine.get(BlockEngineType.BIOGAS),
				"###",
				" X ",
				"YVY",
				'#', "ingotBronze",
				'X', "blockGlass",
				'Y', "gearBronze",
				'V', Blocks.piston);

		if (ForestryAPI.activeMode.getBooleanSetting("energy.engine.clockwork")) {
			RecipeUtil.addRecipe(blocks.engine.get(BlockEngineType.CLOCKWORK),
					"###",
					" X ",
					"ZVY",
					'#', "plankWood",
					'X', "blockGlass",
					'Y', Items.clock,
					'Z', "gearCopper",
					'V', Blocks.piston);
		}
	}
}
