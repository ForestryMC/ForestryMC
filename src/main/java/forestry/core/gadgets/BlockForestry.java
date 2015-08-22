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
package forestry.core.gadgets;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import forestry.api.core.IModelObject;
import forestry.api.core.IVariantObject;
import forestry.core.CreativeTabForestry;
import forestry.core.interfaces.IOwnable;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Utils;

public abstract class BlockForestry extends BlockContainer implements IModelObject {

	public BlockForestry(Material material) {
		super(material);
		setHardness(1.5f);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		IOwnable tile = (IOwnable) world.getTileEntity(pos);
		if (!tile.isOwnable() || tile.allowsRemoval(player)) {
			return super.removedByPlayer(world, pos, player, willHarvest);
		} else {
			return false;
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileForestry) {
			TileForestry tileForestry = (TileForestry) tile;
			Utils.dropInventory(tileForestry, world, pos);
			tileForestry.onRemoval();
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityliving, ItemStack itemstack) {

		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		TileForestry tile = (TileForestry) world.getTileEntity(pos);
		if (entityliving instanceof EntityPlayer) {
			tile.setOwner(((EntityPlayer) entityliving));
		}
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
		try {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileForestry) {
				((TileForestry) tile).onNeighborBlockChange(block);
			}
		} catch (StackOverflowError error) {
			Proxies.log.logThrowable(Level.ERROR, "Stack Overflow Error in BlockMachine.onNeighborBlockChange()", 10, error);
			throw error;
		}
	}
	
	@Override
	public ModelType getModelType() {
		return ModelType.DEFAULT;
	}

}
