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
package forestry.farming.models;

import javax.annotation.Nullable;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumFarmBlockTexture {
	BRICK_STONE(new ItemStack(Blocks.STONEBRICK, 1, 0), TextFormatting.DARK_GRAY),
	BRICK_MOSSY(new ItemStack(Blocks.STONEBRICK, 1, 1), TextFormatting.DARK_GRAY),
	BRICK_CRACKED(new ItemStack(Blocks.STONEBRICK, 1, 2), TextFormatting.DARK_GRAY),
	BRICK(new ItemStack(Blocks.BRICK_BLOCK), TextFormatting.GOLD),
	SANDSTONE_SMOOTH(new ItemStack(Blocks.SANDSTONE, 1, 2), TextFormatting.YELLOW),
	SANDSTONE_CHISELED(new ItemStack(Blocks.SANDSTONE, 1, 1), TextFormatting.YELLOW),
	BRICK_NETHER(new ItemStack(Blocks.NETHER_BRICK), TextFormatting.DARK_RED),
	BRICK_CHISELED(new ItemStack(Blocks.STONEBRICK, 1, 3), TextFormatting.GOLD),
	QUARTZ(new ItemStack(Blocks.QUARTZ_BLOCK, 1, 0), TextFormatting.WHITE),
	QUARTZ_CHISELED(new ItemStack(Blocks.QUARTZ_BLOCK, 1, 1), TextFormatting.WHITE),
	QUARTZ_LINES(new ItemStack(Blocks.QUARTZ_BLOCK, 1, 2), TextFormatting.WHITE);

	private final ItemStack base;
	private final TextFormatting formatting;

	EnumFarmBlockTexture(ItemStack base, TextFormatting formatting) {
		this.base = base;
		this.formatting = formatting;
	}

	public TextFormatting getFormatting() {
		return formatting;
	}

	/**
	 * @return The texture sprite from the material of the farm block
	 */
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getSprite(EnumFarmBlockTexture texture, int side) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		switch (texture) {
			case BRICK:
				return map.getAtlasSprite("minecraft:blocks/brick");
			case BRICK_STONE:
				return map.getAtlasSprite("minecraft:blocks/stonebrick");
			case BRICK_CHISELED:
				return map.getAtlasSprite("minecraft:blocks/stonebrick_carved");
			case BRICK_CRACKED:
				return map.getAtlasSprite("minecraft:blocks/stonebrick_cracked");
			case BRICK_MOSSY:
				return map.getAtlasSprite("minecraft:blocks/stonebrick_mossy");
			case BRICK_NETHER:
				return map.getAtlasSprite("minecraft:blocks/nether_brick");
			case SANDSTONE_CHISELED:
				if (side == 0) {
					return map.getAtlasSprite("minecraft:blocks/sandstone_bottom");
				} else if (side == 1) {
					return map.getAtlasSprite("minecraft:blocks/sandstone_top");
				}
				return map.getAtlasSprite("minecraft:blocks/sandstone_carved");
			case SANDSTONE_SMOOTH:
				if (side == 0) {
					return map.getAtlasSprite("minecraft:blocks/sandstone_bottom");
				} else if (side == 1) {
					return map.getAtlasSprite("minecraft:blocks/sandstone_top");
				}
				return map.getAtlasSprite("minecraft:blocks/sandstone_smooth");
			case QUARTZ:
				if (side == 0) {
					return map.getAtlasSprite("minecraft:blocks/quartz_block_bottom");
				} else if (side == 1) {
					return map.getAtlasSprite("minecraft:blocks/quartz_block_top");
				}
				return map.getAtlasSprite("minecraft:blocks/quartz_block_side");
			case QUARTZ_CHISELED:
				if (side == 0 || side == 1) {
					return map.getAtlasSprite("minecraft:blocks/quartz_block_chiseled_top");
				}
				return map.getAtlasSprite("minecraft:blocks/quartz_block_chiseled");
			case QUARTZ_LINES:
				if (side == 0 || side == 1) {
					return map.getAtlasSprite("minecraft:blocks/quartz_block_lines_top");
				}
				return map.getAtlasSprite("minecraft:blocks/quartz_block_lines");
			default:
				return null;
		}
	}

	public void saveToCompound(NBTTagCompound compound) {
		compound.setInteger("FarmBlock", this.ordinal());
	}

	public String getName() {
		return base.getItem().getItemStackDisplayName(base);
	}

	public String getUid() {
		return toString().toLowerCase(Locale.ENGLISH);
	}

	public ItemStack getBase() {
		return base;
	}

	public static EnumFarmBlockTexture getFromCompound(@Nullable NBTTagCompound compound) {
		if (compound != null) {
			int farmBlockOrdinal = compound.getInteger("FarmBlock");
			if (farmBlockOrdinal < EnumFarmBlockTexture.values().length) {
				return EnumFarmBlockTexture.values()[farmBlockOrdinal];
			}
		}

		return EnumFarmBlockTexture.BRICK_STONE;
	}
}
