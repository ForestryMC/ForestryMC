package forestry.sorting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import forestry.api.core.ILocatable;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRegistry;
import forestry.api.genetics.IFilterRuleType;

public class FilterRegistry implements IFilterRegistry {
	private static final Comparator<IFilterRuleType> FILTER_COMPARATOR = (f, s) -> f.getUID().compareToIgnoreCase(s.getUID());

	private final HashMap<String, IFilterRuleType> filterByName = new LinkedHashMap<>();
	private final HashMap<String, Integer> filterIDByName = new LinkedHashMap<>();
	private final HashMap<Integer, IFilterRuleType> filterByID = new LinkedHashMap<>();

	@Override
	public void registerFilter(IFilterRuleType rule) {
		if (!filterByID.isEmpty()) {
			return;
		}
		filterByName.put(rule.getUID(), rule);
	}

	public void init() {
		List<IFilterRuleType> rules = new LinkedList<>(filterByName.values());
		rules.sort(FILTER_COMPARATOR);
		for (int i = 0; i < rules.size(); i++) {
			IFilterRuleType rule = rules.get(i);
			filterIDByName.put(rule.getUID(), i);
			filterByID.put(i, rule);
		}
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
