package forestry.sorting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

import forestry.api.core.ILocatable;
import forestry.api.genetics.IFilterLogic;
import forestry.api.genetics.IFilterRegistry;
import forestry.api.genetics.IFilterRuleType;

public class DummyFilterRegistry implements IFilterRegistry {
	@Override
	public void registerFilter(IFilterRuleType rule) {
	}

	@Override
	public Collection<IFilterRuleType> getRules() {
		return Collections.emptySet();
	}

	@Override
	public IFilterRuleType getDefaultRule() {
		return DefaultFilterRuleType.CLOSED;
	}

	@Nullable
	@Override
	public IFilterRuleType getRule(String uid) {
		return null;
	}

	@Nullable
	@Override
	public IFilterRuleType getRule(int id) {
		return null;
	}

	@Override
	public IFilterLogic createLogic(ILocatable locatable, IFilterLogic.INetworkHandler networkHandler) {
		return FakeFilterLogic.INSTANCE;
	}

	@Override
	public int getId(IFilterRuleType rule) {
		return 0;
	}
}
