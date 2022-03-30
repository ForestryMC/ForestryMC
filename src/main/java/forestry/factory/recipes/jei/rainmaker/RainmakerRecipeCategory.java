package forestry.factory.recipes.jei.rainmaker;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.fuels.RainSubstrate;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeType;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class RainmakerRecipeCategory extends ForestryRecipeCategory<RainSubstrate> {
	private final IDrawable slot;
	private final IDrawable icon;

	public RainmakerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createBlankDrawable(150, 30), "block.forestry.rainmaker");
		this.slot = guiHelper.getSlotDrawable();
		ItemStack rainmaker = new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.RAINMAKER).block());
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, rainmaker);
	}

	@SuppressWarnings("removal")
	@Override
	public ResourceLocation getUid() {
		return ForestryRecipeType.RAINMAKER.getUid();
	}

	@SuppressWarnings("removal")
	@Override
	public Class<? extends RainSubstrate> getRecipeClass() {
		return ForestryRecipeType.RAINMAKER.getRecipeClass();
	}

	@Override
	public RecipeType<RainSubstrate> getRecipeType() {
		return ForestryRecipeType.RAINMAKER;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RainSubstrate recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
				.setBackground(slot, -1, -1)
				.addItemStack(recipe.getItem());
	}

	@Override
	public void draw(RainSubstrate recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		TranslatableComponent effect = getEffectString(recipe);
		TranslatableComponent speed = new TranslatableComponent("for.jei.rainmaker.speed", recipe.getSpeed());

		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		fontRenderer.draw(stack, effect, 24, 0, Color.darkGray.getRGB());
		fontRenderer.draw(stack, speed, 24, 10, Color.gray.getRGB());
		if (!recipe.isReverse()) {
			TranslatableComponent duration = new TranslatableComponent("for.jei.rainmaker.duration", recipe.getDuration());
			fontRenderer.draw(stack, duration, 24, 20, Color.gray.getRGB());
		}
	}

	private static TranslatableComponent getEffectString(RainSubstrate recipe) {
		if (recipe.isReverse()) {
			return new TranslatableComponent("for.jei.rainmaker.stops.rain");
		} else {
			return new TranslatableComponent("for.jei.rainmaker.causes.rain");
		}
	}
}
