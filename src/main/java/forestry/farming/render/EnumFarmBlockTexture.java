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
package forestry.farming.render;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.core.render.TextureManager;
import forestry.farming.blocks.EnumBlockFarmType;

public enum EnumFarmBlockTexture {
	BRICK_STONE(new ItemStack(Blocks.stonebrick, 1, 0)),
	BRICK_MOSSY(new ItemStack(Blocks.stonebrick, 1, 1)),
	BRICK_CRACKED(new ItemStack(Blocks.stonebrick, 1, 2)),
	BRICK(new ItemStack(Blocks.brick_block)),
	SANDSTONE_SMOOTH(new ItemStack(Blocks.sandstone, 1, 2)),
	SANDSTONE_CHISELED(new ItemStack(Blocks.sandstone, 1, 1)),
	BRICK_NETHER(new ItemStack(Blocks.nether_brick)),
	BRICK_CHISELED(new ItemStack(Blocks.stonebrick, 1, 3)),
	QUARTZ(new ItemStack(Blocks.quartz_block, 1, 0)),
	QUARTZ_CHISELED(new ItemStack(Blocks.quartz_block, 1, 1)),
	QUARTZ_LINES(new ItemStack(Blocks.quartz_block, 1, 2));

	private static final int TYPE_PLAIN = 0;
	private static final int TYPE_REVERSE = 1;
	private static final int TYPE_TOP = 2;
	private static final int TYPE_BAND = 3;
	private static final int TYPE_GEARS = 4;
	private static final int TYPE_HATCH = 5;
	private static final int TYPE_VALVE = 6;
	private static final int TYPE_CONTROL = 7;

	@SideOnly(Side.CLIENT)
	private static List<TextureAtlasSprite> sprites;

	@SideOnly(Side.CLIENT)
	public static void registerSprites() {
		sprites = Arrays.asList(
				TextureManager.registerSprite("blocks/farm/plain"),
				TextureManager.registerSprite("blocks/farm/reverse"),
				TextureManager.registerSprite("blocks/farm/top"),
				TextureManager.registerSprite("blocks/farm/band"),
				TextureManager.registerSprite("blocks/farm/gears"),
				TextureManager.registerSprite("blocks/farm/hatch"),
				TextureManager.registerSprite("blocks/farm/valve"),
				TextureManager.registerSprite("blocks/farm/control")
		);
	}

	private final ItemStack base;

	EnumFarmBlockTexture(ItemStack base) {
		this.base = base;
	}
	
	/**
	 * @return The texture sprite from the type of the farm block
	 */
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getSprite(EnumBlockFarmType type, int side) {
		switch (type) {
			case BASIC: {
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
	
	/**
	 * @return The texture sprite from the material of the farm block
	 */
	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getSprite(EnumFarmBlockTexture texture, int side) {
		TextureManager manager = TextureManager.getInstance();
		switch (texture) {
		case BRICK:
			return manager.getSprite("minecraft", "blocks/brick");
		case BRICK_STONE:
			return manager.getSprite("minecraft", "blocks/stonebrick");
		case BRICK_CHISELED:
			return manager.getSprite("minecraft", "blocks/stonebrick_carved");
		case BRICK_CRACKED:
			return manager.getSprite("minecraft", "blocks/stonebrick_cracked");
		case BRICK_MOSSY:
			return manager.getSprite("minecraft", "blocks/stonebrick_mossy");
		case BRICK_NETHER:
			return manager.getSprite("minecraft", "blocks/nether_brick");
		case SANDSTONE_CHISELED:
			if (side == 0)
				return manager.getSprite("minecraft", "blocks/sandstone_bottom");
			else if (side == 1)
				return manager.getSprite("minecraft", "blocks/sandstone_top");
			return manager.getSprite("minecraft", "blocks/sandstone_carved");
		case SANDSTONE_SMOOTH:
			if (side == 0)
				return manager.getSprite("minecraft", "blocks/sandstone_bottom");
			else if (side == 1)
				return manager.getSprite("minecraft", "blocks/sandstone_top");
			return manager.getSprite("minecraft", "blocks/sandstone_smooth");
		case QUARTZ:
			if (side == 0)
				return manager.getSprite("minecraft", "blocks/quartz_block_bottom");
			else if (side == 1)
				return manager.getSprite("minecraft", "blocks/quartz_block_top");
			return manager.getSprite("minecraft", "blocks/quartz_block_side");
		case QUARTZ_CHISELED:
			if (side == 0 || side == 1)
				return manager.getSprite("minecraft", "blocks/quartz_block_chiseled_top");
			return manager.getSprite("minecraft", "blocks/quartz_block_chiseled");
		case QUARTZ_LINES:
			if (side == 0 || side == 1)
				return manager.getSprite("minecraft", "blocks/quartz_block_lines_top");
			return manager.getSprite("minecraft", "blocks/quartz_block_lines");
		default:
			return null;
		}
	}

	public void saveToCompound(NBTTagCompound compound) {
		compound.setInteger("FarmBlock", this.ordinal());
	}

	public String getName() {
		return base.getItem().getItemStackDisplayName(base);
	}

	public ItemStack getBase() {
		return base;
	}

	public static EnumFarmBlockTexture getFromCompound(NBTTagCompound compound) {
		if (compound != null) {
			int farmBlockOrdinal = compound.getInteger("FarmBlock");
			if (farmBlockOrdinal < EnumFarmBlockTexture.values().length) {
				return EnumFarmBlockTexture.values()[farmBlockOrdinal];
			}
		}

		return EnumFarmBlockTexture.BRICK_STONE;
	}
}
