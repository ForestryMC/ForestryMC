package forestry.factory.recipes.jei.bottler;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeType;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BottlerRecipeCategory extends ForestryRecipeCategory<BottlerRecipe> {
	private final static ResourceLocation guiTexture = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/bottler.png");

	private final IDrawable icon;
	private final IDrawable slot;
	private final IDrawable tank;
	private final IDrawable arrowDown;
	private final IDrawable tankOverlay;

	public BottlerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createBlankDrawable(62, 60), "block.forestry.bottler");

		this.slot = guiHelper.getSlotDrawable();
		this.tank = guiHelper.createDrawable(guiTexture, 79, 13, 18, 60);
		this.arrowDown = guiHelper.createDrawable(guiTexture, 20, 25, 12, 8);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
		ItemStack bottler = new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.BOTTLER).block());
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, bottler);
	}

	@Override
	public RecipeType<BottlerRecipe> getRecipeType() {
		return ForestryRecipeType.BOTTLER;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BottlerRecipe recipe, IFocusGroup focuses) {
		IRecipeSlotBuilder fillInputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 45, 1)
				.setBackground(slot, -1, -1);

		IRecipeSlotBuilder fillOutputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 45, 43)
				.setBackground(slot, -1, -1);

		IRecipeSlotBuilder drainInputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
				.setBackground(slot, -1, -1);

		IRecipeSlotBuilder drainOutputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 1, 43)
				.setBackground(slot, -1, -1);

		RecipeIngredientRole tankSlotRole;

		if (recipe.fillRecipe()) {
			fillInputSlot.addItemStack(recipe.inputStack());
			if (recipe.outputStack() != null) {
				fillOutputSlot.addItemStack(recipe.outputStack());
			}
			tankSlotRole = RecipeIngredientRole.INPUT;
		} else {
			drainInputSlot.addItemStack(recipe.inputStack());
			if (recipe.outputStack() != null) {
				drainOutputSlot.addItemStack(recipe.outputStack());
			}
			tankSlotRole = RecipeIngredientRole.OUTPUT;
		}

		builder.addSlot(tankSlotRole, 23, 1)
				.setFluidRenderer(10000, false, 16, 58)
				.setBackground(tank, -1, -1)
				.setOverlay(tankOverlay, 0, 0)
				.addIngredient(ForgeTypes.FLUID_STACK, recipe.fluid());
	}

	@Override
	public void draw(BottlerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		arrowDown.draw(stack, 3, 26);
		arrowDown.draw(stack, 47, 26);
	}
}
