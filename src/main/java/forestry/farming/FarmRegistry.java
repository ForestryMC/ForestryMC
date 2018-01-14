package forestry.farming;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.farming.IFarmInstance;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmRegistry;
import forestry.api.farming.IFarmable;
import forestry.api.farming.ISimpleFarmLogic;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.farming.logic.FakeFarmInstance;
import forestry.farming.logic.FarmInstance;
import forestry.farming.logic.FarmLogicSimple;

public final class FarmRegistry implements IFarmRegistry {

	private static final FarmRegistry INSTANCE = new FarmRegistry();
	private final Multimap<String, IFarmable> farmables = HashMultimap.create();
	private final Map<ItemStack, Integer> fertilizers = new LinkedHashMap<>();
	private final Map<String, IFarmLogic> logics = new HashMap<>();
	private final Map<String, IFarmInstance> farmInstances = new HashMap<>();

	public static FarmRegistry getInstance() {
		return INSTANCE;
	}

	@Override
	@Deprecated
	public void registerLogic(String identifier, IFarmLogic logic) {
		logics.put(identifier, logic);
	}

	@Override
	public void registerFarmables(String identifier, IFarmable... farmablesArray) {
		farmables.putAll(identifier, Arrays.asList(farmablesArray));
	}

	@Override
	public Collection<IFarmable> getFarmables(String identifier) {
		return farmables.get(identifier);
	}

	@Override
	public IFarmLogic createCropLogic(IFarmInstance instance, boolean isManual, ISimpleFarmLogic simpleFarmLogic) {
		return new FarmLogicSimple(instance, isManual, simpleFarmLogic);
	}

	@Override
	public IFarmInstance createFakeInstance(IFarmLogic logic) {
		return new FakeFarmInstance(logic);
	}

	@Override
	public void registerFertilizer(ItemStack itemStack, int value) {
		if(itemStack.isEmpty()){
			return;
		}
		fertilizers.put(itemStack, value);
	}

	@Override
	public int getFertilizeValue(ItemStack itemStack) {
		for (Entry<ItemStack, Integer> fertilizer : fertilizers.entrySet()) {
			ItemStack fertilizerStack = fertilizer.getKey();
			if (ItemStackUtil.isIdenticalItem(fertilizerStack, itemStack)) {
				return fertilizer.getValue();
			}
		}
		return 0;
	}

	@Override
	public IFarmInstance registerLogic(IFarmInstance farmInstance) {
		farmInstances.put(farmInstance.getIdentifier(), farmInstance);
		return farmInstance;
	}

	@Override
	public IFarmInstance registerLogic(String identifier, BiFunction<IFarmInstance, Boolean, IFarmLogic> logicFactory, String... farmablesIdentifiers) {
		Set<String> fIdentifiers = new HashSet<>(Arrays.asList(farmablesIdentifiers));
		fIdentifiers.add(identifier);
		Collection<IFarmable> farmablesSet = fIdentifiers.stream().map(farmables::get).flatMap(Collection::stream).collect(Collectors.toSet());
		IFarmInstance instance = new FarmInstance(identifier, logicFactory, farmablesSet);
		farmInstances.put(identifier, instance);
		return instance;
	}

	@Override
	@Nullable
	public IFarmInstance getFarm(String identifier) {
		return farmInstances.get(identifier);
	}

	void loadConfig(LocalizedConfiguration config) {
		List<String> defaultFertilizers = new ArrayList<>(getItemStrings());
		Collections.sort(defaultFertilizers);
		String[] defaultSortedFertilizers = defaultFertilizers.toArray(new String[defaultFertilizers.size()]);
		Property fertilizerConf = config.get("fertilizers", "items", defaultSortedFertilizers, Translator.translateToLocal("for.config.farm.fertilizers.items"));

		fertilizers.clear();
		String[] fertilizerList = fertilizerConf.getStringList();
		for (int i = 0; i < fertilizerList.length; i++) {
			try {
				String fertilizer = fertilizerList[i];
				String[] fertilizers = fertilizer.split(";");
				String itemName = fertilizers[0];
				if(itemName.equals("forestry:fertilizerCompound")){
					itemName = "forestry:fertilizer_compound";
				}
				ItemStack fertilizerItem = ItemStackUtil.parseItemStackString(itemName, OreDictionary.WILDCARD_VALUE);
				if(fertilizerItem == null){
					continue;
				}
				int fertilizerValue = Integer.parseInt(fertilizers[1]);
				registerFertilizer(fertilizerItem, fertilizerValue);
			} catch (Exception e) {
				Log.error("Forestry failed to parse a fertilizer entry at the farm config, at the position " + i + ".", e);
			}
		}
	}
	
	private Set<String> getItemStrings() {
		Set<String> itemStrings = new HashSet<>(fertilizers.size());
		for (Entry<ItemStack, Integer> itemStack : fertilizers.entrySet()) {
			String itemString = ItemStackUtil.getStringForItemStack(itemStack.getKey());
			itemString += ";" + itemStack.getValue();
			itemStrings.add(itemString);
		}
		return itemStrings;
	}
	
}
