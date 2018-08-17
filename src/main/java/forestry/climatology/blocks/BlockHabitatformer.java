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

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.EnumTemperature;
import forestry.api.core.IModelManager;
import forestry.climatology.tiles.TileHabitatformer;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IColoredBlock;
import forestry.core.render.MachineStateMapper;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileUtil;

public class BlockHabitatformer extends BlockBase<BlockTypeClimatology> implements IColoredBlock {
	public BlockHabitatformer() {
		super(BlockTypeClimatology.HABITAT_FORMER);

		setHardness(1.0f);
		setHarvestLevel("pickaxe", 0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		ModelLoader.setCustomStateMapper(this, new MachineStateMapper<>(blockType, "habitatformer/"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "habitatformer/base");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileHabitatformer former = TileUtil.getTile(worldIn, pos, TileHabitatformer.class);
		if (former != null) {
			ParticleRender.addClimateParticles(worldIn, pos, rand, former.getTemperature());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
		if (worldIn == null || pos == null) {
			return 0x912237;
		}
		TileHabitatformer former = TileUtil.getTile(worldIn, pos, TileHabitatformer.class);
		if (former != null) {
			EnumTemperature temperature = former.getTemperature();
			return temperature.color;
		}
		return 0x912237;
	}
}
