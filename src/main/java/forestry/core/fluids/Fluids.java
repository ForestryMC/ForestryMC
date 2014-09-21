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

import java.util.Locale;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Fluids {

    WATER, LAVA, FUEL, BIOFUEL, CREOSOTE, STEAM, BIOETHANOL, COAL, PYROTHEUM;
    private final String tag;

    private Fluids() {
        tag = name().toLowerCase(Locale.ENGLISH);
    }

    public String getTag() {
        return tag;
    }

    public Fluid get() {
        return FluidRegistry.getFluid(tag);
    }

    /**
     * Gets a FluidStack filled with qty milliBuckets worth of Fluid.
     *
     * @param qty
     * @return
     */
    public FluidStack get(int qty) {
        return FluidRegistry.getFluidStack(tag, qty);
    }

    /**
     * Gets a FluidStack filled with n buckets worth of Fluid.
     *
     * @param n
     * @return
     */
    public FluidStack getB(int n) {
        return FluidRegistry.getFluidStack(tag, n * FluidContainerRegistry.BUCKET_VOLUME);
    }

    public boolean is(Fluid fluid) {
        return get() == fluid;
    }

    public boolean is(FluidStack fluidStack) {
        return fluidStack != null && get() == fluidStack.getFluid();
    }

    public boolean isContained(ItemStack containerStack) {
        return containerStack != null && FluidHelper.containsFluid(containerStack, get());
    }

    public static boolean areEqual(Fluid fluid, FluidStack fluidStack) {
        if (fluidStack != null && fluid == fluidStack.getFluid())
            return true;
        return fluid == null && fluidStack == null;
    }

}
