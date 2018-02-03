package forestry.farming.multiblock;

import forestry.api.farming.IFarmInventory;

public interface IFarmInventoryInternal extends IFarmInventory {

	int getFertilizerValue();

	boolean useFertilizer();
}
