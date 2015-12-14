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
package forestry.core.blocks;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.core.CreativeTabForestry;
import forestry.core.access.IOwnable;
import forestry.core.access.IRestrictedAccess;
import forestry.core.tiles.TileForestry;
import forestry.core.utils.Log;

public abstract class BlockForestry extends BlockContainer {

	protected BlockForestry(Material material) {
		super(material);
		setHardness(1.5f);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile instanceof IRestrictedAccess) {
			IRestrictedAccess restrictedAccessTile = (IRestrictedAccess) tile;
			if (!restrictedAccessTile.getAccessHandler().allowsRemoval(player)) {
				return false;
			}
		}

		return super.removedByPlayer(world, player, x, y, z);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityLiving, ItemStack itemstack) {

		if (world.isRemote) {
			return;
		}

		if (entityLiving instanceof EntityPlayer) {
			TileEntity tile = world.getTileEntity(i, j, k);

			IOwnable ownable;

			if (tile instanceof IRestrictedAccess) {
				ownable = ((IRestrictedAccess) tile).getAccessHandler();
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
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		try {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof TileForestry) {
				((TileForestry) tile).onNeighborBlockChange(block);
			}
		} catch (StackOverflowError error) {
			Log.logThrowable(Level.ERROR, "Stack Overflow Error in BlockMachine.onNeighborBlockChange()", 10, error);
			throw error;
		}
	}

}
