package forestry.core.data;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.crafting.Ingredient;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeRecipeProvider;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.IWoodAccess;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.modules.ForestryModuleUids;

public class ForestryRecipeProvider extends ForgeRecipeProvider {

	public ForestryRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		IWoodAccess woodAccess = TreeManager.woodAccess;
		List<IWoodType> woodTypes = woodAccess.getRegisteredWoodTypes();
		RecipeDataHelper recipeDataHelper = new RecipeDataHelper(consumer);

		for (IWoodType woodType : woodTypes) {

			Block planks = woodAccess.getBlock(woodType, WoodBlockKind.PLANKS, false).getBlock();
			Block fireproofPlanks = woodAccess.getBlock(woodType, WoodBlockKind.PLANKS, true).getBlock();
			Block log = woodAccess.getBlock(woodType, WoodBlockKind.LOG, false).getBlock();
			Block fireproofLog = woodAccess.getBlock(woodType, WoodBlockKind.LOG, true).getBlock();
			Block door = woodAccess.getBlock(woodType, WoodBlockKind.DOOR, false).getBlock();
			Block fence = woodAccess.getBlock(woodType, WoodBlockKind.FENCE, false).getBlock();
			Block fireproofFence = woodAccess.getBlock(woodType, WoodBlockKind.FENCE, true).getBlock();
			Block fencegate = woodAccess.getBlock(woodType, WoodBlockKind.FENCE_GATE, false).getBlock();
			Block fireproofFencegate = woodAccess.getBlock(woodType, WoodBlockKind.FENCE_GATE, true).getBlock();
			Block slab = woodAccess.getBlock(woodType, WoodBlockKind.SLAB, false).getBlock();
			Block fireproofSlab = woodAccess.getBlock(woodType, WoodBlockKind.SLAB, true).getBlock();
			Block stairs = woodAccess.getBlock(woodType, WoodBlockKind.STAIRS, false).getBlock();
			Block fireproofStairs = woodAccess.getBlock(woodType, WoodBlockKind.STAIRS, true).getBlock();

			if (woodType instanceof EnumForestryWoodType) {
				recipeDataHelper.moduleConditionRecipe(
						ShapelessRecipeBuilder.shapelessRecipe(planks, 4).addIngredient(log).addCriterion("has_log", this.hasItem(log)).setGroup("planks")::build,
						ForestryModuleUids.ARBORICULTURE);
				recipeDataHelper.moduleConditionRecipe(
						ShapedRecipeBuilder.shapedRecipe(fence, 3).key('#', Tags.Items.RODS_WOODEN).key('W', planks).patternLine("W#W").patternLine("W#W").addCriterion("has_planks", this.hasItem(planks)).setGroup("wooden_fence")::build,
						ForestryModuleUids.ARBORICULTURE);
				recipeDataHelper.moduleConditionRecipe(
						ShapedRecipeBuilder.shapedRecipe(fencegate).key('#', Tags.Items.RODS_WOODEN).key('W', planks).patternLine("#W#").patternLine("#W#").addCriterion("has_planks", this.hasItem(planks)).setGroup("wooden_fence_gate")::build,
						ForestryModuleUids.ARBORICULTURE);
				recipeDataHelper.moduleConditionRecipe(
						ShapedRecipeBuilder.shapedRecipe(slab, 6).key('#', planks).patternLine("###").addCriterion("has_planks", this.hasItem(planks)).setGroup("wooden_slab")::build,
						ForestryModuleUids.ARBORICULTURE);
				recipeDataHelper.moduleConditionRecipe(
						ShapedRecipeBuilder.shapedRecipe(stairs, 4).key('#', planks).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_planks", this.hasItem(planks)).setGroup("wooden_stairs")::build,
						ForestryModuleUids.ARBORICULTURE);
			}

			recipeDataHelper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(door, 3).key('#', Ingredient.fromItems(planks, fireproofPlanks)).patternLine("##").patternLine("##").patternLine("##").addCriterion("has_planks", this.hasItem(planks)).setGroup("wooden_door")::build,
					ForestryModuleUids.ARBORICULTURE);


			recipeDataHelper.moduleConditionRecipe(
					ShapelessRecipeBuilder.shapelessRecipe(fireproofPlanks, 4).addIngredient(fireproofLog).addCriterion("has_planks", this.hasItem(fireproofPlanks)).setGroup("planks")::build,
					ForestryModuleUids.ARBORICULTURE);
			recipeDataHelper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(fireproofFence, 3).key('#', Tags.Items.RODS_WOODEN).key('W', fireproofPlanks).patternLine("W#W").patternLine("W#W").addCriterion("has_planks", this.hasItem(fireproofPlanks)).setGroup("wooden_fence")::build,
					ForestryModuleUids.ARBORICULTURE);
			recipeDataHelper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(fireproofFencegate).key('#', Tags.Items.RODS_WOODEN).key('W', fireproofPlanks).patternLine("#W#").patternLine("#W#").addCriterion("has_planks", this.hasItem(fireproofPlanks)).setGroup("wooden_fence_gate")::build,
					ForestryModuleUids.ARBORICULTURE);
			recipeDataHelper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(fireproofSlab, 6).key('#', fireproofPlanks).patternLine("###").addCriterion("has_planks", this.hasItem(fireproofPlanks)).setGroup("wooden_slab")::build,
					ForestryModuleUids.ARBORICULTURE);
			recipeDataHelper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(fireproofStairs, 4).key('#', fireproofPlanks).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_planks", this.hasItem(fireproofPlanks)).setGroup("wooden_stairs")::build,
					ForestryModuleUids.ARBORICULTURE);

		}
	}
}
