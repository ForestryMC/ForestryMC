package forestry.farming;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmRegistry;
import forestry.api.farming.IFarmable;
import forestry.api.farming.ISimpleFarmLogic;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.farming.logic.FarmLogicSimple;

public final class FarmRegistry implements IFarmRegistry {

	private static final FarmRegistry INSTANCE = new FarmRegistry();
	private final Multimap<String, IFarmable> farmables = HashMultimap.create();
	private final Map<ItemStack, Integer> fertilizers = new LinkedHashMap<>();

	static {
		ForestryAPI.farmRegistry = INSTANCE;
	}

	public static FarmRegistry getInstance() {
		return INSTANCE;
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
	public IFarmLogic createLogic(ISimpleFarmLogic simpleFarmLogic) {
		return new FarmLogicSimple(simpleFarmLogic);
	}

	@Override
	public void registerFertilizer(ItemStack itemStack, int value) {
		if(itemStack == null || itemStack.isEmpty()){
			return;
		}
		fertilizers.put(itemStack, value);
	}

	@Override
	public int getFertilizeValue(ItemStack itemStack) {
		for (Entry<ItemStack, Integer> fertilizer : fertilizers.entrySet()) {
			ItemStack fertilizerStack = fertilizer.getKey();
			if (itemStack.getItem() == fertilizerStack.getItem() 
					&& (fertilizerStack.getItemDamage() == itemStack.getItemDamage() 
					|| fertilizerStack.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
				return fertilizer.getValue();
			}
		}
		return 0;
	}
	
	public void loadConfig(LocalizedConfiguration config) {
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
