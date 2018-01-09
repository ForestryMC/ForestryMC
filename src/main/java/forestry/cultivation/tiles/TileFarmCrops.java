package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class TileFarmCrops extends TilePlanter {
	public TileFarmCrops() {
		super("farmCrops");
	}

	@Override
	public ItemStack[] createGermlingStacks() {
		return new ItemStack[]{
			new ItemStack(Items.WHEAT_SEEDS),
			new ItemStack(Items.POTATO),
			new ItemStack(Items.CARROT),
			new ItemStack(Items.BEETROOT_SEEDS)
		};
	}

	@Override
	public ItemStack[] createResourceStacks() {
		return new ItemStack[]{
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.DIRT)
		};
	}

	@Override
	public ItemStack[] createProductionStacks() {
		return new ItemStack[]{
			new ItemStack(Items.WHEAT),
			new ItemStack(Items.POTATO),
			new ItemStack(Items.CARROT),
			new ItemStack(Items.BEETROOT)
		};
	}
}
