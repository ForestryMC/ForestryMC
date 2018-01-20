package forestry.sorting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import forestry.api.core.ILocatable;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRegistry;
import forestry.api.genetics.IFilterRuleType;

public class FilterRegistry implements IFilterRegistry {
	private final HashMap<String, IFilterRuleType> filterByName = new LinkedHashMap<>();
	private final HashMap<String, Integer> filterIDByName = new LinkedHashMap<>();
	private final HashMap<Integer, IFilterRuleType> filterByID = new LinkedHashMap<>();
	private int ids;

	@Override
	public void registerFilter(IFilterRuleType rule) {
		filterByName.put(rule.getUID(), rule);
		filterIDByName.put(rule.getUID(), ids++);
		filterByID.put(ids - 1, rule);
	}

	@Override
	public Collection<IFilterRuleType> getRules() {
		return filterByName.values();
	}

	@Override
	public IFilterRuleType getDefaultRule() {
		return DefaultFilterRuleType.CLOSED;
	}

	@Nullable
	@Override
	public IFilterRuleType getRule(String uid) {
		return filterByName.get(uid);
	}

	@Override
	public int getId(IFilterRuleType rule) {
		return filterIDByName.get(rule.getUID());
	}

	@Override
	public IFilterLogic createLogic(ILocatable locatable, IFilterLogic.INetworkHandler networkHandler) {
		return new FilterLogic(locatable, networkHandler);
	}

	@Nullable
	@Override
	public IFilterRuleType getRule(int id) {
		return filterByID.get(id);
	}
}
