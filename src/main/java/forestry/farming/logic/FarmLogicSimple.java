package forestry.farming.logic;

import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmProperties;
import forestry.api.farming.ISimpleFarmLogic;

public class FarmLogicSimple extends FarmLogicCrops {

	private final ISimpleFarmLogic simpleLogic;

	public FarmLogicSimple(IFarmProperties instance, boolean isManual, ISimpleFarmLogic simpleLogic) {
		super(instance, isManual);
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
