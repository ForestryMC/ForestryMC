package forestry.farming;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.world.item.ItemStack;

import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmPropertiesBuilder;
import forestry.api.farming.IFarmRegistry;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.core.utils.ItemStackUtil;
import forestry.farming.logic.FarmProperties;
import forestry.farming.logic.farmables.FarmableInfo;

public final class FarmRegistry implements IFarmRegistry {

	private static final FarmRegistry INSTANCE = new FarmRegistry();

	private final Multimap<String, IFarmable> farmables = HashMultimap.create();
	private final Map<String, IFarmableInfo> farmableInfo = new LinkedHashMap<>();
	private final Map<ItemStack, Integer> fertilizers = new LinkedHashMap<>();
	private final Map<String, IFarmProperties> farmInstances = new HashMap<>();
	private final Map<String, IFarmPropertiesBuilder> propertiesBuilders = new HashMap<>();
	private FertilizerConfig fertilizer = FertilizerConfig.DUMMY;

	public static FarmRegistry getInstance() {
		return INSTANCE;
	}

	@Override
	public void registerFarmables(String identifier, IFarmable... farmablesArray) {
		IFarmableInfo info = getFarmableInfo(identifier);
		for (IFarmable farmable : farmablesArray) {
			farmable.addInformation(info);
		}
		farmables.putAll(identifier, Arrays.asList(farmablesArray));
	}

	@Override
	public Collection<IFarmable> getFarmables(String identifier) {
		return farmables.get(identifier);
	}

	@Override
	public IFarmableInfo getFarmableInfo(String identifier) {
		return farmableInfo.computeIfAbsent(identifier, FarmableInfo::new);
	}

	@Override
	public void registerFertilizer(ItemStack itemStack, int value) {
		if (itemStack.isEmpty()) {
			return;
		}
		fertilizers.put(itemStack, value);
	}

	@Override
	public int getFertilizeValue(ItemStack itemStack) {
		return fertilizer.getFertilizeValue(itemStack);
	}

	@Override
	public IFarmProperties registerLogic(String identifier, IFarmProperties farmInstance) {
		farmInstances.put(identifier, farmInstance);
		return farmInstance;
	}

	@Override
	public IFarmPropertiesBuilder getPropertiesBuilder(String identifier) {
		return propertiesBuilders.computeIfAbsent(identifier, FarmProperties.Builder::new);
	}

	public IFarmProperties registerProperties(String identifier, IFarmProperties properties) {
		farmInstances.put(identifier, properties);
		return properties;
	}

	@Override
	@Nullable
	public IFarmProperties getProperties(String identifier) {
		return farmInstances.get(identifier);
	}

	private static class FertilizerConfig {
		private static final FertilizerConfig DUMMY = new FertilizerConfig(Collections.emptyMap());

		private final Map<ItemStack, Integer> fertilizers;

		private FertilizerConfig(Map<ItemStack, Integer> fertilizers) {
			this.fertilizers = fertilizers;
		}

		private int getFertilizeValue(ItemStack itemStack) {
			for (Entry<ItemStack, Integer> fertilizer : fertilizers.entrySet()) {
				ItemStack fertilizerStack = fertilizer.getKey();
				if (ItemStackUtil.areItemStacksEqualIgnoreCount(fertilizerStack, itemStack)) {
					return fertilizer.getValue();
				}
			}
			return 0;
		}
	}

}
