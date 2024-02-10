package forestry.core.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import deleteme.RegistryNameFinder;
import forestry.apiculture.items.*;
import net.minecraft.data.CachedOutput;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import org.apache.logging.log4j.util.TriConsumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
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
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.climatology.features.ClimatologyBlocks;
import forestry.climatology.features.ClimatologyItems;
import forestry.core.blocks.BlockTypeCoreTesr;
import forestry.core.blocks.EnumResourceType;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.features.FluidsItems;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.ItemFruit;
import forestry.core.items.definitions.EnumContainerType;
import forestry.core.items.definitions.EnumCraftingMaterial;
import forestry.core.items.definitions.EnumElectronTube;
import forestry.cultivation.blocks.BlockPlanter;
import forestry.cultivation.blocks.BlockTypePlanter;
import forestry.cultivation.features.CultivationBlocks;
import forestry.database.features.DatabaseBlocks;
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
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureItem;
import forestry.sorting.features.SortingBlocks;
import forestry.storage.features.BackpackItems;

public class ForestryRecipeProvider extends RecipeProvider {

	public ForestryRecipeProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		registerArboricultureRecipes(consumer);
		registerApicultureRecipes(consumer);
		registerFoodRecipes(consumer);
		registerBackpackRecipes(consumer);
		registerCharcoalRecipes(consumer);
		registerClimatologyRecipes(consumer);
		registerCoreRecipes(consumer);
		registerBookRecipes(consumer);
		registerCultivationRecipes(consumer);
		registerDatabaseRecipes(consumer);
		registerFactoryRecipes(consumer);
		registerFarmingRecipes(consumer);
		registerFluidsRecipes(consumer);
		registerLepidopterologyRecipes(consumer);
		registerMailRecipes(consumer);
		registerSortingRecipes(consumer);
	}

	private void registerApicultureRecipes(Consumer<FinishedRecipe> helper) {
		registerCombRecipes(helper);

		BlockAlveary plain = ApicultureBlocks.ALVEARY.get(BlockAlvearyType.PLAIN).block();
		ItemLike goldElectronTube = CoreItems.ELECTRON_TUBES.get(EnumElectronTube.GOLD);

		ShapedRecipeBuilder.shaped(plain)
				.define('X', CoreItems.IMPREGNATED_CASING)
				.define('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SCENTED_PANELING))
				.pattern("###").pattern("#X#").pattern("###")
				.unlockedBy("has_casing", has(CoreItems.IMPREGNATED_CASING))
				.group("alveary").save(helper);
		ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.FAN).block())
				.define('#', goldElectronTube)
				.define('X', plain)
				.define('I', Tags.Items.INGOTS_IRON)
				.pattern("I I").pattern(" X ").pattern("I#I")
				.unlockedBy("has_plain", has(plain))
				.group("alveary").save(helper);
		ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.HEATER).block())
				.define('#', goldElectronTube)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('X', plain)
				.define('S', Tags.Items.STONE)
				.pattern("#I#").pattern(" X ").pattern("SSS")
				.unlockedBy("has_plain", has(plain))
				.group("alveary").save(helper);
		ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.HYGRO).block())
				.define('G', Tags.Items.GLASS)
				.define('X', plain)
				.define('I', Tags.Items.INGOTS_IRON)
				.pattern("GIG").pattern("GXG").pattern("GIG")
				.unlockedBy("has_plain", has(plain))
				.group("alveary").save(helper);
		ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.SIEVE).block())
				.define('W', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.WOVEN_SILK))
				.define('X', plain)
				.define('I', Tags.Items.INGOTS_IRON)
				.pattern("III").pattern(" X ").pattern("WWW")
				.unlockedBy("has_plain", has(plain))
				.group("alveary").save(helper);
		ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.STABILISER).block())
				.define('X', plain)
				.define('G', Tags.Items.GEMS_QUARTZ)
				.pattern("G G").pattern("GXG").pattern("G G")
				.unlockedBy("has_plain", has(plain))
				.group("alveary").save(helper);
		ShapedRecipeBuilder.shaped(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.SWARMER).block())
				.define('#', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.DIAMOND))
				.define('X', plain)
				.define('G', Tags.Items.INGOTS_GOLD)
				.pattern("#G#").pattern(" X ").pattern("#G#")
				.unlockedBy("has_plain", has(plain))
				.group("alveary").save(helper);

		ItemLike wovenSilk = CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.WOVEN_SILK);
		ShapedRecipeBuilder.shaped(ApicultureItems.APIARIST_HELMET)
				.define('#', wovenSilk)
				.pattern("###").pattern("# #")
				.unlockedBy("has silk", has(wovenSilk))
				.group("apiarist_armour").save(helper);
		ShapedRecipeBuilder.shaped(ApicultureItems.APIARIST_CHEST)
				.define('#', wovenSilk)
				.pattern("# #").pattern("###").pattern("###")
				.unlockedBy("has silk", has(wovenSilk))
				.group("apiarist_armour").save(helper);
		ShapedRecipeBuilder.shaped(ApicultureItems.APIARIST_LEGS)
				.define('#', wovenSilk)
				.pattern("###").pattern("# #").pattern("# #")
				.unlockedBy("has silk", has(wovenSilk))
				.group("apiarist_armour").save(helper);
		ShapedRecipeBuilder.shaped(ApicultureItems.APIARIST_BOOTS)
				.define('#', wovenSilk)
				.pattern("# #").pattern("# #")
				.unlockedBy("has silk", has(wovenSilk))
				.group("apiarist_armour").save(helper);

		ShapedRecipeBuilder.shaped(ApicultureBlocks.BASE.get(BlockTypeApiculture.APIARY).block())
				.define('S', ItemTags.WOODEN_SLABS)
				.define('P', ItemTags.PLANKS)
				.define('C', CoreItems.IMPREGNATED_CASING)
				.pattern("SSS").pattern("PCP").pattern("PPP")
				.unlockedBy("has_casing", has(CoreItems.IMPREGNATED_CASING)).save(helper);
		ShapedRecipeBuilder.shaped(ApicultureBlocks.BASE.get(BlockTypeApiculture.BEE_HOUSE).block())
				.define('S', ItemTags.WOODEN_SLABS)
				.define('P', ItemTags.PLANKS)
				.define('C', ForestryTags.Items.BEE_COMBS)
				.pattern("SSS").pattern("PCP").pattern("PPP")
				.unlockedBy("has_casing", has(ForestryTags.Items.BEE_COMBS)).save(helper);
		//TODO minecarts and candles once they are flattened

		ShapedRecipeBuilder.shaped(ApicultureBlocks.BEE_CHEST.block())
				.define('G', Tags.Items.GLASS)
				.define('X', ForestryTags.Items.BEE_COMBS)
				.define('Y', Tags.Items.CHESTS_WOODEN)
				.pattern(" G ").pattern("XYX").pattern("XXX")
				.unlockedBy("has_comb", has(ForestryTags.Items.BEE_COMBS)).save(helper);

		ItemLike propolis = ApicultureItems.PROPOLIS.get(EnumPropolis.NORMAL);

		ShapedRecipeBuilder.shaped(CoreItems.BITUMINOUS_PEAT)
				.define('#', ForestryTags.Items.DUSTS_ASH)
				.define('X', CoreItems.PEAT)
				.define('Y', propolis)
				.pattern(" # ").pattern("XYX").pattern(" # ")
				.unlockedBy("has_propolis", has(propolis)).save(helper);

		ShapedRecipeBuilder.shaped(ApicultureItems.FRAME_IMPREGNATED)
				.define('#', CoreItems.STICK_IMPREGNATED)
				.define('S', Tags.Items.STRING)
				.pattern("###").pattern("#S#").pattern("###")
				.unlockedBy("has_impregnated_stick", has(CoreItems.STICK_IMPREGNATED)).save(helper);
		ShapedRecipeBuilder.shaped(ApicultureItems.FRAME_UNTREATED)
				.define('#', Tags.Items.RODS_WOODEN)
				.define('S', Tags.Items.STRING)
				.pattern("###").pattern("#S#").pattern("###")
				.unlockedBy("has_impregnated_stick", has(CoreItems.STICK_IMPREGNATED)).save(helper);

		ShapedRecipeBuilder.shaped(ApicultureItems.HABITAT_LOCATOR)
				.define('X', ForestryTags.Items.INGOTS_BRONZE)
				.define('#', Tags.Items.DUSTS_REDSTONE)
				.pattern(" X ").pattern("X#X").pattern(" X ")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(helper);
		ShapedRecipeBuilder.shaped(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.PULSATING_MESH))
				.define('#', ApicultureItems.PROPOLIS.get(EnumPropolis.PULSATING))
				.pattern("# #").pattern(" # ").pattern("# #")
				.unlockedBy("has_pulsating_propolis", has(ApicultureItems.PROPOLIS.get(EnumPropolis.PULSATING))).save(helper);
		ShapedRecipeBuilder.shaped(ApicultureItems.SCOOP)
				.define('#', Tags.Items.RODS_WOODEN)
				.define('X', ItemTags.WOOL)
				.pattern("#X#").pattern("###").pattern(" # ")
				.unlockedBy("has_wool", has(ItemTags.WOOL)).save(helper);
		ShapedRecipeBuilder.shaped(Items.SLIME_BALL)
				.define('#', propolis)
				.define('X', ApicultureItems.POLLEN_CLUSTER.get(EnumPollenCluster.NORMAL))
				.pattern("#X#").pattern("#X#").pattern("#X#")
				.unlockedBy("has_propolis", has(propolis))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "slime_from_propolis"));
		ShapedRecipeBuilder.shaped(ApicultureItems.SMOKER)
				.define('#', ForestryTags.Items.INGOTS_TIN)
				.define('S', Tags.Items.RODS_WOODEN)
				.define('F', Items.FLINT_AND_STEEL)
				.define('L', Tags.Items.LEATHER)
				.pattern("LS#").pattern("LF#").pattern("###")
				.unlockedBy("has_tin", has(ForestryTags.Items.INGOTS_TIN)).save(helper);
		ShapedRecipeBuilder.shaped(Items.GLISTERING_MELON_SLICE)
				.define('#', ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY))
				.define('X', ApicultureItems.HONEYDEW)
				.define('Y', Items.MELON_SLICE)
				.pattern("#X#").pattern("#Y#").pattern("#X#")
				.unlockedBy("has_melon", has(Items.MELON_SLICE))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "glistering_melon_slice"));

		ItemLike beesWax = CoreItems.BEESWAX;
		ShapedRecipeBuilder.shaped(Items.TORCH, 3)
				.define('#', beesWax)
				.define('Y', Tags.Items.RODS_WOODEN)
				.pattern(" # ").pattern(" # ").pattern(" Y ")
				.unlockedBy("has_wax", has(beesWax))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "torch_from_wax"));
		ShapedRecipeBuilder.shaped(ApicultureItems.WAX_CAST)
				.define('#', beesWax)
				.pattern("###").pattern("# #").pattern("###")
				.unlockedBy("has_wax", has(beesWax))
				.save(helper);
	}

	private void registerCombRecipes(Consumer<FinishedRecipe> helper) {
		for (EnumHoneyComb honeyComb : EnumHoneyComb.VALUES) {
			ItemLike comb = ApicultureItems.BEE_COMBS.get(honeyComb);
			Block combBlock = ApicultureBlocks.BEE_COMB.get(honeyComb).block();
			ShapedRecipeBuilder.shaped(combBlock)
					.define('#', comb)
					.pattern("##")
					.pattern("##")
					.unlockedBy("has_comb", has(comb))
					.group("combs").save(helper);
		}
	}

	private void registerArboricultureRecipes(Consumer<FinishedRecipe> helper) {
		registerWoodRecipes(helper);

		ShapedRecipeBuilder.shaped(ArboricultureItems.GRAFTER)
				.define('B', ForestryTags.Items.INGOTS_BRONZE)
				.define('#', Tags.Items.RODS_WOODEN)
				.pattern("  B").pattern(" # ").pattern("#  ")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(helper);
		ShapedRecipeBuilder.shaped(ArboricultureBlocks.TREE_CHEST.block())
				.define('#', Tags.Items.GLASS)
				.define('X', ItemTags.SAPLINGS)
				.define('Y', Tags.Items.CHESTS_WOODEN)
				.pattern(" # ").pattern("XYX").pattern("XXX")
				.unlockedBy("has_sapling", has(ItemTags.SAPLINGS)).save(helper);
	}

	private void registerWoodRecipes(Consumer<FinishedRecipe> helper) {
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
				ShapelessRecipeBuilder.shapeless(planks, 4).requires(log).unlockedBy("has_log", has(log)).group("planks").save(helper);
				ShapedRecipeBuilder.shaped(fence, 3).define('#', Tags.Items.RODS_WOODEN).define('W', planks).pattern("W#W").pattern("W#W").unlockedBy("has_planks", has(planks)).group("wooden_fence").save(helper);
				ShapedRecipeBuilder.shaped(fencegate).define('#', Tags.Items.RODS_WOODEN).define('W', planks).pattern("#W#").pattern("#W#").unlockedBy("has_planks", has(planks)).group("wooden_fence_gate").save(helper);
				ShapedRecipeBuilder.shaped(slab, 6).define('#', planks).pattern("###").unlockedBy("has_planks", has(planks)).group("wooden_slab").save(helper);
				ShapedRecipeBuilder.shaped(stairs, 4).define('#', planks).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_planks", has(planks)).group("wooden_stairs").save(helper);
			}

			ShapedRecipeBuilder.shaped(door, 3).define('#', Ingredient.of(planks, fireproofPlanks)).pattern("##").pattern("##").pattern("##").unlockedBy("has_planks", has(planks)).group("wooden_door").save(helper);
			ShapelessRecipeBuilder.shapeless(fireproofPlanks, 4).requires(fireproofLog).unlockedBy("has_planks", has(fireproofPlanks)).group("planks").save(helper);
			ShapedRecipeBuilder.shaped(fireproofFence, 3).define('#', Tags.Items.RODS_WOODEN).define('W', fireproofPlanks).pattern("W#W").pattern("W#W").unlockedBy("has_planks", has(fireproofPlanks)).group("wooden_fence").save(helper);
			ShapedRecipeBuilder.shaped(fireproofFencegate).define('#', Tags.Items.RODS_WOODEN).define('W', fireproofPlanks).pattern("#W#").pattern("#W#").unlockedBy("has_planks", has(fireproofPlanks)).group("wooden_fence_gate").save(helper);
			ShapedRecipeBuilder.shaped(fireproofSlab, 6).define('#', fireproofPlanks).pattern("###").unlockedBy("has_planks", has(fireproofPlanks)).group("wooden_slab").save(helper);
			ShapedRecipeBuilder.shaped(fireproofStairs, 4).define('#', fireproofPlanks).pattern("#  ").pattern("## ").pattern("###").unlockedBy("has_planks", has(fireproofPlanks)).group("wooden_stairs").save(helper);

		}
	}

	private void registerFoodRecipes(Consumer<FinishedRecipe> helper) {
		ItemLike waxCapsule = FluidsItems.CONTAINERS.get(EnumContainerType.CAPSULE);
		ItemLike honeyDrop = ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY);

		ShapedRecipeBuilder.shaped(FoodItems.AMBROSIA)
				.define('#', ApicultureItems.HONEYDEW)
				.define('X', ApicultureItems.ROYAL_JELLY)
				.define('Y', waxCapsule)
				.pattern("#Y#").pattern("XXX").pattern("###")
				.unlockedBy("has royal_jelly", has(ApicultureItems.ROYAL_JELLY)).save(helper);
		ShapedRecipeBuilder.shaped(FoodItems.HONEY_POT)
				.define('#', honeyDrop)
				.define('X', waxCapsule)
				.pattern("# #").pattern(" X ").pattern("# #")
				.unlockedBy("has_drop", has(honeyDrop)).save(helper);
		ShapedRecipeBuilder.shaped(FoodItems.HONEYED_SLICE)
				.define('#', honeyDrop)
				.define('X', Items.BREAD)
				.pattern("###").pattern("#X#").pattern("###")
				.unlockedBy("has_drop", has(honeyDrop)).save(helper);
	}

	private void registerBackpackRecipes(Consumer<FinishedRecipe> helper) {

		ShapedRecipeBuilder.shaped(BackpackItems.ADVENTURER_BACKPACK)
				.define('#', ItemTags.WOOL)
				.define('V', Tags.Items.BONES)
				.define('X', Tags.Items.STRING)
				.define('Y', Tags.Items.CHESTS_WOODEN)
				.pattern("X#X").pattern("VYV").pattern("X#X")
				.unlockedBy("has_bone", has(Tags.Items.BONES)).save(helper);

		Block beeChest = ArboricultureBlocks.TREE_CHEST.block();
		ShapedRecipeBuilder.shaped(BackpackItems.APIARIST_BACKPACK)
				.define('#', ItemTags.WOOL)
				.define('V', Tags.Items.RODS_WOODEN)
				.define('X', Tags.Items.STRING)
				.define('Y', beeChest)
				.pattern("X#X").pattern("VYV").pattern("X#X")
				.unlockedBy("has_bee_chest", has(beeChest)).save(helper);
		ShapedRecipeBuilder.shaped(BackpackItems.BUILDER_BACKPACK)
				.define('#', ItemTags.WOOL)
				.define('V', Items.CLAY_BALL)
				.define('X', Tags.Items.STRING)
				.define('Y', Tags.Items.CHESTS_WOODEN)
				.pattern("X#X").pattern("VYV").pattern("X#X")
				.unlockedBy("has_clay", has(Items.CLAY_BALL)).save(helper);
		ShapedRecipeBuilder.shaped(BackpackItems.DIGGER_BACKPACK)
				.define('#', ItemTags.WOOL)
				.define('V', Tags.Items.STONE)
				.define('X', Tags.Items.STRING)
				.define('Y', Tags.Items.CHESTS_WOODEN)
				.pattern("X#X").pattern("VYV").pattern("X#X")
				.unlockedBy("has_stone", has(Tags.Items.STONE)).save(helper);
		ShapedRecipeBuilder.shaped(BackpackItems.FORESTER_BACKPACK)
				.define('#', ItemTags.WOOL)
				.define('V', ItemTags.LOGS)
				.define('X', Tags.Items.STRING)
				.define('Y', Tags.Items.CHESTS_WOODEN)
				.pattern("X#X").pattern("VYV").pattern("X#X")
				.unlockedBy("has_log", has(ItemTags.LOGS)).save(helper);
		ShapedRecipeBuilder.shaped(BackpackItems.HUNTER_BACKPACK)
				.define('#', ItemTags.WOOL)
				.define('V', Tags.Items.FEATHERS)
				.define('X', Tags.Items.STRING)
				.define('Y', Tags.Items.CHESTS_WOODEN)
				.pattern("X#X").pattern("VYV").pattern("X#X")
				.unlockedBy("has_feather", has(Tags.Items.FEATHERS)).save(helper);

		Block butterflyChest = LepidopterologyBlocks.BUTTERFLY_CHEST.block();
		ShapedRecipeBuilder.shaped(BackpackItems.LEPIDOPTERIST_BACKPACK)
				.define('#', ItemTags.WOOL)
				.define('V', butterflyChest)
				.define('X', Tags.Items.STRING)
				.define('Y', Tags.Items.CHESTS_WOODEN)
				.pattern("X#X").pattern("VYV").pattern("X#X")
				.unlockedBy("has_butterfly_chest", has(butterflyChest)).save(helper);
		ShapedRecipeBuilder.shaped(BackpackItems.MINER_BACKPACK)
				.define('#', ItemTags.WOOL)
				.define('V', Tags.Items.INGOTS_IRON)
				.define('X', Tags.Items.STRING)
				.define('Y', Tags.Items.CHESTS_WOODEN)
				.pattern("X#X").pattern("VYV").pattern("X#X")
				.unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(helper);
	}

	private void registerCharcoalRecipes(Consumer<FinishedRecipe> helper) {

		ShapedRecipeBuilder.shaped(CharcoalBlocks.CHARCOAL.block())
				.define('#', Items.CHARCOAL)
				.pattern("###").pattern("###").pattern("###")
				.unlockedBy("has_charcoal", has(Items.CHARCOAL)).save(helper);
		ShapelessRecipeBuilder.shapeless(Items.CHARCOAL, 9)
				.requires(ForestryTags.Items.CHARCOAL_BLOCK)
				.unlockedBy("has_charcoal_block", has(ForestryTags.Items.CHARCOAL_BLOCK))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "charcoal_from_block"));
		ShapedRecipeBuilder.shaped(CharcoalBlocks.WOOD_PILE.block())
				.define('L', ItemTags.LOGS)
				.pattern("LL").pattern("LL")
				.unlockedBy("has_log", has(ItemTags.LOGS)).save(helper);
		ShapelessRecipeBuilder.shapeless(CharcoalBlocks.WOOD_PILE_DECORATIVE.block())
				.requires(CharcoalBlocks.WOOD_PILE.block())
				.unlockedBy("was_wood_pile", has(CharcoalBlocks.WOOD_PILE.block())).save(helper);
		ShapelessRecipeBuilder.shapeless(CharcoalBlocks.WOOD_PILE.block())
				.requires(CharcoalBlocks.WOOD_PILE_DECORATIVE.block())
				.unlockedBy("has_decorative", has(CharcoalBlocks.WOOD_PILE_DECORATIVE.block()))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "wood_pile_from_decorative"));
	}

	private void registerClimatologyRecipes(Consumer<FinishedRecipe> helper) {

		ShapedRecipeBuilder.shaped(ClimatologyBlocks.HABITATFORMER.block())
				.define('S', CoreItems.STURDY_CASING)
				.define('G', Tags.Items.GLASS)
				.define('B', ForestryTags.Items.GEARS_BRONZE)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('C', CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.BASIC))
				.define('T', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.IRON))
				.pattern("GRG").pattern("TST").pattern("BCB")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		//TODO if carpenter recipes are in json then can just use that here
		ShapedRecipeBuilder.shaped(ClimatologyItems.HABITAT_SCREEN)
				.define('G', ForestryTags.Items.GEARS_BRONZE)
				.define('P', Tags.Items.GLASS_PANES)
				.define('I', ForestryTags.Items.INGOTS_BRONZE)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.pattern("IPI").pattern("IPI").pattern("DGD")
				.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND)).save(helper);
	}

	private void registerCoreRecipes(Consumer<FinishedRecipe> helper) {
		SimpleCookingRecipeBuilder.smelting(
						Ingredient.of(ForestryTags.Items.ORES_APATITE),
						CoreItems.APATITE,
						0.5F,
						200)
				.unlockedBy("has_apatite_ore", has(ForestryTags.Items.ORES_APATITE))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "apatite_from_smelting_apatite_ore"));
		SimpleCookingRecipeBuilder.blasting(
						Ingredient.of(ForestryTags.Items.ORES_APATITE),
						CoreItems.APATITE,
						0.5F,
						100)
				.unlockedBy("has_apatite_ore", has(ForestryTags.Items.ORES_APATITE))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "apatite_from_blasting_apatite_ore"));
		SimpleCookingRecipeBuilder.smelting(
						Ingredient.of(ForestryTags.Items.ORES_TIN),
						CoreItems.INGOT_TIN,
						0.5F,
						200)
				.unlockedBy("has_tin_ore", has(ForestryTags.Items.ORES_TIN))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "tin_ingot_from_smelting_tin_ore"));
		SimpleCookingRecipeBuilder.blasting(
						Ingredient.of(ForestryTags.Items.ORES_TIN),
						CoreItems.INGOT_TIN,
						0.5F,
						100)
				.unlockedBy("has_tin_ore", has(ForestryTags.Items.ORES_TIN))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "tin_ingot_from_blasting_tin_ore"));
		SimpleCookingRecipeBuilder.smelting(
						Ingredient.of(ForestryTags.Items.RAW_MATERIALS_TIN),
						CoreItems.INGOT_TIN,
						0.5F,
						200)
				.unlockedBy("has_raw_tin", has(ForestryTags.Items.RAW_MATERIALS_TIN))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "tin_ingot_from_smelting_raw_tin"));
		SimpleCookingRecipeBuilder.blasting(
						Ingredient.of(ForestryTags.Items.RAW_MATERIALS_TIN),
						CoreItems.INGOT_TIN,
						0.5F,
						100)
				.unlockedBy("has_raw_tin", has(ForestryTags.Items.RAW_MATERIALS_TIN))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "tin_ingot_from_blasting_raw_tin"));
		SimpleCookingRecipeBuilder.smelting(
						Ingredient.of(CoreItems.PEAT),
						CoreItems.ASH,
						0.0F,
						200)
				.unlockedBy("has_peat", has(CoreItems.PEAT))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "ash_from_peat_blasting"));
		{
			ShapelessRecipeBuilder.shapeless(CoreItems.RAW_TIN, 9)
					.requires(CoreBlocks.RAW_TIN_BLOCK)
					.unlockedBy(getHasName(CoreBlocks.RAW_TIN_BLOCK), has(CoreBlocks.RAW_TIN_BLOCK))
					.save(helper, new ResourceLocation(Constants.MOD_ID, "raw_tin"));
			ShapedRecipeBuilder.shaped(CoreBlocks.RAW_TIN_BLOCK)
					.define('#', CoreItems.RAW_TIN)
					.pattern("###")
					.pattern("###")
					.pattern("###")
					.unlockedBy(getHasName(CoreItems.RAW_TIN), has(CoreItems.RAW_TIN))
					.save(helper, new ResourceLocation(Constants.MOD_ID, "raw_tin_block"));
		}

		//don't need conditions here generally since core is always enabled
		ShapedRecipeBuilder.shaped(CoreBlocks.BASE.get(BlockTypeCoreTesr.ANALYZER).block())
				.define('T', CoreItems.PORTABLE_ALYZER)
				.define('X', ForestryTags.Items.INGOTS_BRONZE)
				.define('Y', CoreItems.STURDY_CASING)
				.pattern("XTX").pattern(" Y ").pattern("X X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		//TODO how to deal with variable output. Options: wrapper recipe, custom recipe type, leave up to data packs.
		ShapedRecipeBuilder.shaped(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.APATITE).block())
				.define('#', ForestryTags.Items.GEMS_APATITE)
				.pattern("###").pattern("###").pattern("###")
				.unlockedBy("has_apatite", has(ForestryTags.Items.GEMS_APATITE)).save(helper);
		ShapedRecipeBuilder.shaped(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.BRONZE).block())
				.define('#', ForestryTags.Items.INGOTS_BRONZE)
				.pattern("###").pattern("###").pattern("###")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(helper);
		ShapedRecipeBuilder.shaped(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.TIN).block())
				.define('#', ForestryTags.Items.INGOTS_TIN)
				.pattern("###").pattern("###").pattern("###")
				.unlockedBy("has_apatite", has(ForestryTags.Items.INGOTS_TIN)).save(helper);
		ShapedRecipeBuilder.shaped(CoreItems.BRONZE_PICKAXE)
				.define('#', ForestryTags.Items.INGOTS_BRONZE)
				.define('X', Tags.Items.RODS_WOODEN)
				.pattern("###").pattern(" X ").pattern(" X ")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(helper);
		ShapedRecipeBuilder.shaped(CoreItems.BRONZE_SHOVEL)
				.define('#', ForestryTags.Items.INGOTS_BRONZE)
				.define('X', Tags.Items.RODS_WOODEN)
				.pattern(" # ").pattern(" X ").pattern(" X ")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(helper);

		//TODO maybe get clever with a loop here
		ConditionalRecipe.builder()
				.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
				.addRecipe(
						ShapedRecipeBuilder.shaped(CoreItems.GEAR_BRONZE)
								.define('#', ForestryTags.Items.INGOTS_BRONZE)
								.define('X', ForestryTags.Items.GEARS_STONE)
								.pattern(" # ").pattern("#X#").pattern(" # ")
								.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE))::save)
				.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
				.addRecipe(ShapedRecipeBuilder.shaped(CoreItems.GEAR_BRONZE)
						.define('#', ForestryTags.Items.INGOTS_BRONZE)
						.define('X', Items.COPPER_INGOT)
						.pattern(" # ").pattern("#X#").pattern(" # ")
						.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE))::save)
				.build(helper, new ResourceLocation(Constants.MOD_ID, "gear_bronze"));
		ConditionalRecipe.builder()
				.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
				.addRecipe(
						ShapedRecipeBuilder.shaped(CoreItems.GEAR_COPPER)
								.define('#', Items.COPPER_INGOT)
								.define('X', ForestryTags.Items.GEARS_STONE)
								.pattern(" # ").pattern("#X#").pattern(" # ")
								.unlockedBy("has_copper", has(Items.COPPER_INGOT))::save)
				.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
				.addRecipe(ShapedRecipeBuilder.shaped(CoreItems.GEAR_COPPER)
						.define('#', Items.COPPER_INGOT)
						.define('X', Items.COPPER_INGOT)
						.pattern(" # ").pattern("#X#").pattern(" # ")
						.unlockedBy("has_copper", has(Items.COPPER_INGOT))::save)
				.build(helper, new ResourceLocation(Constants.MOD_ID, "gear_copper"));
		ConditionalRecipe.builder()
				.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
				.addRecipe(
						ShapedRecipeBuilder.shaped(CoreItems.GEAR_TIN)
								.define('#', ForestryTags.Items.INGOTS_TIN)
								.define('X', ForestryTags.Items.GEARS_STONE)
								.pattern(" # ").pattern("#X#").pattern(" # ")
								.unlockedBy("has_tin", has(ForestryTags.Items.INGOTS_TIN))::save)
				.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
				.addRecipe(ShapedRecipeBuilder.shaped(CoreItems.GEAR_TIN)
						.define('#', ForestryTags.Items.INGOTS_TIN)
						.define('X', Items.COPPER_INGOT)
						.pattern(" # ").pattern("#X#").pattern(" # ")
						.unlockedBy("has_tin", has(ForestryTags.Items.INGOTS_TIN))::save)
				.build(helper, new ResourceLocation(Constants.MOD_ID, "gear_tin"));

		ShapelessRecipeBuilder.shapeless(CoreItems.INGOT_BRONZE)
				.requires(ForestryTags.Items.INGOTS_TIN)
				.requires(Items.COPPER_INGOT)
				.requires(Items.COPPER_INGOT)
				.requires(Items.COPPER_INGOT)
				.unlockedBy("has_tin", has(ForestryTags.Items.INGOTS_TIN))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "ingot_bronze_alloying"));

		ShapelessRecipeBuilder.shapeless(CoreItems.APATITE, 9)
				.requires(ForestryTags.Items.STORAGE_BLOCKS_APATITE)
				.unlockedBy("has_block", has(ForestryTags.Items.STORAGE_BLOCKS_APATITE)).save(helper);
		ShapelessRecipeBuilder.shapeless(CoreItems.INGOT_BRONZE, 9)
				.requires(ForestryTags.Items.STORAGE_BLOCKS_BRONZE)
				.unlockedBy("has_block", has(ForestryTags.Items.STORAGE_BLOCKS_BRONZE)).save(helper);
		ShapelessRecipeBuilder.shapeless(CoreItems.INGOT_TIN, 9)
				.requires(ForestryTags.Items.STORAGE_BLOCKS_TIN)
				.unlockedBy("has_block", has(ForestryTags.Items.STORAGE_BLOCKS_TIN)).save(helper);
		ShapelessRecipeBuilder.shapeless(CoreItems.KIT_PICKAXE)
				.requires(CoreItems.BRONZE_PICKAXE)
				.requires(CoreItems.CARTON)
				.unlockedBy("has_pickaxe", has(CoreItems.BRONZE_PICKAXE)).save(helper);
		ShapelessRecipeBuilder.shapeless(CoreItems.KIT_SHOVEL)
				.requires(CoreItems.BRONZE_SHOVEL)
				.requires(CoreItems.CARTON)
				.unlockedBy("has_shovel", has(CoreItems.BRONZE_SHOVEL)).save(helper);
		ShapedRecipeBuilder.shaped(CoreItems.SPECTACLES)
				.define('X', ForestryTags.Items.INGOTS_BRONZE)
				.define('Y', Tags.Items.GLASS_PANES)
				.pattern(" X ").pattern("Y Y")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(helper);
		ShapedRecipeBuilder.shaped(CoreItems.PIPETTE)
				.define('#', ItemTags.WOOL)
				.define('X', Tags.Items.GLASS_PANES)
				.pattern("  #").pattern(" X ").pattern("X  ")
				.unlockedBy("has_wool", has(ItemTags.WOOL)).save(helper);
		ShapedRecipeBuilder.shaped(CoreItems.PORTABLE_ALYZER)
				.define('#', Tags.Items.GLASS_PANES)
				.define('X', ForestryTags.Items.INGOTS_TIN)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.pattern("X#X").pattern("X#X").pattern("RDR")
				.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND)).save(helper);
		ShapedRecipeBuilder.shaped(Items.STRING)
				.define('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP))
				.pattern(" # ").pattern(" # ").pattern(" # ")
				.unlockedBy("has_wisp", has(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP)))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "string_from_wisp"));
		ShapedRecipeBuilder.shaped(CoreItems.STURDY_CASING)
				.define('#', ForestryTags.Items.INGOTS_BRONZE)
				.pattern("###").pattern("# #").pattern("###")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(helper);
		ShapedRecipeBuilder.shaped(Items.COBWEB, 4)
				.define('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP))
				.pattern("# #").pattern(" # ").pattern("# #")
				.unlockedBy("has_wisp", has(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP)))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "cobweb_from_wisp"));
		ShapedRecipeBuilder.shaped(CoreItems.WRENCH)
				.define('#', ForestryTags.Items.INGOTS_BRONZE)
				.pattern("# #").pattern(" # ").pattern(" # ")
				.unlockedBy("has_bronze", has(ForestryTags.Items.INGOTS_BRONZE)).save(helper);

		// Manure and Fertilizer
		ShapedRecipeBuilder.shaped(CoreItems.COMPOST, 4)
				.define('#', Blocks.DIRT)
				.define('X', Tags.Items.CROPS_WHEAT)
				.pattern(" X ").pattern("X#X").pattern(" X ")
				.unlockedBy("has_wheat", has(Tags.Items.CROPS_WHEAT))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "compost_wheat"));

		ShapedRecipeBuilder.shaped(CoreItems.COMPOST, 1)
				.define('#', Blocks.DIRT)
				.define('X', ForestryTags.Items.DUSTS_ASH)
				.pattern(" X ").pattern("X#X").pattern(" X ")
				.unlockedBy("has_ash", has(ForestryTags.Items.DUSTS_ASH))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "compost_ash"));

		ShapedRecipeBuilder.shaped(CoreItems.FERTILIZER_COMPOUND, 8)
				.define('#', ItemTags.SAND)
				.define('X', ForestryTags.Items.GEMS_APATITE)
				.pattern(" # ").pattern(" X ").pattern(" # ")
				.unlockedBy("has_apatite", has(ForestryTags.Items.GEMS_APATITE))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "fertilizer_apatite"));

		ShapedRecipeBuilder.shaped(CoreItems.FERTILIZER_COMPOUND, 16)
				.define('#', ForestryTags.Items.DUSTS_ASH)
				.define('X', ForestryTags.Items.GEMS_APATITE)
				.pattern("###").pattern("#X#").pattern("###")
				.unlockedBy("has_apatite", has(ForestryTags.Items.GEMS_APATITE))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "fertilizer_ash"));

		// Humus
		ShapedRecipeBuilder.shaped(CoreBlocks.HUMUS, 8)
				.define('#', Blocks.DIRT)
				.define('X', CoreItems.COMPOST)
				.pattern("###").pattern("#X#").pattern("###")
				.unlockedBy("has_compost", has(CoreItems.COMPOST))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "humus_compost"));

		ShapedRecipeBuilder.shaped(CoreBlocks.HUMUS, 8)
				.define('#', Blocks.DIRT)
				.define('X', CoreItems.FERTILIZER_COMPOUND)
				.pattern("###").pattern("#X#").pattern("###")
				.unlockedBy("has_fertilizer", has(CoreItems.FERTILIZER_COMPOUND))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "humus_fertilizer"));

		TriConsumer<Integer, ItemStack, String> bogRecipe = (amount, container, name) -> ShapedRecipeBuilder.shaped(CoreBlocks.BOG_EARTH, amount)
				.define('#', Blocks.DIRT)
				.define('X', StrictNBTIngredient.of(container))
				.define('Y', ItemTags.SAND)
				.pattern("#Y#").pattern("YXY").pattern("#Y#")
				.unlockedBy("has_sand", has(ItemTags.SAND))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "bog_earth_" + name));

		// Bog earth
		bogRecipe.accept(6, new ItemStack(Items.WATER_BUCKET), "bucket");

		ItemStack canWater = FluidsItems.getContainer(EnumContainerType.CAN, Fluids.WATER);
		ItemStack waxCapsuleWater = FluidsItems.getContainer(EnumContainerType.CAPSULE, Fluids.WATER);
		ItemStack refractoryWater = FluidsItems.getContainer(EnumContainerType.REFRACTORY, Fluids.WATER);
		bogRecipe.accept(8, canWater, "can");
		bogRecipe.accept(8, waxCapsuleWater, "wax_capsule");
		bogRecipe.accept(8, refractoryWater, "refractory");

		// Cans and capsules
		ShapedRecipeBuilder.shaped(FluidsItems.CONTAINERS.get(EnumContainerType.CAN), 12)
				.define('#', ForestryTags.Items.INGOTS_TIN)
				.pattern(" # ")
				.pattern("# #")
				.unlockedBy("has_tin", has(ForestryTags.Items.INGOTS_TIN))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "can"));
		ShapedRecipeBuilder.shaped(FluidsItems.CONTAINERS.get(EnumContainerType.CAPSULE), 4)
				.define('#', CoreItems.BEESWAX)
				.pattern(" # ")
				.pattern("# #")
				.unlockedBy("has_beeswax", has(CoreItems.BEESWAX))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "capsule"));
		ShapedRecipeBuilder.shaped(FluidsItems.CONTAINERS.get(EnumContainerType.REFRACTORY), 4)
				.define('#', CoreItems.REFRACTORY_WAX)
				.pattern(" # ")
				.pattern("# #")
				.unlockedBy("has_refractory_wax", has(CoreItems.REFRACTORY_WAX))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "refractory_capsule"));
	}

	private void registerBookRecipes(Consumer<FinishedRecipe> helper) {
		ShapelessRecipeBuilder.shapeless(CoreItems.FORESTERS_MANUAL)
				.requires(Items.BOOK)
				.requires(ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY))
				.unlockedBy("has_book", has(Items.BOOK))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "foresters_manual_honeydrop"));
		ShapelessRecipeBuilder.shapeless(CoreItems.FORESTERS_MANUAL)
				.requires(Items.BOOK)
				.requires(ItemTags.SAPLINGS)
				.unlockedBy("has_book", has(Items.BOOK))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "foresters_manual_sapling"));
		ShapelessRecipeBuilder.shapeless(CoreItems.FORESTERS_MANUAL)
				.requires(Items.BOOK)
				.requires(LepidopterologyItems.BUTTERFLY_GE)
				.unlockedBy("has_book", has(Items.BOOK))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "foresters_manual_butterfly"));
	}

	private EnumElectronTube getElectronTube(BlockTypePlanter planter) {
		return switch (planter) {
			case ARBORETUM -> EnumElectronTube.GOLD;
			case FARM_CROPS -> EnumElectronTube.BRONZE;
			case PEAT_POG -> EnumElectronTube.OBSIDIAN;
			case FARM_MUSHROOM -> EnumElectronTube.APATITE;
			case FARM_GOURD -> EnumElectronTube.LAPIS;
			case FARM_NETHER -> EnumElectronTube.BLAZE;
			case FARM_ENDER -> EnumElectronTube.ENDER;
		};
	}

	private void registerCultivationRecipes(Consumer<FinishedRecipe> helper) {
		for (BlockTypePlanter planter : BlockTypePlanter.values()) {
			Block managed = CultivationBlocks.PLANTER.get(planter, BlockPlanter.Mode.MANAGED).block();
			Block manual = CultivationBlocks.PLANTER.get(planter, BlockPlanter.Mode.MANUAL).block();

			ShapedRecipeBuilder.shaped(managed)
					.define('G', Tags.Items.GLASS)
					.define('T', CoreItems.ELECTRON_TUBES.get(getElectronTube(planter)))
					.define('C', CoreItems.FLEXIBLE_CASING)
					.define('B', CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.BASIC))
					.pattern("GTG").pattern("TCT").pattern("GBG")
					.unlockedBy("has_casing", has(CoreItems.FLEXIBLE_CASING)).save(helper);

			ShapelessRecipeBuilder.shapeless(manual)
					.requires(managed)
					.unlockedBy("has_managed", has(managed))
					.save(helper);
			ShapelessRecipeBuilder.shapeless(managed)
					.requires(manual)
					.unlockedBy("has_manual", has(manual))
					.save(helper, new ResourceLocation(Constants.MOD_ID, RegistryNameFinder.getRegistryName(managed).getPath() + "_from_manual"));
		}
	}

	private void registerDatabaseRecipes(Consumer<FinishedRecipe> helper) {
		//TODO create FallbackIngredient implementation
		List<FeatureBlock<?, ?>> features = Lists.newArrayList(ApicultureBlocks.BEE_CHEST, ArboricultureBlocks.TREE_CHEST, LepidopterologyBlocks.BUTTERFLY_CHEST);
		List<Ingredient> possibleSpecials = Lists.newArrayList(Ingredient.of(ApicultureItems.ROYAL_JELLY.getItem()), Ingredient.of(CoreItems.FRUITS.get(ItemFruit.EnumFruit.PLUM).getItem()), Ingredient.of(Tags.Items.CHESTS_WOODEN));
		Ingredient possibleSpecial = Ingredient.merge(possibleSpecials);
		for (FeatureBlock<?, ?> featureBlock1 : features) {
			for (FeatureBlock<?, ?> featureBlock2 : features) {
				if (featureBlock1.equals(featureBlock2)) {
					continue;
				}

				ShapedRecipeBuilder.shaped(DatabaseBlocks.DATABASE.block())
						.define('#', CoreItems.PORTABLE_ALYZER)
						.define('C', possibleSpecial)
						.define('S', featureBlock1.block())
						.define('F', featureBlock2.block())
						.define('W', ItemTags.PLANKS)
						.define('I', ForestryTags.Items.INGOTS_BRONZE)
						.define('Y', CoreItems.STURDY_CASING)
						.pattern("I#I").pattern("FYS").pattern("WCW")
						.unlockedBy("has_casing", has(CoreItems.STURDY_CASING))
						.save(helper, new ResourceLocation(Constants.MOD_ID, "database_" + featureBlock1.getIdentifier() + "_" + featureBlock2.getIdentifier()));
			}
		}
	}

	private void registerFactoryRecipes(Consumer<FinishedRecipe> helper) {
		ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.BOTTLER).block())
				.define('#', Tags.Items.GLASS)
				.define('X', FluidsItems.CONTAINERS.get(EnumContainerType.CAN))
				.define('Y', CoreItems.STURDY_CASING)
				.pattern("X#X").pattern("#Y#").pattern("X#X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CARPENTER).block())
				.define('#', Tags.Items.GLASS)
				.define('X', ForestryTags.Items.INGOTS_BRONZE)
				.define('Y', CoreItems.STURDY_CASING)
				.pattern("X#X").pattern("XYX").pattern("X#X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CENTRIFUGE).block())
				.define('#', Tags.Items.GLASS)
				.define('X', Items.COPPER_INGOT)
				.define('Y', CoreItems.STURDY_CASING)
				.pattern("X#X").pattern("XYX").pattern("X#X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		ShapedRecipeBuilder.shaped(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR).block())
				.define('#', Tags.Items.GLASS)
				.define('X', Tags.Items.INGOTS_GOLD)
				.define('Y', CoreItems.STURDY_CASING)
				.define('Z', Tags.Items.CHESTS_WOODEN)
				.pattern("X#X").pattern("#Y#").pattern("XZX")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.FERMENTER).block())
				.define('#', Tags.Items.GLASS)
				.define('X', ForestryTags.Items.GEARS_BRONZE)
				.define('Y', CoreItems.STURDY_CASING)
				.pattern("X#X").pattern("#Y#").pattern("X#X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.MOISTENER).block())
				.define('#', Tags.Items.GLASS)
				.define('X', ForestryTags.Items.GEARS_COPPER)
				.define('Y', CoreItems.STURDY_CASING)
				.pattern("X#X").pattern("#Y#").pattern("X#X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.RAINMAKER).block())
				.define('#', Tags.Items.GLASS)
				.define('X', ForestryTags.Items.GEARS_TIN)
				.define('Y', CoreItems.STURDY_CASING)
				.pattern("X#X").pattern("#Y#").pattern("X#X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		ShapedRecipeBuilder.shaped(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).block())
				.define('#', Tags.Items.GLASS)
				.define('X', Tags.Items.INGOTS_IRON)
				.define('Y', CoreItems.STURDY_CASING)
				.pattern("X#X").pattern("XYX").pattern("X#X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.SQUEEZER).block())
				.define('#', Tags.Items.GLASS)
				.define('X', ForestryTags.Items.INGOTS_TIN)
				.define('Y', CoreItems.STURDY_CASING)
				.pattern("X#X").pattern("XYX").pattern("X#X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
		ShapedRecipeBuilder.shaped(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.STILL).block())
				.define('#', Tags.Items.GLASS)
				.define('X', Tags.Items.DUSTS_REDSTONE)
				.define('Y', CoreItems.STURDY_CASING)
				.pattern("X#X").pattern("#Y#").pattern("X#X")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);
	}

	private void registerFarmingRecipes(Consumer<FinishedRecipe> helper) {
		for (EnumFarmMaterial material : EnumFarmMaterial.values()) {
			Item base = material.getBase().getItem();
			ShapedRecipeBuilder.shaped(FarmingBlocks.FARM.get(EnumFarmBlockType.PLAIN, material).block())
					.define('#', base)
					.define('C', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.TIN))
					.define('W', ItemTags.WOODEN_SLABS)
					.define('I', Items.COPPER_INGOT)
					.pattern("I#I").pattern("WCW")
					.unlockedBy("has_copper", has(Items.COPPER_INGOT)).save(helper);
			ShapedRecipeBuilder.shaped(FarmingBlocks.FARM.get(EnumFarmBlockType.GEARBOX, material).block())
					.define('#', base)
					.define('T', ForestryTags.Items.GEARS_TIN)
					.pattern(" # ").pattern("TTT")
					.unlockedBy("has_tin_gear", has(ForestryTags.Items.GEARS_TIN)).save(helper);
			ShapedRecipeBuilder.shaped(FarmingBlocks.FARM.get(EnumFarmBlockType.HATCH, material).block())
					.define('#', base)
					.define('T', ForestryTags.Items.GEARS_TIN)
					.define('D', ItemTags.WOODEN_TRAPDOORS)
					.pattern(" # ").pattern("TDT")
					.unlockedBy("has_tin_gear", has(ForestryTags.Items.GEARS_TIN)).save(helper);
			ShapedRecipeBuilder.shaped(FarmingBlocks.FARM.get(EnumFarmBlockType.VALVE, material).block())
					.define('#', base)
					.define('T', ForestryTags.Items.GEARS_TIN)
					.define('X', Tags.Items.GLASS)
					.pattern(" # ").pattern("XTX")
					.unlockedBy("has_tin_gear", has(ForestryTags.Items.GEARS_TIN)).save(helper);
			ShapedRecipeBuilder.shaped(FarmingBlocks.FARM.get(EnumFarmBlockType.CONTROL, material).block())
					.define('#', base)
					.define('T', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.GOLD))
					.define('X', Tags.Items.DUSTS_REDSTONE)
					.pattern(" # ").pattern("XTX")
					.unlockedBy("has_tin_gear", has(ForestryTags.Items.GEARS_TIN)).save(helper);
		}
	}

	private void registerFluidsRecipes(Consumer<FinishedRecipe> helper) {
		ForestryFluids milk = ForestryFluids.MILK;
		for (EnumContainerType containerType : EnumContainerType.values()) {
			/*if (containerType == EnumContainerType.JAR || containerType == EnumContainerType.GLASS) {
				continue;
			}*/
			ItemStack filled = FluidsItems.getContainer(containerType, milk);
			ShapedRecipeBuilder.shaped(Items.CAKE)
					.define('A', StrictNBTIngredient.of(filled))
					.define('B', Items.SUGAR)
					.define('C', Items.WHEAT)
					.define('E', Items.EGG)
					.pattern("AAA").pattern("BEB").pattern("CCC")
					.unlockedBy("has_wheat", has(Items.WHEAT))
					.save(helper, new ResourceLocation(Constants.MOD_ID, "cake_" + containerType.getSerializedName()));
		}
	}

	private void registerLepidopterologyRecipes(Consumer<FinishedRecipe> helper) {
		//TODO tag?
		//TODO tag?
		ShapedRecipeBuilder.shaped(LepidopterologyBlocks.BUTTERFLY_CHEST.block())
				.define('#', Tags.Items.GLASS)
				.define('X', LepidopterologyItems.BUTTERFLY_GE)    //TODO tag?
				.define('Y', Tags.Items.CHESTS_WOODEN)
				.pattern(" # ").pattern("XYX").pattern("XXX")
				.unlockedBy("has_butterfly", has(LepidopterologyItems.BUTTERFLY_GE)).save(helper);
	}

	private void registerMailRecipes(Consumer<FinishedRecipe> helper) {
		ShapelessRecipeBuilder.shapeless(MailItems.CATALOGUE)
				.requires(Items.BOOK)
				.requires(ForestryTags.Items.STAMPS)
				.unlockedBy("has_book", has(Items.BOOK)).save(helper);

		//TODO fallback ingredient
		ShapelessRecipeBuilder.shapeless(MailItems.LETTERS.get(ItemLetter.Size.EMPTY, ItemLetter.State.FRESH))
				.requires(Items.PAPER)
				.requires(Ingredient.merge(Lists.newArrayList(Ingredient.of(ForestryTags.Items.PROPOLIS), Ingredient.of(Tags.Items.SLIMEBALLS))))
				.unlockedBy("has_paper", has(Items.PAPER)).save(helper);
		ShapedRecipeBuilder.shaped(MailBlocks.BASE.get(BlockTypeMail.MAILBOX).block())
				.define('#', ForestryTags.Items.INGOTS_TIN)
				.define('X', Tags.Items.CHESTS_WOODEN)
				.define('Y', CoreItems.STURDY_CASING)
				.pattern(" # ").pattern("#Y#").pattern("XXX")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING))
				.save(helper);
		Item[] emptiedLetter = MailItems.LETTERS.getRowFeatures(ItemLetter.Size.EMPTY).stream().map(FeatureItem::getItem).toArray(Item[]::new);
		ShapedRecipeBuilder.shaped(Items.PAPER)
				.define('#', Ingredient.of(emptiedLetter))
				.pattern(" # ").pattern(" # ").pattern(" # ")
				.unlockedBy("has_paper", has(Items.PAPER))
				.save(helper, new ResourceLocation(Constants.MOD_ID, "paper_from_letters"));
		ShapedRecipeBuilder.shaped(MailBlocks.BASE.get(BlockTypeMail.TRADE_STATION).block())
				.define('#', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.BRONZE))
				.define('X', Tags.Items.CHESTS_WOODEN)
				.define('Y', CoreItems.STURDY_CASING)
				.define('Z', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.IRON))
				.define('W', StrictNBTIngredient.of(ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[]{})))
				.pattern("Z#Z").pattern("#Y#").pattern("XWX")
				.unlockedBy("has_casing", has(CoreItems.STURDY_CASING)).save(helper);

		//TODO fallback
		Ingredient glue = Ingredient.merge(Lists.newArrayList(Ingredient.of(ForestryTags.Items.DROP_HONEY), Ingredient.of(Items.SLIME_BALL)));
		for (EnumStampDefinition stampDefinition : EnumStampDefinition.VALUES) {
			ShapedRecipeBuilder.shaped(MailItems.STAMPS.get(stampDefinition), 9)
					.define('X', stampDefinition.getCraftingIngredient())
					.define('#', Items.PAPER)
					.define('Z', glue)
					.pattern("XXX").pattern("###").pattern("ZZZ")
					.unlockedBy("has_paper", has(Items.PAPER)).save(helper);
		}
	}

	private void registerSortingRecipes(Consumer<FinishedRecipe> helper) {
		Ingredient ing = Ingredient.merge(Lists.newArrayList(Ingredient.of(LepidopterologyItems.CATERPILLAR_GE, ApicultureItems.PROPOLIS.get(EnumPropolis.NORMAL)), Ingredient.of(ForestryTags.Items.FRUITS)));

		ShapedRecipeBuilder.shaped(SortingBlocks.FILTER.block(), 2)
				.define('B', ForestryTags.Items.GEARS_BRONZE)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('F', ing)
				.define('W', ItemTags.PLANKS)
				.define('G', Tags.Items.GLASS)
				.pattern("WDW").pattern("FGF").pattern("BDB")
				.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND)).save(helper);
	}

	@Override
	protected void saveAdvancement(CachedOutput cache, JsonObject advancementJson, Path pathIn) {
		//NOOP - We dont replace any of the advancement things yet...
	}

	@Override
	public String getName() {
		return "Forestry Recipes";
	}
}
