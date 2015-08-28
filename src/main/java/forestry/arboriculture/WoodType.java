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
package forestry.arboriculture;

import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.core.render.TextureManager;

public enum WoodType {
	LARCH, TEAK, ACACIA, LIME,
	CHESTNUT, WENGE, BAOBAB, SEQUOIA(4.0f),
	KAPOK, EBONY, MAHOGANY, BALSA(1.0f),
	WILLOW, WALNUT, GREENHEART(7.5f), CHERRY,
	MAHOE, POPLAR, PALM, PAPAYA,
	PINE(3.0f), PLUM, MAPLE, CITRUS,
	GIGANTEUM, IPE, PADAUK, COCOBOLO,
	ZEBRAWOOD;
	
	public static final PropertyEnum WOODTYPE = PropertyEnum.create("woodtype", WoodType.class);

	public static final float DEFAULT_HARDNESS = 2.0f;

	public static final WoodType[] VALUES = values();

	private final float hardness;

	private ItemStack log;
	private ItemStack logFireproof;
	private ItemStack planks;
	private ItemStack planksFireproof;
	private ItemStack slab;
	private ItemStack slabFireproof;
	private ItemStack fence;
	private ItemStack fenceFireproof;
	private ItemStack stairs;
	private ItemStack stairsFireproof;

	WoodType() {
		this(DEFAULT_HARDNESS);
	}

	WoodType(float hardness) {
		this.hardness = hardness;
	}

	public float getHardness() {
		return hardness;
	}

	public void saveToCompound(NBTTagCompound compound) {
		compound.setInteger("WoodType", this.ordinal());
	}

	public static WoodType getFromCompound(NBTTagCompound compound) {

		if (compound != null) {
			int typeOrdinal = compound.getInteger("WoodType");
			if (typeOrdinal < WoodType.VALUES.length) {
				return WoodType.VALUES[typeOrdinal];
			}
		}

		return WoodType.LARCH;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ENGLISH);
	}

	private ItemStack getStack(Block block) {
		NBTTagCompound woodNBT = new NBTTagCompound();
		saveToCompound(woodNBT);

		ItemStack itemStack = new ItemStack(block);
		itemStack.setTagCompound(woodNBT);
		return itemStack;
	}

	public void registerLog(Block block, boolean fireproof) {
		ItemStack itemStack = getStack(block);
		if (fireproof) {
			this.logFireproof = itemStack;
		} else {
			this.log = itemStack;
		}
	}

	public void registerPlanks(Block block, boolean fireproof) {
		ItemStack itemStack = getStack(block);
		if (fireproof) {
			this.planksFireproof = itemStack;
		} else {
			this.planks = itemStack;
		}
	}

	public void registerSlab(Block block, boolean fireproof) {
		ItemStack itemStack = getStack(block);
		if (fireproof) {
			this.slabFireproof = itemStack;
		} else {
			this.slab = itemStack;
		}
	}

	public void registerFence(Block block, boolean fireproof) {
		ItemStack itemStack = getStack(block);
		if (fireproof) {
			this.fenceFireproof = itemStack;
		} else {
			this.fence = itemStack;
		}
	}

	public void registerStairs(Block block, boolean fireproof) {
		ItemStack itemStack = getStack(block);
		if (fireproof) {
			this.stairsFireproof = itemStack;
		} else {
			this.stairs = itemStack;
		}
	}

	public ItemStack getPlanks(boolean fireproof) {
		if (fireproof) {
			return planksFireproof.copy();
		} else {
			return planks.copy();
		}
	}

	public ItemStack getLog(boolean fireproof) {
		if (fireproof) {
			return logFireproof.copy();
		} else {
			return log.copy();
		}
	}

	public ItemStack getSlab(boolean fireproof) {
		if (fireproof) {
			return slabFireproof.copy();
		} else {
			return slab.copy();
		}
	}

	public ItemStack getFence(boolean fireproof) {
		if (fireproof) {
			return fenceFireproof.copy();
		} else {
			return fence.copy();
		}
	}

	public ItemStack getStairs(boolean fireproof) {
		if (fireproof) {
			return stairsFireproof.copy();
		} else {
			return stairs.copy();
		}
	}

	public static WoodType getRandom(Random random) {
		return VALUES[random.nextInt(VALUES.length)];
	}
}
