package forestry.sorting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

import forestry.api.genetics.IFilterRegistry;
import forestry.api.genetics.IFilterRule;

public class DummyFilterRegistry implements IFilterRegistry {
	@Override
	public void registerFilter(IFilterRule rule) {
	}

	@Override
	public Collection<IFilterRule> getRules() {
		return Collections.emptySet();
	}

	@Override
	public IFilterRule getDefaultRule() {
		return DefaultFilterRule.CLOSED;
	}

	@Nullable
	@Override
	public IFilterRule getRule(String uid) {
		return null;
	}

	@Nullable
	@Override
	public IFilterRule getRule(int id) {
		return null;
	}

	@Override
	public int getId(IFilterRule rule) {
		return 0;
	}
}
