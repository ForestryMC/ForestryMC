package forestry.core.genetics;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesDisplayHelper;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;

public class SpeciesDisplayHelper implements ISpeciesDisplayHelper {
	private final Table<ISpeciesType, String, ItemStack> iconStacks = HashBasedTable.create();
	private final ISpeciesRoot root;

	public SpeciesDisplayHelper(ISpeciesRoot root) {
		this.root = root;
		ISpeciesType type = root.getIconType();
		for (IIndividual individual : root.getIndividualTemplates()) {
			ItemStack itemStack = root.getMemberStack(individual, type);
			iconStacks.put(type, individual.getGenome().getPrimary().getUID(), itemStack);
		}
	}

	@Override
	public ItemStack getDisplayStack(IAlleleSpecies species, ISpeciesType type) {
		ItemStack stack = iconStacks.get(type, species.getUID());
		if (stack == null) {
			stack = root.getMemberStack(species, type);
			iconStacks.put(type, species.getUID(), stack);
		}
		return stack;
	}

	@Override
	public ItemStack getDisplayStack(IAlleleSpecies species) {
		return getDisplayStack(species, root.getIconType());
	}
}
