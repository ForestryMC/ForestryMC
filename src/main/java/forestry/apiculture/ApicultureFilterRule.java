package forestry.apiculture;

import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IFilterRuleType;
import forestry.sorting.DefaultFilterRuleType;

public enum ApicultureFilterRule implements IFilterRule {
	PURE_BREED(DefaultFilterRuleType.PURE_BREED) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.isPureBred(BeeChromosomes.SPECIES);
		}
	},
	NOCTURNAL(DefaultFilterRuleType.NOCTURNAL) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getActiveValue(BeeChromosomes.NEVER_SLEEPS);
		}
	},
	PURE_NOCTURNAL(DefaultFilterRuleType.PURE_NOCTURNAL) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getActiveValue(BeeChromosomes.NEVER_SLEEPS) && bee.isPureBred(BeeChromosomes.NEVER_SLEEPS);
		}
	},
	FLYER(DefaultFilterRuleType.FLYER) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getActiveValue(BeeChromosomes.TOLERATES_RAIN);
		}
	},
	PURE_FLYER((DefaultFilterRuleType.PURE_FLYER)) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getActiveValue(BeeChromosomes.TOLERATES_RAIN) && bee.isPureBred(BeeChromosomes.TOLERATES_RAIN);
		}
	},
	CAVE(DefaultFilterRuleType.CAVE) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getActiveValue(BeeChromosomes.CAVE_DWELLING);
		}
	},
	PURE_CAVE(DefaultFilterRuleType.PURE_CAVE) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getActiveValue(BeeChromosomes.CAVE_DWELLING) && bee.isPureBred(BeeChromosomes.CAVE_DWELLING);
		}
	};

	ApicultureFilterRule(IFilterRuleType rule) {
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
		if (!(individual instanceof IBee)) {
			return false;
		}
		return isValid((IBee) individual);
	}

	protected boolean isValid(IBee bee) {
		return false;
	}

	@Override
	public String getRootUID() {
		return BeeManager.beeRoot.getUID();
	}

}
