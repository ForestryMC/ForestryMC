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
package forestry.arboriculture.blocks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.core.IStateMapperRegister;
import forestry.arboriculture.genetics.alleles.AlleleFruit;
import forestry.arboriculture.render.FruitPodStateMapper;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;

public class BlockFruitPod extends BlockCocoa implements IStateMapperRegister, ITileEntityProvider {

	public static List<BlockFruitPod> create() {
		List<BlockFruitPod> blocks = new ArrayList<>();
		for (IAlleleFruit fruit : AlleleFruit.getFruitAllelesWithModels()) {
			BlockFruitPod block = new BlockFruitPod(fruit);
			blocks.add(block);
		}
		return blocks;
	}

	@Nonnull
	private final IAlleleFruit fruit;

	private BlockFruitPod(@Nonnull IAlleleFruit fruit) {
		this.fruit = fruit;
	}

	@Nonnull
	public IAlleleFruit getFruit() {
		return fruit;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		TileFruitPod tile = TileUtil.getTile(world, pos, TileFruitPod.class);
		if (tile == null) {
			return null;
		}
		return tile.getPickBlock();
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!canBlockStay(world, pos, state)) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
			return;
		}

		TileFruitPod tile = TileUtil.getTile(world, pos, TileFruitPod.class);
		if (tile == null) {
			return;
		}

		tile.onBlockTick();
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (!world.isRemote) {
			TileFruitPod tile = TileUtil.getTile(world, pos, TileFruitPod.class);
			if (tile != null) {
				for (ItemStack drop : tile.getDrops()) {
					ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
				}
			}
		}

		return super.removedByPlayer(world, pos, player, willHarvest);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Collections.emptyList();
	}
	
	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		EnumFacing facing = state.getValue(FACING);
		return BlockUtil.isValidPodLocation(world, pos, facing);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		world.removeTileEntity(pos);
		super.breakBlock(world, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileFruitPod();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new FruitPodStateMapper());
	}

	/* IGrowable */
	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		TileFruitPod podTile = TileUtil.getTile(world, pos, TileFruitPod.class);
		if (podTile != null) {
			return podTile.canMature();
		}
		return false;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		TileFruitPod podTile = TileUtil.getTile(world, pos, TileFruitPod.class);
		if (podTile != null) {
			podTile.addRipeness(0.5f);
		}
	}
}
