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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.arboriculture.tiles.TileFruitPod;
import forestry.core.render.TextureManager;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.plugins.PluginArboriculture;

public class BlockFruitPod extends BlockCocoa {

	public BlockFruitPod() {
		super();
	}

	public static TileFruitPod getPodTile(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileFruitPod)) {
			return null;
		}

		return (TileFruitPod) tile;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {

		if (!canBlockStay(world, x, y, z)) {
			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
			return;
		}

		TileFruitPod tile = getPodTile(world, x, y, z);
		if (tile == null) {
			return;
		}

		tile.onBlockTick();
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!world.isRemote) {
			TileFruitPod tile = getPodTile(world, x, y, z);
			if (tile != null) {
				for (ItemStack drop : tile.getDrop()) {
					ItemStackUtil.dropItemStackAsEntity(drop, world, x, y, z);
				}
			}
		}

		return super.removedByPlayer(world, player, x, y, z);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<>();
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return BlockUtil.getDirectionalMetadata(world, x, y, z) >= 0;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		world.removeTileEntity(x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileFruitPod();
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private static IIcon defaultIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		defaultIcon = TextureManager.registerTex(register, "pods/papaya.2");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int par1, int par2) {
		return defaultIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileFruitPod pod = getPodTile(world, x, y, z);
		if (pod != null) {
			IIcon podIcon = pod.getIcon();
			if (podIcon != null) {
				return podIcon;
			}
		}

		return defaultIcon;
	}

	@Override
	public int getRenderType() {
		return PluginArboriculture.modelIdPods;
	}

	/* IGrowable */

	@Override
	// canFertilize
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
		TileFruitPod podTile = getPodTile(world, x, y, z);
		if (podTile != null) {
			return podTile.canMature();
		}
		return false;
	}

	@Override
	// shouldFertilize
	public boolean func_149852_a(World world, Random random, int x, int y, int z) {
		return true;
	}

	@Override
	// fertilize
	public void func_149853_b(World world, Random random, int x, int y, int z) {
		TileFruitPod podTile = getPodTile(world, x, y, z);
		if (podTile != null) {
			podTile.mature();
		}
	}
}
