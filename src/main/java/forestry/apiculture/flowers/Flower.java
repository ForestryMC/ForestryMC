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

import com.google.common.base.Objects;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.genetics.IFlower;

final class Flower implements IFlower {

	private final Block block;
	private final int meta;
	private Double weight;

	public Flower(Block block, int meta, double weight) {
		this.block = block;
		this.meta = meta;
		this.weight = weight;
	}

	public boolean isPlantable() {
		return this.weight != 0.0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IFlower)) {
			return false;
		}

		IFlower flower = (IFlower) obj;

		return Block.isEqualTo(this.block, flower.getBlock()) && (this.meta == OreDictionary.WILDCARD_VALUE || flower.getMeta() == OreDictionary.WILDCARD_VALUE || this.meta == flower.getMeta());
	}

	@Override
	public int compareTo(@Nonnull IFlower other) {
		return this.weight.compareTo(other.getWeight());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(block);
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public int getMeta() {
		return meta;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("block", block).add("meta", meta).add("weight", weight).toString();
	}

}
