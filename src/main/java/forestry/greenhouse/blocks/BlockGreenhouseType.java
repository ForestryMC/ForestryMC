package forestry.greenhouse.blocks;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Locale;

import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.greenhouse.blocks.BlockGreenhouse.State;
import forestry.greenhouse.tiles.TileGreenhouseHatch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum BlockGreenhouseType {
	PLAIN,
	GLASS(Material.GLASS),
	//item, energy / fluid
	HATCH_INPUT(true),
	HATCH_OUTPUT(true),
	//energy
	GEARBOX(true),
	//water
	VALVE(true),
	//temperature
	HEATER(true, true), FAN(true, true),
	//humidity
	HUMIDIFIER(true, true), DEHUMIDIFIER(true, true),
	//temperature and humidity
	WINDOW(Material.GLASS, false, true),
	//temperature and humidity
	WINDOW_UP(Material.GLASS, false, true),
	CONTROL(true),
	DOOR,
	CLIMATE_CONTROL(true),
	BUTTERFLY_HATCH(true);

	public static final BlockGreenhouseType[] VALUES = values();

	public final boolean hasOverlaySprite;
	public final boolean activatable;
	public final Material material;

	BlockGreenhouseType(Material material, boolean hasOverlaySprite, boolean activatable) {
		this.hasOverlaySprite = hasOverlaySprite;
		this.activatable = activatable;
		this.material = material;
	}

	BlockGreenhouseType(Material material, boolean hasOverlaySprite) {
		this(material, hasOverlaySprite, false);
	}

	BlockGreenhouseType(Material material) {
		this(material, false, false);
	}
	
	BlockGreenhouseType(boolean hasOverlaySprite, boolean activatable) {
		this(Material.ROCK, hasOverlaySprite, activatable);
	}

	BlockGreenhouseType(boolean hasOverlaySprite) {
		this(Material.ROCK, hasOverlaySprite);
	}

	BlockGreenhouseType() {
		this(Material.ROCK);
	}

	@SideOnly(Side.CLIENT)
	private static EnumMap<BlockGreenhouseSprites, TextureAtlasSprite> sprites;

	private enum BlockGreenhouseSprites {
		GEARS("gears"), 
		VALVE("valve"), 
		HEATER_OFF("heater.off"), HEATER_ON("heater.on"), 
		FAN_OFF("fan.off"), FAN_ON("fan.on"), 
		HUMIDIFIER_OFF("humidifier.off"), HUMIDIFIER_ON("humidifier.on"), 
		DEHUMIDIFIER_OFF("dehumidifier.off"), DEHUMIDIFIER_ON("dehumidifier.on"), 
		CONTROL("control"), 
		HATCH_DEFAULT("hatch"), HATCH_INPUT("hatch_input"), HATCH_OUTPUT("hatch_output"), 
		CLIMATE_CONTROL("climate_control"), 
		BUTTERFLY_HATCH("butterfly_hatch");

		public static final BlockGreenhouseSprites[] VALUES = values();

		private final String spriteName;

		BlockGreenhouseSprites(String spriteName) {
			this.spriteName = spriteName;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerSprites() {
		sprites = new EnumMap<>(BlockGreenhouseSprites.class);
		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();

		for (BlockGreenhouseSprites sprite : BlockGreenhouseSprites.VALUES) {
			ResourceLocation location = new ResourceLocation(Constants.MOD_ID, "blocks/greenhouse/" + sprite.spriteName);
			TextureAtlasSprite textureAtlasSprite = map.registerSprite(location);
			sprites.put(sprite, textureAtlasSprite);
		}
	}

	/**
	 * @return The texture sprite from the type and the {@link IBlockState} of the greenhouse block
	 */
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getSprite(BlockGreenhouseType type, @Nullable EnumFacing facing, @Nullable IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos) {
		TileEntity tile = null;
		if (world != null && pos != null) {
			tile = world.getTileEntity(pos);
		}
		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();
		switch (type) {
			case PLAIN:
				return map.getAtlasSprite("minecraft:blocks/brick");
			case GLASS:
				return map.getAtlasSprite("minecraft:blocks/glass_green");
			case GEARBOX:
				return sprites.get(BlockGreenhouseSprites.GEARS);
			case VALVE:
				return sprites.get(BlockGreenhouseSprites.VALVE);
			case HEATER:
				if (state == null || state.getValue(BlockGreenhouse.STATE) == State.OFF) {
					return sprites.get(BlockGreenhouseSprites.HEATER_OFF);
				} else {
					return sprites.get(BlockGreenhouseSprites.HEATER_ON);
				}
			case FAN:
				if (state == null || state.getValue(BlockGreenhouse.STATE) == State.OFF) {
					return sprites.get(BlockGreenhouseSprites.FAN_OFF);
				} else {
					return sprites.get(BlockGreenhouseSprites.FAN_ON);
				}
			case HUMIDIFIER:
				if (state == null || state.getValue(BlockGreenhouse.STATE) == State.OFF) {
					return sprites.get(BlockGreenhouseSprites.HUMIDIFIER_OFF);
				} else {
					return sprites.get(BlockGreenhouseSprites.HUMIDIFIER_ON);
				}
			case DEHUMIDIFIER:
				if (state == null || state.getValue(BlockGreenhouse.STATE) == State.OFF) {
					return sprites.get(BlockGreenhouseSprites.DEHUMIDIFIER_OFF);
				} else {
					return sprites.get(BlockGreenhouseSprites.DEHUMIDIFIER_ON);
				}
			case CONTROL:
				return sprites.get(BlockGreenhouseSprites.CONTROL);
			case HATCH_OUTPUT:
			case HATCH_INPUT:
				if (tile == null || facing == null || !(tile instanceof TileGreenhouseHatch)) {
					return sprites.get(BlockGreenhouseSprites.HATCH_DEFAULT);
				}
				TileGreenhouseHatch hatch = (TileGreenhouseHatch) tile;
				if (hatch.getOutwardsDir() == null) {
					return sprites.get(BlockGreenhouseSprites.HATCH_DEFAULT);
				}
				if (hatch.getOutwardsDir() == facing) {
					return sprites.get(BlockGreenhouseSprites.HATCH_OUTPUT);
				} else if (hatch.getOutwardsDir().getOpposite() == facing) {
					return sprites.get(BlockGreenhouseSprites.HATCH_INPUT);
				}
				return Minecraft.getMinecraft().getTextureMapBlocks().missingImage;
			case CLIMATE_CONTROL:
				if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
					return Minecraft.getMinecraft().getTextureMapBlocks().missingImage;
				}
				return sprites.get(BlockGreenhouseSprites.CLIMATE_CONTROL);
			case BUTTERFLY_HATCH:
				if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
					return Minecraft.getMinecraft().getTextureMapBlocks().missingImage;
				}
				return sprites.get(BlockGreenhouseSprites.BUTTERFLY_HATCH);
			default:
				return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}
	}

	public static EnumMap<BlockGreenhouseSprites, TextureAtlasSprite> getSprites() {
		return sprites;
	}

	@Override
	public String toString() {
		return name().toLowerCase(Locale.ENGLISH);
	}

}
