package forestry.factory.recipes.jei.rainmaker;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.fuels.RainSubstrate;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.Collections;

public class RainmakerRecipeWrapper implements IRecipeCategoryExtension {
    private final RainSubstrate substrate;

    public RainmakerRecipeWrapper(RainSubstrate substrate) {
        this.substrate = substrate;
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Collections.singletonList(substrate.getItem()));
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, MatrixStack matrixStack, double mouseX, double mouseY) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        fontRenderer.func_243248_b(matrixStack, getEffectString(), 24, 0, Color.darkGray.getRGB());
        fontRenderer.func_243248_b(
                matrixStack,
                new TranslationTextComponent("for.jei.rainmaker.speed", substrate.getSpeed()),
                24,
                10,
                Color.gray.getRGB()
        );
        if (!substrate.isReverse()) {
            fontRenderer.func_243248_b(
                    matrixStack,
                    new TranslationTextComponent("for.jei.rainmaker.duration", substrate.getDuration()),
                    24,
                    20,
                    Color.gray.getRGB()
            );
        }
    }

    private TranslationTextComponent getEffectString() {
        if (substrate.isReverse()) {
            return new TranslationTextComponent("for.jei.rainmaker.stops.rain");
        } else {
            return new TranslationTextComponent("for.jei.rainmaker.causes.rain");
        }
    }
}
