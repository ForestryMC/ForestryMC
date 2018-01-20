package forestry.farming.logic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmProperties;

public abstract class FarmLogicSoil extends FarmLogic {

	public FarmLogicSoil(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
	}

	protected boolean isAcceptedSoil(IBlockState blockState) {
		return properties.isAcceptedSoil(blockState);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemStack) {
		return properties.isAcceptedResource(itemStack);
	}

}
