package forestry.apiculture;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IIndividual;
import forestry.sorting.DefaultFilterRule;

public enum ApicultureFilterLogic implements IFilterLogic {
	PURE_BREED(DefaultFilterRule.PURE_BREED){
		@Override
		protected boolean isValid(IBee bee) {
			return bee.isPureBred(EnumBeeChromosome.SPECIES);
		}
	},
	NOCTURNAL(DefaultFilterRule.NOCTURNAL){
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getNeverSleeps();
		}
	},
	PURE_NOCTURNAL(DefaultFilterRule.PURE_NOCTURNAL){
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getNeverSleeps() && bee.isPureBred(EnumBeeChromosome.NEVER_SLEEPS);
		}
	},
	FLYER(DefaultFilterRule.FLYER){
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getToleratesRain();
		}
	},
	PURE_FLYER((DefaultFilterRule.PURE_FLYER)){
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getToleratesRain() && bee.isPureBred(EnumBeeChromosome.TOLERATES_RAIN);
		}
	},
	CAVE(DefaultFilterRule.CAVE){
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getCaveDwelling();
		}
	},
	PURE_CAVE(DefaultFilterRule.PURE_CAVE){
		@Override
		protected boolean isValid(IBee bee) {
			return bee.getGenome().getCaveDwelling() && bee.isPureBred(EnumBeeChromosome.CAVE_DWELLING);
		}
	};

	ApicultureFilterLogic(IFilterRule rule) {
		rule.addLogic(this);
	}

	public static void init() {
	}

	@Override
	public boolean isValid(ItemStack itemStack, IFilterData data) {
		if(!data.isPresent()){
			return false;
		}
		IIndividual individual = data.getIndividual();
		if(!(individual instanceof IBee)){
			return false;
		}
		return isValid((IBee)individual);
	}

	protected boolean isValid(IBee bee){
		return false;
	}

	@Override
	public String getRootUID() {
		return BeeManager.beeRoot.getUID();
	}

}
