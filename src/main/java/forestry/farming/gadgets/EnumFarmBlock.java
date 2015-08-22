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
package forestry.farming.gadgets;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.render.TextureManager;

public enum EnumFarmBlock implements IStringSerializable {
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

	private final ItemStack base;

	private EnumFarmBlock(ItemStack base) {
		this.base = base;
	}

	public void saveToCompound(NBTTagCompound compound) {
		compound.setInteger("FarmBlock", this.ordinal());
	}
	
	@SideOnly(Side.CLIENT)
	private static List<TextureAtlasSprite> icons;

	@SideOnly(Side.CLIENT)
	public static void registerIcons(TextureMap register) {
		TextureManager textureManager = TextureManager.getInstance();
		icons = Arrays.asList(
				textureManager.registerTex(register, "farm/plain"),
				textureManager.registerTex(register, "farm/reverse"),
				textureManager.registerTex(register, "farm/top"),
				textureManager.registerTex(register, "farm/band"),
				textureManager.registerTex(register, "farm/gears"),
				textureManager.registerTex(register, "farm/hatch"),
				textureManager.registerTex(register, "farm/valve"),
				textureManager.registerTex(register, "farm/control")
		);
	}

	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getIcon(int type) {
		return icons.get(type);
	}

	public String getName() {
		return base.getItem().getItemStackDisplayName(base);
	}

	public ItemStack getBase() {
		return base;
	}

	public static EnumFarmBlock getFromCompound(NBTTagCompound compound) {

		if (compound != null) {
			int farmBlockOrdinal = compound.getInteger("FarmBlock");
			if (farmBlockOrdinal < EnumFarmBlock.values().length) {
				return EnumFarmBlock.values()[farmBlockOrdinal];
			}
		}

		return EnumFarmBlock.BRICK_STONE;
	}
}
