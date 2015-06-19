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
package forestry.core.gadgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.CreativeTabForestry;
import forestry.core.config.ForestryItem;
import forestry.core.render.TextureManager;

public class BlockResource extends Block {
	public BlockResource() {
		super(Material.rock);
		setHardness(3F);
		setResistance(5F);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float par6, int par7) {
		super.dropBlockAsItemWithChance(world, x, y, z, metadata, par6, par7);

		if (metadata == 0) {
			this.dropXpOnBlockBreak(world, x, y, z, MathHelper.getRandomIntegerInRange(world.rand, 1, 4));
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		if (metadata == 0) {
			int fortmod = world.rand.nextInt(fortune + 2) - 1;
			if (fortmod < 0) {
				fortmod = 0;
			}

			int amount = (2 + world.rand.nextInt(5)) * (fortmod + 1);
			if (amount > 0) {
				drops.add(ForestryItem.apatite.getItemStack(amount));
			}
		} else {
			drops.add(new ItemStack(this, 1, metadata));
		}

		return drops;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	// / CREATIVE INVENTORY
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
		itemList.add(new ItemStack(this, 1, 2));
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon iconApatite;
	@SideOnly(Side.CLIENT)
	private IIcon iconCopper;
	@SideOnly(Side.CLIENT)
	private IIcon iconTin;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		iconApatite = TextureManager.getInstance().registerTex(register, "ores/apatite");
		iconCopper = TextureManager.getInstance().registerTex(register, "ores/copper");
		iconTin = TextureManager.getInstance().registerTex(register, "ores/tin");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int j) {
		if (j == 0) {
			return iconApatite;
		} else if (j == 1) {
			return iconCopper;
		} else if (j == 2) {
			return iconTin;
		} else {
			return null;
		}
	}

}
