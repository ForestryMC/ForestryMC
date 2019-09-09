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
package forestry.climatology.blocks;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import forestry.api.core.EnumTemperature;
import forestry.climatology.tiles.TileHabitatFormer;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IColoredBlock;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileUtil;

public class BlockHabitatFormer extends BlockBase<BlockTypeClimatology> implements IColoredBlock {
	public BlockHabitatFormer() {
		super(BlockTypeClimatology.HABITAT_FORMER, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f).harvestTool(ToolType.AXE).harvestLevel(0));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileHabitatFormer former = TileUtil.getTile(worldIn, pos, TileHabitatFormer.class);
		if (former != null) {
			ParticleRender.addClimateParticles(worldIn, pos, rand, former.getTemperature(), former.getHumidity());
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int colorMultiplier(BlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) {
		if (worldIn == null || pos == null) {
			return 0x912237;
		}
		TileHabitatFormer former = TileUtil.getTile(worldIn, pos, TileHabitatFormer.class);
		if (former != null) {
			EnumTemperature temperature = former.getTemperature();
			return temperature.color;
		}
		return 0x912237;
	}
}
