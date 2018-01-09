package forestry.api.genetics;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @since 5.8
 */
public interface IFilterRegistry {
	/**
	 * Registers a filter rule.
	 */
	void registerFilter(IFilterRule rule);

	Collection<IFilterRule> getRules();

	IFilterRule getDefaultRule();

	@Nullable
	IFilterRule getRule(String uid);

	@Nullable
	IFilterRule getRule(int id);

	int getId(IFilterRule rule);

	default IFilterRule getRuleOrDefault(String uid){
		IFilterRule rule = getRule(uid);
		return rule != null ? rule : getDefaultRule();
	}

	default IFilterRule getRuleOrDefault(int id){
		IFilterRule rule = getRule(id);
		return rule != null ? rule : getDefaultRule();
	}
}
