package forestry.factory.recipes.jei;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Rectangle;
import java.util.List;

import forestry.core.gui.GuiForestry;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.gui.ContainerWorktable;
import forestry.factory.recipes.jei.bottler.BottlerRecipeCategory;
import forestry.factory.recipes.jei.bottler.BottlerRecipeHandler;
import forestry.factory.recipes.jei.bottler.BottlerRecipeMaker;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeCategory;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeHandler;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeMaker;
import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeCategory;
import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeHandler;
import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeMaker;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeCategory;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeHandler;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeMaker;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeCategory;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeHandler;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeMaker;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeCategory;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeHandler;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeMaker;
import forestry.factory.recipes.jei.squeezer.SqueezerContainerRecipeWrapper;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeCategory;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeHandler;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeMaker;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeWrapper;
import forestry.factory.recipes.jei.still.StillRecipeCategory;
import forestry.factory.recipes.jei.still.StillRecipeHandler;
import forestry.factory.recipes.jei.still.StillRecipeMaker;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@JEIPlugin
public class FactoryJeiPlugin extends BlankModPlugin {
	@Override
	public void register(@Nonnull IModRegistry registry) {
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new BottlerRecipeCategory(guiHelper),
				new CarpenterRecipeCategory(guiHelper),
				new CentrifugeRecipeCategory(guiHelper),
				new FabricatorRecipeCategory(guiHelper),
				new FermenterRecipeCategory(guiHelper),
				new MoistenerRecipeCategory(guiHelper),
				new SqueezerRecipeCategory(guiHelper, ForestryRecipeCategoryUid.SQUEEZER),
				new StillRecipeCategory(guiHelper));
		
		registry.addRecipeHandlers(new BottlerRecipeHandler(),
				new CarpenterRecipeHandler(),
				new CentrifugeRecipeHandler(),
				new FabricatorRecipeHandler(),
				new FermenterRecipeHandler(),
				new MoistenerRecipeHandler(),
				new SqueezerRecipeHandler<>(SqueezerRecipeWrapper.class, ForestryRecipeCategoryUid.SQUEEZER),
				new SqueezerRecipeHandler<>(SqueezerContainerRecipeWrapper.class, ForestryRecipeCategoryUid.SQUEEZER),
				new StillRecipeHandler());

		registry.addRecipes(BottlerRecipeMaker.getBottlerRecipes());
		registry.addRecipes(CarpenterRecipeMaker.getCarpenterRecipes());
		registry.addRecipes(CentrifugeRecipeMaker.getCentrifugeRecipe());
		registry.addRecipes(FabricatorRecipeMaker.getFabricatorRecipes());
		registry.addRecipes(FermenterRecipeMaker.getFermenterRecipes());
		registry.addRecipes(MoistenerRecipeMaker.getMoistenerRecipes());
		registry.addRecipes(SqueezerRecipeMaker.getSqueezerRecipes());
		registry.addRecipes(SqueezerRecipeMaker.getSqueezerContainerRecipes());
		registry.addRecipes(StillRecipeMaker.getStillRecipes());

		IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
		transferRegistry.addRecipeTransferHandler(ContainerWorktable.class, VanillaRecipeCategoryUid.CRAFTING, 19, 9, 1, 36);

		registry.addAdvancedGuiHandlers(new ForestryAdvancedGuiHandler());
	}

	private static class ForestryAdvancedGuiHandler implements IAdvancedGuiHandler<GuiForestry> {
		@Nonnull
		@Override
		public Class<GuiForestry> getGuiContainerClass() {
			return GuiForestry.class;
		}

		@Nullable
		@Override
		public List<Rectangle> getGuiExtraAreas(GuiForestry guiContainer) {
			GuiForestry<?, ?> guiForestry = guiContainer;
			return guiForestry.getExtraGuiAreas();
		}
	}
}
