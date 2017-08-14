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
public enum BlockClimatiserSprite {
	HYGRO("hygroregulator"),
	HEATER_OFF("heater.off"), HEATER_ON("heater.on"),
	FAN_OFF("fan.off"), FAN_ON("fan.on"),
	HUMIDIFIER_OFF("humidifier.off"), HUMIDIFIER_ON("humidifier.on"),
	DEHUMIDIFIER_OFF("dehumidifier.off"), DEHUMIDIFIER_ON("dehumidifier.on");

	public static final BlockClimatiserSprite[] VALUES = values();
	private static EnumMap<BlockClimatiserSprite, TextureAtlasSprite> sprites;
	private static TextureAtlasSprite missingImage;

	private final String spriteName;

	BlockClimatiserSprite(String spriteName) {
		this.spriteName = spriteName;
	}

	public static void registerSprites() {
		sprites = new EnumMap<>(BlockClimatiserSprite.class);
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();

		for (BlockClimatiserSprite sprite : BlockClimatiserSprite.VALUES) {
			ResourceLocation location = new ResourceLocation(Constants.MOD_ID, "blocks/greenhouse/" + sprite.spriteName);
			TextureAtlasSprite textureAtlasSprite = map.registerSprite(location);
			sprites.put(sprite, textureAtlasSprite);
		}
		missingImage = map.missingImage;
	}

	/**
	 * @return The texture sprite from the type and the {@link IBlockState} of the greenhouse block
	 */
	public static TextureAtlasSprite getSprite(BlockClimatiserType type, @Nullable EnumFacing facing, @Nullable IBlockState state) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		switch (type) {
			case HYGRO:
				return sprites.get(BlockClimatiserSprite.HYGRO);
			case HEATER:
				if (state == null || state.getValue(State.PROPERTY) == State.OFF) {
					return sprites.get(BlockClimatiserSprite.HEATER_OFF);
				} else {
					return sprites.get(BlockClimatiserSprite.HEATER_ON);
				}
			case FAN:
				if (state == null || state.getValue(State.PROPERTY) == State.OFF) {
					return sprites.get(BlockClimatiserSprite.FAN_OFF);
				} else {
					return sprites.get(BlockClimatiserSprite.FAN_ON);
				}
			case HUMIDIFIER:
				if (state == null || state.getValue(State.PROPERTY) == State.OFF) {
					return sprites.get(BlockClimatiserSprite.HUMIDIFIER_OFF);
				} else {
					return sprites.get(BlockClimatiserSprite.HUMIDIFIER_ON);
				}
			case DEHUMIDIFIER:
				if (state == null || state.getValue(State.PROPERTY) == State.OFF) {
					return sprites.get(BlockClimatiserSprite.DEHUMIDIFIER_OFF);
				} else {
					return sprites.get(BlockClimatiserSprite.DEHUMIDIFIER_ON);
				}
		}
		return missingImage;
	}

}
