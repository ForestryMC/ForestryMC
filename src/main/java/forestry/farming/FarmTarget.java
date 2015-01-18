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

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.vect.MutableVect;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;
import forestry.farming.gadgets.StructureLogicFarm;

public class FarmTarget {

	private final Vect start;
	private final ForgeDirection direction;
	private final int limit;

	private int yOffset;
	private int extent;

	public FarmTarget(Vect start, ForgeDirection direction, int limit) {
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

	public int getLimit() {
		return limit;
	}

	public int getExtent() {
		return extent;
	}

	public ForgeDirection getDirection() {
		return direction;
	}

	public void setExtentAndYOffset(World world) {
		Vect groundPosition = getGroundPosition(world);
		if (groundPosition == null) {
			extent = 0;
			return;
		}

		MutableVect position = new MutableVect(groundPosition);
		for (extent = 0; extent < getLimit(); extent++) {
			Block ground = VectUtil.getBlock(world, position);
			if (!StructureLogicFarm.bricks.contains(ground)) {
				break;
			}
			position.add(getDirection());
		}

		yOffset = groundPosition.getY() + 1 - getStart().getY();
	}

	private Vect getGroundPosition(World world) {
		for (int yOffset = 2; yOffset > -3; yOffset--) {
			Vect position = getStart().add(0, yOffset, 0);
			Block ground = VectUtil.getBlock(world, position);
			if (StructureLogicFarm.bricks.contains(ground)) {
				return position;
			}
		}
		return null;
	}
}
