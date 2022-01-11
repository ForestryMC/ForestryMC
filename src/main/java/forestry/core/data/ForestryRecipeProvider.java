package forestry.core.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.util.TriConsumer;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.IWoodAccess;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.circuits.ICircuit;
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
import forestry.book.features.BookItems;
import forestry.climatology.features.ClimatologyBlocks;
import forestry.climatology.features.ClimatologyItems;
import forestry.core.blocks.BlockTypeCoreTesr;
import forestry.core.blocks.EnumResourceType;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.config.Constants;
import forestry.core.data.builder.RecipeDataHelper;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.features.FluidsItems;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.ItemFruit;
import forestry.core.items.definitions.EnumContainerType;
import forestry.core.items.definitions.EnumCraftingMaterial;
import forestry.core.items.definitions.EnumElectronTube;
import forestry.core.recipes.ComplexIngredient;
import forestry.core.recipes.ModuleEnabledCondition;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.features.CultivationBlocks;
import forestry.database.features.DatabaseBlocks;
import forestry.energy.blocks.BlockTypeEngine;
import forestry.energy.features.EnergyBlocks;
import forestry.factory.blocks.BlockTypeFactoryPlain;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.blocks.EnumFarmMaterial;
import forestry.farming.features.FarmingBlocks;
import forestry.food.features.FoodItems;
import forestry.lepidopterology.features.LepidopterologyBlocks;
import forestry.lepidopterology.features.LepidopterologyItems;
import forestry.mail.blocks.BlockTypeMail;
import forestry.mail.features.MailBlocks;
import forestry.mail.features.MailItems;
import forestry.mail.items.EnumStampDefinition;
import forestry.mail.items.ItemLetter;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureItem;
import forestry.sorting.features.SortingBlocks;
import forestry.storage.features.BackpackItems;
import forestry.worktable.features.WorktableBlocks;

public class ForestryRecipeProvider extends RecipeProvider {

	public ForestryRecipeProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		RecipeDataHelper helper = new RecipeDataHelper(consumer);
		registerArboricultureRecipes(helper);
		registerApicultureRecipes(helper);
		registerFoodRecipes(helper);
		registerBackpackRecipes(helper);
		registerCharcoalRecipes(helper);
		registerClimatologyRecipes(helper);
		registerCoreRecipes(helper);
		registerBookRecipes(helper);
		registerCultivationRecipes(helper);
		registerDatabaseRecipes(helper);
		registerEnergyRecipes(helper);
		registerFactoryRecipes(helper);
		registerFarmingRecipes(helper);
		registerFluidsRecipes(helper);
		registerLepidopterologyRecipes(helper);
		registerMailRecipes(helper);
		registerSortingRecipes(helper);
		registerWorktableRecipes(helper);
	}

	private void registerApicultureRecipes(RecipeDataHelper helper) {
		registerCombRecipes(helper);

		BlockAlveary plain = ApicultureBlocks.ALVEARY.get(BlockAlvearyType.PLAIN).block();
		Item goldElectronTube = CoreItems.ELECTRON_TUBES.get(EnumElectronTube.GOLD).item();


		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(plain)
						.define('X', CoreItems.IMPREGNATED_CASING.item())
						.define('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SCENTED_PANELING).item())
						.pattern("###").pattern("#X#").pattern("###")
						.unlockedBy("has_casing", has(CoreItems.IMPREGNATED_CASING.item()))
						.group("alveary")::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.FAN).block())
						.define('#', goldElectronTube)
						.define('X', plain)
						.define('I', Tags.Items.INGOTS_IRON)
						.pattern("I I").pattern(" X ").pattern("I#I")
						.unlockedBy("has_plain", has(plain))
						.group("alveary")::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.HEATER).block())
						.define('#', goldElectronTube)
						.define('I', Tags.Items.INGOTS_IRON)
						.define('X', plain)
						.define('S', Tags.Items.STONE)
						.pattern("#I#").pattern(" X ").pattern("SSS")
						.unlockedBy("has_plain", has(plain))
						.group("alveary")::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.HYGRO).block())
						.define('G', Tags.Items.GLASS)
						.define('X', plain)
						.define('I', Tags.Items.INGOTS_IRON)
						.pattern("GIG").pattern("GXG").pattern("GIG")
						.unlockedBy("has_plain", has(plain))
						.group("alveary")::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.SIEVE).block())
						.define('W', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.WOVEN_SILK).item())
						.define('X', plain)
						.define('I', Tags.Items.INGOTS_IRON)
						.pattern("III").pattern(" X ").pattern("WWW")
						.unlockedBy("has_plain", has(plain))
						.group("alveary")::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.STABILISER).block())
						.define('X', plain)
						.define('G', Tags.Items.GEMS_QUARTZ)
						.pattern("G G").pattern("GXG").pattern("G G")
						.unlockedBy("has_plain", has(plain))
						.group("alveary")::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.SWARMER).block())
						.define('#', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.DIAMOND).item())
						.define('X', plain)
						.define('G', Tags.Items.INGOTS_GOLD)
						.pattern("#G#").pattern(" X ").pattern("#G#")
						.unlockedBy("has_plain", has(plain))
						.group("alveary")::save,
				ForestryModuleUids.APICULTURE);

		Item wovenSilk = CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.WOVEN_SILK).item();
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureItems.APIARIST_HELMET.item())
						.define('#', wovenSilk)
						.pattern("###").pattern("# #")
						.unlockedBy("has silk", has(wovenSilk))
						.group("apiarist_armour")::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureItems.APIARIST_CHEST.item())
						.define('#', wovenSilk)
						.pattern("# #").pattern("###").pattern("###")
						.unlockedBy("has silk", has(wovenSilk))
						.group("apiarist_armour")::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureItems.APIARIST_LEGS.item())
						.define('#', wovenSilk)
						.pattern("###").pattern("# #").pattern("# #")
						.unlockedBy("has silk", has(wovenSilk))
						.group("apiarist_armour")::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureItems.APIARIST_BOOTS.item())
						.define('#', wovenSilk)
						.pattern("# #").pattern("# #")
						.unlockedBy("has silk", has(wovenSilk))
						.group("apiarist_armour")::save,
				ForestryModuleUids.APICULTURE);

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureBlocks.BASE.get(BlockTypeApiculture.APIARY).block())
						.define('S', ItemTags.WOODEN_SLABS)
						.define('P', ItemTags.PLANKS)
						.define('C', CoreItems.IMPREGNATED_CASING.item())
						.pattern("SSS").pattern("PCP").pattern("PPP")
						.unlockedBy("has_casing", has(CoreItems.IMPREGNATED_CASING.item()))::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureBlocks.BASE.get(BlockTypeApiculture.BEE_HOUSE).block())
						.define('S', ItemTags.WOODEN_SLABS)
						.define('P', ItemTags.PLANKS)
						.define('C', ForestryTags.Items.BEE_COMBS)
						.pattern("SSS").pattern("PCP").pattern("PPP")
						.unlockedBy("has_casing", has(ForestryTags.Items.BEE_COMBS))::save,
				ForestryModuleUids.APICULTURE);
		//TODO minecarts and candles once they are flattened

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureBlocks.BEE_CHEST.block())
						.define('G', Tags.Items.GLASS)
						.define('X', ForestryTags.Items.BEE_COMBS)
						.define('Y', Tags.Items.CHESTS_WOODEN)
						.pattern(" G ").pattern("XYX").pattern("XXX")
						.unlockedBy("has_comb", has(ForestryTags.Items.BEE_COMBS))::save,
				ForestryModuleUids.APICULTURE);

		Item propolis = ApicultureItems.PROPOLIS.get(EnumPropolis.NORMAL).item();

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(CoreItems.BITUMINOUS_PEAT.item())
						.define('#', ForestryTags.Items.DUSTS_ASH)
						.define('X', CoreItems.PEAT.item())
						.define('Y', propolis)
						.pattern(" # ").pattern("XYX").pattern(" # ")
						.unlockedBy("has_propolis", has(propolis))::save,
				ForestryModuleUids.APICULTURE);

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureItems.FRAME_IMPREGNATED.item())
						.define('#', CoreItems.STICK_IMPREGNATED.item())
						.define('S', Tags.Items.STRING)
						.pattern("###").pattern("#S#").pattern("###")
						.unlockedBy("has_impregnated_stick", has(CoreItems.STICK_IMPREGNATED.item()))::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureItems.FRAME_UNTREATED.item())
						.define('#', Tags.Items.RODS_WOODEN)
						.define('S', Tags.Items.STRING)
						.pattern("###").pattern("#S#").pattern("###")
						.unlockedBy("has_impregnated_stick", has(CoreItems.STICK_IMPREGNATED.item()))::save,
				ForestryModuleUids.APICULTURE);

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureItems.HABITAT_LOCATOR.item())
						.define('X', ForestryTags.Items.INGOTS_BRONZE)
						.define('#', Tags.Items.DUSTS_REDSTONE)
						.pattern(" X ").pattern("X#X").pattern(" X ")
						.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE))::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.PULSATING_MESH).item())
						.define('#', ApicultureItems.PROPOLIS.get(EnumPropolis.PULSATING).item())
						.pattern("# #").pattern(" # ").pattern("# #")
						.unlockedBy("has_pulsating_propolis", has(ApicultureItems.PROPOLIS.get(EnumPropolis.PULSATING).item()))::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureItems.SCOOP.item())
						.define('#', Tags.Items.RODS_WOODEN)
						.define('X', ItemTags.WOOL)
						.pattern("#X#").pattern("###").pattern(" # ")
						.unlockedBy("has_wool", has(ItemTags.WOOL))::save,
				ForestryModuleUids.APICULTURE
		);
		helper.moduleConditionRecipe(
				(consumer) -> ShapedRecipeBuilder.shaped(Items.SLIME_BALL)
						.define('#', propolis)
						.define('X', ApicultureItems.POLLEN_CLUSTER.get(EnumPollenCluster.NORMAL).item())
						.pattern("#X#").pattern("#X#").pattern("#X#")
						.unlockedBy("has_propolis", has(propolis))
						.save(consumer, new ResourceLocation(Constants.MOD_ID, "slime_from_propolis")),
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureItems.SMOKER.item())
						.define('#', ForestryTags.Items.INGOTS_TIN)
						.define('S', Tags.Items.RODS_WOODEN)
						.define('F', Items.FLINT_AND_STEEL)
						.define('L', Tags.Items.LEATHER)
						.pattern("LS#").pattern("LF#").pattern("###")
						.unlockedBy("has_tin", has(ForestryTags.Items.INGOTS_TIN))::save,
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(Items.GLISTERING_MELON_SLICE)
						.define('#', ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY).item())
						.define('X', ApicultureItems.HONEYDEW.item())
						.define('Y', Items.MELON_SLICE)
						.pattern("#X#").pattern("#Y#").pattern("#X#")
						.unlockedBy("has_melon", has(Items.MELON_SLICE))::save,
				ForestryModuleUids.APICULTURE);

		Item beesWax = CoreItems.BEESWAX.item();
		helper.moduleConditionRecipe(
				(consumer) -> ShapedRecipeBuilder.shaped(Items.TORCH, 3)
						.define('#', beesWax)
						.define('Y', Tags.Items.RODS_WOODEN)
						.pattern(" # ").pattern(" # ").pattern(" Y ")
						.unlockedBy("has_wax", has(beesWax)).save(consumer, new ResourceLocation(Constants.MOD_ID, "torch_from_wax")),
				ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ApicultureItems.WAX_CAST.item())
						.define('#', beesWax)
						.pattern("###").pattern("# #").pattern("###")
						.unlockedBy("has_wax", has(beesWax))::save,
				ForestryModuleUids.APICULTURE);
	}

	private void registerCombRecipes(RecipeDataHelper helper) {
		for (EnumHoneyComb honeyComb : EnumHoneyComb.VALUES) {
			Item comb = ApicultureItems.BEE_COMBS.get(honeyComb).item();
			Block combBlock = ApicultureBlocks.BEE_COMB.get(honeyComb).block();
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(combBlock).define('#', comb).pattern("##").pattern("##").unlockedBy("has_comb", has(comb)).group("combs")::save,
					ForestryModuleUids.APICULTURE
			);
		}
	}

	private void registerArboricultureRecipes(RecipeDataHelper helper) {
		registerWoodRecipes(helper);

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ArboricultureItems.GRAFTER.item())
						.define('B', ForestryTags.Items.INGOTS_BRONZE)
						.define('#', Tags.Items.RODS_WOODEN)
						.pattern("  B").pattern(" # ").pattern("#  ")
						.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE))::save,
				ForestryModuleUids.ARBORICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ArboricultureBlocks.TREE_CHEST.block())
						.define('#', Tags.Items.GLASS)
						.define('X', ItemTags.SAPLINGS)
						.define('Y', Tags.Items.CHESTS_WOODEN)
						.pattern(" # ").pattern("XYX").pattern("XXX")
						.unlockedBy("has_sapling", has(ItemTags.SAPLINGS))::save,
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
						ShapelessRecipeBuilder.shapeless(planks, 4).requires(log).unlockedBy("has_log", has(log)).group("planks")::save,
						ForestryModuleUids.ARBORICULTURE);
				helper.moduleConditionRecipe(
						ShapedRecipeBuilder.shaped(fence, 3).define('#', Tags.Items.RODS_WOODEN).define('W', planks).pattern("W#W").pattern("W#W").unlockedBy("has_planks", has(planks)).group("wooden_fence")::save,
						ForestryModuleUids.ARBORICULTURE);
				helper.moduleConditionRecipe(
						ShapedRecipeBuilder.shaped(fencegate).define('#', Tags.Items.RODS_WOODEN).define('W', planks).pattern("#W#").pattern("#W#").unlockedBy("has_planks", has(planks)).group("wooden_fence_gate")::save,
						ForestryModuleUids.ARBORICULTURE);
				helper.moduleConditionRecipe(
						ShapedRecipeBuilder.shaped(slab, 6).define('#', planks).pattern("###").unlockedBy("has_planks", has(planks)).group("wooden_slab")::save,
						ForestryModuleUids.ARBORICULTURE);
				helper.moduleConditionRecipe(
						ShapedRecipeBuilder.shaped(stairs, 4).define('#', planks).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_planks", has(planks)).group("wooden_stairs")::save,
						ForestryModuleUids.ARBORICULTURE);
			}

			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(door, 3).define('#', Ingredient.of(planks, fireproofPlanks)).pattern("##").pattern("##").pattern("##").unlockedBy("has_planks", has(planks)).group("wooden_door")::save,
					ForestryModuleUids.ARBORICULTURE);
			helper.moduleConditionRecipe(
					ShapelessRecipeBuilder.shapeless(fireproofPlanks, 4).requires(fireproofLog).unlockedBy("has_planks", has(fireproofPlanks)).group("planks")::save,
					ForestryModuleUids.ARBORICULTURE);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(fireproofFence, 3).define('#', Tags.Items.RODS_WOODEN).define('W', fireproofPlanks).pattern("W#W").pattern("W#W").unlockedBy("has_planks", has(fireproofPlanks)).group("wooden_fence")::save,
					ForestryModuleUids.ARBORICULTURE);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(fireproofFencegate).define('#', Tags.Items.RODS_WOODEN).define('W', fireproofPlanks).pattern("#W#").pattern("#W#").unlockedBy("has_planks", has(fireproofPlanks)).group("wooden_fence_gate")::save,
					ForestryModuleUids.ARBORICULTURE);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(fireproofSlab, 6).define('#', fireproofPlanks).pattern("###").unlockedBy("has_planks", has(fireproofPlanks)).group("wooden_slab")::save,
					ForestryModuleUids.ARBORICULTURE);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(fireproofStairs, 4).define('#', fireproofPlanks).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_planks", has(fireproofPlanks)).group("wooden_stairs")::save,
					ForestryModuleUids.ARBORICULTURE);

		}
	}

	private void registerFoodRecipes(RecipeDataHelper helper) {

		Item waxCapsule = FluidsItems.CONTAINERS.get(EnumContainerType.CAPSULE).item();
		Item honeyDrop = ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY).item();

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FoodItems.AMBROSIA.item())
						.define('#', ApicultureItems.HONEYDEW.item())
						.define('X', ApicultureItems.ROYAL_JELLY.item())
						.define('Y', waxCapsule)
						.pattern("#Y#").pattern("XXX").pattern("###")
						.unlockedBy("has royal_jelly", has(ApicultureItems.ROYAL_JELLY.item()))::save,
				ForestryModuleUids.FOOD);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FoodItems.HONEY_POT.item())
						.define('#', honeyDrop)
						.define('X', waxCapsule)
						.pattern("# #").pattern(" X ").pattern("# #")
						.unlockedBy("has_drop", has(honeyDrop))::save,
				ForestryModuleUids.FOOD);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FoodItems.HONEYED_SLICE.item())
						.define('#', honeyDrop)
						.define('X', Items.BREAD)
						.pattern("###").pattern("#X#").pattern("###")
						.unlockedBy("has_drop", has(honeyDrop))::save,
				ForestryModuleUids.FOOD);
	}

	private void registerBackpackRecipes(RecipeDataHelper helper) {

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(BackpackItems.ADVENTURER_BACKPACK.item())
						.define('#', ItemTags.WOOL)
						.define('V', Tags.Items.BONES)
						.define('X', Tags.Items.STRING)
						.define('Y', Tags.Items.CHESTS_WOODEN)
						.pattern("X#X").pattern("VYV").pattern("X#X")
						.unlockedBy("has_bone", has(Tags.Items.BONES))::save,
				ForestryModuleUids.BACKPACKS);

		Block beeChest = ArboricultureBlocks.TREE_CHEST.block();
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(BackpackItems.APIARIST_BACKPACK.item())
						.define('#', ItemTags.WOOL)
						.define('V', Tags.Items.RODS_WOODEN)
						.define('X', Tags.Items.STRING)
						.define('Y', beeChest)
						.pattern("X#X").pattern("VYV").pattern("X#X")
						.unlockedBy("has_bee_chest", has(beeChest))::save,
				ForestryModuleUids.BACKPACKS, ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(BackpackItems.BUILDER_BACKPACK.item())
						.define('#', ItemTags.WOOL)
						.define('V', Items.CLAY_BALL)
						.define('X', Tags.Items.STRING)
						.define('Y', Tags.Items.CHESTS_WOODEN)
						.pattern("X#X").pattern("VYV").pattern("X#X")
						.unlockedBy("has_clay", has(Items.CLAY_BALL))::save,
				ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(BackpackItems.DIGGER_BACKPACK.item())
						.define('#', ItemTags.WOOL)
						.define('V', Tags.Items.STONE)
						.define('X', Tags.Items.STRING)
						.define('Y', Tags.Items.CHESTS_WOODEN)
						.pattern("X#X").pattern("VYV").pattern("X#X")
						.unlockedBy("has_stone", has(Tags.Items.STONE))::save,
				ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(BackpackItems.FORESTER_BACKPACK.item())
						.define('#', ItemTags.WOOL)
						.define('V', ItemTags.LOGS)
						.define('X', Tags.Items.STRING)
						.define('Y', Tags.Items.CHESTS_WOODEN)
						.pattern("X#X").pattern("VYV").pattern("X#X")
						.unlockedBy("has_log", has(ItemTags.LOGS))::save,
				ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(BackpackItems.HUNTER_BACKPACK.item())
						.define('#', ItemTags.WOOL)
						.define('V', Tags.Items.FEATHERS)
						.define('X', Tags.Items.STRING)
						.define('Y', Tags.Items.CHESTS_WOODEN)
						.pattern("X#X").pattern("VYV").pattern("X#X")
						.unlockedBy("has_feather", has(Tags.Items.FEATHERS))::save,
				ForestryModuleUids.BACKPACKS);

		Block butterflyChest = LepidopterologyBlocks.BUTTERFLY_CHEST.block();
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(BackpackItems.LEPIDOPTERIST_BACKPACK.item())
						.define('#', ItemTags.WOOL)
						.define('V', butterflyChest)
						.define('X', Tags.Items.STRING)
						.define('Y', Tags.Items.CHESTS_WOODEN)
						.pattern("X#X").pattern("VYV").pattern("X#X")
						.unlockedBy("has_butterfly_chest", has(butterflyChest))::save,
				ForestryModuleUids.BACKPACKS, ForestryModuleUids.LEPIDOPTEROLOGY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(BackpackItems.MINER_BACKPACK.item())
						.define('#', ItemTags.WOOL)
						.define('V', Tags.Items.INGOTS_IRON)
						.define('X', Tags.Items.STRING)
						.define('Y', Tags.Items.CHESTS_WOODEN)
						.pattern("X#X").pattern("VYV").pattern("X#X")
						.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))::save,
				ForestryModuleUids.BACKPACKS);
	}

	private void registerCharcoalRecipes(RecipeDataHelper helper) {

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(CharcoalBlocks.CHARCOAL.block())
						.define('#', Items.CHARCOAL)
						.pattern("###").pattern("###").pattern("###")
						.unlockedBy("has_charcoal", has(Items.CHARCOAL))::save,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				(consumer) -> ShapelessRecipeBuilder.shapeless(Items.CHARCOAL, 9)
						.requires(ForestryTags.Items.CHARCOAL)
						.unlockedBy("has_charcoal_block", has(ForestryTags.Items.CHARCOAL))
						.save(consumer, new ResourceLocation(Constants.MOD_ID, "charcoal_from_block")),
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(CharcoalBlocks.WOOD_PILE.block())
						.define('L', ItemTags.LOGS)
						.pattern("LL").pattern("LL")
						.unlockedBy("has_log", has(ItemTags.LOGS))::save,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapeless(CharcoalBlocks.WOOD_PILE_DECORATIVE.block())
						.requires(CharcoalBlocks.WOOD_PILE.block())
						.unlockedBy("was_wood_pile", has(CharcoalBlocks.WOOD_PILE.block()))::save,
				ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapeless(CharcoalBlocks.WOOD_PILE.block())
						.requires(CharcoalBlocks.WOOD_PILE_DECORATIVE.block())
						.unlockedBy("has_decorative", has(CharcoalBlocks.WOOD_PILE_DECORATIVE.block()))::save,
				new ResourceLocation(Constants.MOD_ID, "wood_pile_from_decorative"), ForestryModuleUids.CHARCOAL);
	}

	private void registerClimatologyRecipes(RecipeDataHelper helper) {

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(ClimatologyBlocks.HABITATFORMER.block())
						.define('S', CoreItems.STURDY_CASING.item())
						.define('G', Tags.Items.GLASS)
						.define('B', ForestryTags.Items.GEARS_BRONZE)
						.define('R', Tags.Items.DUSTS_REDSTONE)
						.define('C', CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.BASIC).item())
						.define('T', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.IRON).item())
						.pattern("GRG").pattern("TST").pattern("BCB")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.CLIMATOLOGY);
		//TODO if carpenter recipes are in json then can just use that here
		helper.simpleConditionalRecipe(
				ShapedRecipeBuilder.shaped(ClimatologyItems.HABITAT_SCREEN.item())
						.define('G', ForestryTags.Items.GEARS_BRONZE)
						.define('P', Tags.Items.GLASS_PANES)
						.define('I', ForestryTags.Items.INGOTS_BRONZE)
						.define('D', Tags.Items.GEMS_DIAMOND)
						.pattern("IPI").pattern("IPI").pattern("DGD")
						.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))::save,
				new AndCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.CLIMATOLOGY),
						new NotCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.FACTORY))));
	}

	private void registerCoreRecipes(RecipeDataHelper helper) {
		Consumer<IFinishedRecipe> consumer = helper.getConsumer();

		CookingRecipeBuilder.smelting(
				Ingredient.of(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE)),
				CoreItems.APATITE,
				0.5F,
				200)
				.unlockedBy("has_apatite_ore", has(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE)))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "apatite_from_blasting"));
		CookingRecipeBuilder.smelting(
				Ingredient.of(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN)),
				CoreItems.INGOT_TIN,
				0.5F,
				200)
				.unlockedBy("has_tin_ore", has(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN)))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "tin_ingot_from_blasting"));
		CookingRecipeBuilder.smelting(
				Ingredient.of(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER)),
				CoreItems.INGOT_COPPER,
				0.5F,
				200)
				.unlockedBy("has_copper_ore", has(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER)))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "copper_ingot_from_blasting"));
		CookingRecipeBuilder.smelting(
				Ingredient.of(CoreItems.PEAT),
				CoreItems.ASH.item(),
				0.0F,
				200)
				.unlockedBy("has_peat", has(CoreItems.PEAT))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "ash_from_peat_blasting"));

		//don't need conditions here generally since core is always enabled
		ShapedRecipeBuilder.shaped(CoreBlocks.BASE.get(BlockTypeCoreTesr.ANALYZER).block())
				.define('T', CoreItems.PORTABLE_ALYZER.item())
				.define('X', ForestryTags.Items.INGOTS_BRONZE)
				.define('Y', CoreItems.STURDY_CASING.item())
				.pattern("XTX").pattern(" Y ").pattern("X X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item())).save(consumer);
		//TODO how to deal with variable output. Options: wrapper recipe, custom recipe type, leave up to data packs.
		ShapedRecipeBuilder.shaped(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.APATITE).block())
				.define('#', ForestryTags.Items.GEMS_APATITE)
				.pattern("###").pattern("###").pattern("###")
				.unlockedBy("has_apatite", has(ForestryTags.Items.GEMS_APATITE)).save(consumer);
		ShapedRecipeBuilder.shaped(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.BRONZE).block())
				.define('#', ForestryTags.Items.INGOTS_BRONZE)
				.pattern("###").pattern("###").pattern("###")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(consumer);
		ShapedRecipeBuilder.shaped(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.COPPER).block())
				.define('#', ForestryTags.Items.INGOTS_COPPER)
				.pattern("###").pattern("###").pattern("###")
				.unlockedBy("has_copper", has(ForestryTags.Items.INGOTS_COPPER)).save(consumer);
		ShapedRecipeBuilder.shaped(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.TIN).block())
				.define('#', ForestryTags.Items.INGOTS_TIN)
				.pattern("###").pattern("###").pattern("###")
				.unlockedBy("has_apatite", has(ForestryTags.Items.INGOTS_TIN)).save(consumer);
		ShapedRecipeBuilder.shaped(CoreItems.BRONZE_PICKAXE.item())
				.define('#', ForestryTags.Items.INGOTS_BRONZE)
				.define('X', Tags.Items.RODS_WOODEN)
				.pattern("###").pattern(" X ").pattern(" X ")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(consumer);
		ShapedRecipeBuilder.shaped(CoreItems.BRONZE_SHOVEL.item())
				.define('#', ForestryTags.Items.INGOTS_BRONZE)
				.define('X', Tags.Items.RODS_WOODEN)
				.pattern(" # ").pattern(" X ").pattern(" X ")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(consumer);

		//TODO maybe get clever with a loop here
		ConditionalRecipe.builder()
				.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
				.addRecipe(
						ShapedRecipeBuilder.shaped(CoreItems.GEAR_BRONZE.item())
								.define('#', ForestryTags.Items.INGOTS_BRONZE)
								.define('X', ForestryTags.Items.GEARS_STONE)
								.pattern(" # ").pattern("#X#").pattern(" # ")
								.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE))::save)
				.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
				.addRecipe(ShapedRecipeBuilder.shaped(CoreItems.GEAR_BRONZE.item())
						.define('#', ForestryTags.Items.INGOTS_BRONZE)
						.define('X', ForestryTags.Items.INGOTS_COPPER)
						.pattern(" # ").pattern("#X#").pattern(" # ")
						.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE))::save)
			.build(helper.getConsumer(), new ResourceLocation(Constants.MOD_ID, "gear_bronze"));
		ConditionalRecipe.builder()
				.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
				.addRecipe(
						ShapedRecipeBuilder.shaped(CoreItems.GEAR_COPPER.item())
								.define('#', ForestryTags.Items.INGOTS_COPPER)
								.define('X', ForestryTags.Items.GEARS_STONE)
								.pattern(" # ").pattern("#X#").pattern(" # ")
								.unlockedBy("has_copper", has(ForestryTags.Items.INGOTS_COPPER))::save)
				.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
				.addRecipe(ShapedRecipeBuilder.shaped(CoreItems.GEAR_COPPER.item())
						.define('#', ForestryTags.Items.INGOTS_COPPER)
						.define('X', ForestryTags.Items.INGOTS_COPPER)
						.pattern(" # ").pattern("#X#").pattern(" # ")
						.unlockedBy("has_copper", has(ForestryTags.Items.INGOTS_COPPER))::save)
			.build(helper.getConsumer(), new ResourceLocation(Constants.MOD_ID, "gear_copper"));
		ConditionalRecipe.builder()
				.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
				.addRecipe(
						ShapedRecipeBuilder.shaped(CoreItems.GEAR_TIN.item())
								.define('#', ForestryTags.Items.INGOTS_TIN)
								.define('X', ForestryTags.Items.GEARS_STONE)
								.pattern(" # ").pattern("#X#").pattern(" # ")
								.unlockedBy("has_tin", has(ForestryTags.Items.INGOTS_TIN))::save)
				.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
				.addRecipe(ShapedRecipeBuilder.shaped(CoreItems.GEAR_TIN.item())
						.define('#', ForestryTags.Items.INGOTS_TIN)
						.define('X', ForestryTags.Items.INGOTS_COPPER)
						.pattern(" # ").pattern("#X#").pattern(" # ")
						.unlockedBy("has_tin", has(ForestryTags.Items.INGOTS_TIN))::save)
				.build(helper.getConsumer(), new ResourceLocation(Constants.MOD_ID, "gear_tin"));

		ShapelessRecipeBuilder.shapeless(CoreItems.INGOT_BRONZE.item())
				.requires(ForestryTags.Items.INGOTS_TIN)
				.requires(ForestryTags.Items.INGOTS_COPPER)
				.requires(ForestryTags.Items.INGOTS_COPPER)
				.requires(ForestryTags.Items.INGOTS_COPPER)
				.unlockedBy("has_tin", has(ForestryTags.Items.INGOTS_TIN))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "ingot_bronze_alloying"));

		ShapelessRecipeBuilder.shapeless(CoreItems.APATITE.item(), 9)
				.requires(ForestryTags.Items.STORAGE_BLOCKS_APATITE)
				.unlockedBy("has_block", has(ForestryTags.Items.STORAGE_BLOCKS_APATITE)).save(consumer);
		ShapelessRecipeBuilder.shapeless(CoreItems.INGOT_BRONZE.item(), 9)
				.requires(ForestryTags.Items.STORAGE_BLOCKS_BRONZE)
				.unlockedBy("has_block", has(ForestryTags.Items.STORAGE_BLOCKS_BRONZE)).save(consumer);
		ShapelessRecipeBuilder.shapeless(CoreItems.INGOT_COPPER.item(), 9)
				.requires(ForestryTags.Items.STORAGE_BLOCKS_COPPER)
				.unlockedBy("has_block", has(ForestryTags.Items.STORAGE_BLOCKS_COPPER)).save(consumer);
		ShapelessRecipeBuilder.shapeless(CoreItems.INGOT_TIN.item(), 9)
				.requires(ForestryTags.Items.STORAGE_BLOCKS_TIN)
				.unlockedBy("has_block", has(ForestryTags.Items.STORAGE_BLOCKS_TIN)).save(consumer);
		ShapelessRecipeBuilder.shapeless(CoreItems.KIT_PICKAXE.item())
				.requires(CoreItems.BRONZE_PICKAXE.item())
				.requires(CoreItems.CARTON.item())
				.unlockedBy("has_pickaxe", has(CoreItems.BRONZE_PICKAXE.item())).save(consumer);
		ShapelessRecipeBuilder.shapeless(CoreItems.KIT_SHOVEL.item())
				.requires(CoreItems.BRONZE_SHOVEL.item())
				.requires(CoreItems.CARTON.item())
				.unlockedBy("has_shovel", has(CoreItems.BRONZE_SHOVEL.item())).save(consumer);
		ShapedRecipeBuilder.shaped(CoreItems.SPECTACLES.item())
				.define('X', ForestryTags.Items.INGOTS_BRONZE)
				.define('Y', Tags.Items.GLASS_PANES)
				.pattern(" X ").pattern("Y Y")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(consumer);
		ShapedRecipeBuilder.shaped(CoreItems.PIPETTE.item())
				.define('#', ItemTags.WOOL)
				.define('X', Tags.Items.GLASS_PANES)
				.pattern("  #").pattern(" X ").pattern("X  ")
				.unlockedBy("has_wool", has(ItemTags.WOOL)).save(consumer);
		helper.simpleConditionalRecipe(
				ShapedRecipeBuilder.shaped(CoreItems.PORTABLE_ALYZER.item())
						.define('#', Tags.Items.GLASS_PANES)
						.define('X', ForestryTags.Items.INGOTS_TIN)
						.define('R', Tags.Items.DUSTS_REDSTONE)
						.define('D', Tags.Items.GEMS_DIAMOND)
						.pattern("X#X").pattern("X#X").pattern("RDR")
						.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))::save,
				new NotCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.FACTORY)));
		ShapedRecipeBuilder.shaped(Items.STRING)
				.define('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).item())
				.pattern(" # ").pattern(" # ").pattern(" # ")
				.unlockedBy("has_wisp", has(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).item()))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "string_from_wisp"));
		ShapedRecipeBuilder.shaped(CoreItems.STURDY_CASING.item())
				.define('#', ForestryTags.Items.INGOTS_BRONZE)
				.pattern("###").pattern("# #").pattern("###")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(consumer);
		ShapedRecipeBuilder.shaped(Items.COBWEB, 4)
				.define('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).item())
				.pattern("# #").pattern(" # ").pattern("# #")
				.unlockedBy("has_wisp", has(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).item()))
				.save(consumer, new ResourceLocation("cobweb_from_wisp"));
		ShapedRecipeBuilder.shaped(CoreItems.WRENCH.item())
				.define('#', ForestryTags.Items.INGOTS_BRONZE)
				.pattern("# #").pattern(" # ").pattern(" # ")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(consumer);

		// Manure and Fertilizer
		ShapedRecipeBuilder.shaped(CoreItems.COMPOST, 4)
				.define('#', Blocks.DIRT)
				.define('X', Tags.Items.CROPS_WHEAT)
				.pattern(" X ").pattern("X#X").pattern(" X ")
				.unlockedBy("has_wheat", has(Tags.Items.CROPS_WHEAT))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "compost_wheat"));

		ShapedRecipeBuilder.shaped(CoreItems.COMPOST, 1)
				.define('#', Blocks.DIRT)
				.define('X', ForestryTags.Items.DUSTS_ASH)
				.pattern(" X ").pattern("X#X").pattern(" X ")
				.unlockedBy("has_ash", has(ForestryTags.Items.DUSTS_ASH))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "compost_ash"));

		ShapedRecipeBuilder.shaped(CoreItems.FERTILIZER_COMPOUND, 8)
				.define('#', ItemTags.SAND)
				.define('X', ForestryTags.Items.GEMS_APATITE)
				.pattern(" # ").pattern(" X ").pattern(" # ")
				.unlockedBy("has_apatite", has(ForestryTags.Items.GEMS_APATITE))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "fertilizer_apatite"));

		ShapedRecipeBuilder.shaped(CoreItems.FERTILIZER_COMPOUND, 16)
				.define('#', ForestryTags.Items.DUSTS_ASH)
				.define('X', ForestryTags.Items.GEMS_APATITE)
				.pattern("###").pattern("#X#").pattern("###")
				.unlockedBy("has_apatite", has(ForestryTags.Items.GEMS_APATITE))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "fertilizer_ash"));

		// Humus
		ShapedRecipeBuilder.shaped(CoreBlocks.HUMUS, 8)
				.define('#', Blocks.DIRT)
				.define('X', CoreItems.COMPOST)
				.pattern("###").pattern("#X#").pattern("###")
				.unlockedBy("has_compost", has(CoreItems.COMPOST))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "humus_compost"));

		ShapedRecipeBuilder.shaped(CoreBlocks.HUMUS, 8)
				.define('#', Blocks.DIRT)
				.define('X', CoreItems.FERTILIZER_COMPOUND)
				.pattern("###").pattern("#X#").pattern("###")
				.unlockedBy("has_fertilizer", has(CoreItems.FERTILIZER_COMPOUND))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "humus_fertilizer"));

		TriConsumer<Integer, ItemStack, String> bogRecipe = (amount, container, name) -> ShapedRecipeBuilder.shaped(CoreBlocks.BOG_EARTH, amount)
				.define('#', Blocks.DIRT)
				.define('X', new ComplexIngredient(container))
				.define('Y', ItemTags.SAND)
				.pattern("#Y#").pattern("YXY").pattern("#Y#")
				.unlockedBy("has_sand", has(ItemTags.SAND))
				.save(consumer, new ResourceLocation(Constants.MOD_ID, "bog_earth_" + name));

		// Bog earth
		bogRecipe.accept(6, new ItemStack(Items.WATER_BUCKET), "bucket");

		ItemStack canWater = FluidsItems.getContainer(EnumContainerType.CAN, Fluids.WATER);
		ItemStack waxCapsuleWater = FluidsItems.getContainer(EnumContainerType.CAPSULE, Fluids.WATER);
		ItemStack refractoryWater = FluidsItems.getContainer(EnumContainerType.REFRACTORY, Fluids.WATER);
		bogRecipe.accept(8, canWater, "can");
		bogRecipe.accept(8, waxCapsuleWater, "wax_capsule");
		bogRecipe.accept(8, refractoryWater, "refractory");
	}

	private void registerBookRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapeless(BookItems.BOOK.item())
						.requires(Items.BOOK)
						.requires(ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY).item())
						.unlockedBy("has_book", has(Items.BOOK))::save,
				new ResourceLocation(Constants.MOD_ID, "book_forester_drop"),
				ForestryModuleUids.BOOK, ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapeless(BookItems.BOOK.item())
						.requires(Items.BOOK)
						.requires(ItemTags.SAPLINGS)
						.unlockedBy("has_book", has(Items.BOOK))::save,
				new ResourceLocation(Constants.MOD_ID, "book_forester_sapling"),
				ForestryModuleUids.BOOK);
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapeless(BookItems.BOOK.item())
						.requires(Items.BOOK)
						.requires(LepidopterologyItems.BUTTERFLY_GE.item())
						.unlockedBy("has_book", has(Items.BOOK))::save,
				new ResourceLocation(Constants.MOD_ID, "book_forester_butterfly"),
				ForestryModuleUids.BOOK, ForestryModuleUids.LEPIDOPTEROLOGY);
	}

	private EnumElectronTube getElectronTube(BlockTypePlanter planter) {
		switch (planter) {
			case ARBORETUM:
				return EnumElectronTube.GOLD;
			case FARM_CROPS:
				return EnumElectronTube.BRONZE;
			case PEAT_POG:
				return EnumElectronTube.OBSIDIAN;
			case FARM_MUSHROOM:
				return EnumElectronTube.APATITE;
			case FARM_GOURD:
				return EnumElectronTube.LAPIS;
			case FARM_NETHER:
				return EnumElectronTube.BLAZE;
			case FARM_ENDER:
				return EnumElectronTube.ENDER;
			default:
				return null;
		}
	}

	private void registerCultivationRecipes(RecipeDataHelper helper) {
		for (BlockTypePlanter planter : BlockTypePlanter.values()) {
			Block managed = CultivationBlocks.PLANTER.get(planter, BlockPlanter.Mode.MANAGED).block();
			Block manual = CultivationBlocks.PLANTER.get(planter, BlockPlanter.Mode.MANUAL).block();

			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(managed)
							.define('G', Tags.Items.GLASS)
							.define('T', CoreItems.ELECTRON_TUBES.get(getElectronTube(planter)).item())
							.define('C', CoreItems.FLEXIBLE_CASING.item())
							.define('B', CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.BASIC).item())
							.pattern("GTG").pattern("TCT").pattern("GBG")
							.unlockedBy("has_casing", has(CoreItems.FLEXIBLE_CASING.item()))::save,
					ForestryModuleUids.CULTIVATION);

			helper.moduleConditionRecipe(
					ShapelessRecipeBuilder.shapeless(manual)
							.requires(managed)
							.unlockedBy("has_managed", has(managed))::save,
					ForestryModuleUids.CULTIVATION);
			helper.moduleConditionRecipe(
					ShapelessRecipeBuilder.shapeless(managed)
							.requires(manual)
							.unlockedBy("has_manual", has(manual))::save,
					new ResourceLocation(Constants.MOD_ID, managed.getRegistryName().getPath() + "_from_manual"),
					ForestryModuleUids.CULTIVATION);
		}
	}

	private void registerDatabaseRecipes(RecipeDataHelper helper) {
		//TODO create FallbackIngredient implementation
		List<FeatureBlock<?, ?>> features = Lists.newArrayList(ApicultureBlocks.BEE_CHEST, ArboricultureBlocks.TREE_CHEST, LepidopterologyBlocks.BUTTERFLY_CHEST);
		List<Ingredient> possibleSpecials = Lists.newArrayList(Ingredient.of(ApicultureItems.ROYAL_JELLY.getItem()), Ingredient.of(CoreItems.FRUITS.get(ItemFruit.EnumFruit.PLUM).getItem()), Ingredient.of(Tags.Items.CHESTS_WOODEN));
		Ingredient possibleSpecial = Ingredient.merge(possibleSpecials);
		for (FeatureBlock<?, ?> featureBlock1 : features) {
			for (FeatureBlock<?, ?> featureBlock2 : features) {
				if (featureBlock1.equals(featureBlock2)) {
					continue;
				}

				helper.moduleConditionRecipe(
						ShapedRecipeBuilder.shaped(DatabaseBlocks.DATABASE.block())
								.define('#', CoreItems.PORTABLE_ALYZER.item())
								.define('C', possibleSpecial)
								.define('S', featureBlock1.block())
								.define('F', featureBlock2.block())
								.define('W', ItemTags.PLANKS)
								.define('I', ForestryTags.Items.INGOTS_BRONZE)
								.define('Y', CoreItems.STURDY_CASING.item())
								.pattern("I#I").pattern("FYS").pattern("WCW")
								.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
						new ResourceLocation(Constants.MOD_ID, "database_" + featureBlock1.getIdentifier() + "_" + featureBlock2.getIdentifier()),
						ForestryModuleUids.DATABASE, featureBlock1.getModuleId(), featureBlock2.getModuleId());
			}
		}
	}

	private void registerEnergyRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(EnergyBlocks.ENGINES.get(BlockTypeEngine.BIOGAS).block())
						.define('#', ForestryTags.Items.INGOTS_BRONZE)
						.define('V', Items.PISTON)
						.define('X', Tags.Items.GLASS)
						.define('Y', ForestryTags.Items.GEARS_BRONZE)
						.pattern("###").pattern(" X ").pattern("YVY")
						.unlockedBy("has_piston", has(Items.PISTON))::save,
				ForestryModuleUids.ENERGY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(EnergyBlocks.ENGINES.get(BlockTypeEngine.CLOCKWORK).block())
						.define('#', ItemTags.PLANKS)
						.define('V', Items.PISTON)
						.define('X', Tags.Items.GLASS)
						.define('Y', Items.CLOCK)
						.define('Z', ForestryTags.Items.GEARS_COPPER)
						.pattern("###").pattern(" X ").pattern("ZVY")
						.unlockedBy("has_piston", has(Items.PISTON))::save,
				ForestryModuleUids.ENERGY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(EnergyBlocks.ENGINES.get(BlockTypeEngine.PEAT).block())
						.define('#', ForestryTags.Items.INGOTS_COPPER)
						.define('V', Items.PISTON)
						.define('X', Tags.Items.GLASS)
						.define('Y', ForestryTags.Items.GEARS_COPPER)
						.pattern("###").pattern(" X ").pattern("YVY")
						.unlockedBy("has_piston", has(Items.PISTON))::save,
				ForestryModuleUids.ENERGY);
	}

	private void registerFactoryRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.BOTTLER).block())
						.define('#', Tags.Items.GLASS)
						.define('X', FluidsItems.CONTAINERS.get(EnumContainerType.CAN).item())
						.define('Y', CoreItems.STURDY_CASING.item())
						.pattern("X#X").pattern("#Y#").pattern("X#X")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CARPENTER).block())
						.define('#', Tags.Items.GLASS)
						.define('X', ForestryTags.Items.INGOTS_BRONZE)
						.define('Y', CoreItems.STURDY_CASING.item())
						.pattern("X#X").pattern("XYX").pattern("X#X")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CENTRIFUGE).block())
						.define('#', Tags.Items.GLASS)
						.define('X', ForestryTags.Items.INGOTS_COPPER)
						.define('Y', CoreItems.STURDY_CASING.item())
						.pattern("X#X").pattern("XYX").pattern("X#X")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR).block())
						.define('#', Tags.Items.GLASS)
						.define('X', Tags.Items.INGOTS_GOLD)
						.define('Y', CoreItems.STURDY_CASING.item())
						.define('Z', Tags.Items.CHESTS_WOODEN)
						.pattern("X#X").pattern("#Y#").pattern("XZX")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.FERMENTER).block())
						.define('#', Tags.Items.GLASS)
						.define('X', ForestryTags.Items.GEARS_BRONZE)
						.define('Y', CoreItems.STURDY_CASING.item())
						.pattern("X#X").pattern("#Y#").pattern("X#X")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.MOISTENER).block())
						.define('#', Tags.Items.GLASS)
						.define('X', ForestryTags.Items.GEARS_COPPER)
						.define('Y', CoreItems.STURDY_CASING.item())
						.pattern("X#X").pattern("#Y#").pattern("X#X")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.RAINMAKER).block())
						.define('#', Tags.Items.GLASS)
						.define('X', ForestryTags.Items.GEARS_TIN)
						.define('Y', CoreItems.STURDY_CASING.item())
						.pattern("X#X").pattern("#Y#").pattern("X#X")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).block())
						.define('#', Tags.Items.GLASS)
						.define('X', Tags.Items.INGOTS_IRON)
						.define('Y', CoreItems.STURDY_CASING.item())
						.pattern("X#X").pattern("XYX").pattern("X#X")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.SQUEEZER).block())
						.define('#', Tags.Items.GLASS)
						.define('X', ForestryTags.Items.INGOTS_TIN)
						.define('Y', CoreItems.STURDY_CASING.item())
						.pattern("X#X").pattern("XYX").pattern("X#X")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.STILL).block())
						.define('#', Tags.Items.GLASS)
						.define('X', Tags.Items.DUSTS_REDSTONE)
						.define('Y', CoreItems.STURDY_CASING.item())
						.pattern("X#X").pattern("#Y#").pattern("X#X")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.FACTORY);
	}

	private void registerFarmingRecipes(RecipeDataHelper helper) {
		for (EnumFarmMaterial material : EnumFarmMaterial.values()) {
			Item base = material.getBase().getItem();
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(FarmingBlocks.FARM.get(EnumFarmBlockType.PLAIN, material).block())
							.define('#', base)
							.define('C', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.TIN).item())
							.define('W', ItemTags.WOODEN_SLABS)
							.define('I', ForestryTags.Items.INGOTS_COPPER)
							.pattern("I#I").pattern("WCW")
							.unlockedBy("has_copper", has(ForestryTags.Items.INGOTS_COPPER))::save,
					ForestryModuleUids.FARMING);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(FarmingBlocks.FARM.get(EnumFarmBlockType.GEARBOX, material).block())
							.define('#', base)
							.define('T', ForestryTags.Items.GEARS_TIN)
							.pattern(" # ").pattern("TTT")
							.unlockedBy("has_tin_gear", has(ForestryTags.Items.GEARS_TIN))::save,
					ForestryModuleUids.FARMING);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(FarmingBlocks.FARM.get(EnumFarmBlockType.HATCH, material).block())
							.define('#', base)
							.define('T', ForestryTags.Items.GEARS_TIN)
							.define('D', ItemTags.WOODEN_TRAPDOORS)
							.pattern(" # ").pattern("TDT")
							.unlockedBy("has_tin_gear", has(ForestryTags.Items.GEARS_TIN))::save,
					ForestryModuleUids.FARMING);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(FarmingBlocks.FARM.get(EnumFarmBlockType.VALVE, material).block())
							.define('#', base)
							.define('T', ForestryTags.Items.GEARS_TIN)
							.define('X', Tags.Items.GLASS)
							.pattern(" # ").pattern("XTX")
							.unlockedBy("has_tin_gear", has(ForestryTags.Items.GEARS_TIN))::save,
					ForestryModuleUids.FARMING);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(FarmingBlocks.FARM.get(EnumFarmBlockType.CONTROL, material).block())
							.define('#', base)
							.define('T', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.GOLD).item())
							.define('X', Tags.Items.DUSTS_REDSTONE)
							.pattern(" # ").pattern("XTX")
							.unlockedBy("has_tin_gear", has(ForestryTags.Items.GEARS_TIN))::save,
					ForestryModuleUids.FARMING);
		}
	}

	private void registerFluidsRecipes(RecipeDataHelper helper) {
		ForestryFluids milk = ForestryFluids.MILK;
		for (EnumContainerType containerType : EnumContainerType.values()) {
			/*if (containerType == EnumContainerType.JAR || containerType == EnumContainerType.GLASS) {
				continue;
			}*/
			ItemStack filled = FluidsItems.getContainer(containerType, milk);
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(Items.CAKE)
							.define('A', new ComplexIngredient(filled))
							.define('B', Items.SUGAR)
							.define('C', Items.WHEAT)
							.define('E', Items.EGG)
							.pattern("AAA").pattern("BEB").pattern("CCC")
							.unlockedBy("has_wheat", has(Items.WHEAT))::save,
					new ResourceLocation(Constants.MOD_ID, "cake_" + containerType.getSerializedName()),
					ForestryModuleUids.FLUIDS);
		}
	}

	private void registerLepidopterologyRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(LepidopterologyBlocks.BUTTERFLY_CHEST.block())
						.define('#', Tags.Items.GLASS)
						.define('X', LepidopterologyItems.BUTTERFLY_GE.item())    //TODO tag?
						.define('Y', Tags.Items.CHESTS_WOODEN)
						.pattern(" # ").pattern("XYX").pattern("XXX")
						.unlockedBy("has_butterfly", has(LepidopterologyItems.BUTTERFLY_GE.item()))::save,
				ForestryModuleUids.LEPIDOPTEROLOGY);
	}

	private void registerMailRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapeless(MailItems.CATALOGUE.item())
						.requires(Items.BOOK)
						.requires(ForestryTags.Items.STAMPS)
						.unlockedBy("has_book", has(Items.BOOK))::save,
				ForestryModuleUids.MAIL);

		//TODO fallback ingredient
		helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapeless(MailItems.LETTERS.get(ItemLetter.Size.EMPTY, ItemLetter.State.FRESH).item())
						.requires(Items.PAPER)
						.requires(Ingredient.merge(Lists.newArrayList(Ingredient.of(ForestryTags.Items.PROPOLIS), Ingredient.of(Tags.Items.SLIMEBALLS))))
						.unlockedBy("has_paper", has(Items.PAPER))::save,
				ForestryModuleUids.MAIL);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(MailBlocks.BASE.get(BlockTypeMail.MAILBOX).block())
						.define('#', ForestryTags.Items.INGOTS_TIN)
						.define('X', Tags.Items.CHESTS_WOODEN)
						.define('Y', CoreItems.STURDY_CASING.item())
						.pattern(" # ").pattern("#Y#").pattern("XXX")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.MAIL);
		Item[] emptiedLetter = MailItems.LETTERS.getRowFeatures(ItemLetter.Size.EMPTY).stream().map(FeatureItem::getItem).toArray(Item[]::new);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(Items.PAPER)
						.define('#', Ingredient.of(emptiedLetter))
						.pattern(" # ").pattern(" # ").pattern(" # ")
						.unlockedBy("has_paper", has(Items.PAPER))::save,
				ForestryModuleUids.MAIL);
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(MailBlocks.BASE.get(BlockTypeMail.TRADE_STATION).block())
						.define('#', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.BRONZE))
						.define('X', Tags.Items.CHESTS_WOODEN)
						.define('Y', CoreItems.STURDY_CASING.item())
						.define('Z', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.IRON).item())
						.define('W', new ComplexIngredient(ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[]{})))
						.pattern("Z#Z").pattern("#Y#").pattern("XWX")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING.item()))::save,
				ForestryModuleUids.MAIL);

		//TODO fallback
		Ingredient glue = Ingredient.merge(Lists.newArrayList(Ingredient.of(ForestryTags.Items.DROP_HONEY), Ingredient.of(Items.SLIME_BALL)));
		for (EnumStampDefinition stampDefinition : EnumStampDefinition.VALUES) {
			helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shaped(MailItems.STAMPS.get(stampDefinition).item(), 9)
							.define('X', stampDefinition.getCraftingIngredient())
							.define('#', Items.PAPER)
							.define('Z', glue)
							.pattern("XXX").pattern("###").pattern("ZZZ")
							.unlockedBy("has_paper", has(Items.PAPER))::save,
					ForestryModuleUids.MAIL);
		}
	}

	private void registerSortingRecipes(RecipeDataHelper helper) {
		Ingredient ing = Ingredient.merge(Lists.newArrayList(Ingredient.of(LepidopterologyItems.CATERPILLAR_GE.item(), ApicultureItems.PROPOLIS.get(EnumPropolis.NORMAL).item()), Ingredient.of(ForestryTags.Items.FRUITS)));

		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(SortingBlocks.FILTER.block(), 2)
						.define('B', ForestryTags.Items.GEARS_BRONZE)
						.define('D', Tags.Items.GEMS_DIAMOND)
						.define('F', ing)
						.define('W', ItemTags.PLANKS)
						.define('G', Tags.Items.GLASS)
						.pattern("WDW").pattern("FGF").pattern("BDB")
						.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))::save,
				ForestryModuleUids.SORTING);
	}

	private void registerWorktableRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shaped(WorktableBlocks.WORKTABLE.block())
						.define('B', Items.BOOK)
						.define('C', Tags.Items.CHESTS_WOODEN)
						.define('W', Items.CRAFTING_TABLE)
						.pattern("B").pattern("W").pattern("C")
						.unlockedBy("has_crafting_table", has(Items.CRAFTING_TABLE))::save,
				ForestryModuleUids.WORKTABLE);
	}

	@Override
	protected void saveAdvancement(DirectoryCache cache, JsonObject advancementJson, Path pathIn) {
		//NOOP - We dont replace any of the advancement things yet...
	}

	@Override
	public String getName() {
		return "Forestry Recipes";
	}
}
