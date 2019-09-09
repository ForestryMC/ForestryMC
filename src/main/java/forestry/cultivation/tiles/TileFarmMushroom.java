package forestry.cultivation.tiles;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import forestry.cultivation.ModuleCultivation;
import forestry.farming.logic.ForestryFarmIdentifier;

public class TileFarmMushroom extends TilePlanter {
	public TileFarmMushroom() {
		super(ModuleCultivation.getTiles().MUSHROOM, ForestryFarmIdentifier.SHROOM);
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
			new ItemStack(Blocks.PODZOL),
			new ItemStack(Blocks.PODZOL),
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
