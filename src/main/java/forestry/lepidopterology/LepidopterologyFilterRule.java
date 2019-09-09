package forestry.lepidopterology;

import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;

import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IFilterRuleType;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.sorting.DefaultFilterRuleType;

public enum LepidopterologyFilterRule implements IFilterRule {
	PURE_BREED(DefaultFilterRuleType.PURE_BREED) {
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.isPureBred(ButterflyChromosomes.SPECIES);
		}
	},
	NOCTURNAL(DefaultFilterRuleType.NOCTURNAL) {
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getActiveValue(ButterflyChromosomes.NOCTURNAL);
		}
	},
	PURE_NOCTURNAL(DefaultFilterRuleType.PURE_NOCTURNAL) {
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getActiveValue(ButterflyChromosomes.NOCTURNAL) && butterfly.isPureBred(ButterflyChromosomes.NOCTURNAL);
		}
	},
	FLYER(DefaultFilterRuleType.FLYER) {
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getActiveValue(ButterflyChromosomes.TOLERANT_FLYER);
		}
	},
	PURE_FLYER(DefaultFilterRuleType.PURE_FLYER) {
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getActiveValue(ButterflyChromosomes.TOLERANT_FLYER) && butterfly.isPureBred(ButterflyChromosomes.TOLERANT_FLYER);
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
