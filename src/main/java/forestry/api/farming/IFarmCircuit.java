package forestry.api.farming;

import forestry.api.circuits.ICircuit;

public interface IFarmCircuit extends ICircuit {

	IFarmLogic getFarmLogic();
}
