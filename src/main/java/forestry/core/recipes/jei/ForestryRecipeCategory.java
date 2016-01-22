package forestry.core.recipes.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Translator;

public abstract class ForestryRecipeCategory implements IRecipeCategory {

	protected ForestryTooltipCallback tooltip;
	@Nonnull
	protected final IDrawable background;
	@Nonnull
	protected final String localizedName;
	
	public ForestryRecipeCategory(IDrawable background, String unlocalizedName) {
		this.background = background;
		this.localizedName = Translator.translateToLocal(unlocalizedName);
	}
	
	public ForestryRecipeCategory(IDrawable background, String unlocalizedName, int slots) {
		this.background = background;
		this.localizedName = Translator.translateToLocal(unlocalizedName);
		this.tooltip = new ForestryTooltipCallback(slots);
	}

	@Nonnull
	@Override
	public String getTitle() {
		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		if(tooltip != null){
			this.tooltip = new ForestryTooltipCallback(tooltip.getTooltip().length);
		}
	}

}
