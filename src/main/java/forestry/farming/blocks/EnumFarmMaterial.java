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
package forestry.farming.blocks;

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

import forestry.api.core.IBlockSubtype;

public enum EnumFarmMaterial implements IBlockSubtype {
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

	EnumFarmMaterial(ItemStack base, TextFormatting formatting) {
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
	public static TextureAtlasSprite getSprite(EnumFarmMaterial texture, int side) {
		AtlasTexture map = Minecraft.getInstance().getTextureMap();
		switch (texture) {
			case BRICK:
				return map.getAtlasSprite("minecraft:block/bricks");
			case BRICK_STONE:
				return map.getAtlasSprite("minecraft:block/stone_bricks");
			case BRICK_CHISELED:
				return map.getAtlasSprite("minecraft:block/chiseled_stone_bricks");
			case BRICK_CRACKED:
				return map.getAtlasSprite("minecraft:block/cracked_stone_bricks");
			case BRICK_MOSSY:
				return map.getAtlasSprite("minecraft:block/mossy_stone_bricks");
			case BRICK_NETHER:
				return map.getAtlasSprite("minecraft:block/nether_bricks");
			case SANDSTONE_CHISELED:
				if (side == 0) {
					return map.getAtlasSprite("minecraft:block/sandstone_bottom");
				} else if (side == 1) {
					return map.getAtlasSprite("minecraft:block/sandstone_top");
				}
				return map.getAtlasSprite("minecraft:block/chiseled_sandstone");
			case SANDSTONE_SMOOTH:
				if (side == 0) {
					return map.getAtlasSprite("minecraft:block/sandstone_bottom");
				} else if (side == 1) {
					return map.getAtlasSprite("minecraft:block/sandstone_top");
				}
				return map.getAtlasSprite("minecraft:block/cut_sandstone");
			case QUARTZ:
				if (side == 0) {
					return map.getAtlasSprite("minecraft:block/quartz_block_bottom");
				} else if (side == 1) {
					return map.getAtlasSprite("minecraft:block/quartz_block_top");
				}
				return map.getAtlasSprite("minecraft:block/quartz_block_side");
			case QUARTZ_CHISELED:
				if (side == 0 || side == 1) {
					return map.getAtlasSprite("minecraft:block/chiseled_quartz_block_top");
				}
				return map.getAtlasSprite("minecraft:block/chiseled_quartz_block");
			case QUARTZ_LINES:
				if (side == 0 || side == 1) {
					return map.getAtlasSprite("minecraft:block/quartz_pillar_top");
				}
				return map.getAtlasSprite("minecraft:block/quartz_pillar");
			default:
				return null;
		}
	}

	public TextureAtlasSprite[] getSprites() {
		TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
		for (int side = 0; side < textures.length; side++) {
			textures[side] = getSprite(this, side);
		}
		return textures;
	}

	public void saveToCompound(CompoundNBT compound) {
		compound.putInt("FarmBlock", this.ordinal());
	}

	public ITextComponent getDisplayName() {
		return base.getItem().getDisplayName(base);
	}

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	public String getUid() {
		return toString().toLowerCase(Locale.ENGLISH);
	}

	public ItemStack getBase() {
		return base;
	}
}
