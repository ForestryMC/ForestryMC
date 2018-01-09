package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class TileFarmGourd extends TilePlanter {
	public TileFarmGourd() {
		super("farmGourd");
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
		return new ItemStack[]{
			new ItemStack(Blocks.MELON_BLOCK),
			new ItemStack(Blocks.PUMPKIN),
			new ItemStack(Blocks.PUMPKIN),
			new ItemStack(Blocks.MELON_BLOCK)
		};
	}
}
