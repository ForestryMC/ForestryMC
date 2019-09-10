package forestry.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;

import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;

import forestry.core.config.Constants;
import forestry.core.recipes.ModuleEnabledCondition;

//Useful when there is either a recipe, or it is disabled. Convenience from not having to provide an
//ID when building
//
//also contains convienience method for module condition
public class RecipeDataHelper {

	private Consumer<IFinishedRecipe> consumer;

	public RecipeDataHelper(Consumer<IFinishedRecipe> consumer) {
		this.consumer = consumer;
	}

	public void simpleConditionalRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, ICondition... conditions) {
		ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
		for (ICondition condition : conditions) {
			builder.addCondition(condition);
		}

		//this list stuff is a bit ugly but it's the quickest way I could think of to store one object quickly
		List<IFinishedRecipe> finishedRecipes = new ArrayList<>(1);
		recipe.accept(finishedRecipes::add);    //hack to get the finished recipe

		IFinishedRecipe finishedRecipe = finishedRecipes.get(0);
		builder.addRecipe(finishedRecipe);
		builder.build(consumer, finishedRecipe.getID());
	}

	public void moduleConditionRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, String moduleUID) {
		simpleConditionalRecipe(recipe, new ModuleEnabledCondition(Constants.MOD_ID, moduleUID));
	}
}
