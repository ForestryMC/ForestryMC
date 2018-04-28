package forestry.core.recipes.json;

import com.google.gson.JsonObject;

import java.util.function.BooleanSupplier;

import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import forestry.modules.ModuleHelper;

public class DisableRecipe implements IConditionFactory {

	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json) {    //TODO - cache enabled modules?
		return () -> ModuleHelper.isEnabled(json.get("module").getAsString());
	}
}
