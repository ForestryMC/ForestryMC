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
package forestry.farming;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.core.vect.MutableVect;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;
import forestry.farming.gadgets.StructureLogicFarm;

public class FarmTarget {

	private final Vect start;
	private final FarmDirection direction;
	private final int limit;

	private int yOffset;
	private int extent;

	public FarmTarget(Vect start, FarmDirection direction, int limit) {
		this.start = start;
		this.direction = direction;
		this.limit = limit;
	}

	public Vect getStart() {
		return start;
	}

	public int getYOffset() {
		return this.yOffset;
	}

	public int getExtent() {
		return extent;
	}

	public FarmDirection getDirection() {
		return direction;
	}

	public void setExtentAndYOffset(World world, Vect platformPosition) {
		if (platformPosition == null) {
			extent = 0;
			return;
		}

		MutableVect position = new MutableVect(platformPosition);
		for (extent = 0; extent < limit; extent++) {
			Block platform = VectUtil.getBlock(world, position);
			if (!StructureLogicFarm.bricks.contains(platform)) {
				break;
			}
			position.add(getDirection().getForgeDirection());
		}

		yOffset = platformPosition.getY() + 1 - getStart().getY();
	}
}
