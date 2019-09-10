package forestry.farming.logic;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;

public class FakeFarmProperties implements IFarmProperties {
	private final IFarmLogic logic;

	public FakeFarmProperties(IFarmLogic logic) {
		this.logic = logic;
	}

	@Override
	public void registerFarmables(String identifier) {
	}

	@Override
	public void registerSoil(Block soil) {
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
	public boolean isAcceptedSoil(Block block) {
		return false;
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemStack) {
		return false;
	}

	@Override
	public Collection<Block> getSoils() {
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
