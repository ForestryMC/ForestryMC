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

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.EnumTemperature;
import forestry.climatology.tiles.TileHabitatFormer;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IColoredBlock;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileUtil;

public class BlockHabitatFormer extends BlockBase<BlockTypeClimatology> implements IColoredBlock {
	public BlockHabitatFormer() {
		super(BlockTypeClimatology.HABITAT_FORMER, Block.Properties.of(Material.METAL).strength(1.0f));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
		TileHabitatFormer former = TileUtil.getTile(worldIn, pos, TileHabitatFormer.class);
		if (former != null) {
			ParticleRender.addClimateParticles(worldIn, pos, rand, former.getTemperature(), former.getHumidity());
		}
	}

	@Override
	public int colorMultiplier(BlockState state, @Nullable BlockGetter worldIn, @Nullable BlockPos pos, int tintIndex) {
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
