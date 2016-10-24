/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraftforge.common.EnumPlantType;

/**
 * Used to check for pollination traits without altering the world by changing vanilla leaves to forestry ones.
 * For a normal pollinatable that can be mated, see {@link IPollinatable}.
 */
public interface ICheckPollinatable {

	/**
	 * @return plant type this pollinatable is classified as.
	 * (Can be used by bees to determine whether to interact or not.)
	 */
	EnumPlantType getPlantType();

	/**
	 * @return IIndividual containing the genetic information of this IPollinatable
	 */
	IIndividual getPollen();

	/**
	 * Checks whether this can mate with the given pollen.
	 *
	 * Must be the one to check genetic equivalency.
	 *
	 * @param pollen IIndividual representing the pollen.
	 * @return true if mating is possible, false otherwise.
	 */
	boolean canMateWith(IIndividual pollen);

	/**
	 * @return true if this has already been pollinated.
	 */
	boolean isPollinated();
}
