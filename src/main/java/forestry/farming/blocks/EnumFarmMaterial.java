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
import java.util.function.Function;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import forestry.api.core.IBlockSubtype;
import forestry.core.utils.ResourceUtil;

public enum EnumFarmMaterial implements IBlockSubtype {
	BRICK_STONE(new ItemStack(Blocks.STONE_BRICKS), ChatFormatting.DARK_GRAY, "stone_bricks"),
	BRICK_MOSSY(new ItemStack(Blocks.MOSSY_STONE_BRICKS), ChatFormatting.DARK_GRAY, "mossy_stone_bricks"),
	BRICK_CRACKED(new ItemStack(Blocks.CRACKED_STONE_BRICKS), ChatFormatting.DARK_GRAY, "cracked_stone_bricks"),
	BRICK(new ItemStack(Blocks.BRICKS), ChatFormatting.GOLD, "bricks"),
	SANDSTONE_SMOOTH(new ItemStack(Blocks.SMOOTH_SANDSTONE), ChatFormatting.YELLOW, pillarTexture("cut_sandstone", "sandstone_bottom", "sandstone_top")),
	SANDSTONE_CHISELED(new ItemStack(Blocks.CHISELED_SANDSTONE), ChatFormatting.YELLOW, pillarTexture("chiseled_sandstone", "sandstone_bottom", "sandstone_top")),
	BRICK_NETHER(new ItemStack(Blocks.NETHER_BRICKS), ChatFormatting.DARK_RED, "nether_bricks"),
	BRICK_CHISELED(new ItemStack(Blocks.CHISELED_STONE_BRICKS), ChatFormatting.GOLD, "chiseled_stone_bricks"),
	QUARTZ(new ItemStack(Blocks.QUARTZ_BLOCK), ChatFormatting.WHITE, pillarTexture("quartz_block_side", "quartz_block_bottom", "quartz_block_top")),
	QUARTZ_CHISELED(new ItemStack(Blocks.CHISELED_QUARTZ_BLOCK), ChatFormatting.WHITE, pillarTexture("chiseled_quartz_block", "chiseled_quartz_block_top", "chiseled_quartz_block_top")),
	QUARTZ_LINES(new ItemStack(Blocks.QUARTZ_PILLAR), ChatFormatting.WHITE, pillarTexture("quartz_pillar", "chiseled_quartz_block_top", "chiseled_quartz_block_top"));

	private final ItemStack base;
	private final ChatFormatting formatting;
	private final Function<Direction, String> texture;

	EnumFarmMaterial(ItemStack base, ChatFormatting formatting, String texture) {
		this(base, formatting, (direction) -> texture);
	}

	EnumFarmMaterial(ItemStack base, ChatFormatting formatting, Function<Direction, String> texture) {
		this.base = base;
		this.formatting = formatting;
		this.texture = texture;
	}

	public ChatFormatting getFormatting() {
		return formatting;
	}

	private static Function<Direction, String> pillarTexture(String side, String bottom, String top) {
		return direction -> {
			switch (direction) {
				case UP:
					return top;
				case DOWN:
					return bottom;
				default:
					return side;
			}
		};
	}

	public TextureAtlasSprite[] getSprites() {
		TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
		for (Direction direction : Direction.VALUES) {
			textures[direction.get3DDataValue()] = ResourceUtil.getBlockSprite("block/" + texture.apply(direction));
		}
		return textures;
	}

	public void saveToCompound(CompoundTag compound) {
		compound.putInt("FarmBlock", this.ordinal());
	}

	public Component getDisplayName() {
		return base.getItem().getName(base);
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	public ItemStack getBase() {
		return base;
	}
}
