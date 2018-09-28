package forestry.farming;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
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

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmRegistry;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.api.farming.ISimpleFarmLogic;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.farming.logic.FakeFarmProperties;
import forestry.farming.logic.FarmLogicSimple;
import forestry.farming.logic.FarmProperties;
import forestry.farming.logic.farmables.FarmableInfo;

public final class FarmRegistry implements IFarmRegistry {

	private static final FarmRegistry INSTANCE = new FarmRegistry();

	private final Multimap<String, IFarmable> farmables = HashMultimap.create();
	private final Map<String, IFarmableInfo> farmableInfo = new LinkedHashMap<>();
	private final Map<ItemStack, Integer> fertilizers = new LinkedHashMap<>();
	private final Map<String, IFarmProperties> farmInstances = new HashMap<>();
	private FertilizerConfig fertilizer = FertilizerConfig.DUMMY;

	public static FarmRegistry getInstance() {
		return INSTANCE;
	}

	@Override
	@Deprecated
	public void registerLogic(String identifier, IFarmLogic logic) {
	}

	@Override
	public void registerFarmables(String identifier, IFarmable... farmablesArray) {
		IFarmableInfo info = getFarmableInfo(identifier);
		for(IFarmable farmable : farmablesArray){
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
	public IFarmLogic createCropLogic(IFarmProperties instance, boolean isManual, ISimpleFarmLogic simpleFarmLogic) {
		return new FarmLogicSimple(instance, isManual, simpleFarmLogic);
	}

	@Override
	public IFarmProperties createFakeInstance(IFarmLogic logic) {
		return new FakeFarmProperties(logic);
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
		return fertilizer.getFertilizeValue(itemStack);
	}

	@Override
	public IFarmProperties registerLogic(String identifier, IFarmProperties farmInstance) {
		farmInstances.put(identifier, farmInstance);
		return farmInstance;
	}

	@Override
	public IFarmProperties registerLogic(String identifier, BiFunction<IFarmProperties, Boolean, IFarmLogic> logicFactory, String... farmablesIdentifiers) {
		Set<String> identifiers = new HashSet<>(Arrays.asList(farmablesIdentifiers));
		identifiers.add(identifier);
		IFarmProperties instance = new FarmProperties(logicFactory, identifiers, identifier);
		farmInstances.put(identifier, instance);
		return instance;
	}

	@Override
	@Nullable
	public IFarmProperties getProperties(String identifier) {
		return farmInstances.get(identifier);
	}

	void loadConfig(LocalizedConfiguration config) {
		Map<String, String> defaultEntries = getItemStrings();
		List<String> defaultFertilizers = new ArrayList<>(defaultEntries.values());
		Collections.sort(defaultFertilizers);
		String[] defaultSortedFertilizers = defaultFertilizers.toArray(new String[defaultFertilizers.size()]);
		Property property = config.get("fertilizers", "items", defaultSortedFertilizers, Translator.translateToLocal("for.config.farm.fertilizers.items"));

		ImmutableMap<ItemStack, Integer> fertilizerMap = checkConfig(property, defaultEntries);
		fertilizer = new FertilizerConfig(fertilizerMap);
	}

	private ImmutableMap<ItemStack, Integer> checkConfig(Property property, Map<String, String> defaultEntries){
		String[] fertilizerList = property.getStringList();
		ImmutableMap.Builder<ItemStack, Integer> fertilizerMap = new ImmutableMap.Builder<>();
		Map<String, String> configEntries = parseConfig(fertilizerList, fertilizerMap);

		List<String> newEntries = new ArrayList<>(Arrays.asList(fertilizerList));
		for(Entry<String, String> defaultEntry : defaultEntries.entrySet()){
			if(!configEntries.containsKey(defaultEntry.getKey())){
				newEntries.add(defaultEntry.getValue());
			}
		}

		if(newEntries.size() > fertilizerList.length){
			Collections.sort(newEntries);
			property.set(newEntries.toArray(new String[newEntries.size()]));
			return checkConfig(property, defaultEntries);
		}

		return fertilizerMap.build();
	}

	private Map<String, String> parseConfig(String[] fertilizerList, ImmutableMap.Builder<ItemStack, Integer> fertilizerMap){
		Map<String, String> configEntries = new HashMap<>();
		for(String entry : fertilizerList){
			String[] spited = entry.split(";");
			if(spited.length < 2){
				Log.error("Forestry failed to parse a entry of the fertilizer config.");
				continue;
			}
			String itemName = spited[0];
			ItemStack fertilizerItem = ItemStackUtil.parseItemStackString(itemName, OreDictionary.WILDCARD_VALUE);
			if(fertilizerItem == null || fertilizerItem.isEmpty()){
				Log.error("Forestry failed to parse a entry of the fertilizer config, because the item doesn't exists.");
				continue;
			}
			String value = spited[1];
			int fertilizerValue = Integer.parseInt(value);
			if(fertilizerValue > 0) {
				fertilizerMap.put(fertilizerItem, fertilizerValue);
			}
			configEntries.put(itemName, value);
		}
		return configEntries;
	}

	private Map<String, String> getItemStrings() {
		Map<String, String> itemStrings = new HashMap<>(fertilizers.size());
		for (Entry<ItemStack, Integer> itemStack : fertilizers.entrySet()) {
			String itemString = ItemStackUtil.getStringForItemStack(itemStack.getKey());
			itemStrings.put(itemString, itemString + ";" + itemStack.getValue());
		}
		return itemStrings;
	}

	private static class FertilizerConfig{
		private static final FertilizerConfig DUMMY = new FertilizerConfig(Collections.emptyMap());

		private final Map<ItemStack, Integer> fertilizers;

		private FertilizerConfig(Map<ItemStack, Integer> fertilizers) {
			this.fertilizers = fertilizers;
		}

		private int getFertilizeValue(ItemStack itemStack) {
			for (Entry<ItemStack, Integer> fertilizer : fertilizers.entrySet()) {
				ItemStack fertilizerStack = fertilizer.getKey();
				if (ItemStackUtil.isIdenticalItem(fertilizerStack, itemStack)) {
					return fertilizer.getValue();
				}
			}
			return 0;
		}
	}

}
