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
package forestry.apiculture.flowers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import net.minecraft.block.BlockState;

public final class Flower {

	private final BlockState blockState;
	private Double weight;

	public Flower(BlockState blockState, double weight) {
		this.blockState = blockState;
		this.weight = weight;
	}

	public boolean isPlantable() {
		return this.weight != 0.0;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Flower)) {
			return false;
		}

		Flower flower = (Flower) obj;

		return this.blockState == flower.getBlockState();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(blockState);
	}

	public BlockState getBlockState() {
		return blockState;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("blockState", blockState).add("weight", weight).toString();
	}

}
