package forestry.farming;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmRegistry;
import forestry.api.farming.IFarmable;
import forestry.api.farming.ISimpleFarmLogic;

public class DummyFarmRegistry implements IFarmRegistry {

	@Override
	public void registerFarmLogic(String identifier, IFarmLogic logic) {

	}

	@Override
	public void registerFarmables(String identifier, IFarmable... farmable) {
	}

	@Override
	public Collection<IFarmable> getFarmables(String identifier) {
		return Collections.emptyList();
	}

	@Override
	public IFarmLogic createLogic(ISimpleFarmLogic simpleFarmLogic) {
		return null;
	}

	@Override
	public void registerFertilizer(ItemStack itemStack, int value) {
	}

	@Override
	public int getFertilizeValue(ItemStack itemStack) {
		return 0;
	}
}
