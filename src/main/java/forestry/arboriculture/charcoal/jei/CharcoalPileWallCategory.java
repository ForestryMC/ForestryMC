package forestry.arboriculture.charcoal.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CharcoalPileWallCategory extends ForestryRecipeCategory<ICharcoalPileWall> {
	private final IDrawableStatic slot;
	private final IDrawableStatic arrow;
	private final IDrawableAnimated arrowAnimated;
	private final IDrawableStatic flame;
	private final IDrawableAnimated flameAnimated;
	private final IDrawable icon;

	public CharcoalPileWallCategory(IGuiHelper helper) {
		super(helper.createBlankDrawable(120, 38), "for.jei.charcoal.pile");
		ResourceLocation resourceLocation = new ResourceLocation(Constants.MOD_ID, "textures/gui/jei/recipes.png");
		arrow = helper.createDrawable(resourceLocation, 0, 14, 22, 16);
		IDrawableStatic arrowAnimated = helper.createDrawable(resourceLocation, 22, 14, 22, 16);
		this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 160, StartDirection.LEFT, false);
		flame = helper.createDrawable(resourceLocation, 0, 0, 14, 14);
		IDrawableStatic flameAnimated = helper.createDrawable(resourceLocation, 14, 0, 14, 14);
		this.flameAnimated = helper.createAnimatedDrawable(flameAnimated, 260, StartDirection.TOP, true);
		this.slot = helper.getSlotDrawable();
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, CharcoalBlocks.WOOD_PILE.stack());
	}

	@Override
	public RecipeType<ICharcoalPileWall> getRecipeType() {
		return CharcoalJeiPlugin.RECIPE_TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void draw(ICharcoalPileWall recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		flame.draw(stack, 52, 0);
		flameAnimated.draw(stack, 52, 0);
		arrow.draw(stack, 50, 16);
		arrowAnimated.draw(stack, 50, 16);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ICharcoalPileWall recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 17)
				.setBackground(slot, -1, -1)
				.addItemStacks(recipe.getDisplayItems());

		builder.addSlot(RecipeIngredientRole.INPUT, 21, 17)
				.setBackground(slot, -1, -1)
				.addItemStack(CharcoalBlocks.WOOD_PILE.stack());

		int amount = 9 + recipe.getCharcoalAmount();

		ItemStack coal = new ItemStack(Items.CHARCOAL, amount);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 17)
				.setBackground(slot, -1, -1)
				.addItemStack(coal);

		ItemStack ash = CoreItems.ASH.stack(amount / 4);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 105, 17)
				.setBackground(slot, -1, -1)
				.addItemStack(ash);
	}
}
