package forestry.arboriculture.genetics;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import genetics.api.alleles.IAllele;

import forestry.api.arboriculture.ILeafProvider;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.core.IItemProvider;
import forestry.arboriculture.features.ArboricultureBlocks;

public class LeafProvider implements ILeafProvider {

    @Nullable
    private IAlleleTreeSpecies treeSpecies = null;

    @Override
    public void init(IAlleleTreeSpecies treeSpecies) {
        this.treeSpecies = treeSpecies;
    }

    @Override
    public ItemStack getDecorativeLeaves() {
        IAllele allele = treeSpecies;
        if (allele == null) {
            allele = TreeDefinition.Oak.getTemplate().get(TreeChromosomes.SPECIES);
        }
        return ArboricultureBlocks.LEAVES_DECORATIVE.findFeature(allele.getRegistryName().toString()).map(IItemProvider::stack).orElse(ItemStack.EMPTY);
    }

}