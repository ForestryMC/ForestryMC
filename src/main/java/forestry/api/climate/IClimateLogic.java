/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.function.BiFunction;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;

public interface IClimateLogic extends INbtReadable, INbtWritable, IClimateTransformer {
	/**
	 * @return The parent of this container.
	 */
	IClimateHousing getHousing();

	/**
	 * Update the climate in a region.
	 */
	void update();

	void onRemoval();

	/**
	 * Sets the targeted state of this logic.
	 */
	void setTarget(IClimateState target);

	void setState(IClimateState state);

	float getResourceModifier();

	float getChangeModifier();

	float getRangeModifier();

	LogicInfo createInfo();

	IClimateManipulator createManipulator(ClimateType type, BiFunction<ClimateType, LogicInfo, Float> changeSupplier);

	default World getWorldObj() {
		return getHousing().getWorldObj();
	}

	@Override
	default BlockPos getCoordinates() {
		return getHousing().getCoordinates();
	}
}
