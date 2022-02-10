package forestry.factory.recipes.jei;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.RainSubstrate;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.blocks.BlockFactoryPlain;
import forestry.factory.recipes.jei.bottler.BottlerRecipe;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeWrapper;
import forestry.factory.recipes.jei.moistener.MoistenerRecipe;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import forestry.core.config.Constants;
import forestry.core.features.FluidsItems;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.GuiForestry;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.utils.ClientUtils;
import forestry.core.utils.JeiUtil;
import forestry.factory.ModuleFactory;
import forestry.factory.blocks.BlockTypeFactoryPlain;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
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
import forestry.factory.recipes.jei.carpenter.CarpenterRecipeTransferHandler;
import forestry.factory.recipes.jei.centrifuge.CentrifugeRecipeCategory;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeCategory;
import forestry.factory.recipes.jei.fabricator.FabricatorRecipeTransferHandler;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeCategory;
import forestry.factory.recipes.jei.fermenter.FermenterRecipeMaker;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeCategory;
import forestry.factory.recipes.jei.moistener.MoistenerRecipeMaker;
import forestry.factory.recipes.jei.rainmaker.RainmakerRecipeCategory;
import forestry.factory.recipes.jei.squeezer.SqueezerRecipeCategory;
import forestry.factory.recipes.jei.still.StillRecipeCategory;
import forestry.modules.ForestryModuleUids;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;

@JeiPlugin
@OnlyIn(Dist.CLIENT)
public class FactoryJeiPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FACTORY);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		List<IRecipeCategory<?>> categories = new ArrayList<>();

		if (ModuleFactory.machineEnabled()) {
			categories.add(new BottlerRecipeCategory(guiHelper));
		}

		if (ModuleFactory.machineEnabled()) {
			categories.add(new CarpenterRecipeCategory(guiHelper));
		}

		if (ModuleFactory.machineEnabled()) {
			categories.add(new CentrifugeRecipeCategory(guiHelper));
		}

		if (ModuleFactory.machineEnabled()) {
			RecipeManager recipeManager = ClientUtils.getRecipeManager();
			categories.add(new FabricatorRecipeCategory(guiHelper, recipeManager));
		}

		if (ModuleFactory.machineEnabled()) {
			categories.add(new FermenterRecipeCategory(guiHelper));
		}

		if (ModuleFactory.machineEnabled()) {
			categories.add(new MoistenerRecipeCategory(guiHelper));
		}

		if (ModuleFactory.machineEnabled()) {
			categories.add(new RainmakerRecipeCategory(guiHelper));
		}

		if (ModuleFactory.machineEnabled()) {
			categories.add(new SqueezerRecipeCategory(guiHelper));
		}

		if (ModuleFactory.machineEnabled()) {
			categories.add(new StillRecipeCategory(guiHelper));
		}

		registry.addRecipeCategories(categories.toArray(new IRecipeCategory[0]));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		RecipeManager recipeManager = ClientUtils.getRecipeManager();

		if (ModuleFactory.machineEnabled()) {
			List<BottlerRecipe> recipes = BottlerRecipeMaker.getBottlerRecipes(registry.getIngredientManager());
			registry.addRecipes(recipes, ForestryRecipeCategoryUid.BOTTLER);
		}

		if (ModuleFactory.machineEnabled()) {
			Collection<ICarpenterRecipe> recipes = RecipeManagers.carpenterManager.getRecipes(recipeManager);
			registry.addRecipes(recipes, ForestryRecipeCategoryUid.CARPENTER);
		}

		if (ModuleFactory.machineEnabled()) {
			Collection<ICentrifugeRecipe> recipes = RecipeManagers.centrifugeManager.getRecipes(recipeManager);
			registry.addRecipes(recipes, ForestryRecipeCategoryUid.CENTRIFUGE);
		}

		if (ModuleFactory.machineEnabled()) {
			Collection<IFabricatorRecipe> recipes = RecipeManagers.fabricatorManager.getRecipes(recipeManager);
			registry.addRecipes(recipes, ForestryRecipeCategoryUid.FABRICATOR);
		}

		if (ModuleFactory.machineEnabled()) {
			List<FermenterRecipeWrapper> recipes = FermenterRecipeMaker.getFermenterRecipes(recipeManager);
			registry.addRecipes(recipes, ForestryRecipeCategoryUid.FERMENTER);
		}

		if (ModuleFactory.machineEnabled()) {
			List<MoistenerRecipe> recipes = MoistenerRecipeMaker.getMoistenerRecipes(recipeManager);
			registry.addRecipes(recipes, ForestryRecipeCategoryUid.MOISTENER);
		}

		if (ModuleFactory.machineEnabled()) {
			Collection<RainSubstrate> recipes = FuelManager.rainSubstrate.values();
			registry.addRecipes(recipes, ForestryRecipeCategoryUid.RAINMAKER);
		}

		if (ModuleFactory.machineEnabled()) {
			Collection<ISqueezerRecipe> recipes = RecipeManagers.squeezerManager.getRecipes(recipeManager);
			registry.addRecipes(recipes, ForestryRecipeCategoryUid.SQUEEZER);
		}

		if (ModuleFactory.machineEnabled()) {
			Collection<IStillRecipe> recipes = RecipeManagers.stillManager.getRecipes(recipeManager);
			registry.addRecipes(recipes, ForestryRecipeCategoryUid.STILL);
		}

		if (ModuleFactory.machineEnabled()) {
			BlockFactoryPlain rainTank = FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).getBlock();
			JeiUtil.addDescription(registry, rainTank);
		}
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeTransferHandler(new CarpenterRecipeTransferHandler(), ForestryRecipeCategoryUid.CARPENTER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeTransferHandler(new FabricatorRecipeTransferHandler(), ForestryRecipeCategoryUid.FABRICATOR);
		}
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeCatalyst(new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.BOTTLER).block()), ForestryRecipeCategoryUid.BOTTLER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeCatalyst(new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CARPENTER).block()), ForestryRecipeCategoryUid.CARPENTER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeCatalyst(new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CENTRIFUGE).block()), ForestryRecipeCategoryUid.CENTRIFUGE);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeCatalyst(new ItemStack(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR).block()), ForestryRecipeCategoryUid.FABRICATOR);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeCatalyst(new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.FERMENTER).block()), ForestryRecipeCategoryUid.FERMENTER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeCatalyst(new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.MOISTENER).block()), ForestryRecipeCategoryUid.MOISTENER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeCatalyst(new ItemStack(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).block()), ForestryRecipeCategoryUid.RAINMAKER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeCatalyst(new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.SQUEEZER).block()), ForestryRecipeCategoryUid.SQUEEZER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeCatalyst(new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.STILL).block()), ForestryRecipeCategoryUid.STILL);
		}
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		registry.addGenericGuiContainerHandler(GuiForestry.class, new ForestryAdvancedGuiHandler<ContainerForestry>());

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeClickArea(GuiBottler.class, 107, 33, 26, 22, ForestryRecipeCategoryUid.BOTTLER);
			registry.addRecipeClickArea(GuiBottler.class, 45, 33, 26, 22, ForestryRecipeCategoryUid.BOTTLER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeClickArea(GuiCarpenter.class, 98, 48, 21, 26, ForestryRecipeCategoryUid.CARPENTER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeClickArea(GuiCentrifuge.class, 38, 22, 38, 14, ForestryRecipeCategoryUid.CENTRIFUGE);
			registry.addRecipeClickArea(GuiCentrifuge.class, 38, 54, 38, 14, ForestryRecipeCategoryUid.CENTRIFUGE);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeClickArea(GuiFabricator.class, 121, 53, 18, 18, ForestryRecipeCategoryUid.FABRICATOR);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeClickArea(GuiFermenter.class, 72, 40, 32, 18, ForestryRecipeCategoryUid.FERMENTER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeClickArea(GuiMoistener.class, 123, 35, 19, 21, ForestryRecipeCategoryUid.MOISTENER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeClickArea(GuiSqueezer.class, 76, 41, 43, 16, ForestryRecipeCategoryUid.SQUEEZER);
		}

		if (ModuleFactory.machineEnabled()) {
			registry.addRecipeClickArea(GuiStill.class, 73, 17, 33, 57, ForestryRecipeCategoryUid.STILL);
		}
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
		IIngredientSubtypeInterpreter<ItemStack> subtypeInterpreter = (itemStack, context) -> {
			LazyOptional<IFluidHandlerItem> fluidHandler = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
			return fluidHandler.map(handler -> handler.getFluidInTank(0))
					.map(fluid -> fluid.getFluid().getRegistryName().toString())
					.orElse(IIngredientSubtypeInterpreter.NONE);
		};

		for (Item container : FluidsItems.CONTAINERS.itemArray()) {
			subtypeRegistry.registerSubtypeInterpreter(container, subtypeInterpreter);
		}
	}


	static class ForestryAdvancedGuiHandler<T extends AbstractContainerMenu> implements IGuiContainerHandler<GuiForestry<?>> {
		@Override
		public List<Rect2i> getGuiExtraAreas(GuiForestry<?> guiContainer) {
			return guiContainer.getExtraGuiAreas();
		}

		@Nullable
		@Override
		public Object getIngredientUnderMouse(GuiForestry<?> guiContainer, double mouseX, double mouseY) {
			return guiContainer.getFluidStackAtPosition(mouseX, mouseY);
		}
	}
}
