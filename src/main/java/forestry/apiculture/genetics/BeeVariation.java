package forestry.apiculture.genetics;

import net.minecraft.item.ItemStack;

import genetics.api.alleles.IAlleleTemplate;
import genetics.api.individual.IGenome;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;

public abstract class BeeVariation implements IBeeDefinition {

	private final IAlleleTemplate template;
	private final IGenome genome;

	protected BeeVariation(IBeeDefinition bee) {
		template = initializeTemplate(bee.getTemplate());
		genome = template.toGenome();
	}

	protected abstract IAlleleTemplate initializeTemplate(IAlleleTemplate template);

	@Override
	public IAlleleTemplate getTemplate() {
		return template;
	}

	@Override
	public IGenome getGenome() {
		return genome;
	}

	@Override
	public IBee createIndividual() {
		return template.toIndividual(BeeHelper.getRoot());
	}

	@Override
	public final ItemStack getMemberStack(EnumBeeType beeType) {
		IBee bee = createIndividual();
		return BeeHelper.getRoot().getTypes().createStack(bee, beeType);
	}

	@Override
	public IAlleleBeeSpecies getSpecies() {
		return genome.getActiveAllele(BeeChromosomes.SPECIES);
	}

	public static class RainResist extends BeeVariation {
		public RainResist(IBeeDefinition bee) {
			super(bee);
		}

		@Override
		protected IAlleleTemplate initializeTemplate(IAlleleTemplate template) {
			return template.createBuilder().set(BeeChromosomes.TOLERATES_RAIN, true).build();
		}
	}
}
