package forestry.farming.logic;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.api.farming.ISoil;

public class FakeFarmProperties implements IFarmProperties {
	private final IFarmLogic logic;

	public FakeFarmProperties(IFarmLogic logic) {
		this.logic = logic;
	}

	@Override
	public void registerFarmables(String identifier) {
	}

	@Override
	public void registerSoil(ItemStack resource, IBlockState soilState, boolean hasMetaData) {
	}

	@Override
	public void addGermlings(ItemStack... germlings) {

	}

	@Override
	public void addGermlings(Collection<ItemStack> germlings) {

	}

	@Override
	public void addProducts(Collection<ItemStack> products) {

	}

	@Override
	public void addProducts(ItemStack... products) {

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
	public Collection<ISoil> getSoils() {
		return Collections.emptySet();
	}

	@Override
	public Collection<IFarmable> getFarmables() {
		return Collections.emptySet();
	}

	@Override
	public Collection<IFarmableInfo> getFarmableInfo() {
		return Collections.emptySet();
	}

	@Override
	public IFarmLogic getLogic(boolean manuel) {
		return logic;
	}
}
