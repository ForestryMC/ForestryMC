package forestry.arboriculture.charcoal.jei;

import java.util.Collections;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.ICharcoalPileWall;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CharcoalPileWallWrapper implements IRecipeWrapper {

	private final ICharcoalPileWall pileWall;

	public CharcoalPileWallWrapper(ICharcoalPileWall pileWall) {
		this.pileWall = pileWall;
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, pileWall.getDisplyItems());
		ingredients.setOutputs(ItemStack.class, Collections.singletonList(new ItemStack(Items.COAL, 9 + pileWall.getCharcoalAmount(), 1)));
	}

}
