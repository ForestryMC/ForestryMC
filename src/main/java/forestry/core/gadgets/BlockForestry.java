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
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.core.IModelRegister;
import forestry.core.CreativeTabForestry;
import forestry.core.interfaces.IOwnable;
import forestry.core.interfaces.IRestrictedAccessTile;
import forestry.core.proxy.Proxies;

public abstract class BlockForestry extends BlockContainer implements IModelRegister {

	protected BlockForestry(Material material) {
		super(material);
		setHardness(1.5f);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof IRestrictedAccessTile) {
			IRestrictedAccessTile restrictedAccessTile = (IRestrictedAccessTile) tile;
			if (!restrictedAccessTile.getAccessHandler().allowsRemoval(player)) {
				return false;
			}
		}
		return super.removedByPlayer(world, pos, player, willHarvest);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack stack) {
		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		if (entityLiving instanceof EntityPlayer) {
			TileEntity tile = world.getTileEntity(pos);

			IOwnable ownable;

			if (tile instanceof IRestrictedAccessTile) {
				ownable = ((IRestrictedAccessTile) tile).getAccessHandler();
			} else if (tile instanceof IOwnable) {
				ownable = (IOwnable) tile;
			} else {
				return;
			}

			EntityPlayer player = (EntityPlayer) entityLiving;
			GameProfile gameProfile = player.getGameProfile();
			ownable.setOwner(gameProfile);
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

}
