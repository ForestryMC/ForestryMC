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

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
public enum EnumFarmBlockTexture {
	BRICK_STONE(new ItemStack(Blocks.STONE_BRICKS), TextFormatting.DARK_GRAY),
	BRICK_MOSSY(new ItemStack(Blocks.MOSSY_STONE_BRICKS), TextFormatting.DARK_GRAY),
	BRICK_CRACKED(new ItemStack(Blocks.CRACKED_STONE_BRICKS), TextFormatting.DARK_GRAY),
	BRICK(new ItemStack(Blocks.BRICKS), TextFormatting.GOLD),
	SANDSTONE_SMOOTH(new ItemStack(Blocks.SMOOTH_SANDSTONE), TextFormatting.YELLOW),
	SANDSTONE_CHISELED(new ItemStack(Blocks.CHISELED_SANDSTONE), TextFormatting.YELLOW),
	BRICK_NETHER(new ItemStack(Blocks.NETHER_BRICKS), TextFormatting.DARK_RED),
	BRICK_CHISELED(new ItemStack(Blocks.CHISELED_STONE_BRICKS), TextFormatting.GOLD),
	QUARTZ(new ItemStack(Blocks.QUARTZ_BLOCK), TextFormatting.WHITE),
	QUARTZ_CHISELED(new ItemStack(Blocks.CHISELED_QUARTZ_BLOCK), TextFormatting.WHITE),
	QUARTZ_LINES(new ItemStack(Blocks.QUARTZ_PILLAR), TextFormatting.WHITE);

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
	@OnlyIn(Dist.CLIENT)
	public static TextureAtlasSprite getSprite(EnumFarmBlockTexture texture, int side) {
		AtlasTexture map = Minecraft.getInstance().getTextureMap();
		switch (texture) {
			case BRICK:
				return map.getAtlasSprite("minecraft:block/brick");
			case BRICK_STONE:
				return map.getAtlasSprite("minecraft:block/stonebrick");
			case BRICK_CHISELED:
				return map.getAtlasSprite("minecraft:block/stonebrick_carved");
			case BRICK_CRACKED:
				return map.getAtlasSprite("minecraft:block/stonebrick_cracked");
			case BRICK_MOSSY:
				return map.getAtlasSprite("minecraft:block/stonebrick_mossy");
			case BRICK_NETHER:
				return map.getAtlasSprite("minecraft:block/nether_brick");
			case SANDSTONE_CHISELED:
				if (side == 0) {
					return map.getAtlasSprite("minecraft:block/sandstone_bottom");
				} else if (side == 1) {
					return map.getAtlasSprite("minecraft:block/sandstone_top");
				}
				return map.getAtlasSprite("minecraft:block/sandstone_carved");
			case SANDSTONE_SMOOTH:
				if (side == 0) {
					return map.getAtlasSprite("minecraft:block/sandstone_bottom");
				} else if (side == 1) {
					return map.getAtlasSprite("minecraft:block/sandstone_top");
				}
				return map.getAtlasSprite("minecraft:block/sandstone_smooth");
			case QUARTZ:
				if (side == 0) {
					return map.getAtlasSprite("minecraft:block/quartz_block_bottom");
				} else if (side == 1) {
					return map.getAtlasSprite("minecraft:block/quartz_block_top");
				}
				return map.getAtlasSprite("minecraft:block/quartz_block_side");
			case QUARTZ_CHISELED:
				if (side == 0 || side == 1) {
					return map.getAtlasSprite("minecraft:block/quartz_block_chiseled_top");
				}
				return map.getAtlasSprite("minecraft:block/quartz_block_chiseled");
			case QUARTZ_LINES:
				if (side == 0 || side == 1) {
					return map.getAtlasSprite("minecraft:block/quartz_block_lines_top");
				}
				return map.getAtlasSprite("minecraft:block/quartz_block_lines");
			default:
				return null;
		}
	}

	public void saveToCompound(CompoundNBT compound) {
		compound.putInt("FarmBlock", this.ordinal());
	}

	public ITextComponent getName() {
		return base.getItem().getDisplayName(base);
	}

	public String getUid() {
		return toString().toLowerCase(Locale.ENGLISH);
	}

	public ItemStack getBase() {
		return base;
	}

	public static EnumFarmBlockTexture getFromCompound(@Nullable CompoundNBT compound) {
		if (compound != null) {
			int farmBlockOrdinal = compound.getInt("FarmBlock");
			if (farmBlockOrdinal < EnumFarmBlockTexture.values().length) {
				return EnumFarmBlockTexture.values()[farmBlockOrdinal];
			}
		}

		return EnumFarmBlockTexture.BRICK_STONE;
	}
}
