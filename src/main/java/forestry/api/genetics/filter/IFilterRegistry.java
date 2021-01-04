package forestry.api.genetics.filter;

import forestry.api.core.ILocatable;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @since 5.8
 */
public interface IFilterRegistry {
	/**
	 * Registers a filter rule.
	 */
	void registerFilter(IFilterRuleType rule);

	Collection<IFilterRuleType> getRules();

	IFilterRuleType getDefaultRule();

	@Nullable
	IFilterRuleType getRule(String uid);

	@Nullable
	IFilterRuleType getRule(int id);

	int getId(IFilterRuleType rule);

	IFilterLogic createLogic(ILocatable locatable, IFilterLogic.INetworkHandler networkHandler);

	default IFilterRuleType getRuleOrDefault(String uid) {
		IFilterRuleType rule = getRule(uid);
		return rule != null ? rule : getDefaultRule();
	}

	default IFilterRuleType getRuleOrDefault(int id) {
		IFilterRuleType rule = getRule(id);
		return rule != null ? rule : getDefaultRule();
	}
}
