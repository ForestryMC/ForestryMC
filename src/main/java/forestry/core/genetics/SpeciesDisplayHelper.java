package forestry.core.genetics;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.minecraft.item.ItemStack;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;

import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.ISpeciesDisplayHelper;


public class SpeciesDisplayHelper implements ISpeciesDisplayHelper {
	private final Table<IOrganismType, String, ItemStack> iconStacks = HashBasedTable.create();
	private final IForestrySpeciesRoot<IIndividual> root;

	public SpeciesDisplayHelper(IForestrySpeciesRoot<IIndividual> root) {
		this.root = root;
		IOrganismType type = root.getIconType();
		for (IIndividual individual : root.getIndividualTemplates()) {
			ItemStack itemStack = root.getTypes().createStack(individual, type);
			iconStacks.put(type, individual.getGenome().getPrimary().getRegistryName().toString(), itemStack);
		}
	}

	@Override
	public ItemStack getDisplayStack(IAlleleSpecies species, IOrganismType type) {
		ItemStack stack = iconStacks.get(type, species.getRegistryName().toString());
		if (stack == null) {
			stack = root.getTypes().createStack(root.templateAsIndividual(root.getTemplates().getTemplate(species.getRegistryName().toString())), type);
			iconStacks.put(type, species.getRegistryName().toString(), stack);
		}
		return stack;
	}

	@Override
	public ItemStack getDisplayStack(IAlleleSpecies species) {
		return getDisplayStack(species, root.getIconType());
	}
}
