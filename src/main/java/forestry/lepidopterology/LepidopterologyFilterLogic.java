package forestry.lepidopterology;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IFilterData;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRule;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IButterfly;
import forestry.sorting.DefaultFilterRule;

public enum LepidopterologyFilterLogic implements IFilterLogic {
	PURE_BREED(DefaultFilterRule.PURE_BREED){
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.isPureBred(EnumButterflyChromosome.SPECIES);
		}
	},
	NOCTURNAL(DefaultFilterRule.NOCTURNAL){
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getNocturnal();
		}
	},
	PURE_NOCTURNAL(DefaultFilterRule.PURE_NOCTURNAL){
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getNocturnal() && butterfly.isPureBred(EnumButterflyChromosome.NOCTURNAL);
		}
	},
	FLYER(DefaultFilterRule.FLYER){
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getTolerantFlyer();
		}
	},
	PURE_FLYER(DefaultFilterRule.PURE_FLYER){
		@Override
		protected boolean isValid(IButterfly butterfly) {
			return butterfly.getGenome().getTolerantFlyer() && butterfly.isPureBred(EnumButterflyChromosome.TOLERANT_FLYER);
		}
	};

	LepidopterologyFilterLogic(IFilterRule rule) {
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
		if(!(individual instanceof IButterfly)){
			return false;
		}
		return isValid((IButterfly) individual);
	}

	protected boolean isValid(IButterfly butterfly){
		return false;
	}

	@Override
	public String getRootUID() {
		return ButterflyManager.butterflyRoot.getUID();
	}

}
