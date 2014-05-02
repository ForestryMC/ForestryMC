/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.utils;

import java.util.Locale;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Fluids {

    WATER, LAVA, FUEL, BIOFUEL, CREOSOTE, STEAM;

    public String getTag() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public Fluid get() {
        return FluidRegistry.getFluid(getTag());
    }

    public FluidStack get(int qty) {
        return FluidRegistry.getFluidStack(getTag(), qty);
    }

    public boolean is(Fluid fluid) {
        return get() == fluid;
    }

    public boolean is(FluidStack fluidStack) {
        return fluidStack != null && get() == fluidStack.getFluid();
    }

    public boolean isContained(ItemStack containerStack) {
        return containerStack != null && LiquidHelper.containsFluid(containerStack, Fluids.WATER.get());
    }

    public static boolean areEqual(Fluid fluid, FluidStack fluidStack){
        if(fluidStack != null && fluid == fluidStack.getFluid())
            return true;
        return fluid == null && fluidStack == null;
    }
}
