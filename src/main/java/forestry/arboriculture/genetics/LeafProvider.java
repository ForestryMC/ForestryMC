package forestry.arboriculture.genetics;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ILeafProvider;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.PluginArboriculture;
import net.minecraft.item.ItemStack;

public class LeafProvider implements ILeafProvider {
	
	private IAlleleTreeSpecies treeSpecies;
	
	public LeafProvider() {
	}
	
	@Override
	public void init(IAlleleTreeSpecies treeSpecies) {
		this.treeSpecies = treeSpecies;
	}

	@Override
	public ItemStack getDecorativeLeaves() {
		IAllele allele = treeSpecies;
		if(allele == null){
			allele = TreeDefinition.Oak.getTemplate()[EnumTreeChromosome.SPECIES.ordinal()];
		}
		return PluginArboriculture.getBlocks().getDecorativeLeaves(allele.getUID());
	}

}