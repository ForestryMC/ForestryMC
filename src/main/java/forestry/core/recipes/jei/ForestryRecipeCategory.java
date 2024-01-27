package forestry.core.recipes.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;

public abstract class ForestryRecipeCategory<T> implements IRecipeCategory<T> {
	private final IDrawable background;
	private final String localizedName;

	public ForestryRecipeCategory(IDrawable background, String unlocalizedName) {
		this.background = background;
		this.localizedName = Component.translatable(unlocalizedName).getString();
	}

	@Override
	public Component getTitle() {
		return Component.translatable(localizedName);
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	abstract public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses);
}
