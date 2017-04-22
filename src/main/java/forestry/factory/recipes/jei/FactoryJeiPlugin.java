package forestry.factory.recipes.jei;

import javax.annotation.Nullable;
import java.awt.Rectangle;
import java.util.List;

import com.google.common.base.Preconditions;
import forestry.api.core.ForestryAPI;
import forestry.core.gui.GuiForestry;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.utils.JeiUtil;
import forestry.factory.PluginFactory;
import forestry.factory.blocks.BlockRegistryFactory;
import forestry.factory.gui.GuiBottler;
import forestry.factory.gui.GuiCarpenter;
import forestry.factory.gui.GuiCentrifuge;
import forestry.factory.gui.GuiFabricator;
import forestry.factory.gui.GuiFermenter;
import forestry.factory.gui.GuiMoistener;
import forestry.factory.gui.GuiSqueezer;
import forestry.factory.gui.GuiStill;
import forestry.factory.recipes.jei.bottler.BottlerRecipeCategory;
import forestry.factory.recipes.jei.bottler.BottlerRecipeMaker;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeCategory;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeMaker;
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeTransferHandler;
import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeCategory;
import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeMaker;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeCategory;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeMaker;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeTransferHandler;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeCategory;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeMaker;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeCategory;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeMaker;
import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeCategory;
import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeMaker;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeCategory;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeMaker;
import forestry.factory.recipes.jei.still.StillRecipeCategory;
import forestry.factory.recipes.jei.still.StillRecipeMaker;
import forestry.plugins.ForestryPluginUids;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.BlankAdvancedGuiHandler;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@JEIPlugin
@SideOnly(Side.CLIENT)
public class FactoryJeiPlugin extends BlankModPlugin {
	@Nullable
	public static IJeiHelpers jeiHelpers;
	
	@Override
	public void register(IModRegistry registry) {
		registry.addAdvancedGuiHandlers(new ForestryAdvancedGuiHandler());
		
		if(!ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)){
			return;
		}
		
		jeiHelpers = registry.getJeiHelpers();
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

		registry.addRecipes(BottlerRecipeMaker.getBottlerRecipes(registry.getIngredientRegistry()), ForestryRecipeCategoryUid.BOTTLER);
		registry.addRecipes(CarpenterRecipeMaker.getCarpenterRecipes(), ForestryRecipeCategoryUid.CARPENTER);
		registry.addRecipes(CentrifugeRecipeMaker.getCentrifugeRecipe(), ForestryRecipeCategoryUid.CENTRIFUGE);
		registry.addRecipes(FabricatorRecipeMaker.getFabricatorRecipes(), ForestryRecipeCategoryUid.FABRICATOR);
		registry.addRecipes(FermenterRecipeMaker.getFermenterRecipes(jeiHelpers.getStackHelper()), ForestryRecipeCategoryUid.FERMENTER);
		registry.addRecipes(MoistenerRecipeMaker.getMoistenerRecipes(), ForestryRecipeCategoryUid.MOISTENER);
		registry.addRecipes(RainmakerRecipeMaker.getRecipes(), ForestryRecipeCategoryUid.RAINMAKER);
		registry.addRecipes(SqueezerRecipeMaker.getSqueezerRecipes(), ForestryRecipeCategoryUid.SQUEEZER);
		registry.addRecipes(SqueezerRecipeMaker.getSqueezerContainerRecipes(registry.getIngredientRegistry()), ForestryRecipeCategoryUid.SQUEEZER);
		registry.addRecipes(StillRecipeMaker.getStillRecipes(), ForestryRecipeCategoryUid.STILL);

		registry.addRecipeClickArea(GuiBottler.class, 107, 33, 26, 22, ForestryRecipeCategoryUid.BOTTLER);
		registry.addRecipeClickArea(GuiBottler.class, 45, 33, 26, 22, ForestryRecipeCategoryUid.BOTTLER);
		registry.addRecipeClickArea(GuiCarpenter.class, 98, 48, 21, 26, ForestryRecipeCategoryUid.CARPENTER);
		registry.addRecipeClickArea(GuiCentrifuge.class, 38, 22, 38, 14, ForestryRecipeCategoryUid.CENTRIFUGE);
		registry.addRecipeClickArea(GuiCentrifuge.class, 38, 54, 38, 14, ForestryRecipeCategoryUid.CENTRIFUGE);
		registry.addRecipeClickArea(GuiFabricator.class, 121, 53, 18, 18, ForestryRecipeCategoryUid.FABRICATOR);
		registry.addRecipeClickArea(GuiFermenter.class, 72, 40, 32, 18, ForestryRecipeCategoryUid.FERMENTER);
		registry.addRecipeClickArea(GuiMoistener.class, 123, 35, 19, 21, ForestryRecipeCategoryUid.MOISTENER);
		registry.addRecipeClickArea(GuiSqueezer.class, 76, 41, 43, 16, ForestryRecipeCategoryUid.SQUEEZER);
		registry.addRecipeClickArea(GuiStill.class, 73, 17, 33, 57, ForestryRecipeCategoryUid.STILL);

		BlockRegistryFactory blocks = PluginFactory.getBlocks();
		Preconditions.checkNotNull(blocks);

		registry.addRecipeCategoryCraftingItem(new ItemStack(blocks.bottler), ForestryRecipeCategoryUid.BOTTLER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(blocks.carpenter), ForestryRecipeCategoryUid.CARPENTER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(blocks.centrifuge), ForestryRecipeCategoryUid.CENTRIFUGE);
		registry.addRecipeCategoryCraftingItem(new ItemStack(blocks.fabricator), ForestryRecipeCategoryUid.FABRICATOR);
		registry.addRecipeCategoryCraftingItem(new ItemStack(blocks.fermenter), ForestryRecipeCategoryUid.FERMENTER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(blocks.moistener), ForestryRecipeCategoryUid.MOISTENER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(blocks.rainmaker), ForestryRecipeCategoryUid.RAINMAKER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(blocks.squeezer), ForestryRecipeCategoryUid.SQUEEZER);
		registry.addRecipeCategoryCraftingItem(new ItemStack(blocks.still), ForestryRecipeCategoryUid.STILL);
		registry.addRecipeCategoryCraftingItem(new ItemStack(blocks.worktable), VanillaRecipeCategoryUid.CRAFTING);

		IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
		transferRegistry.addRecipeTransferHandler(new WorktableRecipeTransferHandler(), VanillaRecipeCategoryUid.CRAFTING);
		transferRegistry.addRecipeTransferHandler(new CarpenterRecipeTransferHandler(), ForestryRecipeCategoryUid.CARPENTER);
		transferRegistry.addRecipeTransferHandler(new FabricatorRecipeTransferHandler(), ForestryRecipeCategoryUid.FABRICATOR);

		JeiUtil.addDescription(registry,
				blocks.raintank,
				blocks.worktable
		);
	}

	private static class ForestryAdvancedGuiHandler extends BlankAdvancedGuiHandler<GuiForestry> {

		@Override
		public Class<GuiForestry> getGuiContainerClass() {
			return GuiForestry.class;
		}

		@Nullable
		@Override
		public List<Rectangle> getGuiExtraAreas(GuiForestry guiContainer) {
			return ((GuiForestry<?>) guiContainer).getExtraGuiAreas();
		}

		@Nullable
		@Override
		public Object getIngredientUnderMouse(GuiForestry guiContainer, int mouseX, int mouseY) {
			return guiContainer.getFluidStackAtPosition(mouseX, mouseY);
		}
	}
}
