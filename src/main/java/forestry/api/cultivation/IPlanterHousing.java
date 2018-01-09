package forestry.api.cultivation;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.IFarmInventory;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.api.genetics.IHousing;

public interface IPlanterHousing extends IHousing {
	/**
	 * Callback for {@link IFarmLogic}s to plant a sapling, seed, germling, stem.
	 * Will remove the appropriate germling from the farm's inventory.
	 * It's up to the logic to only call this on a valid location.
	 *
	 * @return true if planting was successful, false otherwise.
	 */
	boolean plantGermling(IFarmable germling, World world, BlockPos pos);

	/* INTERACTION WITH HATCHES */
	IFarmInventory getFarmInventory();

	/* LOGIC */
	IFarmLogic getFarmLogic();

	/* GUI */
	int getStoredFertilizerScaled(int scale);
}
