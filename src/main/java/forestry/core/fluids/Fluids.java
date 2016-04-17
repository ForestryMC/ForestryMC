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
package forestry.core.fluids;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.config.Constants;
import forestry.core.items.EnumContainerType;
import forestry.core.items.ItemLiquidContainer;

public enum Fluids {

	ETHANOL(new Color(255, 111, 0), 790, 1000) {
		@Override
		public String getTag() {
			return "bioethanol";
		}

		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 300, true);
		}

		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.BUCKET,
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}
	},
	BIOMASS(new Color(100, 132, 41), 400, 6560) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 100, true);
		}

		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.BUCKET,
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}
	},
	GLASS(new Color(164, 164, 164), 2400, 10000) {
		@Override
		public int getTemperature() {
			return 1200;
		}

		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 0, true);
		}

		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.BUCKET
			);
		}
	},
	HONEY(new Color(255, 196, 35), 1420, 73600) {
		@Override
		public String getTag() {
			return "for.honey";
		}

		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.BUCKET,
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}

		@Override
		public void setProperties(ItemLiquidContainer liquidContainer) {
			if (liquidContainer.getType() != EnumContainerType.BUCKET) {
				liquidContainer.setDrink(Constants.FOOD_HONEY_HEAL, Constants.FOOD_HONEY_SATURATION);
			}
		}
	},
	LEGACY_HONEY(new Color(255, 196, 35)) {
		@Override
		public String getTag() {
			return "honey";
		}
	},
	ICE(new Color(175, 242, 255), 920, 1000) {
		@Override
		public int getTemperature() {
			return 265;
		}

		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.BUCKET,
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}
	},
	JUICE(new Color(168, 201, 114)) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.BUCKET,
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}

		@Override
		public void setProperties(ItemLiquidContainer liquidContainer) {
			if (liquidContainer.getType() != EnumContainerType.BUCKET) {
				liquidContainer.setDrink(Constants.FOOD_JUICE_HEAL, Constants.FOOD_JUICE_SATURATION);
			}
		}
	},
	MILK(new Color(255, 255, 255), 1030, 3000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Override
		public List<ItemStack> getOtherContainers() {
			return Collections.singletonList(
					new ItemStack(Items.milk_bucket)
			);
		}
	},
	SEEDOIL("SeedOil", new Color(255, 255, 168), 885, 5000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 2, true);
		}

		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.BUCKET,
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}
	},
	SHORT_MEAD("ShortMead", new Color(239, 154, 56), 1000, 1200) {
		@Override
		public String getTag() {
			return "short.mead";
		}

		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.BUCKET,
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}
	},
	MEAD("Mead", new Color(202, 102, 0), 1000, 1200) {
		@Override
		public String getTag() {
			return "mead";
		}

		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.BUCKET,
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}
	},
	// Vanilla
	WATER(new Color(0x2432ec)) {
		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}
	},
	LAVA(new Color(0xfd461f)) {
		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.CAN,
					EnumContainerType.REFRACTORY
			);
		}
	},
	// BuildCraft
	FUEL(new Color(0xffff00)) {
		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}
	},
	OIL(new Color(0x404040)) {
		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}
	},
	// Railcraft
	CREOSOTE(new Color(0x635c03)),
	STEAM(new Color(0x91938F));

	public static final Fluids[] forestryFluids = {ETHANOL, BIOMASS, GLASS, HONEY, LEGACY_HONEY, ICE, JUICE, MILK, SEEDOIL, SHORT_MEAD, MEAD};

	private static final Map<String, Fluids> tagToFluid = new HashMap<>();

	static {
		for (Fluids fluids : Fluids.values()) {
			tagToFluid.put(fluids.getTag(), fluids);
		}
	}

	private final String containerNameKey;
	private final String tag;
	private final int density, viscosity;
	@Nonnull
	private final Color color;

	Fluids(@Nonnull Color color) {
		this(null, color, 1000, 1000);
	}

	Fluids(@Nonnull Color color, int density, int viscosity) {
		this(null, color, density, viscosity);
	}

	Fluids(String containerNameKey, @Nonnull Color color, int density, int viscosity) {
		if (containerNameKey == null) {
			containerNameKey = WordUtils.capitalize(toString().toLowerCase(Locale.ENGLISH));
		}
		this.containerNameKey = containerNameKey;
		this.tag = name().toLowerCase(Locale.ENGLISH);
		this.color = color;
		this.density = density;
		this.viscosity = viscosity;
	}

	public int getTemperature() {
		return 295;
	}

	public String getTag() {
		return tag;
	}

	public final int getDensity() {
		return density;
	}

	public final int getViscosity() {
		return viscosity;
	}

	public final Fluid getFluid() {
		return FluidRegistry.getFluid(getTag());
	}

	public final FluidStack getFluid(int mb) {
		return FluidRegistry.getFluidStack(getTag(), mb);
	}

	public final Block getBlock() {
		Fluid fluid = getFluid();
		if (fluid == null) {
			return null;
		}
		return fluid.getBlock();
	}

	@Nonnull
	public final Color getColor() {
		return color;
	}

	public final boolean is(Fluid fluid) {
		return getFluid() == fluid;
	}

	public final boolean is(FluidStack fluidStack) {
		return fluidStack != null && getFluid() == fluidStack.getFluid();
	}

	public final boolean isContained(ItemStack containerStack) {
		return containerStack != null && FluidHelper.containsFluid(containerStack, getFluid());
	}

	public static boolean areEqual(Fluid fluid, FluidStack fluidStack) {
		if (fluidStack != null && fluid == fluidStack.getFluid()) {
			return true;
		}
		return fluid == null && fluidStack == null;
	}

	public static boolean areEqual(FluidStack a, FluidStack b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		return a.isFluidEqual(b);
	}

	@Nonnull
	public static Color getFluidColor(FluidStack fluidStack) {
		if (fluidStack != null) {
			Fluid fluid = fluidStack.getFluid();
			if (fluid != null) {
				Fluids fluids = tagToFluid.get(fluid.getName());
				if (fluids != null) {
					return fluids.getColor();
				}
			}
		}

		return Fluids.WATER.getColor();
	}

	/** FluidBlock and Container registration */
	public String getContainerNameKey() {
		return containerNameKey;
	}

	public EnumSet<EnumContainerType> getContainerTypes() {
		return EnumSet.noneOf(EnumContainerType.class);
	}

	/**
	 * Add non-forestry containers for this fluid.
	 */
	public List<ItemStack> getOtherContainers() {
		return Collections.emptyList();
	}

	/**
	 * Create a FluidBlock for this fluid.
	 */
	public Block makeBlock() {
		return null;
	}

	/**
	 * Set the properties for an ItemLiquidContainer before it gets registered.
	 */
	public void setProperties(ItemLiquidContainer liquidContainer) {

	}
}
