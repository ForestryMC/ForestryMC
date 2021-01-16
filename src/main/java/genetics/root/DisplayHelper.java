package genetics.root;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IDisplayHelper;
import genetics.api.root.IIndividualRoot;

public class DisplayHelper<I extends IIndividual> implements IDisplayHelper<I> {
	private final IIndividualRoot<I> root;
	private final Table<IOrganismType, String, ItemStack> iconStacks = HashBasedTable.create();

	public DisplayHelper(IIndividualRoot<I> root) {
		this.root = root;
	}

	@Override
	public String getLocalizedShortName(IChromosomeType chromosomeType) {
		return I18n.format(getTranslationKeyShort(chromosomeType));
	}

	@Override
	public String getTranslationKeyShort(IChromosomeType chromosomeType) {
		return "chromosome." + chromosomeType.getName() + ".short";
	}

	@Override
	public String getLocalizedName(IChromosomeType chromosomeType) {
		return I18n.format(getTranslationKey(chromosomeType));
	}

	@Override
	public String getTranslationKey(IChromosomeType chromosomeType) {
		return "chromosome." + chromosomeType.getName();
	}

	@Override
	public ItemStack getDisplayStack(IAlleleSpecies species, IOrganismType type) {
		String registryName = species.getRegistryName().toString();
		ItemStack stack = iconStacks.get(type, registryName);
		if (stack == null) {
			stack = root.createStack(species, type);
			iconStacks.put(type, registryName, stack);
		}
		return stack;
	}

	@Override
	public ItemStack getDisplayStack(IAlleleSpecies species) {
		return getDisplayStack(species, root.getTypes().getDefaultType());
	}
}
