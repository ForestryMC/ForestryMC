package forestry.arboriculture.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.individual.IGenome;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.core.ItemGroups;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemBlockForestry;

public class ItemBlockDecorativeLeaves extends ItemBlockForestry<BlockDecorativeLeaves> implements IColoredItem {
    public ItemBlockDecorativeLeaves(BlockDecorativeLeaves block) {
        super(block, new Item.Properties().group(ItemGroups.tabArboriculture));
    }

    @Override
    public ITextComponent getDisplayName(ItemStack itemStack) {
        BlockDecorativeLeaves block = getBlock();
        TreeDefinition treeDefinition = block.getDefinition();
        String unlocalizedSpeciesName = treeDefinition.getUnlocalizedName();
        return ItemBlockLeaves.getDisplayName(unlocalizedSpeciesName);
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
