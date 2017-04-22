package forestry.arboriculture.charcoal.jei;

import forestry.arboriculture.PluginArboriculture;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CharcoalPileWallCategory extends ForestryRecipeCategory<CharcoalPileWallWrapper> {

	private final IDrawableStatic slot;
	private final IDrawableStatic arrow;
	private final IDrawableAnimated arrowAnimated;
	private final IDrawableStatic flame;
	private final IDrawableAnimated flameAnimated;
	
	public CharcoalPileWallCategory(IGuiHelper helper) {
		super(helper.createBlankDrawable(120, 38), "tile.for.charcoal.pile.wall.name");
		ResourceLocation resourceLocation = new ResourceLocation(Constants.MOD_ID, "textures/gui/jei/charcoal_pile_wall.png");
		arrow = helper.createDrawable(resourceLocation, 0, 14, 22, 16);
		IDrawableStatic arrowAnimated = helper.createDrawable(resourceLocation, 22, 14, 22, 16);
		this.arrowAnimated = helper.createAnimatedDrawable(arrowAnimated, 160, StartDirection.LEFT, false);
		flame = helper.createDrawable(resourceLocation, 0, 0, 14, 14);
		IDrawableStatic flameAnimated = helper.createDrawable(resourceLocation, 14, 0, 14, 14);
		this.flameAnimated = helper.createAnimatedDrawable(flameAnimated, 260, StartDirection.TOP, true);
		this.slot = helper.getSlotDrawable();
	}

	@Override
	public String getUid() {
		return CharcoalJeiPlugin.RECIPE_UID;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		flame.draw(minecraft, 52, 0);
		flameAnimated.draw(minecraft, 52, 0);
		arrow.draw(minecraft, 50, 16);
		arrowAnimated.draw(minecraft, 50, 16);
		slot.draw(minecraft, 0, 16);
		slot.draw(minecraft, 20, 16);
		slot.draw(minecraft, 84, 16);
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CharcoalPileWallWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
		itemStackGroup.init(0, true, 0, 16);
		itemStackGroup.init(1, true, 20, 16);
		itemStackGroup.init(2, false, 84, 16);
		itemStackGroup.set(0, ingredients.getInputs(ItemStack.class).get(0));
		itemStackGroup.set(1, new ItemStack(PluginArboriculture.getBlocks().woodPile));
		itemStackGroup.set(2, ingredients.getOutputs(ItemStack.class).get(0));
	}

}
