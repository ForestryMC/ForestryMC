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
package forestry.farming.multiblock;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.sprite.ISprite;
import forestry.core.render.TextureManager;

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

	@SideOnly(Side.CLIENT)
	private static List<ISprite> icons;

	@SideOnly(Side.CLIENT)
	public static void registerIcons() {
		TextureManager textureManager = TextureManager.getInstance();
		icons = Arrays.asList(
				textureManager.registerTex("blocks", "farm/plain"),
				textureManager.registerTex("blocks", "farm/reverse"),
				textureManager.registerTex("blocks", "farm/top"),
				textureManager.registerTex("blocks", "farm/band"),
				textureManager.registerTex("blocks", "farm/gears"),
				textureManager.registerTex("blocks", "farm/hatch"),
				textureManager.registerTex("blocks", "farm/valve"),
				textureManager.registerTex("blocks", "farm/control")
		);
	}

	private final ItemStack base;

	EnumFarmBlockTexture(ItemStack base) {
		this.base = base;
	}

	@SideOnly(Side.CLIENT)
	public static ISprite getIcon(int type) {
		return icons.get(type);
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
