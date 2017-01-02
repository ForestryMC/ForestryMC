package forestry.arboriculture.charcoal.jei;

import forestry.arboriculture.charcoal.CharcoalPileWall;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CharcoalPileWallHandler implements IRecipeHandler<CharcoalPileWall> {

	private IGuiHelper guiHelper;
	
	public CharcoalPileWallHandler(IGuiHelper guiHelper) {
		this.guiHelper = guiHelper;
	}
	
	@Override
	public Class<CharcoalPileWall> getRecipeClass() {
		return CharcoalPileWall.class;
	}

	@Override
	public String getRecipeCategoryUid(CharcoalPileWall recipe) {
		return CharcoalJeiPlugin.RECIPE_UID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CharcoalPileWall recipe) {
		return new CharcoalPileWallWrapper(recipe, guiHelper);
	}

	@Override
	public boolean isRecipeValid(CharcoalPileWall recipe) {
		if(recipe.getDisplyItems().isEmpty()){
			return false;
		}
		if(recipe.getCharcoalAmount() <= 0) {
			return false;
		}
		return true;
	}

}
