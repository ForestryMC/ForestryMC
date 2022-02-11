package forestry.factory.recipes.jei.fermenter;

import forestry.api.recipes.IFermenterRecipe;
import net.minecraft.world.item.ItemStack;

public record FermenterRecipeWrapper(IFermenterRecipe recipe,
									 ItemStack fermentable) {

}
