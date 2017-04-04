package forestry.arboriculture.charcoal.jei;

import java.util.Collections;

import forestry.api.arboriculture.ICharcoalPileWall;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CharcoalPileWallWrapper extends BlankRecipeWrapper {

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
