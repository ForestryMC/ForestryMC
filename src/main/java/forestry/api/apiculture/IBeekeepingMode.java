/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBeekeepingMode {

	/**
	 * @return Localized name of this beekeeping mode.
	 */
	String getName();

	/**
	 * @return Localized list of strings outlining the behaviour of this beekeeping mode.
	 */
	List<String> getDescription();

	/**
	 * @return Float used to modify the wear on comb frames.
	 */
	float getWearModifier();

	/**
	 * @return fertility taking into account the birthing queen and surroundings.
	 */
	int getFinalFertility(IBee queen, World world, BlockPos pos);

	/**
	 * @return true if the queen is genetically "fatigued" and should not be reproduced anymore.
	 */
	boolean isFatigued(IBee queen, IBeeHousing housing);

	/**		
	 * @return true if the queen is being overworked in the bee housing (with chance). will trigger a negative effect.		
	 * @deprecated bees max out at 16x productivity instead of being overworked.
	 * TODO remove this method in 1.13
	 */
	@Deprecated
	default boolean isOverworked(IBee queen, IBeeHousing housing) {
		return false;
	}

	/**
	 * @return true if the genetic structure of the queen is breaking down during spawning of the offspring (with chance). will trigger a negative effect.
	 */
	boolean isDegenerating(IBee queen, IBee offspring, IBeeHousing housing);

	/**
	 * @return true if an offspring of this queen is considered a natural
	 */
	boolean isNaturalOffspring(IBee queen);

	/**
	 * @return true if this mode allows the passed queen or princess to be multiplied
	 */
	boolean mayMultiplyPrincess(IBee queen);

	/**
	 * @return the bee modifier for this mode
	 */
	IBeeModifier getBeeModifier();
}
