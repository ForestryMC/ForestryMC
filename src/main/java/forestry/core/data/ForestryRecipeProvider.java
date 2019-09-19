/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.data;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.common.data.ForgeRecipeProvider;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.IWoodAccess;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.apiculture.blocks.BlockAlveary;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.blocks.BlockTypeApiculture;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumHoneyDrop;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.EnumPropolis;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.climatology.features.ClimatologyBlocks;
import forestry.climatology.features.ClimatologyItems;
import forestry.core.blocks.BlockTypeCoreTesr;
import forestry.core.blocks.EnumResourceType;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.features.FluidsItems;
import forestry.core.items.EnumContainerType;
import forestry.core.items.EnumCraftingMaterial;
import forestry.core.items.EnumElectronTube;
import forestry.core.recipes.ModuleEnabledCondition;
import forestry.food.features.FoodItems;
import forestry.lepidopterology.features.LepidopterologyBlocks;
import forestry.modules.ForestryModuleUids;
import forestry.storage.features.BackpackItems;

public class ForestryRecipeProvider extends ForgeRecipeProvider {

	public ForestryRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		RecipeDataHelper helper = new RecipeDataHelper(consumer);
		registerArboricultureRecipes(helper);
		registerApicultureRecipes(helper);
		registerFoodRecipes(helper);
		registerBackpackRecipes(helper);
		registerCharcoalRecipes(helper);
		addClimatologyRecipes(helper);
		registerCoreRecipes(helper);
	}

	private void registerApicultureRecipes(RecipeDataHelper helper) {
		registerCombRecipes(helper);
		
		BlockAlveary plain = ApicultureBlocks.ALVEARY.get(BlockAlvearyType.PLAIN).getBlock();
		Item goldElectronTube = CoreItems.ELECTRON_TUBES.get(EnumElectronTube.GOLD).getItem();


		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(plain)
						.key('X', CoreItems.IMPREGNATED_CASING.getItem())
						.key('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SCENTED_PANELING).getItem())
						.patternLine("###").patternLine("#X#").patternLine("###")
						.addCriterion("has_casing", this.hasItem(CoreItems.IMPREGNATED_CASING.getItem()))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.FAN).getBlock())
						.key('#', goldElectronTube)
						.key('X', plain)
						.key('I', Tags.Items.INGOTS_IRON)
						.patternLine("I I").patternLine(" X ").patternLine("I#I")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.HEATER).getBlock())
						.key('#', goldElectronTube)
						.key('I', Tags.Items.INGOTS_IRON)
						.key('X', plain)
						.key('S', Tags.Items.STONE)
						.patternLine("#I#").patternLine(" X ").patternLine("SSS")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.HYGRO).getBlock())
						.key('G', Tags.Items.GLASS)
						.key('X', plain)
						.key('I', Tags.Items.INGOTS_IRON)
						.patternLine("GIG").patternLine("GXG").patternLine("GIG")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.SIEVE).getBlock())
						.key('W', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.WOVEN_SILK).getItem())
						.key('X', plain)
						.key('I', Tags.Items.INGOTS_IRON)
						.patternLine("III").patternLine(" X ").patternLine("WWW")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.STABILISER).getBlock())
						.key('X', plain)
						.key('G', Tags.Items.GEMS_QUARTZ)
						.patternLine("G G").patternLine("GXG").patternLine("G G")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.SWARMER).getBlock())
						.key('#', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.DIAMOND).getItem())
						.key('X', plain)
						.key('G', Tags.Items.INGOTS_GOLD)
						.patternLine("#G#").patternLine(" X ").patternLine("#G#")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);

		Item wovenSilk = CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.WOVEN_SILK).getItem();
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.APIARIST_HELMET.getItem())
						.key('#', wovenSilk)
						.patternLine("###").patternLine("# #")
						.addCriterion("has silk", this.hasItem(wovenSilk))
						.setGroup("apiarist_armour")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.APIARIST_CHEST.getItem())
						.key('#', wovenSilk)
						.patternLine("# #").patternLine("###").patternLine("###")
						.addCriterion("has silk", this.hasItem(wovenSilk))
						.setGroup("apiarist_armour")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.APIARIST_LEGS.getItem())
						.key('#', wovenSilk)
						.patternLine("###").patternLine("# #").patternLine("# #")
						.addCriterion("has silk", this.hasItem(wovenSilk))
						.setGroup("apiarist_armour")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.APIARIST_BOOTS.getItem())
						.key('#', wovenSilk)
						.patternLine("# #").patternLine("# #")
						.addCriterion("has silk", this.hasItem(wovenSilk))
						.setGroup("apiarist_armour")::build,
				ForestryModuleUids.APICULTURE);
		
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.BASE.get(BlockTypeApiculture.APIARY).getBlock())
						.key('S', ItemTags.WOODEN_SLABS)
						.key('P', ItemTags.PLANKS)
						.key('C', CoreItems.IMPREGNATED_CASING.getItem())
						.patternLine("SSS").patternLine("PCP").patternLine("PPP")
						.addCriterion("has_casing", this.hasItem(CoreItems.IMPREGNATED_CASING.getItem()))::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.BASE.get(BlockTypeApiculture.BEE_HOUSE).getBlock())
						.key('S', ItemTags.WOODEN_SLABS)
						.key('P', ItemTags.PLANKS)
						.key('C', ForestryTags.Items.BEE_COMBS)
						.patternLine("SSS").patternLine("PCP").patternLine("PPP")
						.addCriterion("has_casing", this.hasItem(ForestryTags.Items.BEE_COMBS))::build,
				ForestryModuleUids.APICULTURE);
		//TODO minecarts and candles once they are flattened

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.BEE_CHEST.getBlock())
						.key('G', Tags.Items.GLASS)
						.key('X', ForestryTags.Items.BEE_COMBS)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine(" G ").patternLine("XYX").patternLine("XXX")
						.addCriterion("has_comb", this.hasItem(ForestryTags.Items.BEE_COMBS))::build,
				ForestryModuleUids.APICULTURE);

		Item propolis = ApicultureItems.PROPOLIS.get(EnumPropolis.NORMAL).getItem();
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(CoreItems.BITUMINOUS_PEAT.getItem())
						.key('#', ForestryTags.Items.ASH)
						.key('X', CoreItems.PEAT.getItem())
						.key('Y', propolis)
						.patternLine(" # ").patternLine("XYX").patternLine(" # ")
						.addCriterion("has_propolis", this.hasItem(propolis))::build,
				ForestryModuleUids.APICULTURE);

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.FRAME_IMPREGNATED.getItem())
						.key('#', CoreItems.STICK_IMPREGNATED.getItem())
						.key('S', Tags.Items.STRING)
						.patternLine("###").patternLine("#S#").patternLine("###")
						.addCriterion("has_impregnated_stick", this.hasItem(CoreItems.STICK_IMPREGNATED.getItem()))::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.FRAME_UNTREATED.getItem())
						.key('#', Tags.Items.RODS_WOODEN)
						.key('S', Tags.Items.STRING)
						.patternLine("###").patternLine("#S#").patternLine("###")
						.addCriterion("has_impregnated_stick", this.hasItem(CoreItems.STICK_IMPREGNATED.getItem()))::build,
				ForestryModuleUids.APICULTURE);

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.HABITAT_LOCATOR.getItem())
						.key('X', ForestryTags.Items.INGOT_BRONZE)
						.key('#', Tags.Items.DUSTS_REDSTONE)
						.patternLine(" X ").patternLine("X#X").patternLine(" X ")
						.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE))::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.PULSATING_MESH).getItem())
						.key('#', ApicultureItems.PROPOLIS.get(EnumPropolis.PULSATING).getItem())
						.patternLine("# #").patternLine(" # ").patternLine("# #")
						.addCriterion("has_pulsating_propolis", this.hasItem(ApicultureItems.PROPOLIS.get(EnumPropolis.PULSATING).getItem()))::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.SCOOP.getItem())
						.key('#', Tags.Items.RODS_WOODEN)
						.key('X', ItemTags.WOOL)
						.patternLine("#X#").patternLine("###").patternLine(" # ")
						.addCriterion("has_wool", this.hasItem(ItemTags.WOOL))::build,
				ForestryModuleUids.APICULTURE
		);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(Items.SLIME_BALL)
						.key('#', propolis)
						.key('X', ApicultureItems.POLLEN_CLUSTER.get(EnumPollenCluster.NORMAL).getItem())
						.patternLine("#X#").patternLine("#X#").patternLine("#X#")
						.addCriterion("has_propolis", this.hasItem(propolis))::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.SMOKER.getItem())
						.key('#', ForestryTags.Items.INGOT_TIN)
						.key('S', Tags.Items.RODS_WOODEN)
						.key('F', Items.FLINT_AND_STEEL)
						.key('L', Tags.Items.LEATHER)
						.patternLine("LS#").patternLine("LF#").patternLine("###")
						.addCriterion("has_tin", this.hasItem(ForestryTags.Items.INGOT_TIN))::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(Items.GLISTERING_MELON_SLICE)
						.key('#', ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY).getItem())
						.key('X', ApicultureItems.HONEYDEW.getItem())
						.key('Y', Items.MELON_SLICE)
						.patternLine("#X#").patternLine("#Y#").patternLine("#X#")
						.addCriterion("has_melon", this.hasItem(Items.MELON_SLICE))::build,
				ForestryModuleUids.APICULTURE);

		Item beesWax = CoreItems.BEESWAX.getItem();
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(Items.TORCH, 3)
						.key('#', beesWax)
						.key('Y', Tags.Items.RODS_WOODEN)
						.patternLine(" # ").patternLine(" # ").patternLine(" Y ")
						.addCriterion("has_wax", this.hasItem(beesWax))::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.WAX_CAST.getItem())
						.key('#', beesWax)
						.patternLine("###").patternLine("# #").patternLine("###")
						.addCriterion("has_wax", this.hasItem(beesWax))::build,
				ForestryModuleUids.APICULTURE);
	}

	private void registerCombRecipes(RecipeDataHelper helper) {
		for (EnumHoneyComb honeyComb : EnumHoneyComb.VALUES) {
			Item comb = ApicultureItems.BEE_COMBS.get(honeyComb).getItem();
			Block combBlock = ApicultureBlocks.BEE_COMB.get(honeyComb).getBlock();
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(combBlock).key('#', comb).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_at_least_9_comb", this.hasItem(MinMaxBounds.IntBound.atLeast(9), comb)).setGroup("combs")::build,
					ForestryModuleUids.APICULTURE
			);
		}
	}

	private void registerArboricultureRecipes(RecipeDataHelper helper) {
		registerWoodRecipes(helper);
		
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ArboricultureItems.GRAFTER.getItem())
						.key('B', ForestryTags.Items.INGOT_BRONZE)
						.key('#', Tags.Items.RODS_WOODEN)
						.patternLine("  B").patternLine(" # ").patternLine("#  ")
						.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE))::build,
				ForestryModuleUids.ARBORICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ArboricultureBlocks.TREE_CHEST.getBlock())
						.key('#', Tags.Items.GLASS)
						.key('X', ItemTags.SAPLINGS)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine(" # ").patternLine("XYX").patternLine("XXX")
						.addCriterion("has_sapling", this.hasItem(ItemTags.SAPLINGS))::build,
				ForestryModuleUids.ARBORICULTURE);
	}

	private void registerWoodRecipes(RecipeDataHelper helper) {
		IWoodAccess woodAccess = TreeManager.woodAccess;
		List<IWoodType> woodTypes = woodAccess.getRegisteredWoodTypes();

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
				helper.moduleConditionRecipe(
						ShapelessRecipeBuilder.shapelessRecipe(planks, 4).addIngredient(log).addCriterion("has_log", this.hasItem(log)).setGroup("planks")::build,
						ForestryModuleUids.ARBORICULTURE);
				helper.moduleConditionRecipe(
						ShapedRecipeBuilder.shapedRecipe(fence, 3).key('#', Tags.Items.RODS_WOODEN).key('W', planks).patternLine("W#W").patternLine("W#W").addCriterion("has_planks", this.hasItem(planks)).setGroup("wooden_fence")::build,
						ForestryModuleUids.ARBORICULTURE);
				helper.moduleConditionRecipe(
						ShapedRecipeBuilder.shapedRecipe(fencegate).key('#', Tags.Items.RODS_WOODEN).key('W', planks).patternLine("#W#").patternLine("#W#").addCriterion("has_planks", this.hasItem(planks)).setGroup("wooden_fence_gate")::build,
						ForestryModuleUids.ARBORICULTURE);
				helper.moduleConditionRecipe(
						ShapedRecipeBuilder.shapedRecipe(slab, 6).key('#', planks).patternLine("###").addCriterion("has_planks", this.hasItem(planks)).setGroup("wooden_slab")::build,
						ForestryModuleUids.ARBORICULTURE);
				helper.moduleConditionRecipe(
						ShapedRecipeBuilder.shapedRecipe(stairs, 4).key('#', planks).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_planks", this.hasItem(planks)).setGroup("wooden_stairs")::build,
						ForestryModuleUids.ARBORICULTURE);
			}

			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(door, 3).key('#', Ingredient.fromItems(planks, fireproofPlanks)).patternLine("##").patternLine("##").patternLine("##").addCriterion("has_planks", this.hasItem(planks)).setGroup("wooden_door")::build,
					ForestryModuleUids.ARBORICULTURE);
			helper.moduleConditionRecipe(
					ShapelessRecipeBuilder.shapelessRecipe(fireproofPlanks, 4).addIngredient(fireproofLog).addCriterion("has_planks", this.hasItem(fireproofPlanks)).setGroup("planks")::build,
					ForestryModuleUids.ARBORICULTURE);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(fireproofFence, 3).key('#', Tags.Items.RODS_WOODEN).key('W', fireproofPlanks).patternLine("W#W").patternLine("W#W").addCriterion("has_planks", this.hasItem(fireproofPlanks)).setGroup("wooden_fence")::build,
					ForestryModuleUids.ARBORICULTURE);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(fireproofFencegate).key('#', Tags.Items.RODS_WOODEN).key('W', fireproofPlanks).patternLine("#W#").patternLine("#W#").addCriterion("has_planks", this.hasItem(fireproofPlanks)).setGroup("wooden_fence_gate")::build,
					ForestryModuleUids.ARBORICULTURE);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(fireproofSlab, 6).key('#', fireproofPlanks).patternLine("###").addCriterion("has_planks", this.hasItem(fireproofPlanks)).setGroup("wooden_slab")::build,
					ForestryModuleUids.ARBORICULTURE);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(fireproofStairs, 4).key('#', fireproofPlanks).patternLine("#  ").patternLine("## ").patternLine("###").addCriterion("has_planks", this.hasItem(fireproofPlanks)).setGroup("wooden_stairs")::build,
					ForestryModuleUids.ARBORICULTURE);

		}
	}

	private void registerFoodRecipes(RecipeDataHelper helper) {
		
		Item waxCapsule = FluidsItems.CONTAINERS.get(EnumContainerType.CAPSULE).getItem();
		Item honeyDrop = ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY).getItem();

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(FoodItems.AMBROSIA.getItem())
						.key('#', ApicultureItems.HONEYDEW.getItem())
						.key('X', ApicultureItems.ROYAL_JELLY.getItem())
						.key('Y', waxCapsule)
						.patternLine("#Y#").patternLine("XXX").patternLine("###")
						.addCriterion("has royal_jelly", this.hasItem(ApicultureItems.ROYAL_JELLY.getItem()))::build,
				ForestryModuleUids.FOOD);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(FoodItems.HONEY_POT.getItem())
						.key('#', honeyDrop)
						.key('X', waxCapsule)
						.patternLine("# #").patternLine(" X ").patternLine("# #")
						.addCriterion("has_drop", this.hasItem(honeyDrop))::build,
				ForestryModuleUids.FOOD);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(FoodItems.HONEYED_SLICE.getItem())
						.key('#', honeyDrop)
						.key('X', Items.BREAD)
						.patternLine("###").patternLine("#X#").patternLine("###")
						.addCriterion("has_drop", this.hasItem(honeyDrop))::build,
				ForestryModuleUids.FOOD);
	}

	private void registerBackpackRecipes(RecipeDataHelper helper) {

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(BackpackItems.ADVENTURER_BACKPACK.getItem())
						.key('#', ItemTags.WOOL)
						.key('V', Tags.Items.BONES)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_bone", this.hasItem(Tags.Items.BONES))::build,
				ForestryModuleUids.BACKPACKS);

		Block beeChest = ArboricultureBlocks.TREE_CHEST.getBlock();
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(BackpackItems.APIARIST_BACKPACK.getItem())
						.key('#', ItemTags.WOOL)
						.key('V', Tags.Items.RODS_WOODEN)
						.key('X', Tags.Items.STRING)
						.key('Y', beeChest)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_bee_chest", this.hasItem(beeChest))::build,
				ForestryModuleUids.BACKPACKS, ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(BackpackItems.BUILDER_BACKPACK.getItem())
						.key('#', ItemTags.WOOL)
						.key('V', Items.CLAY_BALL)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_clay", this.hasItem(Items.CLAY_BALL))::build,
				ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(BackpackItems.DIGGER_BACKPACK.getItem())
						.key('#', ItemTags.WOOL)
						.key('V', Tags.Items.STONE)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_stone", this.hasItem(Tags.Items.STONE))::build,
				ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(BackpackItems.FORESTER_BACKPACK.getItem())
						.key('#', ItemTags.WOOL)
						.key('V', ItemTags.LOGS)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_log", this.hasItem(ItemTags.LOGS))::build,
				ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(BackpackItems.HUNTER_BACKPACK.getItem())
						.key('#', ItemTags.WOOL)
						.key('V', Tags.Items.FEATHERS)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_feather", this.hasItem(Tags.Items.FEATHERS))::build,
				ForestryModuleUids.BACKPACKS);

		Block butterflyChest = LepidopterologyBlocks.BUTTERFLY_CHEST.getBlock();
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(BackpackItems.LEPIDOPTERIST_BACKPACK.getItem())
						.key('#', ItemTags.WOOL)
						.key('V', butterflyChest)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_butterfly_chest", this.hasItem(butterflyChest))::build,
				ForestryModuleUids.BACKPACKS, ForestryModuleUids.LEPIDOPTEROLOGY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(BackpackItems.MINER_BACKPACK.getItem())
						.key('#', ItemTags.WOOL)
						.key('V', Tags.Items.INGOTS_IRON)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_iron", this.hasItem(Tags.Items.INGOTS_IRON))::build,
				ForestryModuleUids.BACKPACKS);
	}

	private void registerCharcoalRecipes(RecipeDataHelper helper) {

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(CharcoalBlocks.CHARCOAL.getBlock())
						.key('#', Items.CHARCOAL)
						.patternLine("###").patternLine("###").patternLine("###")
						.addCriterion("has_enough_charcoal", this.hasItem(MinMaxBounds.IntBound.atLeast(9), Items.CHARCOAL))::build,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapelessRecipe(Items.CHARCOAL, 9)
						.addIngredient(ForestryTags.Items.CHARCOAL)
						.addCriterion("has_charcoal_block", this.hasItem(ForestryTags.Items.CHARCOAL))::build,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(CharcoalBlocks.LOAM.getBlock())
						.key('C', Items.CLAY_BALL)
						.key('S', ItemTags.SAND)
						.key('F', CoreItems.COMPOST.getItem())
						.patternLine("CFC").patternLine("SCS").patternLine("CFC")
						.addCriterion("has_compost", this.hasItem(CoreItems.COMPOST.getItem()))::build,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(CharcoalBlocks.WOOD_PILE.getBlock())
						.key('L', ItemTags.LOGS)
						.patternLine("LL").patternLine("LL")
						.addCriterion("has_log", this.hasItem(ItemTags.LOGS))::build,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapelessRecipe(CharcoalBlocks.WOOD_PILE_DECORATIVE.getBlock())
						.addIngredient(CharcoalBlocks.WOOD_PILE.getBlock())
						.addCriterion("was_wood_pile", this.hasItem(CharcoalBlocks.WOOD_PILE.getBlock()))::build,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapelessRecipe(CharcoalBlocks.WOOD_PILE.getBlock())
						.addIngredient(CharcoalBlocks.WOOD_PILE_DECORATIVE.getBlock())
						.addCriterion("has_decorative", this.hasItem(CharcoalBlocks.WOOD_PILE_DECORATIVE.getBlock()))::build,
				new ResourceLocation(Constants.MOD_ID, "wood_pile_from_decorative"), ForestryModuleUids.CHARCOAL);
	}

	private void addClimatologyRecipes(RecipeDataHelper helper) {

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ClimatologyBlocks.HABITATFORMER.getBlock())
						.key('S', CoreItems.STURDY_CASING.getItem())
						.key('G', Tags.Items.GLASS)
						.key('B', ForestryTags.Items.GEAR_BRONZE)
						.key('R', Tags.Items.DUSTS_REDSTONE)
						.key('C', CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.BASIC).getItem())
						.key('T', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.IRON).getItem())
						.patternLine("GRG").patternLine("TST").patternLine("BCB")
						.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.getItem()))::build,
				ForestryModuleUids.CLIMATOLOGY);
		//TODO if carpenter recipes are in json then can just use that here
		helper.simpleConditionalRecipe(
				ShapedRecipeBuilder.shapedRecipe(ClimatologyItems.HABITAT_SCREEN.getItem())
						.key('G', ForestryTags.Items.GEAR_BRONZE)
						.key('P', Tags.Items.GLASS_PANES)
						.key('I', ForestryTags.Items.INGOT_BRONZE)
						.key('D', Tags.Items.GEMS_DIAMOND)
						.patternLine("IPI").patternLine("IPI").patternLine("DGD")
						.addCriterion("has_diamond", this.hasItem(Tags.Items.GEMS_DIAMOND))::build,
				new AndCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.CLIMATOLOGY),
						new NotCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.FACTORY))));
	}

	private void registerCoreRecipes(RecipeDataHelper helper) {
		Consumer<IFinishedRecipe> consumer = helper.getConsumer();

		//don't need conditions here generally since core is always enabled
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.BASE.get(BlockTypeCoreTesr.ANALYZER).getBlock())
				.key('T', CoreItems.PORTABLE_ALYZER.getItem())
				.key('X', ForestryTags.Items.INGOT_BRONZE)
				.key('Y', CoreItems.STURDY_CASING.getItem())
				.patternLine("XTX").patternLine(" Y ").patternLine("X X")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.getItem())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.ASH_BRICK.getBlock())
				.key('A', ForestryTags.Items.ASH)
				.key('#', Tags.Items.INGOTS_BRICK)
				.patternLine("A#A").patternLine("# #").patternLine("A#A")
				.addCriterion("has_ash", this.hasItem(ForestryTags.Items.ASH)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.ASH_STAIRS.getBlock(), 4)
				.key('#', CoreBlocks.ASH_BRICK.getBlock())
				.patternLine("#  ").patternLine("## ").patternLine("###")
				.addCriterion("has_brick", this.hasItem(CoreBlocks.ASH_BRICK.getBlock())).build(consumer);
		//TODO how to deal with variable output. Options: wrapper recipe, custom recipe type, leave up to data packs.
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.APATITE).getBlock())
				.key('#', ForestryTags.Items.GEM_APATITE)
				.patternLine("###").patternLine("###").patternLine("###")
				.addCriterion("has_apatite", this.hasItem(ForestryTags.Items.GEM_APATITE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.BRONZE).getBlock())
				.key('#', ForestryTags.Items.INGOT_BRONZE)
				.patternLine("###").patternLine("###").patternLine("###")
				.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.COPPER).getBlock())
				.key('#', ForestryTags.Items.INGOT_COPPER)
				.patternLine("###").patternLine("###").patternLine("###")
				.addCriterion("has_copper", this.hasItem(ForestryTags.Items.INGOT_COPPER)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.TIN).getBlock())
				.key('#', ForestryTags.Items.INGOT_TIN)
				.patternLine("###").patternLine("###").patternLine("###")
				.addCriterion("has_apatite", this.hasItem(ForestryTags.Items.INGOT_TIN)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.BRONZE_PICKAXE.getItem())
				.key('#', ForestryTags.Items.INGOT_BRONZE)
				.key('X', Tags.Items.RODS_WOODEN)
				.patternLine("###").patternLine(" X ").patternLine(" X ")
				.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.BRONZE_SHOVEL.getItem())
				.key('#', ForestryTags.Items.INGOT_BRONZE)
				.key('X', Tags.Items.RODS_WOODEN)
				.patternLine(" # ").patternLine(" X ").patternLine(" X ")
				.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE)).build(consumer);
		helper.simpleConditionalRecipe(
				ShapedRecipeBuilder.shapedRecipe(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.CAMOUFLAGED_PANELING).getItem())
						.key('W', ItemTags.PLANKS)
						.key('Y', Tags.Items.DYES_YELLOW)
						.key('B', Tags.Items.DYES_BLUE)
						.key('R', Tags.Items.DYES_RED)
						.patternLine("WWW").patternLine("YBR").patternLine("WWW")
						.addCriterion("has_dye", this.hasItem(Tags.Items.DYES))::build,
				new NotCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.FACTORY)));

		//TODO maybe get clever with a loop here
		ConditionalRecipe.builder()
				.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
				.addRecipe(
						ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_BRONZE.getItem())
								.key('#', ForestryTags.Items.INGOT_BRONZE)
								.key('X', ForestryTags.Items.GEAR_STONE)
								.patternLine(" # ").patternLine("#X#").patternLine(" # ")
								.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE))::build)
				.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
				.addRecipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_BRONZE.getItem())
						.key('#', ForestryTags.Items.INGOT_BRONZE)
						.key('X', ForestryTags.Items.INGOT_COPPER)
						.patternLine(" # ").patternLine("#X#").patternLine(" # ")
						.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE))::build)
				.build(helper.getConsumer(), new ResourceLocation(Constants.MOD_ID, "gear_bronze"));
		ConditionalRecipe.builder()
				.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
				.addRecipe(
						ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_COPPER.getItem())
								.key('#', ForestryTags.Items.INGOT_COPPER)
								.key('X', ForestryTags.Items.GEAR_STONE)
								.patternLine(" # ").patternLine("#X#").patternLine(" # ")
								.addCriterion("has_copper", this.hasItem(ForestryTags.Items.INGOT_COPPER))::build)
				.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
				.addRecipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_COPPER.getItem())
						.key('#', ForestryTags.Items.INGOT_COPPER)
						.key('X', ForestryTags.Items.INGOT_COPPER)
						.patternLine(" # ").patternLine("#X#").patternLine(" # ")
						.addCriterion("has_copper", this.hasItem(ForestryTags.Items.INGOT_COPPER))::build)
				.build(helper.getConsumer(), new ResourceLocation(Constants.MOD_ID, "gear_copper"));
		ConditionalRecipe.builder()
				.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
				.addRecipe(
						ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_TIN.getItem())
								.key('#', ForestryTags.Items.INGOT_TIN)
								.key('X', ForestryTags.Items.GEAR_STONE)
								.patternLine(" # ").patternLine("#X#").patternLine(" # ")
								.addCriterion("has_tin", this.hasItem(ForestryTags.Items.INGOT_TIN))::build)
				.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
				.addRecipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_TIN.getItem())
						.key('#', ForestryTags.Items.INGOT_TIN)
						.key('X', ForestryTags.Items.INGOT_COPPER)
						.patternLine(" # ").patternLine("#X#").patternLine(" # ")
						.addCriterion("has_tin", this.hasItem(ForestryTags.Items.INGOT_TIN))::build)
				.build(helper.getConsumer(), new ResourceLocation(Constants.MOD_ID, "gear_tin"));

		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.INGOT_BRONZE.getItem())
				.addIngredient(ForestryTags.Items.INGOT_TIN)
				.addIngredient(ForestryTags.Items.INGOT_COPPER)
				.addIngredient(ForestryTags.Items.INGOT_COPPER)
				.addIngredient(ForestryTags.Items.INGOT_COPPER)
				.addCriterion("has_tin", this.hasItem(ForestryTags.Items.INGOT_TIN))
				.build(consumer, new ResourceLocation(Constants.MOD_ID, "ingot_bronze_alloying"));

		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.APATITE.getItem(), 9)
				.addIngredient(ForestryTags.Items.STORAGE_BLOCK_APATITE)
				.addCriterion("has_block", this.hasItem(ForestryTags.Items.STORAGE_BLOCK_APATITE)).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.INGOT_BRONZE.getItem(), 9)
				.addIngredient(ForestryTags.Items.STORAGE_BLOCK_BRONZE)
				.addCriterion("has_block", this.hasItem(ForestryTags.Items.STORAGE_BLOCK_BRONZE)).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.INGOT_COPPER.getItem(), 9)
				.addIngredient(ForestryTags.Items.STORAGE_BLOCK_COPPER)
				.addCriterion("has_block", this.hasItem(ForestryTags.Items.STORAGE_BLOCK_COPPER)).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.INGOT_TIN.getItem(), 9)
				.addIngredient(ForestryTags.Items.STORAGE_BLOCK_TIN)
				.addCriterion("has_block", this.hasItem(ForestryTags.Items.STORAGE_BLOCK_TIN)).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.KIT_PICKAXE.getItem())
				.addIngredient(CoreItems.BRONZE_PICKAXE.getItem())
				.addIngredient(CoreItems.CARTON.getItem())
				.addCriterion("has_pickaxe", this.hasItem(CoreItems.BRONZE_PICKAXE.getItem())).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.KIT_SHOVEL.getItem())
				.addIngredient(CoreItems.BRONZE_SHOVEL.getItem())
				.addIngredient(CoreItems.CARTON.getItem())
				.addCriterion("has_shovel", this.hasItem(CoreItems.BRONZE_SHOVEL.getItem())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.SPECTACLES.getItem())
				.key('X', ForestryTags.Items.INGOT_BRONZE)
				.key('Y', Tags.Items.GLASS_PANES)
				.patternLine(" X ").patternLine("Y Y")
				.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.PIPETTE.getItem())
				.key('#', ItemTags.WOOL)
				.key('X', Tags.Items.GLASS_PANES)
				.patternLine("  #").patternLine(" X ").patternLine("X  ")
				.addCriterion("has_wool", this.hasItem(ItemTags.WOOL)).build(consumer);
		helper.simpleConditionalRecipe(
				ShapedRecipeBuilder.shapedRecipe(CoreItems.PORTABLE_ALYZER.getItem())
						.key('#', Tags.Items.GLASS_PANES)
						.key('X', ForestryTags.Items.INGOT_TIN)
						.key('R', Tags.Items.DUSTS_REDSTONE)
						.key('D', Tags.Items.GEMS_DIAMOND)
						.patternLine("X#X").patternLine("X#X").patternLine("RDR")
						.addCriterion("has_diamond", this.hasItem(Tags.Items.GEMS_DIAMOND))::build,
				new NotCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.FACTORY)));
		ShapedRecipeBuilder.shapedRecipe(Items.STRING)
				.key('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).getItem())
				.patternLine(" # ").patternLine(" # ").patternLine(" # ")
				.addCriterion("has_wisp", this.hasItem(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).getItem())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.STURDY_CASING.getItem())
				.key('#', ForestryTags.Items.INGOT_BRONZE)
				.patternLine("###").patternLine("# #").patternLine("###")
				.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(Items.COBWEB, 4)
				.key('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).getItem())
				.patternLine("# #").patternLine(" # ").patternLine("# #")
				.addCriterion("has_wisp", this.hasItem(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).getItem())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.WRENCH.getItem())
				.key('#', ForestryTags.Items.INGOT_BRONZE)
				.patternLine("# #").patternLine(" # ").patternLine(" # ")
				.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE)).build(consumer);

	}
}
