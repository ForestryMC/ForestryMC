package forestry.arboriculture.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.core.ItemGroups;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.definitions.IColoredItem;

import genetics.api.individual.IGenome;

public class ItemBlockDecorativeLeaves extends ItemBlockForestry<BlockDecorativeLeaves> implements IColoredItem {
	public ItemBlockDecorativeLeaves(BlockDecorativeLeaves block) {
		super(block, new Item.Properties().tab(ItemGroups.tabArboriculture));
	}

	@Override
	public Component getName(ItemStack itemStack) {
		BlockDecorativeLeaves block = getBlock();
		TreeDefinition treeDefinition = block.getDefinition();
		return ItemBlockLeaves.getDisplayName(treeDefinition.createIndividual());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		BlockDecorativeLeaves block = getBlock();
		TreeDefinition treeDefinition = block.getDefinition();

		IGenome genome = treeDefinition.getGenome();

		if (renderPass == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			return fruitProvider.getDecorativeColor();
		}
		return genome.getActiveAllele(TreeChromosomes.SPECIES).getLeafSpriteProvider().getColor(false);
	}
}
