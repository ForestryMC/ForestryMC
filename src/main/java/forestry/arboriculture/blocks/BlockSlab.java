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

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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

import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import forestry.arboriculture.tiles.TileWood;

public class BlockSlab extends net.minecraft.block.BlockSlab implements IWoodTyped, ITileEntityProvider {

	private final boolean fireproof;

	public BlockSlab(boolean fireproof) {
		super(false, Material.wood);

		this.fireproof = fireproof;

		setCreativeTab(Tabs.tabArboriculture);
		setLightOpacity(0);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setHarvestLevel("axe", 0);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		WoodType.registerIcons(register);
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return WoodType.ACACIA.getPlankIcon();
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileWood wood = BlockWood.getWoodTile(world, x, y, z);
		WoodType type = wood.getWoodType();
		return type.getPlankIcon();
	}

	@Override
	public Item getItemDropped(int meta, Random random, int par3) {
		return Item.getItemFromBlock(this);
	}

	@Override
	protected ItemStack createStackedBlock(int meta) {
		return new ItemStack(Blocks.wooden_slab, 2, meta & 7);
	}

	@Override
	public String func_150002_b(int var1) {
		return "SomeSlab";
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List list) {
		for (WoodType woodType : WoodType.VALUES) {
			list.add(woodType.getSlab(fireproof));
		}
	}

	@Override
	public final TileEntity createNewTileEntity(World world, int meta) {
		return new TileWood();
	}

	/* PROPERTIES */
	@Override
	public final ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		ItemStack itemStack = new ItemStack(this);
		NBTTagCompound nbt = BlockWood.getTagCompound(world, x, y, z);
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
		return new ArrayList<ItemStack>();
	}

	@Override
	public final float getBlockHardness(World world, int x, int y, int z) {
		TileWood wood = BlockWood.getWoodTile(world, x, y, z);
		if (wood == null) {
			return WoodType.DEFAULT_HARDNESS;
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

	@Override
	public String getBlockKind() {
		return "slab";
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return true;
	}

	// Minecraft's BlockSlab overrides this for their slabs, so we change it back to normal here
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return Item.getItemFromBlock(this);
	}
}
