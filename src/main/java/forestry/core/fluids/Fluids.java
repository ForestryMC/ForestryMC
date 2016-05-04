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
import javax.annotation.Nullable;
import java.awt.Color;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import forestry.core.items.DrinkProperties;
import forestry.core.items.EnumContainerType;
import forestry.core.proxy.Proxies;
import forestry.core.render.ForestryResource;

public enum Fluids {

	BIO_ETHANOL(new Color(255, 111, 0), 790, 1000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 300, true);
		}

		@Nonnull
		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
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

		@Nonnull
		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
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
	},
	FOR_HONEY(new Color(255, 196, 35), 1420, 73600) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Nonnull
		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}

		@Override
		public DrinkProperties getDrinkProperties() {
			return new DrinkProperties(2, 0.2f, 64);
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

		@Nonnull
		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
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

		@Nonnull
		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}

		@Override
		public DrinkProperties getDrinkProperties() {
			return new DrinkProperties(2, 0.2f, 32);
		}
	},
	MILK(new Color(255, 255, 255), 1030, 3000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}

		@Nonnull
		@Override
		public List<ItemStack> getOtherContainers() {
			return Collections.singletonList(
					new ItemStack(Items.MILK_BUCKET)
			);
		}
	},
	SEED_OIL(new Color(255, 255, 168), 885, 5000) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this, 2, true);
		}

		@Nonnull
		@Override
		public EnumSet<EnumContainerType> getContainerTypes() {
			return EnumSet.of(
					EnumContainerType.CAN,
					EnumContainerType.CAPSULE,
					EnumContainerType.REFRACTORY
			);
		}
	},
	SHORT_MEAD(new Color(239, 154, 56), 1000, 1200) {
		@Override
		public Block makeBlock() {
			return new BlockForestryFluid(this);
		}
	},
	// Vanilla
	WATER(new Color(0x2432ec)) {
		@Nonnull
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
		@Nonnull
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
		@Nonnull
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
		@Nonnull
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

	public static final Fluids[] FORESTRY_FLUIDs = {BIO_ETHANOL, BIOMASS, GLASS, FOR_HONEY, ICE, JUICE, MILK, SEED_OIL, SHORT_MEAD};

	private static final Map<String, Fluids> tagToFluid = new HashMap<>();

	static {
		for (Fluids fluidDefinition : Fluids.values()) {
			tagToFluid.put(fluidDefinition.getTag(), fluidDefinition);
		}
	}

	private final String tag;
	private final int density, viscosity;
	@Nonnull
	private final Color color;

	private final ResourceLocation[] resources = new ResourceLocation[2];

	Fluids(@Nonnull Color color) {
		this(color, 1000, 1000);
	}

	Fluids(@Nonnull Color color, int density, int viscosity) {
		this.tag = name().toLowerCase(Locale.ENGLISH).replace('_', '.');
		this.color = color;
		this.density = density;
		this.viscosity = viscosity;

		resources[0] = new ForestryResource("blocks/liquid/" + getTag() + "_still");
		if (flowTextureExists()) {
			resources[1] = new ForestryResource("blocks/liquid/" + getTag() + "_flow");
		}
	}

	public int getTemperature() {
		return 295;
	}

	public final String getTag() {
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
				Fluids fluidDefinition = tagToFluid.get(fluid.getName());
				if (fluidDefinition != null) {
					return fluidDefinition.getColor();
				}
			}
		}

		return Fluids.WATER.getColor();
	}

	/** FluidBlock and Container registration */
	@Nonnull
	public EnumSet<EnumContainerType> getContainerTypes() {
		return EnumSet.noneOf(EnumContainerType.class);
	}

	/**
	 * Add non-forestry containers for this fluid.
	 */
	@Nonnull
	public List<ItemStack> getOtherContainers() {
		return Collections.emptyList();
	}

	/**
	 * Create a FluidBlock for this fluid.
	 */
	@Nullable
	public Block makeBlock() {
		return null;
	}

	/**
	 * Get the properties for an ItemLiquidContainer before it gets registered.
	 */
	@Nullable
	public DrinkProperties getDrinkProperties() {
		return null;
	}
	
	public boolean flowTextureExists() {
		if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
			return true;
		}
		try {
			ResourceLocation resourceLocation = new ForestryResource("blocks/liquid/" + getTag() + "_flow");
			IResourceManager resourceManager = Proxies.common.getClientInstance().getResourceManager();
			return resourceManager.getResource(resourceLocation) != null;
		} catch (IOException e) {
			return false;
		}
	}

	public ResourceLocation[] getResources() {
		return resources;
	}
}
