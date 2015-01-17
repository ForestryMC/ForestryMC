package forestry.apiculture;

import forestry.core.utils.StackUtils;
import net.minecraft.item.ItemStack;

final class Flower implements Comparable<Flower> {

	public final ItemStack Item;
	public final Double Weight;
	public final boolean isPlantable;

	public Flower(ItemStack item, double weight, boolean isPlantable) {
		this.Item = item;
		this.Weight = weight;
		this.isPlantable = isPlantable;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Flower))
			return false;
		return StackUtils.isIdenticalItem(this.Item, ((Flower) obj).Item) && this.isPlantable == ((Flower) obj).isPlantable;
	}

	@Override
	public int compareTo(Flower other) {
		return this.Weight.compareTo(other.Weight);
	}
}