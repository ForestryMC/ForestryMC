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

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.IGuiHandler;

import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.items.ItemForestryBlock;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.energy.GuiHandlerEnergy;
import forestry.energy.gadgets.EngineBronze;
import forestry.energy.gadgets.EngineClockwork;
import forestry.energy.gadgets.EngineCopper;
import forestry.energy.gadgets.EngineDefinition;
import forestry.energy.proxy.ProxyEnergy;

@Plugin(pluginID = "Energy", name = "Energy", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.energy.description")
public class PluginEnergy extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.energy.proxy.ClientProxyEnergy", serverSide = "forestry.energy.proxy.ProxyEnergy")
	public static ProxyEnergy proxy;
	public static MachineDefinition definitionEngineCopper;
	public static MachineDefinition definitionEngineBronze;
	public static MachineDefinition definitionEngineClockwork;

	@Override
	public void preInit() {
		super.preInit();

		ForestryBlock.engine.registerBlock(new BlockBase(Material.iron, true), ItemForestryBlock.class, "engine");

		definitionEngineCopper = ((BlockBase) ForestryBlock.engine.block()).addDefinition(new EngineDefinition(Defaults.DEFINITION_ENGINECOPPER_META, "forestry.EngineCopper", EngineCopper.class,
				PluginEnergy.proxy.getRenderDefaultEngine(Defaults.TEXTURE_PATH_BLOCKS + "/engine_copper_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.engine.getItemStack(1, Defaults.DEFINITION_ENGINECOPPER_META),
				"###",
				" X ",
				"YVY",
				'#', "ingotCopper",
				'X', "blockGlass",
				'Y', "gearCopper",
				'V', Blocks.piston)));
		definitionEngineBronze = ((BlockBase) ForestryBlock.engine.block()).addDefinition(new EngineDefinition(Defaults.DEFINITION_ENGINEBRONZE_META, "forestry.EngineBronze", EngineBronze.class,
				PluginEnergy.proxy.getRenderDefaultEngine(Defaults.TEXTURE_PATH_BLOCKS + "/engine_bronze_"), ShapedRecipeCustom.createShapedRecipe(
				ForestryBlock.engine.getItemStack(1, Defaults.DEFINITION_ENGINEBRONZE_META),
				"###",
				" X ",
				"YVY",
				'#', "ingotBronze",
				'X', "blockGlass",
				'Y', "gearBronze",
				'V', Blocks.piston)));

		ShapedRecipeCustom clockworkRecipe = null;
		if (GameMode.getGameMode().getBooleanSetting("energy.engine.clockwork")) {
			clockworkRecipe = ShapedRecipeCustom.createShapedRecipe(
					ForestryBlock.engine.getItemStack(1, Defaults.DEFINITION_ENGINECLOCKWORK_META),
					"###",
					" X ",
					"ZVY",
					'#', "plankWood",
					'X', "blockGlass",
					'Y', Items.clock,
					'Z', "gearCopper",
					'V', Blocks.piston);
		}

		definitionEngineClockwork = ((BlockBase) ForestryBlock.engine.block()).addDefinition(new EngineDefinition(Defaults.DEFINITION_ENGINECLOCKWORK_META, "forestry.EngineClockwork", EngineClockwork.class,
				PluginEnergy.proxy.getRenderDefaultEngine(Defaults.TEXTURE_PATH_BLOCKS + "/engine_clock_"), clockworkRecipe));
	}

	@Override
	public void doInit() {
		super.doInit();

		definitionEngineCopper.register();
		definitionEngineBronze.register();
		definitionEngineClockwork.register();
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerEnergy();
	}
}
