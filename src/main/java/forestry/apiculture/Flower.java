package forestry.apiculture;

import forestry.core.utils.StackUtils;
import net.minecraft.item.ItemStack;

final class Flower implements Comparable<Flower> {

	public final ItemStack item;
	public final Double weight;
	public final boolean isPlantable;

	public Flower(ItemStack item, double weight, boolean isPlantable) {
		this.item = item;
		this.weight = weight;
		this.isPlantable = isPlantable;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Flower))
			return false;
		return StackUtils.isIdenticalItem(this.item, ((Flower) obj).item) && this.isPlantable == ((Flower) obj).isPlantable;
	}

	@Override
	public int compareTo(Flower other) {
		return this.weight.compareTo(other.weight);
	}
}
