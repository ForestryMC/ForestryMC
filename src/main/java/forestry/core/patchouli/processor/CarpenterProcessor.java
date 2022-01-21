package forestry.core.patchouli.processor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.RecipeManagers;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class CarpenterProcessor implements IComponentProcessor {
	protected ICarpenterRecipe recipe;

	@Override
	public void setup(IVariableProvider variables) {
		int index;
		try {
			index = variables.get("index").asNumber().intValue();
		} catch (Exception e) {
			index = 0;
		}

		ItemStack itemStack;
		try {
			itemStack = variables.get("item").as(ItemStack.class);
		} catch (Exception e) {
			itemStack = ItemStack.EMPTY;
		}

        /*
        Manually iterate over all the carpenter recipes by result item ResourceLocation id
        because looking up the recipe in carpenterManager doesn't work for some recipes
        (idk why, maybe i was doing it wrong)
        */
		List<ICarpenterRecipe> matches = new ArrayList<>();

		for (ICarpenterRecipe icr : RecipeManagers.carpenterManager.getRecipes(null)) {
			ItemStack result = icr.getResult();

			if (result.getItem() == itemStack.getItem()) {
				matches.add(icr);
				break;
			}
		}

		this.recipe = matches.get(index);
	}

	@Override
	public IVariable process(String key) {
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
