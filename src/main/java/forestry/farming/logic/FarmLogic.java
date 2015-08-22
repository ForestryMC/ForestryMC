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
package forestry.farming.logic;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmLogic;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.render.TextureManager;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;

public abstract class FarmLogic implements IFarmLogic {

	protected final IFarmHousing housing;

	protected boolean isManual;

	private static final ImmutableSet<Block> breakableSoil = ImmutableSet.of(
			Blocks.air,
			Blocks.dirt,
			Blocks.grass,
			Blocks.sand,
			Blocks.farmland,
			Blocks.mycelium,
			Blocks.soul_sand,
			Blocks.water,
			Blocks.flowing_water,
			Blocks.end_stone,
			ForestryBlock.soil.block()
	);

	public FarmLogic(IFarmHousing housing) {
		this.housing = housing;
	}

	public FarmLogic setManual(boolean flag) {
		isManual = flag;
		return this;
	}

	public static boolean canBreakSoil(World world, Vect position) {
		Block block = VectUtil.getBlock(world, position);
		return breakableSoil.contains(block) || block.isReplaceable(world, position.toBlockPos());
	}

	protected World getWorld() {
		return housing.getWorld();
	}


	protected final boolean isAirBlock(Block block) {
		return block.getMaterial() == Material.air;
	}

	protected final boolean isWaterSourceBlock(World world, Vect position) {
		return world.getBlockState(position.toBlockPos()) == Blocks.water.getDefaultState(); //TODO Fix
	}

	protected final Vect translateWithOffset(BlockPos pos, EnumFacing direction, int step) {
		return new Vect(pos.offset(direction, step));
	}

	protected final void setBlock(Vect position, IBlockState state) {
		getWorld().setBlockState(position.toBlockPos(), state, Defaults.FLAG_BLOCK_UPDATE | Defaults.FLAG_BLOCK_SYNCH);
	}
	
	public TextureAtlasSprite getSprite(String modID, String identifier){
		return TextureManager.getInstance().getTex(Minecraft.getMinecraft().getTextureMapBlocks(), modID, identifier);
	}

}
