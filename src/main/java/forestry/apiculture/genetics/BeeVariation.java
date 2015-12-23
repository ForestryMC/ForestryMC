package forestry.apiculture.genetics;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.IAllele;
import forestry.core.genetics.alleles.AlleleHelper;

public abstract class BeeVariation implements IBeeDefinition {

	private final IAllele[] template;
	private final IBeeGenome genome;

	protected BeeVariation(IBeeDefinition bee) {
		template = bee.getTemplate();
		initializeTemplate(template);
		genome = BeeManager.beeRoot.templateAsGenome(template);
	}

	protected abstract void initializeTemplate(IAllele[] template);

	@Override
	public IAllele[] getTemplate() {
		return template;
	}

	@Override
	public IBeeGenome getGenome() {
		return genome;
	}

	@Override
	public IBee getIndividual() {
		return new Bee(genome);
	}

	@Override
	public final ItemStack getMemberStack(EnumBeeType beeType) {
		IBee bee = getIndividual();
		return BeeManager.beeRoot.getMemberStack(bee, beeType.ordinal());
	}

	public static class RainResist extends BeeVariation {
		public RainResist(IBeeDefinition bee) {
			super(bee);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			AlleleHelper.instance.set(template, EnumBeeChromosome.TOLERANT_FLYER, true);
		}
	}
}
