package forestry.greenhouse.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.render.TextureManager;
import forestry.greenhouse.blocks.BlockGreenhouse.State;

public enum BlockGreenhouseType {
	PLAIN(true),
	GLASS(true),
	GEARBOX(true),
	VALVE(true),
	FAN(true, true),
	HEATER(true, true),
	DRYER(true, true),
	CONTROL(true),
	SPRINKLER(false, true);
	
	public static final BlockGreenhouseType[] VALUES = values();
	
	/* TEXTURE IDS*/
	private static final int TYPE_PLAIN = 0;
	private static final int TYPE_GLASS = 1;
	private static final int TYPE_GEARS = 2;
	private static final int TYPE_VALVE = 3;
	private static final int TYPE_FAN_OFF = 4;
	private static final int TYPE_FAN_ON = 5;
	private static final int TYPE_HEATER_OFF = 6;
	private static final int TYPE_HEATER_ON = 7;
	private static final int TYPE_DRYER = 8;
	private static final int TYPE_CONTROL = 9;
	
	public final boolean canCamouflage;
	public final boolean activatable;
	
	BlockGreenhouseType(boolean canCamouflage, boolean activatable) {
		this.canCamouflage = canCamouflage;
		this.activatable = activatable;
	}
	
	BlockGreenhouseType(boolean canCamouflage) {
		this.canCamouflage = canCamouflage;
		this.activatable = false;
	}
	
	BlockGreenhouseType() {
		this.canCamouflage = false;
		this.activatable = false;
	}

	@SideOnly(Side.CLIENT)
	private static List<TextureAtlasSprite> sprites;

	@SideOnly(Side.CLIENT)
	public static void registerSprites() {
		sprites = Arrays.asList(
				TextureManager.getSprite("minecraft", "blocks/brick"),
				TextureManager.getSprite("minecraft", "blocks/glass_green"),
				TextureManager.registerSprite("blocks/greenhouse/gears"),
				TextureManager.registerSprite("blocks/greenhouse/valve"),
				TextureManager.registerSprite("blocks/greenhouse/fan.off"),
				TextureManager.registerSprite("blocks/greenhouse/fan.on"),
				TextureManager.registerSprite("blocks/greenhouse/heater.off"),
				TextureManager.registerSprite("blocks/greenhouse/heater.off"),
				TextureManager.registerSprite("blocks/greenhouse/dryer"),
				TextureManager.registerSprite("blocks/greenhouse/control")
		);
	}
	
	/**
	 * @return The texture sprite from the type and the {@link IBlockState} of the greenhouse block
	 */
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getSprite(BlockGreenhouseType type, @Nullable IBlockState state) {
		switch (type) {
			case PLAIN:
				return sprites.get(TYPE_PLAIN);
			case GLASS:
				return sprites.get(TYPE_GLASS);
			case GEARBOX:
				return sprites.get(TYPE_GEARS);
			case VALVE:
				return sprites.get(TYPE_VALVE);
			case FAN:
				if(state == null || state.getValue(BlockGreenhouse.STATE) == State.OFF)
					return sprites.get(TYPE_FAN_OFF);
				else
					return sprites.get(TYPE_FAN_ON);
			case HEATER:
				if(state == null || state.getValue(BlockGreenhouse.STATE) == State.OFF)
					return sprites.get(TYPE_HEATER_OFF);
				else
					return sprites.get(TYPE_HEATER_ON);
			case DRYER:
				return sprites.get(TYPE_DRYER);
			case CONTROL:
				return sprites.get(TYPE_CONTROL);
			default:
				return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();	
		}
	}
	
	@Override
	public String toString() {
		return name().toLowerCase(Locale.ENGLISH);
	}
	
}
