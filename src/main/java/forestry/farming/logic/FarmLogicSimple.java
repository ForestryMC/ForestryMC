package forestry.farming.logic;

import forestry.api.farming.ISimpleFarmLogic;
import net.minecraft.item.ItemStack;

public class FarmLogicSimple extends FarmLogicCrops {

	protected final ISimpleFarmLogic simpleLogic;
	
	public FarmLogicSimple(ISimpleFarmLogic simpleLogic) {
		super(simpleLogic.getSeeds());
		this.simpleLogic = simpleLogic;
	}

	@Override
	public String getName() {
		return simpleLogic.getName();
	}

	@Override
	public ItemStack getIconItemStack() {
		return simpleLogic.getIconItemStack();
	}

}
