package forestry.arboriculture.charcoal.jei;

import com.google.common.collect.ImmutableList;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.core.PluginCore;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;

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
		int amount = 9 + pileWall.getCharcoalAmount();
		ItemStack charcoal = new ItemStack(Items.COAL, amount, 1);
		ItemStack ash = new ItemStack(PluginCore.getItems().ash, amount / 4);
		ImmutableList<ItemStack> outputs = ImmutableList.of(
			charcoal,
			ash
		);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

}
