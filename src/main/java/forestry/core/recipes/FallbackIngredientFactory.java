package forestry.core.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import javax.annotation.Nonnull;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

@SuppressWarnings("unused")
public class FallbackIngredientFactory implements IIngredientFactory {
	@Nonnull
	@Override
	public Ingredient parse(JsonContext context, JsonObject json) {
		Ingredient ret;
		try {
			ret = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "primary"), context);
		} catch (JsonSyntaxException e) {
			ret = Ingredient.EMPTY;	//throws exception if item doesn't exist
		}
		if (ret.getMatchingStacks().length == 0) {
			ret = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "fallback"), context);
		}
		return ret;
	}
}
