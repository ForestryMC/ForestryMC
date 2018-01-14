package forestry.farming;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;

import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmInstance;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmRegistry;
import forestry.api.farming.IFarmable;
import forestry.api.farming.ISimpleFarmLogic;
import forestry.farming.logic.FakeFarmInstance;

public class DummyFarmRegistry implements IFarmRegistry {

	@Override
	public void registerLogic(String identifier, IFarmLogic logic) {
		//Dummy-Implementation
	}

	@Override
	public void registerFarmables(String identifier, IFarmable... farmable) {
		//Dummy-Implementation
	}

	@Override
	public Collection<IFarmable> getFarmables(String identifier) {
		//Dummy-Implementation
		return Collections.emptyList();
	}

	@Override
	public IFarmInstance registerLogic(IFarmInstance farmInstance) {
		//Dummy-Implementation
		return null;
	}

	@Override
	public IFarmInstance registerLogic(String identifier, BiFunction<IFarmInstance, Boolean, IFarmLogic> logicFactory, String... farmablesIdentifiers) {
		//Dummy-Implementation
		return null;
	}

	@Nullable
	@Override
	public IFarmLogic createCropLogic(IFarmInstance instance, boolean isManual, ISimpleFarmLogic simpleFarmLogic) {
		//Dummy-Implementation
		return null;
	}

	@Override
	public IFarmInstance createFakeInstance(IFarmLogic logic) {
		return new FakeFarmInstance(logic);
	}

	@Override
	public void registerFertilizer(ItemStack itemStack, int value) {
		//Dummy-Implementation
	}

	@Override
	public int getFertilizeValue(ItemStack itemStack) {
		//Dummy-Implementation
		return 0;
	}

	@Nullable
	@Override
	public IFarmInstance getFarm(String identifier) {
		//Dummy-Implementation
		return null;
	}
}
