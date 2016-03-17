package forestry.greenhouse.blocks;

import java.util.EnumMap;
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
	private static EnumMap<BlockGreenhouseSprites, TextureAtlasSprite> sprites = new EnumMap(BlockGreenhouseSprites.class);
	
	private static enum BlockGreenhouseSprites{
		PLAIN, GLASS, GEARS("gears"), VALVE("valve"), FAN_OFF("fan.off"), FAN_ON("fan.on"), HEATER_OFF("heater.off"), HEATER_ON("heater.on"), DRYER("dryer"), CONTROL("control"), HATCH_DEFAULT("hatch"), HATCH_INPUT("hatch_input"), HATCH_OUTPUT("hatch_output");
		
		public static final BlockGreenhouseSprites[] VALUES = values();
		
		private String spriteName;
		
		private BlockGreenhouseSprites(String spriteName) {
			this.spriteName = spriteName;
		}
		
		private BlockGreenhouseSprites() {
			this.spriteName = null;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerSprites() {
		for(BlockGreenhouseSprites sprite : BlockGreenhouseSprites.VALUES){
			if(sprite == BlockGreenhouseSprites.PLAIN){
				sprites.put(sprite, TextureManager.getSprite("minecraft", "blocks/brick"));
			}else if(sprite == BlockGreenhouseSprites.GLASS){
				sprites.put(sprite, TextureManager.getSprite("minecraft", "blocks/glass_green"));
			}else{
				sprites.put(sprite, TextureManager.registerSprite("blocks/greenhouse/" + sprite.spriteName));
			}
		}
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
			return sprites.get(BlockGreenhouseSprites.PLAIN);
		case GLASS:
			return sprites.get(BlockGreenhouseSprites.GLASS);
			case GEARBOX:
				return sprites.get(BlockGreenhouseSprites.GEARS);
			case VALVE:
				return sprites.get(BlockGreenhouseSprites.VALUES);
			case FAN:
				if(state == null || state.getValue(BlockGreenhouse.STATE) == State.OFF) {
					return sprites.get(BlockGreenhouseSprites.FAN_OFF);
				} else {
					return sprites.get(BlockGreenhouseSprites.FAN_ON);
				}
			case HEATER:
				if(state == null || state.getValue(BlockGreenhouse.STATE) == State.OFF) {
					return sprites.get(BlockGreenhouseSprites.HEATER_OFF);
				} else {
					return sprites.get(BlockGreenhouseSprites.HEATER_ON);
				}
			case DRYER:
				return sprites.get(BlockGreenhouseSprites.DRYER);
			case CONTROL:
				return sprites.get(BlockGreenhouseSprites.CONTROL);
			case HATCH_OUTPUT:
			case HATCH_INPUT:
				if(tile == null || facing == null || !(tile instanceof TileGreenhouseHatch)){
					return sprites.get(BlockGreenhouseSprites.HATCH_DEFAULT);
				}
				TileGreenhouseHatch hatch = (TileGreenhouseHatch) tile;
				if(hatch.getOutwardsDir() == null){
					return sprites.get(BlockGreenhouseSprites.HATCH_DEFAULT);
				}
				if(hatch.getOutwardsDir() == facing){
					return sprites.get(BlockGreenhouseSprites.HATCH_OUTPUT);
				}else if(hatch.getOutwardsDir().getOpposite() == facing){
					return sprites.get(BlockGreenhouseSprites.HATCH_INPUT);
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
