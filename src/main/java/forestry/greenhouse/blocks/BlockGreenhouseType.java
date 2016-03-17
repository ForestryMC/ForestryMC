package forestry.greenhouse.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.render.TextureManager;
import forestry.greenhouse.blocks.BlockGreenhouse.State;
import forestry.greenhouse.tiles.TileGreenhouseHatch;

public enum BlockGreenhouseType {
	PLAIN,
	GLASS,
	HATCH_INPUT(true),
	HATCH_OUTPUT(true),
	GEARBOX(true),
	VALVE(true),
	FAN(true, true),
	HEATER(true, true),
	DRYER(true, true),
	CONTROL(true),
	SPRINKLER(false, true),
	DOOR;
	
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
	private static final int TYPE_HATCH_INPUT = 10;
	private static final int TYPE_HATCH_OUTPUT = 11;
	private static final int TYPE_HATCH = 12;
	
	public final boolean hasOverlaySprite;
	public final boolean activatable;
	
	BlockGreenhouseType(boolean hasOverlaySprite, boolean activatable) {
		this.hasOverlaySprite = hasOverlaySprite;
		this.activatable = activatable;
	}
	
	BlockGreenhouseType(boolean hasOverlaySprite) {
		this.hasOverlaySprite = hasOverlaySprite;
		this.activatable = false;
	}
	
	BlockGreenhouseType() {
		this.hasOverlaySprite = false;
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
				TextureManager.registerSprite("blocks/greenhouse/heater.on"),
				TextureManager.registerSprite("blocks/greenhouse/dryer"),
				TextureManager.registerSprite("blocks/greenhouse/control"),
				TextureManager.registerSprite("blocks/greenhouse/hatch_input"),
				TextureManager.registerSprite("blocks/greenhouse/hatch_output"),
				TextureManager.registerSprite("blocks/greenhouse/hatch")
		);
	}
	
	/**
	 * @return The texture sprite from the type and the {@link IBlockState} of the greenhouse block
	 */
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getSprite(BlockGreenhouseType type, EnumFacing facing, @Nullable IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos) {
		TileEntity tile = null;
		if(world != null && pos != null){
			tile = world.getTileEntity(pos);
		}
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
				if(state == null || state.getValue(BlockGreenhouse.STATE) == State.OFF) {
					return sprites.get(TYPE_FAN_OFF);
				} else {
					return sprites.get(TYPE_FAN_ON);
				}
			case HEATER:
				if(state == null || state.getValue(BlockGreenhouse.STATE) == State.OFF) {
					return sprites.get(TYPE_HEATER_OFF);
				} else {
					return sprites.get(TYPE_HEATER_ON);
				}
			case DRYER:
				return sprites.get(TYPE_DRYER);
			case CONTROL:
				return sprites.get(TYPE_CONTROL);
			case HATCH_OUTPUT:
			case HATCH_INPUT:
				if(tile == null || facing == null || !(tile instanceof TileGreenhouseHatch)){
					return sprites.get(TYPE_HATCH);
				}
				TileGreenhouseHatch hatch = (TileGreenhouseHatch) tile;
				if(hatch.getOutwardsDir() == null){
					return sprites.get(TYPE_HATCH);
				}
				if(hatch.getOutwardsDir() == facing){
					return sprites.get(TYPE_HATCH_OUTPUT);
				}else if(hatch.getOutwardsDir().getOpposite() == facing){
					return sprites.get(TYPE_HATCH_INPUT);
				}
				return null;
			default:
				return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();	
		}
	}
	
	@Override
	public String toString() {
		return name().toLowerCase(Locale.ENGLISH);
	}
	
}
