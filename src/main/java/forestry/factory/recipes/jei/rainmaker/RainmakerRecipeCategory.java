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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;

public class RainmakerRecipeCategory extends ForestryRecipeCategory<RainSubstrate> {
	private final IDrawable slot;
	private final IDrawable icon;

	public RainmakerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createBlankDrawable(150, 30), "block.forestry.rainmaker");
		this.slot = guiHelper.getSlotDrawable();
		ItemStack rainmaker = new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.RAINMAKER).block());
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, rainmaker);
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
		Component effect = getEffectString(recipe);
		Component speed = Component.translatable("for.jei.rainmaker.speed", recipe.getSpeed());

		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		fontRenderer.draw(stack, effect, 24, 0, Color.darkGray.getRGB());
		fontRenderer.draw(stack, speed, 24, 10, Color.gray.getRGB());
		if (!recipe.isReverse()) {
			Component duration = Component.translatable("for.jei.rainmaker.duration", recipe.getDuration());
			fontRenderer.draw(stack, duration, 24, 20, Color.gray.getRGB());
		}
	}

	private static Component getEffectString(RainSubstrate recipe) {
		if (recipe.isReverse()) {
			return Component.translatable("for.jei.rainmaker.stops.rain");
		} else {
			return Component.translatable("for.jei.rainmaker.causes.rain");
		}
	}
}
