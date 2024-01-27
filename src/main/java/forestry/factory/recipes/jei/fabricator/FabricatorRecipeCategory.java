package forestry.factory.recipes.jei.fabricator;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeType;
import forestry.core.utils.JeiUtil;
import forestry.factory.blocks.BlockFactoryPlain;
import forestry.factory.blocks.BlockTypeFactoryPlain;
import forestry.factory.features.FactoryBlocks;
import forestry.modules.features.FeatureBlock;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabricatorRecipeCategory extends ForestryRecipeCategory<IFabricatorRecipe> {
	private final static ResourceLocation guiTexture = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/fabricator.png");
	private final IDrawable icon;
	@Nullable
	private final RecipeManager manager;
	private final ICraftingGridHelper craftingGridHelper;

	public FabricatorRecipeCategory(IGuiHelper guiHelper, @Nullable RecipeManager manager) {
		super(guiHelper.createDrawable(guiTexture, 20, 16, 136, 54), "block.forestry.fabricator");

		FeatureBlock<BlockFactoryPlain, BlockItem> fabricatorFeatureBlock = FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR);
		ItemStack fabricator = new ItemStack(fabricatorFeatureBlock.block());
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, fabricator);
		this.manager = manager;
		this.craftingGridHelper = guiHelper.createCraftingGridHelper();
	}

	private static Map<Fluid, List<IFabricatorSmeltingRecipe>> getSmeltingInputs(@Nullable RecipeManager manager) {
		Map<Fluid, List<IFabricatorSmeltingRecipe>> smeltingInputs = new HashMap<>();
		RecipeManagers.fabricatorSmeltingManager.getRecipes(manager)
				.forEach(smelting -> {
					Fluid fluid = smelting.getProduct().getFluid();
					if (!smeltingInputs.containsKey(fluid)) {
						smeltingInputs.put(fluid, new ArrayList<>());
					}

					smeltingInputs.get(fluid).add(smelting);
				});

		return smeltingInputs;
	}

	@Override
	public RecipeType<IFabricatorRecipe> getRecipeType() {
		return ForestryRecipeType.FABRICATOR;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IFabricatorRecipe recipe, IFocusGroup focuses) {
		Ingredient plan = recipe.getPlan();
		builder.addSlot(RecipeIngredientRole.INPUT, 119, 1)
			.addIngredients(plan);

		FluidStack recipeLiquid = recipe.getLiquid();
		Fluid recipeFluid = recipeLiquid.getFluid();
		List<IFabricatorSmeltingRecipe> smeltingRecipes = getSmeltingInputs(manager).get(recipeFluid);
		List<ItemStack> smeltingInput = smeltingRecipes.stream()
			.flatMap(s -> Arrays.stream(s.getResource().getItems()))
			.toList();

		builder.addSlot(RecipeIngredientRole.INPUT, 6, 5)
			.addItemStacks(smeltingInput);

		builder.addSlot(RecipeIngredientRole.INPUT, 6, 32)
			.setFluidRenderer(2000, false, 16, 16)
			.addIngredient(ForgeTypes.FLUID_STACK, recipeLiquid);

		ShapedRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();
		List<IRecipeSlotBuilder> craftingSlots = JeiUtil.layoutSlotGrid(builder, RecipeIngredientRole.INPUT, 3, 3, 47, 1, 18);
		JeiUtil.setCraftingItems(craftingSlots, craftingGridRecipe, craftingGridHelper);

		builder.addSlot(RecipeIngredientRole.OUTPUT, 119, 37)
				.addItemStack(craftingGridRecipe.getResultItem());
	}
}
