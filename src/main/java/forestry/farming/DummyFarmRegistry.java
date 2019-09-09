package forestry.farming;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;

import net.minecraft.item.ItemStack;

import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmRegistry;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.api.farming.ISimpleFarmLogic;
import forestry.farming.logic.farmables.FarmableInfo;

public class DummyFarmRegistry implements IFarmRegistry {

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
	public IFarmableInfo getFarmableInfo(String identifier) {
		return new FarmableInfo(identifier);
	}

	@Override
	public IFarmProperties registerLogic(String identifier, IFarmProperties farmInstance) {
		return null;
	}

	@Override
	public IFarmProperties registerLogic(String identifier, BiFunction<IFarmProperties, Boolean, IFarmLogic> logicFactory, String... farmablesIdentifiers) {
		//Dummy-Implementation
		return null;
	}

	@Nullable
	@Override
	public IFarmLogic createCropLogic(IFarmProperties instance, boolean isManual, ISimpleFarmLogic simpleFarmLogic) {
		//Dummy-Implementation
		return null;
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
	public IFarmProperties getProperties(String identifier) {
		//Dummy-Implementation
		return null;
	}
}
