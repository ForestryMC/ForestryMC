package forestry.arboriculture.genetics;

import net.minecraft.world.item.ItemStack;

import forestry.api.arboriculture.ILeafProvider;
import forestry.api.core.IItemProvider;
import forestry.arboriculture.features.ArboricultureBlocks;

public class LeafProvider implements ILeafProvider {

	private final TreeDefinition definition;

	public LeafProvider(TreeDefinition definition) {
		this.definition = definition;
	}

	@Override
	public ItemStack getDecorativeLeaves() {
		return ArboricultureBlocks.LEAVES_DECORATIVE
				.getProbably(definition)
				.map(IItemProvider::stack)
				.orElse(ItemStack.EMPTY);
	}

}