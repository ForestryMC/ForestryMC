/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;

public class BlockSlab extends net.minecraft.block.BlockSlab implements IWoodTyped {

	public static enum SlabCat {
		CAT0, CAT1, CAT2
	}

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
		WoodType type = WoodType.VALUES[(8 * cat.ordinal()) + (meta & 7)];
		return type.getPlankIcon();
	}

	@Override
	public Item getItemDropped(int meta, Random random, int par3) {
		return Item.getItemFromBlock(this);
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	/*public int idPicked(World world, int x, int y, int z) {
		return blockID;
	}*/

	/**
	 * Get the block's damage value (for use with pick block).
	 */
	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return super.getDamageValue(world, x, y, z) & 7;
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
		for (int i = 0; i < 8; ++i)
			itemList.add(new ItemStack(item, 1, i));
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
		if(meta + cat.ordinal() * 8 < WoodType.VALUES.length)
			return WoodType.VALUES[meta + cat.ordinal() * 8];
		else
			return WoodType.LARCH;
	}

	@Override
	public String getBlockKind() {
		return "slab";
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return true;
	}
}
