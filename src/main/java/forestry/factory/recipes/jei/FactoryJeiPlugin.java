package forestry.factory.recipes.jei;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Rectangle;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.core.gui.GuiForestry;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.PluginFactory;
import forestry.factory.gui.GuiBottler;
import forestry.factory.gui.GuiCarpenter;
import forestry.factory.gui.GuiCentrifuge;
import forestry.factory.gui.GuiFabricator;
import forestry.factory.gui.GuiFermenter;
import forestry.factory.gui.GuiMoistener;
import forestry.factory.gui.GuiSqueezer;
import forestry.factory.gui.GuiStill;
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
import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeCategory;
import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeHandler;
import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeMaker;
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
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@JEIPlugin
public class FactoryJeiPlugin extends BlankModPlugin {
	@Override
	public void register(@Nonnull IModRegistry registry) {
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(
				new BottlerRecipeCategory(guiHelper),
				new CarpenterRecipeCategory(guiHelper),
				new CentrifugeRecipeCategory(guiHelper),
				new FabricatorRecipeCategory(guiHelper),
				new FermenterRecipeCategory(guiHelper),
				new MoistenerRecipeCategory(guiHelper),
				new RainmakerRecipeCategory(guiHelper),
				new SqueezerRecipeCategory(guiHelper),
				new StillRecipeCategory(guiHelper)
		);
		
		registry.addRecipeHandlers(
				new BottlerRecipeHandler(),
				new CarpenterRecipeHandler(),
				new CentrifugeRecipeHandler(),
				new FabricatorRecipeHandler(),
				new FermenterRecipeHandler(),
				new MoistenerRecipeHandler(),
				new RainmakerRecipeHandler(),
				new SqueezerRecipeHandler<>(SqueezerRecipeWrapper.class),
				new SqueezerRecipeHandler<>(SqueezerContainerRecipeWrapper.class),
				new StillRecipeHandler()
		);

		registry.addRecipes(BottlerRecipeMaker.getBottlerRecipes());
		registry.addRecipes(CarpenterRecipeMaker.getCarpenterRecipes());
		registry.addRecipes(CentrifugeRecipeMaker.getCentrifugeRecipe());
		registry.addRecipes(FabricatorRecipeMaker.getFabricatorRecipes());
		registry.addRecipes(FermenterRecipeMaker.getFermenterRecipes(jeiHelpers.getStackHelper()));
		registry.addRecipes(MoistenerRecipeMaker.getMoistenerRecipes());
		registry.addRecipes(RainmakerRecipeMaker.getRecipes());
		registry.addRecipes(SqueezerRecipeMaker.getSqueezerRecipes());
		registry.addRecipes(SqueezerRecipeMaker.getSqueezerContainerRecipes());
		registry.addRecipes(StillRecipeMaker.getStillRecipes());

		registry.addRecipeClickArea(GuiBottler.class, 80, 37, 26, 22, ForestryRecipeCategoryUid.BOTTLER);
		registry.addRecipeClickArea(GuiCarpenter.class, 98, 48, 21, 26, ForestryRecipeCategoryUid.CARPENTER);
		registry.addRecipeClickArea(GuiCentrifuge.class, 52, 22, 16, 48, ForestryRecipeCategoryUid.CENTRIFUGE);
		registry.addRecipeClickArea(GuiFabricator.class, 121, 53, 18, 18, ForestryRecipeCategoryUid.FABRICATOR);
		registry.addRecipeClickArea(GuiFermenter.class, 72, 40, 32, 18, ForestryRecipeCategoryUid.FERMENTER);
		registry.addRecipeClickArea(GuiMoistener.class, 123, 35, 19, 21, ForestryRecipeCategoryUid.MOISTENER);
		registry.addRecipeClickArea(GuiSqueezer.class, 76, 41, 43, 16, ForestryRecipeCategoryUid.SQUEEZER);
		registry.addRecipeClickArea(GuiStill.class, 73, 17, 33, 57, ForestryRecipeCategoryUid.STILL);

		registry.addRecipeCategoryCraftingItem(new ItemStack(PluginFactory.blocks.bottler), ForestryRecipeCategoryUid.BOTTLER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(PluginFactory.blocks.carpenter), ForestryRecipeCategoryUid.CARPENTER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(PluginFactory.blocks.centrifuge), ForestryRecipeCategoryUid.CENTRIFUGE);
		registry.addRecipeCategoryCraftingItem(new ItemStack(PluginFactory.blocks.fabricator), ForestryRecipeCategoryUid.FABRICATOR);
		registry.addRecipeCategoryCraftingItem(new ItemStack(PluginFactory.blocks.fermenter), ForestryRecipeCategoryUid.FERMENTER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(PluginFactory.blocks.moistener), ForestryRecipeCategoryUid.MOISTENER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(PluginFactory.blocks.rainmaker), ForestryRecipeCategoryUid.RAINMAKER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(PluginFactory.blocks.squeezer), ForestryRecipeCategoryUid.SQUEEZER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(PluginFactory.blocks.still), ForestryRecipeCategoryUid.STILL);

		IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
		transferRegistry.addRecipeTransferHandler(new WorktableRecipeTransferHandler());

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
