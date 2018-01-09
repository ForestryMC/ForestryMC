package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class TileFarmMushroom extends TilePlanter {
	public TileFarmMushroom() {
		super("farmShroom");
	}

	@Override
	public ItemStack[] createGermlingStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.RED_MUSHROOM),
			new ItemStack(Blocks.BROWN_MUSHROOM),
			new ItemStack(Blocks.BROWN_MUSHROOM),
			new ItemStack(Blocks.RED_MUSHROOM)
		};
	}

	@Override
	public ItemStack[] createResourceStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.MYCELIUM),
			new ItemStack(Blocks.DIRT, 1, 2),
			new ItemStack(Blocks.DIRT, 1, 2),
			new ItemStack(Blocks.MYCELIUM)
		};
	}

	@Override
	public ItemStack[] createProductionStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.RED_MUSHROOM),
			new ItemStack(Blocks.BROWN_MUSHROOM),
			new ItemStack(Blocks.BROWN_MUSHROOM),
			new ItemStack(Blocks.RED_MUSHROOM)
		};
	}
}
