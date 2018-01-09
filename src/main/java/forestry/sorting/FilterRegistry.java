package forestry.sorting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import forestry.api.genetics.IFilterRegistry;
import forestry.api.genetics.IFilterRule;

public class FilterRegistry implements IFilterRegistry {
	private final HashMap<String, IFilterRule> filterByName = new LinkedHashMap<>();
	private final HashMap<String, Integer> filterIDByName = new LinkedHashMap<>();
	private final HashMap<Integer, IFilterRule> filterByID = new LinkedHashMap<>();
	private int ids;

	@Override
	public void registerFilter(IFilterRule rule) {
		filterByName.put(rule.getUID(), rule);
		filterIDByName.put(rule.getUID(), ids++);
		filterByID.put(ids - 1, rule);
	}

	@Override
	public Collection<IFilterRule> getRules() {
		return filterByName.values();
	}

	@Override
	public IFilterRule getDefaultRule() {
		return DefaultFilterRule.ITEM;
	}

	@Nullable
	@Override
	public IFilterRule getRule(String uid) {
		return filterByName.get(uid);
	}

	@Override
	public int getId(IFilterRule rule) {
		return filterIDByName.get(rule.getUID());
	}

	@Nullable
	@Override
	public IFilterRule getRule(int id) {
		return filterByID.get(id);
	}
}
