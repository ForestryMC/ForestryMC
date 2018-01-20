package forestry.farming.logic;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmInstance;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmable;
import forestry.api.farming.ISoil;

public class FakeFarmInstance implements IFarmInstance {
	private final IFarmLogic logic;

	public FakeFarmInstance(IFarmLogic logic) {
		this.logic = logic;
	}

	@Override
	public void registerFarmables(String identifier) {
	}

	@Override
	public void registerSoil(ItemStack resource, IBlockState soilState, boolean hasMetaData) {
	}

	@Override
	public boolean isAcceptedSoil(IBlockState blockState) {
		return false;
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemStack) {
		return false;
	}

	@Override
	public String getIdentifier() {
		return "farmFake";
	}

	@Override
	public Collection<ISoil> getSoils() {
		return Collections.emptySet();
	}

	@Override
	public Collection<IFarmable> getFarmables() {
		return Collections.emptySet();
	}

	@Override
	public IFarmLogic getLogic(boolean manuel) {
		return logic;
	}
}
