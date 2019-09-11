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

import net.minecraftforge.common.Tags;
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
import forestry.core.ModuleCore;
import forestry.core.items.EnumCraftingMaterial;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.modules.ForestryModuleUids;

public class ForestryRecipeProvider extends ForgeRecipeProvider {

	public ForestryRecipeProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		RecipeDataHelper helper = new RecipeDataHelper(consumer);
		registerWoodRecipes(helper);
		registerApicultureRecipes(helper);
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
}
