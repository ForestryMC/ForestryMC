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

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.config.Constants;
import forestry.core.config.ForestryItem;
import forestry.core.items.ItemLiquidContainer;

import static forestry.core.items.ItemLiquidContainer.EnumContainerType;

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
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case BUCKET:
					return ForestryItem.bucketEthanol;
				case CAN:
					return ForestryItem.canEthanol;
				case CAPSULE:
					return ForestryItem.waxCapsuleEthanol;
				case REFRACTORY:
					return ForestryItem.refractoryEthanol;
				default:
					return null;
			}
		}
	},
	BIOMASS(new Color(100, 132, 41), 400, 6560) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 100, true);
		}

		@Override
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case BUCKET:
					return ForestryItem.bucketBiomass;
				case CAN:
					return ForestryItem.canBiomass;
				case CAPSULE:
					return ForestryItem.waxCapsuleBiomass;
				case REFRACTORY:
					return ForestryItem.refractoryBiomass;
				default:
					return null;
			}
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
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case BUCKET:
					return ForestryItem.bucketGlass;
				default:
					return null;
			}
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
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case BUCKET:
					return ForestryItem.bucketHoney;
				case CAN:
					return ForestryItem.canHoney;
				case CAPSULE:
					return ForestryItem.waxCapsuleHoney;
				case REFRACTORY:
					return ForestryItem.refractoryHoney;
				default:
					return null;
			}
		}

		@Override
		public void setProperties(ItemLiquidContainer liquidContainer) {
			if (liquidContainer.getType() != EnumContainerType.BUCKET) {
				liquidContainer.setDrink(Constants.FOOD_HONEY_HEAL, Constants.FOOD_HONEY_SATURATION);
			}
		}
	},
	LEGACY_HONEY {
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
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case BUCKET:
					return ForestryItem.bucketIce;
				case CAN:
					return ForestryItem.canIce;
				case CAPSULE:
					return ForestryItem.waxCapsuleIce;
				case REFRACTORY:
					return ForestryItem.refractoryIce;
				default:
					return null;
			}
		}
	},
	JUICE(new Color(168, 201, 114)) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Override
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case BUCKET:
					return ForestryItem.bucketJuice;
				case CAN:
					return ForestryItem.canJuice;
				case CAPSULE:
					return ForestryItem.waxCapsuleJuice;
				case REFRACTORY:
					return ForestryItem.refractoryJuice;
				default:
					return null;
			}
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
			return Arrays.asList(
					new ItemStack(Items.milk_bucket)
			);
		}
	},
	SEEDOIL(new Color(255, 255, 168), 885, 5000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 2, true);
		}

		@Override
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case BUCKET:
					return ForestryItem.bucketSeedoil;
				case CAN:
					return ForestryItem.canSeedOil;
				case CAPSULE:
					return ForestryItem.waxCapsuleSeedOil;
				case REFRACTORY:
					return ForestryItem.refractorySeedOil;
				default:
					return null;
			}
		}
	},
	SHORT_MEAD(new Color(239, 154, 56), 1000, 1200) {
		@Override
		public String getTag() {
			return "short.mead";
		}

		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Override
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case BUCKET:
					return ForestryItem.bucketShortMead;
				default:
					return null;
			}
		}
	},
	// Vanilla
	WATER(new Color(0x2432ec)) {
		@Override
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case CAN:
					return ForestryItem.canWater;
				case CAPSULE:
					return ForestryItem.waxCapsuleWater;
				case REFRACTORY:
					return ForestryItem.refractoryWater;
				default:
					return null;
			}
		}
	},
	LAVA(new Color(0xfd461f)) {
		@Override
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case CAN:
					return ForestryItem.canLava;
				case REFRACTORY:
					return ForestryItem.refractoryLava;
				default:
					return null;
			}
		}
	},
	// BuildCraft
	FUEL(new Color(0xffff00)) {
		@Override
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case CAN:
					return ForestryItem.canFuel;
				case CAPSULE:
					return ForestryItem.waxCapsuleFuel;
				case REFRACTORY:
					return ForestryItem.refractoryFuel;
				default:
					return null;
			}
		}
	},
	OIL(new Color(0x404040)) {
		@Override
		public ForestryItem getContainerForType(EnumContainerType type) {
			switch (type) {
				case CAN:
					return ForestryItem.canOil;
				case CAPSULE:
					return ForestryItem.waxCapsuleOil;
				case REFRACTORY:
					return ForestryItem.refractoryOil;
				default:
					return null;
			}
		}
	},
	// Railcraft
	CREOSOTE, STEAM,
	// Thermal Expansion
	COAL, PYROTHEUM;

	public static final Fluids[] forestryFluids = {ETHANOL, BIOMASS, GLASS, HONEY, LEGACY_HONEY, ICE, JUICE, MILK, SEEDOIL, SHORT_MEAD};

	private final String tag;
	private final int density, viscosity;
	private final Color color;

	Fluids() {
		this(null);
	}

	Fluids(Color color) {
		this(color, 1000, 1000);
	}

	Fluids(Color color, int density, int viscosity) {
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

	/** FluidBlock and Container registration */
	/**
	 * Add the filled containers for this fluid.
	 * They will be automatically created and registered.
	 */
	public ForestryItem getContainerForType(EnumContainerType type) {
		return null;
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
