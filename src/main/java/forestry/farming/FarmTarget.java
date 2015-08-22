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
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.core.vect.MutableVect;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;
import forestry.farming.gadgets.StructureLogicFarm;
import forestry.farming.logic.FarmLogic;

public class FarmTarget {

	private final Vect start;
	private final EnumFacing direction;
	private final int limit;

	private int yOffset;
	private int extent;

	public FarmTarget(Vect start, EnumFacing direction, int limit) {
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

	public EnumFacing getDirection() {
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
			Vect soilPosition = new Vect(position.x, position.y + 1, position.z);
			if (!StructureLogicFarm.bricks.contains(platform) || !FarmLogic.canBreakSoil(world, soilPosition)) {
				break;
			}
			position.add(getDirection());
		}

		yOffset = platformPosition.getY() + 1 - getStart().getY();
	}
}
