/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import net.minecraft.util.math.BlockPos;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;

public interface IGreenhouseSettings extends INbtReadable, INbtWritable {
	
	/**
	 * @return the position on that the manager starts to test.
	 */
	BlockPos getStartPosOffset();
	
	void setStartPosOffset(BlockPos offset);
	
	/**
	 * @return the length of the manager on the east and west side
	 */
	int getLengthEastWest();
	
	void setLengthEastWest(int length);
	
	/**
	 * @return the length of the manager on the north and south side
	 */
	int getLengthNorthSouth();
	
	void setLengthNorthSouth(int length);
	
	/**
	 * @return the maximal height of the manager on the y axis.
	 */
	int getHeight();
	
	void setHeight(int height);
	
	/**
	 * @return the maximal depth of the manager on the y axis.
	 */
	int getDepth();
	
	void setDepth(int depth);
	
	/**
	 * @return a array with the length of two. The two positions represent the edges of the greenhouse.
	 */
	Position2D[] getEdges();

	Position2D getMaxEdge();

	Position2D getMinEdge();
	
	IGreenhouseSettings clamp(IGreenhouseSettings clampSettings);
	
	/**
	 *
	 * @param edgeId an {@link Integer} between 0 and 1
	 * @param position the position of this edge
	 */
	void setEdge(int edgeId, Position2D position);
}
