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
package forestry.greenhouse.blocks;

import javax.annotation.Nullable;
import java.util.EnumMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.config.Constants;

@SideOnly(Side.CLIENT)
public enum BlockGreenhouseSprite {
	TOP("top"),
	GEARS("gears"),
	BORDER("border"),
	BORDER_TOP("border_top"),
	BORDER_TOP_CENTER("border_top_center"),
	HYGRO("hygroregulator"),
	CONTROL("control"),
	GREENHOUSE_SCREEN_0("greenhouse_screen_0"),
	GREENHOUSE_SCREEN_1("greenhouse_screen_1");

	public static final BlockGreenhouseSprite[] VALUES = values();
	private static EnumMap<BlockGreenhouseSprite, TextureAtlasSprite> sprites;
	private static TextureAtlasSprite missingImage;

	private final String spriteName;

	BlockGreenhouseSprite(String spriteName) {
		this.spriteName = spriteName;
	}

	public static void registerSprites() {
		sprites = new EnumMap<>(BlockGreenhouseSprite.class);
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();

		for (BlockGreenhouseSprite sprite : BlockGreenhouseSprite.VALUES) {
			ResourceLocation location = new ResourceLocation(Constants.MOD_ID, "blocks/greenhouse/" + sprite.spriteName);
			TextureAtlasSprite textureAtlasSprite = map.registerSprite(location);
			sprites.put(sprite, textureAtlasSprite);
		}
		missingImage = map.missingImage;
	}

	/**
	 * @return The texture sprite from the type and the {@link IBlockState} of the greenhouse block
	 */
	public static TextureAtlasSprite getSprite(BlockGreenhouseType type, @Nullable EnumFacing facing, @Nullable IBlockState state, int layer) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		switch (type) {
			case BORDER:
				if (facing == EnumFacing.UP) {
					return sprites.get(BlockGreenhouseSprite.BORDER_TOP);
				} else if (facing == EnumFacing.DOWN) {
					return sprites.get(BlockGreenhouseSprite.TOP);
				}
				return sprites.get(BlockGreenhouseSprite.BORDER);
			case BORDER_CENTER:
				if (facing == EnumFacing.UP) {
					if (layer == 0) {
						return sprites.get(BlockGreenhouseSprite.BORDER_TOP_CENTER);
					}
					return sprites.get(BlockGreenhouseSprite.GREENHOUSE_SCREEN_1);
				}
				return sprites.get(BlockGreenhouseSprite.BORDER);
			case GEARBOX:
				if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
					return sprites.get(BlockGreenhouseSprite.TOP);
				}
				return sprites.get(BlockGreenhouseSprite.GEARS);
			case CONTROL:
				if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
					return sprites.get(BlockGreenhouseSprite.TOP);
				}
				return sprites.get(BlockGreenhouseSprite.CONTROL);
			case SCREEN:
				if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
					return sprites.get(BlockGreenhouseSprite.TOP);
				}
				if (layer == 0) {
					return sprites.get(BlockGreenhouseSprite.GREENHOUSE_SCREEN_0);
				}
				return sprites.get(BlockGreenhouseSprite.GREENHOUSE_SCREEN_1);
			case PLAIN:
				if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
					return sprites.get(BlockGreenhouseSprite.TOP);
				}
				if (layer == 0) {
					return missingImage;
				}
			default:
				return map.getAtlasSprite("forestry:blocks/ash_brick");
		}
	}

}
