/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.greenhouse;

/**
 * The limits that a greenhouse has. Used in the {@link IGreenhouseProvider} to test if the greenhouse is tested.
 * At the test the provider stops if the currently tested position is out of this limits and marks the greenhouse as open.
 */
public interface IGreenhouseLimits {

	/**
	 * @return The maximum bounding-box coordinate.
	 */
	Position2D getMaximumCoordinates();

	/**
	 * @return The minimum bounding-box coordinate.
	 */
	Position2D getMinimumCoordinates();

	/**
	 * @return the maximal height of the manager on the y axis.
	 */
	int getHeight();

	/**
	 * @return the maximal depth of the manager on the y axis.
	 */
	int getDepth();
}
