package forestry.farming.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.IStringSerializable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum EnumFarmBlockType implements IStringSerializable {
	PLAIN,
	BAND,
	GEARBOX,
	HATCH,
	VALVE,
	CONTROL;

	public static final EnumFarmBlockType[] VALUES = values();

	private static final int TYPE_PLAIN = 0;
	private static final int TYPE_REVERSE = 1;
	private static final int TYPE_TOP = 2;
	private static final int TYPE_BAND = 3;
	private static final int TYPE_GEARS = 4;
	private static final int TYPE_HATCH = 5;
	private static final int TYPE_VALVE = 6;
	private static final int TYPE_CONTROL = 7;

	@OnlyIn(Dist.CLIENT)
	private static List<TextureAtlasSprite> sprites;

	@OnlyIn(Dist.CLIENT)
	public static void registerSprites() {
		AtlasTexture map = Minecraft.getInstance().getTextureMap();
		sprites = Arrays.asList(
			//TODO - dynamic textures
			//			map.registerSprite(new ResourceLocation(Constants.MOD_ID, "block/farm/plain")),
			//			map.registerSprite(new ResourceLocation(Constants.MOD_ID, "block/farm/reverse")),
			//			map.registerSprite(new ResourceLocation(Constants.MOD_ID, "block/farm/top")),
			//			map.registerSprite(new ResourceLocation(Constants.MOD_ID, "block/farm/band")),
			//			map.registerSprite(new ResourceLocation(Constants.MOD_ID, "block/farm/gears")),
			//			map.registerSprite(new ResourceLocation(Constants.MOD_ID, "block/farm/hatch")),
			//			map.registerSprite(new ResourceLocation(Constants.MOD_ID, "block/farm/valve")),
			//			map.registerSprite(new ResourceLocation(Constants.MOD_ID, "block/farm/control"))
		);
	}

	/**
	 * @return The texture sprite from the type of the farm block
	 */
	@OnlyIn(Dist.CLIENT)
	public static TextureAtlasSprite getSprite(EnumFarmBlockType type, int side) {
		switch (type) {
			case PLAIN: {
				if (side == 2) {
					return sprites.get(TYPE_REVERSE);
				} else if (side == 0 || side == 1) {
					return sprites.get(TYPE_TOP);
				} else {
					return sprites.get(TYPE_PLAIN);
				}
			}
			case BAND:
				return sprites.get(TYPE_BAND);
			case GEARBOX:
				return sprites.get(TYPE_GEARS);
			case HATCH:
				return sprites.get(TYPE_HATCH);
			case VALVE:
				return sprites.get(TYPE_VALVE);
			case CONTROL:
				return sprites.get(TYPE_CONTROL);
			default:
				return sprites.get(TYPE_PLAIN);
		}
	}

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

}
