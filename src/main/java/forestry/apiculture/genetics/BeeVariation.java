package forestry.apiculture.genetics;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeChromosome;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.IAllele;
import forestry.core.genetics.alleles.AlleleHelper;

public abstract class BeeVariation implements IBeeDefinition {

	private final ImmutableMap<BeeChromosome, IAllele> template;
	private final IBeeGenome genome;

	private BeeVariation(IBeeDefinition bee) {
		Map<BeeChromosome, IAllele> templateBuilder = new EnumMap<>(bee.getTemplate());
		initializeTemplate(templateBuilder);
		template = ImmutableMap.copyOf(templateBuilder);
		genome = BeeManager.beeRoot.templateAsGenome(template);
	}

	protected abstract void initializeTemplate(@Nonnull Map<BeeChromosome, IAllele> template);

	@Override
	public ImmutableMap<BeeChromosome, IAllele> getTemplate() {
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
		protected void initializeTemplate(@Nonnull Map<BeeChromosome, IAllele> template) {
			AlleleHelper.instance.set(template, BeeChromosome.TOLERANT_FLYER, true);
		}
	}
}
