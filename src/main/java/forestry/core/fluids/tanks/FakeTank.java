/*
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at railcraft.wikispaces.com.
 */
package mods.railcraft.common.fluids.tanks;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class FakeTank extends FluidTank {

    public static final IFluidTank INSTANCE = new FakeTank();
    public static final IFluidTank[] ARRAY = new IFluidTank[]{INSTANCE};
    public static final FluidTankInfo[] INFO = new FluidTankInfo[]{INSTANCE.getInfo()};

    private FakeTank() {
        super(1);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return null;
    }

}
