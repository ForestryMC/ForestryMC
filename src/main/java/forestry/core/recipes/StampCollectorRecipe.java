package forestry.core.recipes;

import com.google.gson.JsonObject;

import java.util.function.BooleanSupplier;

import net.minecraft.util.JsonUtils;

import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import forestry.core.config.Config;

@SuppressWarnings("unused")
public class StampCollectorRecipe implements IConditionFactory {
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		return () -> !Config.collectorStamps.contains(JsonUtils.getString(json, "UID"));
	}
}
