package forestry.core.recipes.jei;

import forestry.core.config.Constants;
import forestry.core.utils.Translator;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class ForestryRecipeCategory<T extends IRecipeCategoryExtension> implements IRecipeCategory<T> {
    private final IDrawable background;
    private final String localizedName;

    public ForestryRecipeCategory(IDrawable background, String unlocalizedName) {
        this.background = background;
        this.localizedName = new TranslationTextComponent(unlocalizedName).getString();
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }
}
