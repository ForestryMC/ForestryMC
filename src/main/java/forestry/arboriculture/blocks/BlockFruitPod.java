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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.core.IStateMapperRegister;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.blocks.property.PropertyFruit;
import forestry.arboriculture.render.FluidPodStateMapper;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.core.blocks.propertys.UnlistedBlockAccess;
import forestry.core.blocks.propertys.UnlistedBlockPos;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;

public class BlockFruitPod extends BlockCocoa implements IStateMapperRegister {

	public final static PropertyFruit FRUIT = new PropertyFruit("fruit");
	
	public BlockFruitPod() {
		super();
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
				.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
	}

	@Override
	protected BlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{FACING, AGE, FRUIT},
				new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileFruitPod pot = getPodTile(world, pos);
		if(pos != null){
			IAlleleFruit fruit = pot.getAllele();
			state = state.withProperty(FRUIT, fruit);
		}
		return super.getActualState(state, world, pos);
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
		if (!world.isRemote) {
			TileFruitPod tile = getPodTile(world, pos);
			if (tile != null) {
				for (ItemStack drop : tile.getDrop()) {
					ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
				}
			}
		}

		return super.removedByPlayer(world, pos, player, willHarvest);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<>();
	}
	
	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		return BlockUtil.getDirectionalMetadata(world, pos) >= 0;
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
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new FluidPodStateMapper());
	}

	/* IGrowable */
	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		TileFruitPod podTile = getPodTile(world, pos);
		if (podTile != null) {
			return podTile.canMature();
		}
		return false;
	}
	
	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}
	
	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		TileFruitPod podTile = getPodTile(world, pos);
		if (podTile != null) {
			podTile.mature();
		}
	}

	public static void registerSprites() {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleFruit) {
				((IAlleleFruit) allele).getProvider().registerSprites();
			}
		}
	}
}
