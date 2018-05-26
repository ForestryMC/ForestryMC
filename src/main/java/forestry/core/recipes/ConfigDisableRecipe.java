package forestry.core.recipes;

import com.google.gson.JsonObject;

import java.util.function.BooleanSupplier;

import net.minecraft.util.JsonUtils;

import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import forestry.core.config.Config;

@SuppressWarnings("unused")
public class ConfigDisableRecipe implements IConditionFactory {
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {
		String category = JsonUtils.getString(json, "category");
		boolean def;
		if(JsonUtils.hasField(json, "default")) {
			def = JsonUtils.getBoolean(json, "default");
		} else {
			def = true;
		}
		String[] split = category.split(":");
		return () -> Config.configCommon.getBooleanLocalized(split[0], split[1], def);
	}
}
