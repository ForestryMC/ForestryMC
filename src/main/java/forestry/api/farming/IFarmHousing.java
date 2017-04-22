/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import forestry.api.core.IErrorLogicSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public interface IFarmHousing extends IErrorLogicSource {

	BlockPos getCoords();

	Vec3i getArea();

	Vec3i getOffset();

	/**
	 * @return true if any work was done, false otherwise.
	 */
	boolean doWork();

	boolean hasLiquid(FluidStack liquid);

	void removeLiquid(FluidStack liquid);

	/**
	 * Callback for {@link IFarmLogic}s to plant a sapling, seed, germling, stem.
	 * Will remove the appropriate germling from the farm's inventory.
	 * It's up to the logic to only call this on a valid location.
	 *
	 * @return true if planting was successful, false otherwise.
	 */
	boolean plantGermling(IFarmable farmable, World world, BlockPos pos);

	/* INTERACTION WITH HATCHES */
	IFarmInventory getFarmInventory();

	/* LOGIC */
	void setFarmLogic(FarmDirection direction, IFarmLogic logic);

	void resetFarmLogic(FarmDirection direction);

	IFarmLogic getFarmLogic(FarmDirection direction);

	/* GUI */
	int getStoredFertilizerScaled(int scale);
}
