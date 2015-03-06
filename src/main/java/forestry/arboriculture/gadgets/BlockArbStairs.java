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

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.Tabs;
import forestry.arboriculture.WoodType;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;

public class BlockArbStairs extends BlockStairs {

	public BlockArbStairs(Block par2Block, int par3) {
		super(par2Block, par3);
		setCreativeTab(Tabs.tabArboriculture);
		setHardness(2.0F);
		setResistance(5.0F);
	}

	public static TileStairs getStairTile(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileStairs)) {
			return null;
		}

		return (TileStairs) tile;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (WoodType type : WoodType.VALUES) {
			ItemStack stack = new ItemStack(item, 1, 0);
			NBTTagCompound compound = new NBTTagCompound();
			type.saveToCompound(compound);
			stack.setTagCompound(compound);
			list.add(stack);
		}
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (Proxies.common.isSimulating(world) && canHarvestBlock(player, meta)) {
			if (!player.capabilities.isCreativeMode) {
				// Handle TE'd beehives
				TileEntity tile = world.getTileEntity(x, y, z);

				if (tile instanceof TileStairs) {
					TileStairs stairs = (TileStairs) tile;

					ItemStack stack = new ItemStack(this, 1, 0);
					NBTTagCompound compound = new NBTTagCompound();
					stairs.getType().saveToCompound(compound);
					stack.setTagCompound(compound);
					StackUtils.dropItemStackAsEntity(stack, world, x, y, z);
				}
			}
		}

		return world.setBlockToAir(x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		world.removeTileEntity(x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new TileStairs();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		ItemStack itemStack = super.getPickBlock(target, world, x, y, z);
		NBTTagCompound stairsNBT = getTagCompoundForStairs(world, x, y, z);
		itemStack.setTagCompound(stairsNBT);
		return itemStack;
	}

	private static NBTTagCompound getTagCompoundForStairs(IBlockAccess world, int x, int y, int z) {
		TileStairs stairs = getStairTile(world, x, y, z);

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		if (stairs == null || stairs.getType() == null) {
			return nbttagcompound;
		}

		stairs.getType().saveToCompound(nbttagcompound);
		return nbttagcompound;
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		WoodType.registerIcons(register);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileStairs stairs = getStairTile(world, x, y, z);
		if (stairs != null && stairs.getType() != null) {
			return stairs.getType().getPlankIcon();
		} else {
			return WoodType.LARCH.getPlankIcon();
		}
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return true;
	}
}
