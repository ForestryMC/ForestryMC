package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class TileFarmGourd extends TilePlanter {
	public TileFarmGourd() {
		super("farmGourd");
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
		return createList(
				new ItemStack(Blocks.MELON_BLOCK),
				new ItemStack(Blocks.PUMPKIN),
				new ItemStack(Blocks.PUMPKIN),
				new ItemStack(Blocks.MELON_BLOCK)
		);
	}
}
