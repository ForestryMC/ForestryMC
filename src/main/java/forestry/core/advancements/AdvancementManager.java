package forestry.core.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;

public class AdvancementManager {


	public static void registerTriggers() {
		registerAdvancementTrigger(SpeciesDiscoveredTrigger.INSTANCE);
	}

	private static <T extends ICriterionInstance> ICriterionTrigger<T> registerAdvancementTrigger(ICriterionTrigger<T> trigger) {
		CriteriaTriggers.register(trigger);
		return trigger;
	}
}
