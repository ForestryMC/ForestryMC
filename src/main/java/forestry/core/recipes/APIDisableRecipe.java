package forestry.core.recipes;

import com.google.gson.JsonObject;

import java.util.function.BooleanSupplier;

import net.minecraft.util.JsonUtils;

import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import forestry.api.core.ForestryAPI;

@SuppressWarnings("unused")
public class APIDisableRecipe implements IConditionFactory {
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		return () -> ForestryAPI.activeMode.getBooleanSetting(JsonUtils.getString(json, "category"));
	}
}
