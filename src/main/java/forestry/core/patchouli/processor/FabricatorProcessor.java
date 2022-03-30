package forestry.core.patchouli.processor;

import java.util.Arrays;

import com.google.common.base.Preconditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.registries.ForgeRegistries;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.RecipeManagers;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class FabricatorProcessor implements IComponentProcessor {
	@Nullable
	protected IFabricatorRecipe recipe;

	@Override
	public void setup(IVariableProvider variables) {
		ItemStack itemStack = variables.get("item").as(ItemStack.class, ItemStack.EMPTY);

		this.recipe = RecipeManagers.fabricatorManager.getRecipes(null)
				.filter(recipe -> {
					ItemStack result = recipe.getCraftingGridRecipe().getResultItem();
					return itemStack.sameItem(result);
				})
				.findFirst()
				.orElseThrow();
	}

	@Override
	public IVariable process(String key) {
		Preconditions.checkNotNull(recipe);
		if (key.equals("output")) {
			return IVariable.from(this.recipe.getCraftingGridRecipe().getResultItem());
		} else if (key.equals("fluid")) {
			return IVariable.wrap(this.recipe.getLiquid().getFluid().getRegistryName().toString());
		} else if (key.equals("fluidAmount")) {
			return IVariable.wrap(this.recipe.getLiquid().getAmount());
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
		} else if (key.equals("plan")) {
			return IVariable.from(this.recipe.getPlan());
		} else if (key.equals("metal")) {
			if (this.recipe.getLiquid().getFluid().getRegistryName().getPath().contains("glass")) {
				return IVariable.from(new ItemStack(ForgeRegistries.ITEMS.getValue(
						new ResourceLocation("minecraft:sand")
				)));
			}

			return RecipeManagers.fabricatorSmeltingManager.getRecipes(null)
					.filter(r -> r.getProduct().isFluidEqual(this.recipe.getLiquid()))
					.flatMap(r -> Arrays.stream(r.getResource().getItems()))
					.findFirst()
					.map(IVariable::from)
					.orElseGet(IVariable::empty);
		} else {
			return IVariable.empty();
		}
	}
}
