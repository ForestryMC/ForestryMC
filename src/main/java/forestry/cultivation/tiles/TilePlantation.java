package forestry.cultivation.tiles;

import net.minecraft.item.ItemStack;

public class TilePlantation extends TilePlanter {

	public TilePlantation() {
		super("farmRubber");
	}

	@Override
	public ItemStack[] createGermlingStacks() {
		return new ItemStack[0];
	}

	@Override
	public ItemStack[] createResourceStacks() {
		return new ItemStack[0];
	}

	@Override
	public ItemStack[] createProductionStacks() {
		return new ItemStack[0];
	}
}
