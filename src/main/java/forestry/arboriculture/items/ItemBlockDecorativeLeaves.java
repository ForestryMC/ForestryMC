package forestry.arboriculture.items;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemBlockForestry;

public class ItemBlockDecorativeLeaves extends ItemBlockForestry<BlockDecorativeLeaves> implements IColoredItem {
	public ItemBlockDecorativeLeaves(BlockDecorativeLeaves block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemStack) {
		int meta = itemStack.getMetadata();
		BlockDecorativeLeaves block = getBlock();
		TreeDefinition treeDefinition = block.getTreeType(meta);
		String unlocalizedSpeciesName = treeDefinition.getUnlocalizedName();
		return ItemBlockLeaves.getDisplayName(unlocalizedSpeciesName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack itemStack, int renderPass) {
		int meta = itemStack.getMetadata();
		BlockDecorativeLeaves block = getBlock();
		TreeDefinition treeDefinition = block.getTreeType(meta);

		ITreeGenome genome = treeDefinition.getGenome();

		if (renderPass == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruitProvider fruitProvider = genome.getFruitProvider();
			return fruitProvider.getDecorativeColor();
		}
		return genome.getPrimary().getLeafSpriteProvider().getColor(false);
	}
}
