package forestry.api.apiculture;

import net.minecraft.util.math.Vec3i;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IGenomeWrapper;

/**
 * A genome wrapper that handles bee chromosomes.
 *
 * @since Forestry 5.8
 */
public interface IBeeGenomeWrapper extends IGenomeWrapper<EnumBeeChromosome> {

	@Override
	IAlleleBeeSpecies getPrimary();

	@Override
	IAlleleBeeSpecies getSecondary();

	float getSpeed();

	int getLifespan();

	int getFertility();

	EnumTolerance getToleranceTemp();

	EnumTolerance getToleranceHumid();

	boolean getNeverSleeps();

	boolean getToleratesRain();

	boolean getCaveDwelling();

	IFlowerProvider getFlowerProvider();

	int getFlowering();

	Vec3i getTerritory();

	IAlleleBeeEffect getEffect();
}
