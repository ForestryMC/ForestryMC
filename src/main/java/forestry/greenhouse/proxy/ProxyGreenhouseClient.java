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
package forestry.greenhouse.proxy;

import com.google.common.base.Preconditions;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.IClimateContainer;
import forestry.api.climate.IClimateState;
import forestry.api.greenhouse.IGreenhouseBlock;
import forestry.core.models.BlockModelEntry;
import forestry.core.models.ModelEntry;
import forestry.core.models.ModelManager;
import forestry.greenhouse.GreenhouseEventHandler;
import forestry.greenhouse.PluginGreenhouse;
import forestry.greenhouse.blocks.BlockClimatiser;
import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockRegistryGreenhouse;
import forestry.greenhouse.models.ModelCamouflageSprayCan;
import forestry.greenhouse.models.ModelCamouflaged;
import forestry.greenhouse.models.ModelGreenhouseWindow;
import forestry.greenhouse.multiblock.blocks.world.GreenhouseBlockManager;

@SideOnly(Side.CLIENT)
public class ProxyGreenhouseClient extends ProxyGreenhouse {

	private static BlockPos COLOR_BLOCK_POSITION;
	private static BiomeColorHelper.ColorResolver ORIGINAL_GRASS_COLOR;
	private static BiomeColorHelper.ColorResolver ORIGINAL_FOLIAGE_COLOR;

	@Override
	public void initializeModels() {
		BlockRegistryGreenhouse blocks = PluginGreenhouse.getBlocks();
		Preconditions.checkState(blocks != null);

		ModelManager modelManager = ModelManager.getInstance();
		Block greenhouseBlock = blocks.greenhouseBlock;

		if (greenhouseBlock != null) {
			ModelResourceLocation blockModelLocation = new ModelResourceLocation("forestry:greenhouse");
			ModelResourceLocation itemModelLocation = new ModelResourceLocation("forestry:greenhouse", "inventory");
			BlockModelEntry blockModelIndex = new BlockModelEntry(blockModelLocation, itemModelLocation, new ModelCamouflaged(BlockGreenhouse.class), greenhouseBlock);
			modelManager.registerCustomBlockModel(blockModelIndex);
		}
		Block climatiserBlock = blocks.climatiserBlock;

		if (climatiserBlock != null) {
			ModelResourceLocation blockModelLocation = new ModelResourceLocation("forestry:climatiser");
			ModelResourceLocation itemModelLocation = new ModelResourceLocation("forestry:climatiser", "inventory");
			BlockModelEntry blockModelIndex = new BlockModelEntry(blockModelLocation, itemModelLocation, new ModelCamouflaged(BlockClimatiser.class), climatiserBlock);
			modelManager.registerCustomBlockModel(blockModelIndex);
		}
		{
			ModelResourceLocation blockModelLocation = new ModelResourceLocation("forestry:greenhouse.window");
			ModelResourceLocation itemModelLocation = new ModelResourceLocation("forestry:greenhouse_window", "inventory");
			BlockModelEntry blockModelIndex = new BlockModelEntry(blockModelLocation, itemModelLocation, new ModelGreenhouseWindow(), PluginGreenhouse.getBlocks().window);
			modelManager.registerCustomBlockModel(blockModelIndex);
		}
		{
			ModelResourceLocation blockModelLocation = new ModelResourceLocation("forestry:greenhouse.window_up");
			ModelResourceLocation itemModelLocation = new ModelResourceLocation("forestry:greenhouse_window_up", "inventory");
			BlockModelEntry blockModelIndex = new BlockModelEntry(blockModelLocation, itemModelLocation, new ModelGreenhouseWindow(), PluginGreenhouse.getBlocks().roofWindow);
			modelManager.registerCustomBlockModel(blockModelIndex);
		}
		{
			ModelResourceLocation modelLocation = new ModelResourceLocation("forestry:camouflage_spray_can", "inventory");
			ModelEntry itemModelIndex = new ModelEntry(modelLocation, new ModelCamouflageSprayCan());
			modelManager.registerCustomModel(itemModelIndex);
		}
	}

	@Override
	public void preInti() {
		MinecraftForge.EVENT_BUS.register(new GreenhouseEventHandler());
		MinecraftForge.EVENT_BUS.register(this);

		ORIGINAL_GRASS_COLOR = BiomeColorHelper.GRASS_COLOR;
		BiomeColorHelper.GRASS_COLOR = new BiomeColorHelper.ColorResolver() {
			@Override
			public int getColorAtPos(Biome biome, BlockPos blockPosition) {
				COLOR_BLOCK_POSITION = blockPosition;
				return ORIGINAL_GRASS_COLOR.getColorAtPos(biome, blockPosition);
			}
		};

		ORIGINAL_FOLIAGE_COLOR = BiomeColorHelper.FOLIAGE_COLOR;
		BiomeColorHelper.FOLIAGE_COLOR = new BiomeColorHelper.ColorResolver() {
			@Override
			public int getColorAtPos(Biome biome, BlockPos blockPosition) {
				COLOR_BLOCK_POSITION = blockPosition;
				return ORIGINAL_FOLIAGE_COLOR.getColorAtPos(biome, blockPosition);
			}
		};
	}

	@Override
	public void inti() {

	}

	@SubscribeEvent
	public void getFoliageColor(BiomeEvent.GetFoliageColor event) {
		if (COLOR_BLOCK_POSITION != null) {
			IGreenhouseBlock logicBlock = GreenhouseBlockManager.getInstance().getBlock(Minecraft.getMinecraft().world, COLOR_BLOCK_POSITION);
			if (logicBlock != null && logicBlock.getProvider().isClosed()) {
				IClimateContainer container = logicBlock.getProvider().getClimateContainer();
				IClimateState climateState = container.getState();
				double temperature = MathHelper.clamp(climateState.getTemperature(), 0.0F, 1.0F);
				double humidity = MathHelper.clamp(climateState.getHumidity(), 0.0F, 1.0F);
				event.setNewColor(ColorizerGrass.getGrassColor(temperature, humidity));
			}
		}
	}

	@SubscribeEvent
	public void getGrassColor(BiomeEvent.GetGrassColor event) {
		if (COLOR_BLOCK_POSITION != null) {
			IGreenhouseBlock logicBlock = GreenhouseBlockManager.getInstance().getBlock(Minecraft.getMinecraft().world, COLOR_BLOCK_POSITION);
			if (logicBlock != null && logicBlock.getProvider().isClosed()) {
				IClimateContainer container = logicBlock.getProvider().getClimateContainer();
				IClimateState climateState = container.getState();
				double temperature = MathHelper.clamp(climateState.getTemperature(), 0.0F, 1.0F);
				double humidity = MathHelper.clamp(climateState.getHumidity(), 0.0F, 1.0F);
				event.setNewColor(ColorizerGrass.getGrassColor(temperature, humidity));
			}
		}
	}

}
