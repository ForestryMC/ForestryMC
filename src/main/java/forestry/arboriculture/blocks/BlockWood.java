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

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.render.IconProviderWood;
import forestry.arboriculture.tiles.TileWood;
import forestry.core.tiles.TileUtil;

public abstract class BlockWood extends Block implements ITileEntityProvider, IWoodTyped {

	private final String blockKind;
	private final boolean fireproof;

	protected BlockWood(String blockKind, boolean fireproof) {
		super(Material.wood);
		this.blockKind = blockKind;
		this.fireproof = fireproof;

		setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public final String getBlockKind() {
		return blockKind;
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	public static TileWood getWoodTile(IBlockAccess world, int x, int y, int z) {
		return TileUtil.getTile(world, x, y, z, TileWood.class);
	}

	protected static NBTTagCompound getTagCompound(IBlockAccess world, int x, int y, int z) {
		TileWood wood = getWoodTile(world, x, y, z);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		if (wood == null) {
			return nbttagcompound;
		}
		EnumWoodType woodType = wood.getWoodType();
		woodType.saveToCompound(nbttagcompound);
		return nbttagcompound;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public final void registerBlockIcons(IIconRegister register) {
		IconProviderWood.registerIcons(register);
	}

	@Override
	public final TileEntity createNewTileEntity(World world, int meta) {
		return new TileWood();
	}

	@Override
	public final ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		ItemStack itemStack = new ItemStack(this);
		NBTTagCompound nbt = getTagCompound(world, x, y, z);
		itemStack.setTagCompound(nbt);
		return itemStack;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		return TileWood.blockRemovedByPlayer(this, world, player, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		world.removeTileEntity(x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<>();
	}

	@Override
	public final float getBlockHardness(World world, int x, int y, int z) {
		TileWood wood = getWoodTile(world, x, y, z);
		if (wood == null) {
			return EnumWoodType.DEFAULT_HARDNESS;
		}
		return wood.getWoodType().getHardness();
	}

	@Override
	public final boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return !isFireproof();
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return isFireproof() ? 0 : 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return isFireproof() ? 0 : 5;
	}

	@SideOnly(Side.CLIENT)
	public abstract IIcon getIcon(IBlockAccess world, int x, int y, int z, int side);

}
