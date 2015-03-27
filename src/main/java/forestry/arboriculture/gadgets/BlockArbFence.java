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

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
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
import forestry.plugins.PluginArboriculture;

public class BlockArbFence extends BlockFence implements IWoodTyped {

	public enum FenceCat {
		CAT0, CAT1
	}

	public static final int fencesPerCat = 16;
	private final FenceCat cat;

	public BlockArbFence(FenceCat cat) {
		super("", Material.wood);
		this.cat = cat;
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		int totalWoods = WoodType.values().length;
		int count = Math.min(totalWoods - (cat.ordinal() * fencesPerCat), fencesPerCat);
		for (int i = 0; i < count; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
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
			if (block == this || block instanceof BlockFenceGate) {
				return true;
			}

			return block.getMaterial().isOpaque() && block.renderAsNormalBlock() && block.getMaterial() != Material.gourd;
		} else {
			return true;
		}
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
		WoodType woodType = getWoodType(meta);
		if (woodType == null) {
			return null;
		}
		return woodType.getPlankIcon();
	}

	public boolean isFence(IBlockAccess world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return PluginArboriculture.validFences.contains(block);
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
		int woodOrdinal = cat.ordinal() * fencesPerCat + meta;
		if (woodOrdinal < WoodType.VALUES.length) {
			return WoodType.VALUES[woodOrdinal];
		} else {
			return null;
		}
	}

	@Override
	public String getBlockKind() {
		return "fences";
	}
}
