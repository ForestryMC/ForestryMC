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
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.data.ForgeRecipeProvider;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.IWoodAccess;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.blocks.BlockAlveary;
import forestry.apiculture.blocks.BlockAlvearyType;
import forestry.apiculture.blocks.BlockRegistryApiculture;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumHoneyDrop;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.EnumPropolis;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.ModuleCharcoal;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
import forestry.arboriculture.blocks.BlockRegistryCharcoal;
import forestry.arboriculture.items.ItemRegistryArboriculture;
import forestry.climatology.ModuleClimatology;
import forestry.climatology.blocks.BlockRegistryClimatology;
import forestry.climatology.items.ItemRegistryClimatology;
import forestry.core.ModuleCore;
import forestry.core.ModuleFluids;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.config.Constants;
import forestry.core.items.EnumCraftingMaterial;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.ModuleEnabledCondition;
import forestry.food.ModuleFood;
import forestry.food.items.ItemRegistryFood;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.modules.ForestryModuleUids;
import forestry.storage.ModuleBackpacks;
import forestry.storage.items.ItemRegistryBackpacks;

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
	}

	private void registerApicultureRecipes(RecipeDataHelper helper) {
		registerCombRecipes(helper);

		BlockRegistryApiculture blocks = ModuleApiculture.getBlocks();
		ItemRegistryCore coreItems = ModuleCore.getItems();

		BlockAlveary plain = blocks.getAlvearyBlock(BlockAlvearyType.PLAIN);
		Item goldElectronTube = coreItems.electronTubes.get(EnumElectronTube.GOLD);


		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(plain)
						.key('X', coreItems.impregnatedCasing)
						.key('#', coreItems.craftingMaterials.get(EnumCraftingMaterial.SCENTED_PANELING))
						.patternLine("###").patternLine("#X#").patternLine("###")
						.addCriterion("has_casing", this.hasItem(coreItems.impregnatedCasing))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(blocks.getAlvearyBlock(BlockAlvearyType.FAN))
						.key('#', goldElectronTube)
						.key('X', plain)
						.key('I', Tags.Items.INGOTS_IRON)
						.patternLine("I I").patternLine(" X ").patternLine("I#I")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(blocks.getAlvearyBlock(BlockAlvearyType.HEATER))
						.key('#', goldElectronTube)
						.key('I', Tags.Items.INGOTS_IRON)
						.key('X', plain)
						.key('S', Tags.Items.STONE)
						.patternLine("#I#").patternLine(" X ").patternLine("SSS")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(blocks.getAlvearyBlock(BlockAlvearyType.HYGRO))
						.key('G', Tags.Items.GLASS)
						.key('X', plain)
						.key('I', Tags.Items.INGOTS_IRON)
						.patternLine("GIG").patternLine("GXG").patternLine("GIG")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(blocks.getAlvearyBlock(BlockAlvearyType.SIEVE))
						.key('W', coreItems.craftingMaterials.get(EnumCraftingMaterial.WOVEN_SILK))
						.key('X', plain)
						.key('I', Tags.Items.INGOTS_IRON)
						.patternLine("III").patternLine(" X ").patternLine("WWW")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(blocks.getAlvearyBlock(BlockAlvearyType.STABILISER))
						.key('X', plain)
						.key('G', Tags.Items.GEMS_QUARTZ)
						.patternLine("G G").patternLine("GXG").patternLine("G G")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(blocks.getAlvearyBlock(BlockAlvearyType.SWARMER))
						.key('#', coreItems.electronTubes.get(EnumElectronTube.DIAMOND))
						.key('X', plain)
						.key('G', Tags.Items.INGOTS_GOLD)
						.patternLine("#G#").patternLine(" X ").patternLine("#G#")
						.addCriterion("has_plain", this.hasItem(plain))
						.setGroup("alveary")::build,
				ForestryModuleUids.APICULTURE);

		Item wovenSilk = coreItems.craftingMaterials.get(EnumCraftingMaterial.WOVEN_SILK);
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
				ShapedRecipeBuilder.shapedRecipe(blocks.apiary)
						.key('S', ItemTags.WOODEN_SLABS)
						.key('P', ItemTags.PLANKS)
						.key('C', coreItems.impregnatedCasing)
						.patternLine("SSS").patternLine("PCP").patternLine("PPP")
						.addCriterion("has_casing", this.hasItem(coreItems.impregnatedCasing))::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(blocks.beeHouse)
						.key('S', ItemTags.WOODEN_SLABS)
						.key('P', ItemTags.PLANKS)
						.key('C', ForestryTags.Items.BEE_COMBS)
						.patternLine("SSS").patternLine("PCP").patternLine("PPP")
						.addCriterion("has_casing", this.hasItem(ForestryTags.Items.BEE_COMBS))::build,
				ForestryModuleUids.APICULTURE);
		//TODO minecarts and candles once they are flattened

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(blocks.beeChest)
						.key('G', Tags.Items.GLASS)
						.key('X', ForestryTags.Items.BEE_COMBS)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine(" G ").patternLine("XYX").patternLine("XXX")
						.addCriterion("has_comb", this.hasItem(ForestryTags.Items.BEE_COMBS))::build,
				ForestryModuleUids.APICULTURE);

		Item propolis = ApicultureItems.PROPOLIS.get(EnumPropolis.NORMAL).getItem();
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(coreItems.bituminousPeat)
						.key('#', ForestryTags.Items.ASH)
						.key('X', coreItems.peat)
						.key('Y', propolis)
						.patternLine(" # ").patternLine("XYX").patternLine(" # ")
						.addCriterion("has_propolis", this.hasItem(propolis))::build,
				ForestryModuleUids.APICULTURE);

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.FRAME_IMPREGNATED.getItem())
						.key('#', coreItems.stickImpregnated)
						.key('S', Tags.Items.STRING)
						.patternLine("###").patternLine("#S#").patternLine("###")
						.addCriterion("has_impregnated_stick", this.hasItem(coreItems.stickImpregnated))::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.FRAME_UNTREATED.getItem())
						.key('#', Tags.Items.RODS_WOODEN)
						.key('S', Tags.Items.STRING)
						.patternLine("###").patternLine("#S#").patternLine("###")
						.addCriterion("has_impregnated_stick", this.hasItem(coreItems.stickImpregnated))::build,
				ForestryModuleUids.APICULTURE);

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(ApicultureItems.HABITAT_LOCATOR.getItem())
						.key('X', ForestryTags.Items.INGOT_BRONZE)
						.key('#', Tags.Items.DUSTS_REDSTONE)
						.patternLine(" X ").patternLine("X#X").patternLine(" X ")
						.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE))::build,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(coreItems.craftingMaterials.get(EnumCraftingMaterial.PULSATING_MESH))
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

		Item beesWax = coreItems.beeswax;
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
			Block combBlock = ModuleApiculture.getBlocks().beeCombs.get(honeyComb);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(combBlock).key('#', comb).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_at_least_9_comb", this.hasItem(MinMaxBounds.IntBound.atLeast(9), comb)).setGroup("combs")::build,
					ForestryModuleUids.APICULTURE
			);
		}
	}

	private void registerArboricultureRecipes(RecipeDataHelper helper) {
		registerWoodRecipes(helper);

		ItemRegistryArboriculture treeItems = ModuleArboriculture.getItems();
		BlockRegistryArboriculture treeBlocks = ModuleArboriculture.getBlocks();

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(treeItems.grafter)
						.key('B', ForestryTags.Items.INGOT_BRONZE)
						.key('#', Tags.Items.RODS_WOODEN)
						.patternLine("  B").patternLine(" # ").patternLine("#  ")
						.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOT_BRONZE))::build,
				ForestryModuleUids.ARBORICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(treeBlocks.treeChest)
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

		ItemRegistryFood foodItems = ModuleFood.getItems();

		Item waxCapsule = ModuleFluids.getItems().waxCapsuleEmpty;
		Item honeyDrop = ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY).getItem();

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(foodItems.ambrosia)
						.key('#', ApicultureItems.HONEYDEW.getItem())
						.key('X', ApicultureItems.ROYAL_JELLY.getItem())
						.key('Y', waxCapsule)
						.patternLine("#Y#").patternLine("XXX").patternLine("###")
						.addCriterion("has royal_jelly", this.hasItem(ApicultureItems.ROYAL_JELLY.getItem()))::build,
				ForestryModuleUids.FOOD);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(foodItems.honeyPot)
						.key('#', honeyDrop)
						.key('X', waxCapsule)
						.patternLine("# #").patternLine(" X ").patternLine("# #")
						.addCriterion("has_drop", this.hasItem(honeyDrop))::build,
				ForestryModuleUids.FOOD);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(foodItems.honeyedSlice)
						.key('#', honeyDrop)
						.key('X', Items.BREAD)
						.patternLine("###").patternLine("#X#").patternLine("###")
						.addCriterion("has_drop", this.hasItem(honeyDrop))::build,
				ForestryModuleUids.FOOD);
	}

	private void registerBackpackRecipes(RecipeDataHelper helper) {
		ItemRegistryBackpacks backpackItems = ModuleBackpacks.getItems();

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(backpackItems.adventurerBackpack)
						.key('#', ItemTags.WOOL)
						.key('V', Tags.Items.BONES)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_bone", this.hasItem(Tags.Items.BONES))::build,
				ForestryModuleUids.BACKPACKS);

		Block beeChest = ModuleApiculture.getBlocks().beeChest;
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(backpackItems.apiaristBackpack)
						.key('#', ItemTags.WOOL)
						.key('V', Tags.Items.RODS_WOODEN)
						.key('X', Tags.Items.STRING)
						.key('Y', beeChest)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_bee_chest", this.hasItem(beeChest))::build,
				ForestryModuleUids.BACKPACKS, ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(backpackItems.builderBackpack)
						.key('#', ItemTags.WOOL)
						.key('V', Items.CLAY_BALL)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_clay", this.hasItem(Items.CLAY_BALL))::build,
				ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(backpackItems.diggerBackpack)
						.key('#', ItemTags.WOOL)
						.key('V', Tags.Items.STONE)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_stone", this.hasItem(Tags.Items.STONE))::build,
				ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(backpackItems.foresterBackpack)
						.key('#', ItemTags.WOOL)
						.key('V', ItemTags.LOGS)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_log", this.hasItem(ItemTags.LOGS))::build,
				ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(backpackItems.hunterBackpack)
						.key('#', ItemTags.WOOL)
						.key('V', Tags.Items.FEATHERS)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_feather", this.hasItem(Tags.Items.FEATHERS))::build,
				ForestryModuleUids.BACKPACKS);

		Block butterflyChest = ModuleLepidopterology.getBlocks().butterflyChest;
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(backpackItems.lepidopteristBackpack)
						.key('#', ItemTags.WOOL)
						.key('V', butterflyChest)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_butterfly_chest", this.hasItem(butterflyChest))::build,
				ForestryModuleUids.BACKPACKS, ForestryModuleUids.LEPIDOPTEROLOGY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(backpackItems.minerBackpack)
						.key('#', ItemTags.WOOL)
						.key('V', Tags.Items.INGOTS_IRON)
						.key('X', Tags.Items.STRING)
						.key('Y', Tags.Items.CHESTS_WOODEN)
						.patternLine("X#X").patternLine("VYV").patternLine("X#X")
						.addCriterion("has_iron", this.hasItem(Tags.Items.INGOTS_IRON))::build,
				ForestryModuleUids.BACKPACKS);
	}

	private void registerCharcoalRecipes(RecipeDataHelper helper) {
		BlockRegistryCharcoal charcoalBlocks = ModuleCharcoal.getBlocks();
		ItemRegistryCore coreItems = ModuleCore.getItems();

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(charcoalBlocks.charcoal)
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
				ShapedRecipeBuilder.shapedRecipe(charcoalBlocks.loam)
						.key('C', Items.CLAY_BALL)
						.key('S', ItemTags.SAND)
						.key('F', coreItems.compost)
						.patternLine("CFC").patternLine("SCS").patternLine("CFC")
						.addCriterion("has_compost", this.hasItem(coreItems.compost))::build,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(charcoalBlocks.woodPile)
						.key('L', ItemTags.LOGS)
						.patternLine("LL").patternLine("LL")
						.addCriterion("has_log", this.hasItem(ItemTags.LOGS))::build,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapelessRecipe(charcoalBlocks.woodPileDecorative)
						.addIngredient(charcoalBlocks.woodPile)
						.addCriterion("was_wood_pile", this.hasItem(charcoalBlocks.woodPile))::build,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapelessRecipe(charcoalBlocks.woodPile)
						.addIngredient(charcoalBlocks.woodPileDecorative)
						.addCriterion("has_decorative", this.hasItem(charcoalBlocks.woodPileDecorative))::build,
				new ResourceLocation(Constants.MOD_ID, "wood_pile_from_decorative"), ForestryModuleUids.CHARCOAL);
	}

	private void addClimatologyRecipes(RecipeDataHelper helper) {
		BlockRegistryClimatology climatologyBlocks = ModuleClimatology.getBlocks();
		ItemRegistryClimatology climatologyItems = ModuleClimatology.getItems();
		ItemRegistryCore coreItems = ModuleCore.getItems();

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(climatologyBlocks.habitatformer)
						.key('S', coreItems.sturdyCasing)
						.key('G', Tags.Items.GLASS)
						.key('B', ForestryTags.Items.GEAR_BRONZE)
						.key('R', Tags.Items.DUSTS_REDSTONE)
						.key('C', coreItems.circuitboards.get(EnumCircuitBoardType.BASIC))
						.key('T', coreItems.electronTubes.get(EnumElectronTube.IRON))
						.patternLine("GRG").patternLine("TST").patternLine("BCB")
						.addCriterion("has_casing", this.hasItem(coreItems.sturdyCasing))::build,
				ForestryModuleUids.CLIMATOLOGY);
		helper.simpleConditionalRecipe(
				ShapedRecipeBuilder.shapedRecipe(climatologyItems.habitatScreen)
						.key('G', ForestryTags.Items.GEAR_BRONZE)
						.key('P', Tags.Items.GLASS_PANES)
						.key('I', ForestryTags.Items.INGOT_BRONZE)
						.key('D', Tags.Items.GEMS_DIAMOND)
						.patternLine("IPI").patternLine("IPI").patternLine("DGD")
						.addCriterion("has_diamond", this.hasItem(Tags.Items.GEMS_DIAMOND))::build,
				new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.CLIMATOLOGY),
				new NotCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.FACTORY)));
	}
}
