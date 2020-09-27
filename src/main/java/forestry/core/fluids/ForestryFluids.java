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

import forestry.core.ModuleFluids;
import forestry.core.config.Constants;
import forestry.core.items.DrinkProperties;
import forestry.modules.features.FeatureFluid;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.ModFeatureRegistry;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;

@FeatureProvider
public enum ForestryFluids {
    BIO_ETHANOL(new Color(255, 111, 0), 790, 1000, 300),
    BIOMASS(new Color(100, 132, 41), 400, 6560, 100),
    GLASS(new Color(164, 164, 164), 2400, 10000, 0),
    HONEY(new Color(255, 196, 35), 1420, 75600) {
        @Override
        public DrinkProperties getDrinkProperties() {
            return new DrinkProperties(2, 0.2f, 64);
        }
    },
    ICE(new Color(175, 242, 255), 520, 1000) {
        @Override
        public int getTemperature() {
            return 265;
        }
    },
    JUICE(new Color(168, 201, 114)) {
        @Override
        public DrinkProperties getDrinkProperties() {
            return new DrinkProperties(2, 0.2f, 32);
        }
    },
    MILK(new Color(255, 255, 255), 1030, 3000) {
        @Override
        public List<ItemStack> getOtherContainers() {
            return Collections.singletonList(
                    new ItemStack(Items.MILK_BUCKET)
            );
        }
    },
    SEED_OIL(new Color(255, 255, 168), 885, 5000, 2),
    SHORT_MEAD(new Color(239, 154, 56), 1000, 1200, 4) {
        @Override
        public DrinkProperties getDrinkProperties() {
            return new DrinkProperties(1, 0.2f, 32);
        }

    };

    private static final Map<ResourceLocation, ForestryFluids> tagToFluid = new HashMap<>();

    static {
        for (ForestryFluids fluidDefinition : ForestryFluids.values()) {
            tagToFluid.put(
                    new ResourceLocation(Constants.MOD_ID, fluidDefinition.feature.getIdentifier()),
                    fluidDefinition
            );
        }
    }

    private final ResourceLocation tag;
    private final FeatureFluid feature;

    ForestryFluids(Color particleColor) {
        this(particleColor, 1000, 1000);
    }

    ForestryFluids(Color particleColor, int density, int viscosity) {
        this(particleColor, density, viscosity, -1);
    }

    ForestryFluids(Color particleColor, int density, int viscosity, int flammability) {
        this.feature = ModFeatureRegistry.get(ModuleFluids.class)
                                         .fluid(name().toLowerCase(Locale.ENGLISH))
                                         .flammability(flammability)
                                         .viscosity(viscosity)
                                         .density(density)
                                         .temperature(getTemperature())
                                         .particleColor(particleColor)
                                         .create();
        this.tag = new ResourceLocation(Constants.MOD_ID, feature.getIdentifier());
    }

    public int getTemperature() {
        return 295;
    }

    public final ResourceLocation getTag() {
        return tag;
    }

    public FeatureFluid getFeature() {
        return feature;
    }

    //@Nullable
    public final Fluid getFluid() {
        return feature.fluid();
    }

    public final FluidStack getFluid(int mb) {
        Fluid fluid = getFluid();
        if (fluid == Fluids.EMPTY) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(fluid, mb);
    }

    public final Color getParticleColor() {
        return feature.getProperties().particleColor;
    }

    public final boolean is(Fluid fluid) {
        return getFluid() == fluid;
    }

    public final boolean is(FluidStack fluidStack) {
        return getFluid() == fluidStack.getFluid();
    }

    public static boolean areEqual(Fluid fluid, FluidStack fluidStack) {
        return fluid == fluidStack.getFluid();
    }

    @Nullable
    public static ForestryFluids getFluidDefinition(Fluid fluid) {
        if (fluid instanceof ForestryFluid) {
            return tagToFluid.get(fluid.getRegistryName());
        }

        return null;
    }

    @Nullable
    public static ForestryFluids getFluidDefinition(FluidStack fluidStack) {
        if (!fluidStack.isEmpty()) {
            return getFluidDefinition(fluidStack.getFluid());
        }

        return null;
    }

    /**
     * Add non-forestry containers for this fluid.
     */
    public List<ItemStack> getOtherContainers() {
        return Collections.emptyList();
    }

    /**
     * Get the properties for an ItemFluidContainerForestry before it gets registered.
     */
    @Nullable
    public DrinkProperties getDrinkProperties() {
        return feature.getProperties().properties;
    }
}
