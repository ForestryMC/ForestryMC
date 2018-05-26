package forestry.cultivation.tiles;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import forestry.farming.logic.ForestryFarmIdentifier;

public class TileFarmNether extends TilePlanter {
	public TileFarmNether() {
		super(ForestryFarmIdentifier.INFERNAL);
	}

	@Override
	public NonNullList<ItemStack> createGermlingStacks() {
		return createList(
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART)
		);
	}

	@Override
	public NonNullList<ItemStack> createResourceStacks() {
		return createList(
			new ItemStack(Blocks.SOUL_SAND),
			new ItemStack(Blocks.SOUL_SAND),
			new ItemStack(Blocks.SOUL_SAND),
			new ItemStack(Blocks.SOUL_SAND)
		);
	}

	@Override
	public NonNullList<ItemStack> createProductionStacks() {
		return createList(
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART),
			new ItemStack(Items.NETHER_WART)
		);
	}
}
