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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.render.TextureManager;
import forestry.farming.blocks.BlockFarmType;

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
	private static List<IIcon> icons;

	@SideOnly(Side.CLIENT)
	public static void registerIcons(IIconRegister register) {
		icons = Arrays.asList(
				TextureManager.registerTex(register, "farm/plain"),
				TextureManager.registerTex(register, "farm/reverse"),
				TextureManager.registerTex(register, "farm/top"),
				TextureManager.registerTex(register, "farm/band"),
				TextureManager.registerTex(register, "farm/gears"),
				TextureManager.registerTex(register, "farm/hatch"),
				TextureManager.registerTex(register, "farm/valve"),
				TextureManager.registerTex(register, "farm/control")
		);
	}

	private final ItemStack base;

	EnumFarmBlockTexture(ItemStack base) {
		this.base = base;
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getIcon(BlockFarmType type, int side) {
		switch (type) {
			case BASIC: {
				if (side == 2) {
					return icons.get(TYPE_REVERSE);
				} else if (side == 0 || side == 1) {
					return icons.get(TYPE_TOP);
				} else {
					return icons.get(TYPE_PLAIN);
				}
			}
			case BAND:
				return icons.get(TYPE_BAND);
			case GEARBOX:
				return icons.get(TYPE_GEARS);
			case HATCH:
				return icons.get(TYPE_HATCH);
			case VALVE:
				return icons.get(TYPE_VALVE);
			case CONTROL:
				return icons.get(TYPE_CONTROL);
			default:
				return icons.get(TYPE_PLAIN);
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
