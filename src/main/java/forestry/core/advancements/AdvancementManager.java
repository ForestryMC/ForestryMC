package forestry.core.advancements;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import forestry.core.utils.Log;

public class AdvancementManager {

	private static Method criterionRegister;

	public static void registerTriggers() {
		registerAdvancementTrigger(SpeciesDiscoveredTrigger.INSTANCE);
	}

	private static <T extends ICriterionInstance> ICriterionTrigger<T> registerAdvancementTrigger(ICriterionTrigger<T> trigger) {
		if (criterionRegister == null) {
			criterionRegister = ReflectionHelper.findMethod(CriteriaTriggers.class, "register", "func_192118_a", ICriterionTrigger.class);
			criterionRegister.setAccessible(true);
		}
		try {
			trigger = (ICriterionTrigger<T>) criterionRegister.invoke(null, trigger);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException error) {
			Log.error("Failed to register a trigger " + trigger.getId() + " .", error);
		}
		return trigger;
	}
}
