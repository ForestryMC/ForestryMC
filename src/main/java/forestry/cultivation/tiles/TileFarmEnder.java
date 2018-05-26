package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import forestry.farming.logic.ForestryFarmIdentifier;

public class TileFarmEnder extends TilePlanter {
	public TileFarmEnder() {
		super(ForestryFarmIdentifier.ENDER);
	}

	@Override
	public NonNullList<ItemStack> createGermlingStacks() {
		return createList(
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Blocks.CHORUS_FLOWER)
		);
	}

	@Override
	public NonNullList<ItemStack> createResourceStacks() {
		return createList(
			new ItemStack(Blocks.END_STONE),
			new ItemStack(Blocks.END_STONE),
			new ItemStack(Blocks.END_STONE),
			new ItemStack(Blocks.END_STONE)
		);
	}

	@Override
	public NonNullList<ItemStack> createProductionStacks() {
		return createList(
			new ItemStack(Blocks.CHORUS_FLOWER),
			new ItemStack(Items.CHORUS_FRUIT),
			new ItemStack(Items.CHORUS_FRUIT),
			new ItemStack(Blocks.CHORUS_FLOWER)
		);
	}
}
