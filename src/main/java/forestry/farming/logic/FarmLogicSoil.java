package forestry.farming.logic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmInstance;

public abstract class FarmLogicSoil extends FarmLogic {

	public FarmLogicSoil(IFarmInstance instance, boolean isManual) {
		super(instance, isManual);
	}

	protected boolean isAcceptedSoil(IBlockState blockState) {
		return instance.isAcceptedSoil(blockState);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemStack) {
		return instance.isAcceptedResource(itemStack);
	}

}
