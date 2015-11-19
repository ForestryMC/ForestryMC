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
package forestry.core.blocks;

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
import forestry.core.render.TextureManager;
import forestry.plugins.PluginCore;

public class BlockResourceOre extends Block {
	public enum ResourceType {
		APATITE,
		COPPER,
		TIN;

		public static final ResourceType[] VALUES = values();

		@SideOnly(Side.CLIENT)
		public IIcon icon;
	}

	public BlockResourceOre() {
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
		ArrayList<ItemStack> drops = new ArrayList<>();

		if (metadata == ResourceType.APATITE.ordinal()) {
			int fortuneModifier = world.rand.nextInt(fortune + 2) - 1;
			if (fortuneModifier < 0) {
				fortuneModifier = 0;
			}

			int amount = (2 + world.rand.nextInt(5)) * (fortuneModifier + 1);
			if (amount > 0) {
				drops.add(PluginCore.items.apatite.getItemStack(amount));
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
		for (ResourceType resourceType : ResourceType.values()) {
			ItemStack stack = get(resourceType, 1);
			itemList.add(stack);
		}
	}

	/* ICONS */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		ResourceType.APATITE.icon = TextureManager.registerTex(register, "ores/apatite");
		ResourceType.COPPER.icon = TextureManager.registerTex(register, "ores/copper");
		ResourceType.TIN.icon = TextureManager.registerTex(register, "ores/tin");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int meta) {
		if (meta < 0 || meta >= ResourceType.VALUES.length) {
			return null;
		}

		return ResourceType.VALUES[meta].icon;
	}

	public ItemStack get(ResourceType type, int amount) {
		return new ItemStack(this, amount, type.ordinal());
	}
}
