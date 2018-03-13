package forestry.lepidopterology;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IFilterRuleType;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IButterfly;
import forestry.sorting.DefaultFilterRuleType;

public enum LepidopterologyFilterRule implements IFilterRule {
	PURE_BREED(DefaultFilterRuleType.PURE_BREED) {
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.isPureBred(EnumButterflyChromosome.SPECIES);
		}
	},
	NOCTURNAL(DefaultFilterRuleType.NOCTURNAL) {
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getNocturnal();
		}
	},
	PURE_NOCTURNAL(DefaultFilterRuleType.PURE_NOCTURNAL) {
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getNocturnal() && butterfly.isPureBred(EnumButterflyChromosome.NOCTURNAL);
		}
	},
	FLYER(DefaultFilterRuleType.FLYER) {
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getTolerantFlyer();
		}
	},
	PURE_FLYER(DefaultFilterRuleType.PURE_FLYER) {
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getTolerantFlyer() && butterfly.isPureBred(EnumButterflyChromosome.TOLERANT_FLYER);
		}
	};

	LepidopterologyFilterRule(IFilterRuleType rule) {
		rule.addLogic(this);
	}

	public static void init() {
	}

	@Override
	public boolean isValid(ItemStack itemStack, IFilterData data) {
		if (!data.isPresent()) {
			return false;
		}
		IIndividual individual = data.getIndividual();
		if (!(individual instanceof IButterfly)) {
			return false;
		}
		return isValid((IButterfly) individual);
	}

	protected boolean isValid(IButterfly butterfly) {
		return false;
	}

	@Override
	public String getRootUID() {
		return ButterflyManager.butterflyRoot.getUID();
	}

}
