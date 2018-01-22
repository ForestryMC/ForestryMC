package forestry.cultivation.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class TilePlantation extends TilePlanter {

	public TilePlantation() {
		super("farmRubber");
	}

	@Override
	public NonNullList<ItemStack> createGermlingStacks() {
		return NonNullList.create();
	}

	@Override
	public NonNullList<ItemStack> createResourceStacks() {
		return NonNullList.create();
	}

	@Override
	public NonNullList<ItemStack> createProductionStacks() {
		return NonNullList.create();
	}
}
