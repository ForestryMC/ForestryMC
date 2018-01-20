package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TileFarmEnder extends TilePlanter {
	public TileFarmEnder() {
		super("farmEnder");
	}

	@Override
	public ItemStack[] createGermlingStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Blocks.CHORUS_FLOWER)
		};
	}

	@Override
	public ItemStack[] createResourceStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.END_STONE),
			new ItemStack(Blocks.END_STONE),
			new ItemStack(Blocks.END_STONE),
			new ItemStack(Blocks.END_STONE)
		};
	}

	@Override
	public ItemStack[] createProductionStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Items.CHORUS_FRUIT),
			new ItemStack(Items.CHORUS_FRUIT),
			new ItemStack(Blocks.CHORUS_FLOWER)
		};
	}
}
