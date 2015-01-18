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
package forestry.core.utils;

import net.minecraft.world.chunk.Chunk;

public class ChunkCoords
{
	public int dimension;
	public int xCoord;
	public int zCoord;

	public ChunkCoords(Chunk chunk)
	{
		this.dimension = chunk.worldObj.provider.dimensionId;
		this.xCoord = chunk.xPosition;
		this.zCoord = chunk.zPosition;
	}
}
