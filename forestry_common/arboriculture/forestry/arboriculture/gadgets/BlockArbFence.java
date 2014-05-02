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
import net.minecraft.block.BlockFence;
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
import forestry.plugins.PluginArboriculture;

public class BlockArbFence extends BlockFence implements IWoodTyped {

	public static enum FenceCat {
		CAT0, CAT1
	}

	private final FenceCat cat;

	public BlockArbFence(FenceCat cat) {
		super("", Material.wood);
		this.cat = cat;
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		int count = (cat == FenceCat.CAT0 ? 16 : 8);
		for (int i = 0; i < count; i++)
			itemList.add(new ItemStack(this, 1, i));
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canConnectFenceTo(IBlockAccess world, int x, int y, int z) {
		if (!isFence(world, x, y, z)) {
			Block block = world.getBlock(x, y, z);
			return block != null && block.getMaterial().isOpaque() && block.renderAsNormalBlock() ? block.getMaterial() != Material.gourd : false;
		} else
			return true;
	}

	@Override
	public int getRenderType() {
		return PluginArboriculture.modelIdFences;
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

	public boolean isFence(IBlockAccess world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (PluginArboriculture.validFences.contains(block))
			return true;

		return false;
	}

	/* PROPERTIES */
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
		return "fences";
	}
}
