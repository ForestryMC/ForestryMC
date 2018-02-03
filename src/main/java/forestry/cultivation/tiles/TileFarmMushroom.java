package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class TileFarmMushroom extends TilePlanter {
	public TileFarmMushroom() {
		super("farmShroom");
	}

	@Override
	public NonNullList<ItemStack> createGermlingStacks() {
		return createList(
			new ItemStack(Blocks.RED_MUSHROOM),
			new ItemStack(Blocks.BROWN_MUSHROOM),
			new ItemStack(Blocks.BROWN_MUSHROOM),
			new ItemStack(Blocks.RED_MUSHROOM)
		);
	}

	@Override
	public NonNullList<ItemStack> createResourceStacks() {
		return createList(
			new ItemStack(Blocks.MYCELIUM),
			new ItemStack(Blocks.DIRT, 1, 2),
			new ItemStack(Blocks.DIRT, 1, 2),
			new ItemStack(Blocks.MYCELIUM)
		);
	}

	@Override
	public NonNullList<ItemStack> createProductionStacks() {
		return createList(
			new ItemStack(Blocks.RED_MUSHROOM),
			new ItemStack(Blocks.BROWN_MUSHROOM),
			new ItemStack(Blocks.BROWN_MUSHROOM),
			new ItemStack(Blocks.RED_MUSHROOM)
		);
	}
}
