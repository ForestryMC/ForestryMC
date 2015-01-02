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

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;

public class BlockSlab extends net.minecraft.block.BlockSlab implements IWoodTyped {

	public static enum SlabCat {
		CAT0, CAT1, CAT2
	}

	public static final int slabsPerCat = 8;
	private final SlabCat cat;

	public BlockSlab(SlabCat cat) {
		super(false, Material.wood);
		this.cat = cat;
		setCreativeTab(Tabs.tabArboriculture);
		setLightOpacity(0);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
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

	@Override
	public IIcon getIcon(int side, int meta) {
		WoodType type = getWoodType(meta);
		if (type == null)
			return null;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		int totalWoods = WoodType.values().length;
		int count = Math.min(totalWoods - (cat.ordinal() * slabsPerCat), slabsPerCat);
		for (int i = 0; i < count; i++)
			itemList.add(new ItemStack(this, 1, i));
	}

	/* PROPERTIES */
	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 20;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 5;
	}

	@Override
	public WoodType getWoodType(int meta) {
		int woodOrdinal = meta + cat.ordinal() * slabsPerCat;
		if(woodOrdinal < WoodType.VALUES.length)
			return WoodType.VALUES[woodOrdinal];
		else
			return null;
	}

	@Override
	public String getBlockKind() {
		return "slab";
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
