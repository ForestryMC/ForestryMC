package forestry.core.genetics.alleles;

import forestry.api.genetics.IAlleleArea;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleFactory;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IFlowerProvider;
import forestry.apiculture.genetics.AlleleFlowers;
import net.minecraft.util.BlockPos;

public class AlleleFactory implements IAlleleFactory {
	@Override
	public IAlleleFlowers createFlowers(String modId, String category, String name, IFlowerProvider flowerProvider, boolean isDominant) {
		return new AlleleFlowers(modId, category, name, flowerProvider, isDominant);
	}

	@Override
	public IAlleleFloat createFloat(String modId, String category, String name, float value, boolean isDominant) {
		return new AlleleFloat(modId, category, name, value, isDominant);
	}

	@Override
	public IAlleleArea createArea(String modId, String category, String name, BlockPos posDim, boolean isDominant) {
		return new AlleleArea(modId, category, name, posDim, isDominant);
	}

	@Override
	public IAlleleInteger createInteger(String modId, String category, String valueName, int value, boolean isDominant) {
		return new AlleleInteger(modId, category, valueName, value, isDominant);
	}

	@Override
	public IAlleleBoolean createBoolean(String modId, String category, boolean value, boolean isDominant) {
		return new AlleleBoolean(modId, category, value, isDominant);
	}
}
