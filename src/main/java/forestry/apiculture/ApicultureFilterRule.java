package forestry.apiculture;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IFilterRuleType;
import forestry.api.genetics.IIndividual;
import forestry.sorting.DefaultFilterRuleType;

public enum ApicultureFilterRule implements IFilterRule {
	PURE_BREED(DefaultFilterRuleType.PURE_BREED) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.isPureBred(EnumBeeChromosome.SPECIES);
		}
	},
	NOCTURNAL(DefaultFilterRuleType.NOCTURNAL) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getNeverSleeps();
		}
	},
	PURE_NOCTURNAL(DefaultFilterRuleType.PURE_NOCTURNAL) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getNeverSleeps() && bee.isPureBred(EnumBeeChromosome.NEVER_SLEEPS);
		}
	},
	FLYER(DefaultFilterRuleType.FLYER) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getToleratesRain();
		}
	},
	PURE_FLYER((DefaultFilterRuleType.PURE_FLYER)) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getToleratesRain() && bee.isPureBred(EnumBeeChromosome.TOLERATES_RAIN);
		}
	},
	CAVE(DefaultFilterRuleType.CAVE) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getCaveDwelling();
		}
	},
	PURE_CAVE(DefaultFilterRuleType.PURE_CAVE) {
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getCaveDwelling() && bee.isPureBred(EnumBeeChromosome.CAVE_DWELLING);
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
