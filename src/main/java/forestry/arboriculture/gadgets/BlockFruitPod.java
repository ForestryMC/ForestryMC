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
package forestry.arboriculture.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.StackUtils;

public class BlockFruitPod extends BlockCocoa implements IModelRegister {

	public BlockFruitPod() {
		super();
	}

	public static TileFruitPod getPodTile(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileFruitPod)) {
			return null;
		}

		return (TileFruitPod) tile;
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (!canBlockStay(world, pos, state)) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
			return;
		}

		TileFruitPod tile = getPodTile(world, pos);
		if (tile == null) {
			return;
		}

		tile.onBlockTick();
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (Proxies.common.isSimulating(world)) {
			TileFruitPod tile = getPodTile(world, pos);
			if (tile != null) {
				for (ItemStack drop : tile.getDrop()) {
					StackUtils.dropItemStackAsEntity(drop, world, pos.getX(), pos.getY(), pos.getZ());
				}
			}
		}
		return super.removedByPlayer(world, pos, player, willHarvest);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		return BlockUtil.getDirectionalMetadata((World) world, pos) >= 0;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		world.removeTileEntity(pos);
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileFruitPod();
	}
	
	@Override
	public void registerModel(Item item, IModelManager manager) {
		//manager.registerItemModel(item, new WoodMeshDefinition("pods"));
	}

	/* IGrowable */
	
	@Override
	// canFertilize
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		TileFruitPod podTile = getPodTile(world, pos);
		if (podTile != null) {
			return podTile.canMature();
		}
		return false;
	}

	@Override
	// shouldFertilize
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	// fertilize
	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		TileFruitPod podTile = getPodTile(world, pos);
		if (podTile != null) {
			podTile.mature();
		}
	}
}
