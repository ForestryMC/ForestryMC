package forestry.core.patchouli.processor;

import com.google.common.base.Preconditions;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class CarpenterProcessor implements IComponentProcessor {
	@Nullable
	protected ICarpenterRecipe recipe;

	@Override
	public void setup(IVariableProvider variables) {
		ItemStack itemStack = variables.get("item").as(ItemStack.class, ItemStack.EMPTY);

		this.recipe = RecipeManagers.carpenterManager.getRecipes(null)
				.filter(recipe -> recipe.getResult().sameItem(itemStack))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("couldn't fnd a recipe with output: " + itemStack));
	}

	@Override
	public IVariable process(String key) {
		Preconditions.checkNotNull(recipe);
		if (key.equals("output")) {
			return IVariable.from(this.recipe.getResult());
		} else if (key.equals("fluid")) {
			return IVariable.wrap(this.recipe.getFluidResource().getFluid().getRegistryName().toString());
		} else if (key.equals("fluidAmount")) {
			return IVariable.wrap(this.recipe.getFluidResource().getAmount());
		} else if (key.startsWith("ingredient")) {
			int index = Integer.parseInt(key.substring("ingredient".length()));
            if (index < 1 || index > 9) {
                return IVariable.empty();
            }

			Ingredient ingredient;
			try {
				ingredient = this.recipe.getCraftingGridRecipe().getIngredients().get(index - 1);
			} catch (Exception e) {
				ingredient = Ingredient.EMPTY;
			}
			return IVariable.from(ingredient.getItems());
		} else {
			return IVariable.empty();
		}
	}
}
