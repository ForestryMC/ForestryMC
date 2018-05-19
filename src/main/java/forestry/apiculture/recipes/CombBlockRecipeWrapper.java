package forestry.apiculture.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.blocks.BlockHoneyComb;
import forestry.apiculture.items.ItemHoneyComb;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CombBlockRecipeWrapper implements IRecipeWrapper {

	private int meta;
	@Nullable
	private static ItemHoneyComb comb = null;    //will this work? will these be shared fields?
	@Nullable
	private static BlockHoneyComb[] blockHoneyCombs = null;

	public CombBlockRecipeWrapper(int meta) {
		this.meta = meta;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		if (comb == null) {
			comb = ModuleApiculture.getItems().beeComb;
		}
		if (blockHoneyCombs == null) {
			blockHoneyCombs = ModuleApiculture.getBlocks().beeCombs;
		}
		NonNullList<ItemStack> combs = NonNullList.withSize(9, new ItemStack(comb, 1, meta));
		ingredients.setInputs(ItemStack.class, combs);
		ingredients.setOutput(ItemStack.class, new ItemStack(blockHoneyCombs[meta / 16], 1, meta & 15));
	}
}
