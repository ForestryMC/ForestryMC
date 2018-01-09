package forestry.cultivation.tiles;

import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmLogic;

public class TilePlantation extends TilePlanter {
	public TilePlantation(IFarmLogic logic) {
		super(logic);
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
