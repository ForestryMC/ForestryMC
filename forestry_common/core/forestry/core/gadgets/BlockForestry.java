/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gadgets;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import forestry.core.CreativeTabForestry;
import forestry.core.interfaces.IOwnable;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Utils;

public abstract class BlockForestry extends BlockContainer {

	protected static boolean keepInventory = false;
	protected Random furnaceRand;

	public BlockForestry(Material material) {
		super(material);
		setHardness(1.5f);
		furnaceRand = new Random();
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		IOwnable tile = (IOwnable) world.getTileEntity(x, y, z);
		if (!tile.isOwnable() || tile.allowsRemoval(player))
			return super.removedByPlayer(world, player, x, y, z);
		else
			return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {

		if (!Proxies.common.isSimulating(world))
			return;

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileForestry) {
			TileForestry tileForestry = (TileForestry) tile;
			Utils.dropInventory(tileForestry, world, x, y, z);
			tileForestry.onRemoval();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack itemstack) {

		if (!Proxies.common.isSimulating(world))
			return;

		TileForestry tile = (TileForestry) world.getTileEntity(i, j, k);
		if (entityliving instanceof EntityPlayer)
			tile.owner = ((EntityPlayer) entityliving).getGameProfile().getId();
	}
}
