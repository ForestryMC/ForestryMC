/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.gadgets;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
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

public class BlockPlanks extends Block implements IWoodTyped {

	public static enum PlankCat {
		CAT0, CAT1
	}

	private final PlankCat cat;

	public BlockPlanks(PlankCat cat) {
		super(Material.wood);
		this.cat = cat;
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		int count = (cat == PlankCat.CAT0 ? 16 : 8);
		for (int i = 0; i < count; i++)
			itemList.add(new ItemStack(this, 1, i));
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		WoodType.registerIcons(register);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return getWoodType(meta).getPlankIcon();
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	/* PROPERTIES */
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return getWoodType(world.getBlockMetadata(x, y, z)).getHardness();
	}

	@Override
	public boolean isWood(IBlockAccess world, int x, int y, int z) {
		return true;
	}

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
		if(cat.ordinal() * 16 + meta < WoodType.VALUES.length)
			return WoodType.VALUES[cat.ordinal() * 16 + meta];
		else
			return WoodType.LARCH;
	}

	@Override
	public String getBlockKind() {
		return "planks";
	}
}
