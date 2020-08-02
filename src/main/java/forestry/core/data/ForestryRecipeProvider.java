package forestry.core.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.NBTIngredient;
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
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.features.FluidsItems;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.EnumContainerType;
import forestry.core.items.EnumCraftingMaterial;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemFruit;
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

	/*@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {

		CookingRecipeBuilder.smeltingRecipe(
			Ingredient.fromItems(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE)),
			CoreItems.APATITE,
			0.5F,
			200)
			.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(
			Ingredient.fromItems(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN)),
			CoreItems.INGOTS_TIN,
			0.5F,
			200)
			.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(
			Ingredient.fromItems(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER)),
			CoreItems.INGOTS_COPPER,
			0.5F,
			200)
			.build(consumer);
		CookingRecipeBuilder.smeltingRecipe(
			Ingredient.fromItems(CoreItems.PEAT),
			CoreItems.ASH.item(),
			0.0F,
			200)
			.build(consumer);
		// / SMELTING RECIPES

		// / BRONZE INGOTS
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.INGOTS_BRONZE, 4)
			.build(consumer);


		// / STURDY MACHINE
		ShapedRecipeBuilder.shapedRecipe(CoreItems.STURDY_CASING)
			.patternLine("###")
			.patternLine("###")
			.patternLine("###")
			.key('#', ForestryTags.Items.INGOTS_BRONZE)
			.setGroup("core")
			.build(consumer);

		// / CONTAINERS
		ShapedRecipeBuilder.shapedRecipe(FluidsItems.CONTAINERS.get(EnumContainerType.CAN), 12)
			.patternLine(" # ")
			.patternLine("# #")
			.key('#', ForestryTags.Items.INGOTS_TIN)
			.build(consumer);

		// / CAPSULES
		ShapedRecipeBuilder.shapedRecipe(FluidsItems.CONTAINERS.get(EnumContainerType.CAPSULE), 4)
			.patternLine("###")
			.key('#', CoreItems.BEESWAX)
			.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(FluidsItems.CONTAINERS.get(EnumContainerType.CAPSULE), 4)
			.patternLine("###")
			.key('#', CoreItems.REFRACTORY_WAX)
			.build(consumer);

		// / GEARS
		Ingredient gearCenter = FallbackIngredient.fromTag(ForestryTags.Items.GEARS_STONE, ForestryTags.Items.INGOTS_COPPER);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.GEARS_BRONZE)
			.patternLine(" # ")
			.patternLine("#X#")
			.patternLine(" # ")
			.key('#', ForestryTags.Items.INGOTS_BRONZE)
			.key('X', gearCenter)
			.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.GEARS_COPPER)
			.patternLine(" # ")
			.patternLine("#X#")
			.patternLine(" # ")
			.key('#', ForestryTags.Items.INGOTS_COPPER)
			.key('X', gearCenter)
			.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.GEARS_TIN)
			.patternLine(" # ")
			.patternLine("#X#")
			.patternLine(" # ")
			.key('#', ForestryTags.Items.INGOTS_TIN)
			.key('X', gearCenter)
			.build(consumer);

		// / SURVIVALIST TOOLS
		ShapedRecipeBuilder.shapedRecipe(CoreItems.BRONZE_PICKAXE)
			.patternLine(" X ")
			.patternLine(" X ")
			.patternLine("###")
			.key('#', ForestryTags.Items.INGOTS_BRONZE)
			.key('X', Tags.Items.RODS_WOODEN)
			.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.BRONZE_PICKAXE)
			.patternLine(" X ")
			.patternLine(" X ")
			.patternLine(" # ")
			.key('#', ForestryTags.Items.INGOTS_BRONZE)
			.key('X', Tags.Items.RODS_WOODEN)
			.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.KIT_PICKAXE)
			.addIngredient(CoreItems.CARTON)
			.addIngredient(CoreItems.BRONZE_PICKAXE)
			.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.KIT_SHOVEL)
			.addIngredient(CoreItems.CARTON)
			.addIngredient(CoreItems.BRONZE_SHOVEL)
			.build(consumer);

		// / NATURALIST'S ARMOR
		ShapedRecipeBuilder.shapedRecipe(CoreItems.SPECTACLES)
			.patternLine(" X ")
			.patternLine("Y Y")
			.key('X', ForestryTags.Items.INGOTS_BRONZE)
			.key('Y', Tags.Items.GLASS_PANES)
			.build(consumer);

		// / WRENCH
		ShapedRecipeBuilder.shapedRecipe(CoreItems.WRENCH)
			.patternLine("# #")
			.patternLine(" # ")
			.patternLine(" # ")
			.key('#', ForestryTags.Items.INGOTS_BRONZE)
			.build(consumer);

		// / WEB
		ShapedRecipeBuilder.shapedRecipe(Blocks.COBWEB, 4)
			.patternLine("# #")
			.patternLine(" # ")
			.patternLine("# #")
			.key('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP))
			.build(consumer);

		if (!ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
			// Portable ANALYZER
			RecipeUtil.addRecipe("portable_alyzer", items.portableAlyzer.getItemStack(),
				"X#X",
				"X#X",
				"RDR",
				'#', OreDictUtil.PANE_GLASS,
				'X', OreDictUtil.INGOTS_TIN,
				'R', OreDictUtil.DUST_REDSTONE,
				'D', OreDictUtil.GEM_DIAMOND);
			if (Fluids.BIOMASS.getFluid() != null) {
				RecipeUtil.addShapelessRecipe("camouflaged_paneling", ModuleFluids.getItems().getContainer(EnumContainerType.CAPSULE, Fluids.BIOMASS),
					items.craftingMaterial.getCamouflagedPaneling(1));
			}
		}

		// ANALYZER
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.BASE.get(BlockTypeCoreTesr.ANALYZER))
			.patternLine("XTX")
			.patternLine(" Y ")
			.patternLine("X X")
			.key('Y', CoreItems.STURDY_CASING)
			.key('T', CoreItems.PORTABLE_ALYZER)
			.key('X', ForestryTags.Items.INGOTS_BRONZE)
			.build(consumer);

		// Manure and Fertilizer
		int compostWheatAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.wheat");
		if (compostWheatAmount > 0) {
			ItemStack compost = items.compost.getItemStack(compostWheatAmount);
			RecipeUtil.addRecipe("wheat_to_compost", compost, " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', OreDictUtil.CROP_WHEAT);
		}

		int compostAshAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.ash");
		if (compostAshAmount > 0) {
			ItemStack compost = items.compost.getItemStack(compostAshAmount);
			RecipeUtil.addRecipe("ash_to_compost", compost, " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', OreDictUtil.DUST_ASH);
		}

		int fertilizerApatiteAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.fertilizer.apatite");
		if (fertilizerApatiteAmount > 0) {
			ItemStack fertilizer = items.fertilizerCompound.getItemStack(fertilizerApatiteAmount);
			RecipeUtil.addRecipe("sand_to_fertilizer", fertilizer, " # ", " X ", " # ", '#', OreDictUtil.SAND, 'X', OreDictUtil.GEM_APATITE);
		}

		int fertilizerAshAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.fertilizer.ash");
		if (fertilizerAshAmount > 0) {
			ItemStack fertilizer = items.fertilizerCompound.getItemStack(fertilizerAshAmount);
			RecipeUtil.addRecipe("ash_to_fertilizer", fertilizer, "###", "#X#", "###", '#', OreDictUtil.DUST_ASH, 'X', OreDictUtil.GEM_APATITE);
		}

		// Humus
		int humusCompostAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.humus.compost");
		if (humusCompostAmount > 0) {
			ItemStack humus = new ItemStack(blocks.humus, humusCompostAmount);
			RecipeUtil.addRecipe("compost_humus", humus, "###", "#X#", "###", '#', Blocks.DIRT, 'X', items.compost);
		}

		int humusFertilizerAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.humus.fertilizer");
		if (humusFertilizerAmount > 0) {
			ItemStack humus = new ItemStack(blocks.humus, humusFertilizerAmount);
			RecipeUtil.addRecipe("fertilizer_humus", humus, "###", "#X#", "###", '#', Blocks.DIRT, 'X', items.fertilizerCompound);
		}

		// Bog earth
		int bogEarthOutputBucket = ForestryAPI.activeMode.getIntegerSetting("recipe.output.bogearth.bucket");
		if (bogEarthOutputBucket > 0) {
			ItemStack bogEarth = blocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, bogEarthOutputBucket);
			RecipeUtil.addRecipe("bucket_bog_earth", bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', Items.WATER_BUCKET, 'Y', OreDictUtil.SAND);
		}

		int bogEarthOutputCan = ForestryAPI.activeMode.getIntegerSetting("recipe.output.bogearth.can");
		if (bogEarthOutputCan > 0) {
			ItemStack bogEarth = blocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, bogEarthOutputCan);
			ItemStack canWater = fluidItems.getContainer(EnumContainerType.CAN, FluidRegistry.WATER);
			ItemStack waxCapsuleWater = fluidItems.getContainer(EnumContainerType.CAPSULE, FluidRegistry.WATER);
			ItemStack refractoryWater = fluidItems.getContainer(EnumContainerType.REFRACTORY, FluidRegistry.WATER);
			RecipeUtil.addRecipe("can_bog_earth", bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', canWater, 'Y', OreDictUtil.SAND);
			RecipeUtil.addRecipe("capsule_bog_earth", bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', waxCapsuleWater, 'Y', OreDictUtil.SAND);
			RecipeUtil.addRecipe("refractory_capsule_bog_earth", bogEarth, "#Y#", "YXY", "#Y#", '#', Blocks.DIRT, 'X', refractoryWater, 'Y', OreDictUtil.SAND);
		}

		// Crafting Material
		RecipeUtil.addRecipe("silk_to_string", new ItemStack(Items.STRING), "#", "#", "#", '#', items.craftingMaterial.getSilkWisp());

		// / Pipette
		RecipeUtil.addRecipe("pipette", items.pipette, "  #", " X ", "X  ", 'X', OreDictUtil.PANE_GLASS, '#', new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));

		// Storage Blocks
		{
			RecipeUtil.addRecipe("apatite_block", blocks.resourceStorageApatite, "###", "###", "###", '#', OreDictUtil.GEM_APATITE);

			RecipeUtil.addShapelessRecipe("block_to_apatite", new ItemStack(items.apatite, 9), OreDictUtil.BLOCK_APATITE);
		}

		{
			RecipeUtil.addRecipe("copper_block", blocks.resourceStorageCopper, "###", "###", "###", '#', OreDictUtil.INGOTS_COPPER);

			ItemStack ingotCopper = items.ingotCopper.copy();
			ingotCopper.setCount(9);
			RecipeUtil.addShapelessRecipe("block_to_copper", ingotCopper, OreDictUtil.BLOCK_COPPER);
		}

		{
			RecipeUtil.addRecipe("tin_block", blocks.resourceStorageTin, "###", "###", "###", '#', OreDictUtil.INGOTS_TIN);

			ItemStack ingotTin = items.ingotTin.copy();
			ingotTin.setCount(9);
			RecipeUtil.addShapelessRecipe("block_to_tin", ingotTin, OreDictUtil.BLOCK_TIN);
		}

		{
			RecipeUtil.addRecipe("bronze_block", blocks.resourceStorageBronze, "###", "###", "###", '#', OreDictUtil.INGOTS_BRONZE);

			ItemStack ingotBronze = items.ingotBronze.copy();
			ingotBronze.setCount(9);
			RecipeUtil.addShapelessRecipe("block_to_bronze", ingotBronze, OreDictUtil.BLOCK_BRONZE);
		}
		if (!ModuleHelper.isEnabled(ForestryModuleUids.CHARCOAL)) {
			RecipeUtil.addSmelting(new ItemStack(items.ash, 2), new ItemStack(Items.COAL, 1, 1), 0.15F);
		}
	}*/

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
			ShapedRecipeBuilder.shapedRecipe(plain)
				.key('X', CoreItems.IMPREGNATED_CASING.item())
				.key('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SCENTED_PANELING).item())
				.patternLine("###").patternLine("#X#").patternLine("###")
				.addCriterion("has_casing", this.hasItem(CoreItems.IMPREGNATED_CASING.item()))
				.setGroup("alveary")::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.FAN).block())
				.key('#', goldElectronTube)
				.key('X', plain)
				.key('I', Tags.Items.INGOTS_IRON)
				.patternLine("I I").patternLine(" X ").patternLine("I#I")
				.addCriterion("has_plain", this.hasItem(plain))
				.setGroup("alveary")::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.HEATER).block())
				.key('#', goldElectronTube)
				.key('I', Tags.Items.INGOTS_IRON)
				.key('X', plain)
				.key('S', Tags.Items.STONE)
				.patternLine("#I#").patternLine(" X ").patternLine("SSS")
				.addCriterion("has_plain", this.hasItem(plain))
				.setGroup("alveary")::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.HYGRO).block())
				.key('G', Tags.Items.GLASS)
				.key('X', plain)
				.key('I', Tags.Items.INGOTS_IRON)
				.patternLine("GIG").patternLine("GXG").patternLine("GIG")
				.addCriterion("has_plain", this.hasItem(plain))
				.setGroup("alveary")::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.SIEVE).block())
				.key('W', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.WOVEN_SILK).item())
				.key('X', plain)
				.key('I', Tags.Items.INGOTS_IRON)
				.patternLine("III").patternLine(" X ").patternLine("WWW")
				.addCriterion("has_plain", this.hasItem(plain))
				.setGroup("alveary")::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.STABILISER).block())
				.key('X', plain)
				.key('G', Tags.Items.GEMS_QUARTZ)
				.patternLine("G G").patternLine("GXG").patternLine("G G")
				.addCriterion("has_plain", this.hasItem(plain))
				.setGroup("alveary")::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.ALVEARY.get(BlockAlvearyType.SWARMER).block())
				.key('#', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.DIAMOND).item())
				.key('X', plain)
				.key('G', Tags.Items.INGOTS_GOLD)
				.patternLine("#G#").patternLine(" X ").patternLine("#G#")
				.addCriterion("has_plain", this.hasItem(plain))
				.setGroup("alveary")::build,
			ForestryModuleUids.APICULTURE);

		Item wovenSilk = CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.WOVEN_SILK).item();
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureItems.APIARIST_HELMET.item())
				.key('#', wovenSilk)
				.patternLine("###").patternLine("# #")
				.addCriterion("has silk", this.hasItem(wovenSilk))
				.setGroup("apiarist_armour")::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureItems.APIARIST_CHEST.item())
				.key('#', wovenSilk)
				.patternLine("# #").patternLine("###").patternLine("###")
				.addCriterion("has silk", this.hasItem(wovenSilk))
				.setGroup("apiarist_armour")::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureItems.APIARIST_LEGS.item())
				.key('#', wovenSilk)
				.patternLine("###").patternLine("# #").patternLine("# #")
				.addCriterion("has silk", this.hasItem(wovenSilk))
				.setGroup("apiarist_armour")::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureItems.APIARIST_BOOTS.item())
				.key('#', wovenSilk)
				.patternLine("# #").patternLine("# #")
				.addCriterion("has silk", this.hasItem(wovenSilk))
				.setGroup("apiarist_armour")::build,
			ForestryModuleUids.APICULTURE);

		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.BASE.get(BlockTypeApiculture.APIARY).block())
				.key('S', ItemTags.WOODEN_SLABS)
				.key('P', ItemTags.PLANKS)
				.key('C', CoreItems.IMPREGNATED_CASING.item())
				.patternLine("SSS").patternLine("PCP").patternLine("PPP")
				.addCriterion("has_casing", this.hasItem(CoreItems.IMPREGNATED_CASING.item()))::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.BASE.get(BlockTypeApiculture.BEE_HOUSE).block())
				.key('S', ItemTags.WOODEN_SLABS)
				.key('P', ItemTags.PLANKS)
				.key('C', ForestryTags.Items.BEE_COMBS)
				.patternLine("SSS").patternLine("PCP").patternLine("PPP")
				.addCriterion("has_casing", this.hasItem(ForestryTags.Items.BEE_COMBS))::build,
			ForestryModuleUids.APICULTURE);
		//TODO minecarts and candles once they are flattened

		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureBlocks.BEE_CHEST.block())
				.key('G', Tags.Items.GLASS)
				.key('X', ForestryTags.Items.BEE_COMBS)
				.key('Y', Tags.Items.CHESTS_WOODEN)
				.patternLine(" G ").patternLine("XYX").patternLine("XXX")
				.addCriterion("has_comb", this.hasItem(ForestryTags.Items.BEE_COMBS))::build,
			ForestryModuleUids.APICULTURE);

		Item propolis = ApicultureItems.PROPOLIS.get(EnumPropolis.NORMAL).item();

		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(CoreItems.BITUMINOUS_PEAT.item())
				.key('#', ForestryTags.Items.DUSTS_ASH)
				.key('X', CoreItems.PEAT.item())
				.key('Y', propolis)
				.patternLine(" # ").patternLine("XYX").patternLine(" # ")
				.addCriterion("has_propolis", this.hasItem(propolis))::build,
			ForestryModuleUids.APICULTURE);

		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureItems.FRAME_IMPREGNATED.item())
				.key('#', CoreItems.STICK_IMPREGNATED.item())
				.key('S', Tags.Items.STRING)
				.patternLine("###").patternLine("#S#").patternLine("###")
				.addCriterion("has_impregnated_stick", this.hasItem(CoreItems.STICK_IMPREGNATED.item()))::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureItems.FRAME_UNTREATED.item())
				.key('#', Tags.Items.RODS_WOODEN)
				.key('S', Tags.Items.STRING)
				.patternLine("###").patternLine("#S#").patternLine("###")
				.addCriterion("has_impregnated_stick", this.hasItem(CoreItems.STICK_IMPREGNATED.item()))::build,
			ForestryModuleUids.APICULTURE);

		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureItems.HABITAT_LOCATOR.item())
				.key('X', ForestryTags.Items.INGOTS_BRONZE)
				.key('#', Tags.Items.DUSTS_REDSTONE)
				.patternLine(" X ").patternLine("X#X").patternLine(" X ")
				.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOTS_BRONZE))::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.PULSATING_MESH).item())
				.key('#', ApicultureItems.PROPOLIS.get(EnumPropolis.PULSATING).item())
				.patternLine("# #").patternLine(" # ").patternLine("# #")
				.addCriterion("has_pulsating_propolis", this.hasItem(ApicultureItems.PROPOLIS.get(EnumPropolis.PULSATING).item()))::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureItems.SCOOP.item())
				.key('#', Tags.Items.RODS_WOODEN)
				.key('X', ItemTags.WOOL)
				.patternLine("#X#").patternLine("###").patternLine(" # ")
				.addCriterion("has_wool", this.hasItem(ItemTags.WOOL))::build,
			ForestryModuleUids.APICULTURE
		);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(Items.SLIME_BALL)
				.key('#', propolis)
				.key('X', ApicultureItems.POLLEN_CLUSTER.get(EnumPollenCluster.NORMAL).item())
				.patternLine("#X#").patternLine("#X#").patternLine("#X#")
				.addCriterion("has_propolis", this.hasItem(propolis))::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureItems.SMOKER.item())
				.key('#', ForestryTags.Items.INGOTS_TIN)
				.key('S', Tags.Items.RODS_WOODEN)
				.key('F', Items.FLINT_AND_STEEL)
				.key('L', Tags.Items.LEATHER)
				.patternLine("LS#").patternLine("LF#").patternLine("###")
				.addCriterion("has_tin", this.hasItem(ForestryTags.Items.INGOTS_TIN))::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(Items.GLISTERING_MELON_SLICE)
				.key('#', ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY).item())
				.key('X', ApicultureItems.HONEYDEW.item())
				.key('Y', Items.MELON_SLICE)
				.patternLine("#X#").patternLine("#Y#").patternLine("#X#")
				.addCriterion("has_melon", this.hasItem(Items.MELON_SLICE))::build,
			ForestryModuleUids.APICULTURE);

		Item beesWax = CoreItems.BEESWAX.item();
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(Items.TORCH, 3)
				.key('#', beesWax)
				.key('Y', Tags.Items.RODS_WOODEN)
				.patternLine(" # ").patternLine(" # ").patternLine(" Y ")
				.addCriterion("has_wax", this.hasItem(beesWax))::build,
			ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ApicultureItems.WAX_CAST.item())
				.key('#', beesWax)
				.patternLine("###").patternLine("# #").patternLine("###")
				.addCriterion("has_wax", this.hasItem(beesWax))::build,
			ForestryModuleUids.APICULTURE);
	}

	private void registerCombRecipes(RecipeDataHelper helper) {
		for (EnumHoneyComb honeyComb : EnumHoneyComb.VALUES) {
			Item comb = ApicultureItems.BEE_COMBS.get(honeyComb).item();
			Block combBlock = ApicultureBlocks.BEE_COMB.get(honeyComb).block();
			helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(combBlock).key('#', comb).patternLine("###").patternLine("###").patternLine("###").addCriterion("has_comb", this.hasItem(comb)).setGroup("combs")::build,
				ForestryModuleUids.APICULTURE
			);
		}
	}

	private void registerArboricultureRecipes(RecipeDataHelper helper) {
		registerWoodRecipes(helper);

		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ArboricultureItems.GRAFTER.item())
				.key('B', ForestryTags.Items.INGOTS_BRONZE)
				.key('#', Tags.Items.RODS_WOODEN)
				.patternLine("  B").patternLine(" # ").patternLine("#  ")
				.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOTS_BRONZE))::build,
			ForestryModuleUids.ARBORICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ArboricultureBlocks.TREE_CHEST.block())
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

		Item waxCapsule = FluidsItems.CONTAINERS.get(EnumContainerType.CAPSULE).item();
		Item honeyDrop = ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY).item();

		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FoodItems.AMBROSIA.item())
				.key('#', ApicultureItems.HONEYDEW.item())
				.key('X', ApicultureItems.ROYAL_JELLY.item())
				.key('Y', waxCapsule)
				.patternLine("#Y#").patternLine("XXX").patternLine("###")
				.addCriterion("has royal_jelly", this.hasItem(ApicultureItems.ROYAL_JELLY.item()))::build,
			ForestryModuleUids.FOOD);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FoodItems.HONEY_POT.item())
				.key('#', honeyDrop)
				.key('X', waxCapsule)
				.patternLine("# #").patternLine(" X ").patternLine("# #")
				.addCriterion("has_drop", this.hasItem(honeyDrop))::build,
			ForestryModuleUids.FOOD);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FoodItems.HONEYED_SLICE.item())
				.key('#', honeyDrop)
				.key('X', Items.BREAD)
				.patternLine("###").patternLine("#X#").patternLine("###")
				.addCriterion("has_drop", this.hasItem(honeyDrop))::build,
			ForestryModuleUids.FOOD);
	}

	private void registerBackpackRecipes(RecipeDataHelper helper) {

		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(BackpackItems.ADVENTURER_BACKPACK.item())
				.key('#', ItemTags.WOOL)
				.key('V', Tags.Items.BONES)
				.key('X', Tags.Items.STRING)
				.key('Y', Tags.Items.CHESTS_WOODEN)
				.patternLine("X#X").patternLine("VYV").patternLine("X#X")
				.addCriterion("has_bone", this.hasItem(Tags.Items.BONES))::build,
			ForestryModuleUids.BACKPACKS);

		Block beeChest = ArboricultureBlocks.TREE_CHEST.block();
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(BackpackItems.APIARIST_BACKPACK.item())
				.key('#', ItemTags.WOOL)
				.key('V', Tags.Items.RODS_WOODEN)
				.key('X', Tags.Items.STRING)
				.key('Y', beeChest)
				.patternLine("X#X").patternLine("VYV").patternLine("X#X")
				.addCriterion("has_bee_chest", this.hasItem(beeChest))::build,
			ForestryModuleUids.BACKPACKS, ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(BackpackItems.BUILDER_BACKPACK.item())
				.key('#', ItemTags.WOOL)
				.key('V', Items.CLAY_BALL)
				.key('X', Tags.Items.STRING)
				.key('Y', Tags.Items.CHESTS_WOODEN)
				.patternLine("X#X").patternLine("VYV").patternLine("X#X")
				.addCriterion("has_clay", this.hasItem(Items.CLAY_BALL))::build,
			ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(BackpackItems.DIGGER_BACKPACK.item())
				.key('#', ItemTags.WOOL)
				.key('V', Tags.Items.STONE)
				.key('X', Tags.Items.STRING)
				.key('Y', Tags.Items.CHESTS_WOODEN)
				.patternLine("X#X").patternLine("VYV").patternLine("X#X")
				.addCriterion("has_stone", this.hasItem(Tags.Items.STONE))::build,
			ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(BackpackItems.FORESTER_BACKPACK.item())
				.key('#', ItemTags.WOOL)
				.key('V', ItemTags.LOGS)
				.key('X', Tags.Items.STRING)
				.key('Y', Tags.Items.CHESTS_WOODEN)
				.patternLine("X#X").patternLine("VYV").patternLine("X#X")
				.addCriterion("has_log", this.hasItem(ItemTags.LOGS))::build,
			ForestryModuleUids.BACKPACKS);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(BackpackItems.HUNTER_BACKPACK.item())
				.key('#', ItemTags.WOOL)
				.key('V', Tags.Items.FEATHERS)
				.key('X', Tags.Items.STRING)
				.key('Y', Tags.Items.CHESTS_WOODEN)
				.patternLine("X#X").patternLine("VYV").patternLine("X#X")
				.addCriterion("has_feather", this.hasItem(Tags.Items.FEATHERS))::build,
			ForestryModuleUids.BACKPACKS);

		Block butterflyChest = LepidopterologyBlocks.BUTTERFLY_CHEST.block();
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(BackpackItems.LEPIDOPTERIST_BACKPACK.item())
				.key('#', ItemTags.WOOL)
				.key('V', butterflyChest)
				.key('X', Tags.Items.STRING)
				.key('Y', Tags.Items.CHESTS_WOODEN)
				.patternLine("X#X").patternLine("VYV").patternLine("X#X")
				.addCriterion("has_butterfly_chest", this.hasItem(butterflyChest))::build,
			ForestryModuleUids.BACKPACKS, ForestryModuleUids.LEPIDOPTEROLOGY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(BackpackItems.MINER_BACKPACK.item())
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
			ShapedRecipeBuilder.shapedRecipe(CharcoalBlocks.CHARCOAL.block())
				.key('#', Items.CHARCOAL)
				.patternLine("###").patternLine("###").patternLine("###")
				.addCriterion("has_charcoal", this.hasItem(Items.CHARCOAL))::build,
			ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
			ShapelessRecipeBuilder.shapelessRecipe(Items.CHARCOAL, 9)
				.addIngredient(ForestryTags.Items.CHARCOAL)
				.addCriterion("has_charcoal_block", this.hasItem(ForestryTags.Items.CHARCOAL))::build,
			ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(CharcoalBlocks.WOOD_PILE.block())
				.key('L', ItemTags.LOGS)
				.patternLine("LL").patternLine("LL")
				.addCriterion("has_log", this.hasItem(ItemTags.LOGS))::build,
			ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
			ShapelessRecipeBuilder.shapelessRecipe(CharcoalBlocks.WOOD_PILE_DECORATIVE.block())
				.addIngredient(CharcoalBlocks.WOOD_PILE.block())
				.addCriterion("was_wood_pile", this.hasItem(CharcoalBlocks.WOOD_PILE.block()))::build,
			ForestryModuleUids.CHARCOAL);
		helper.moduleConditionRecipe(
			ShapelessRecipeBuilder.shapelessRecipe(CharcoalBlocks.WOOD_PILE.block())
				.addIngredient(CharcoalBlocks.WOOD_PILE_DECORATIVE.block())
				.addCriterion("has_decorative", this.hasItem(CharcoalBlocks.WOOD_PILE_DECORATIVE.block()))::build,
			new ResourceLocation(Constants.MOD_ID, "wood_pile_from_decorative"), ForestryModuleUids.CHARCOAL);
	}

	private void addClimatologyRecipes(RecipeDataHelper helper) {

		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(ClimatologyBlocks.HABITATFORMER.block())
				.key('S', CoreItems.STURDY_CASING.item())
				.key('G', Tags.Items.GLASS)
				.key('B', ForestryTags.Items.GEARS_BRONZE)
				.key('R', Tags.Items.DUSTS_REDSTONE)
				.key('C', CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.BASIC).item())
				.key('T', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.IRON).item())
				.patternLine("GRG").patternLine("TST").patternLine("BCB")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.CLIMATOLOGY);
		//TODO if carpenter recipes are in json then can just use that here
		helper.simpleConditionalRecipe(
			ShapedRecipeBuilder.shapedRecipe(ClimatologyItems.HABITAT_SCREEN.item())
				.key('G', ForestryTags.Items.GEARS_BRONZE)
				.key('P', Tags.Items.GLASS_PANES)
				.key('I', ForestryTags.Items.INGOTS_BRONZE)
				.key('D', Tags.Items.GEMS_DIAMOND)
				.patternLine("IPI").patternLine("IPI").patternLine("DGD")
				.addCriterion("has_diamond", this.hasItem(Tags.Items.GEMS_DIAMOND))::build,
			new AndCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.CLIMATOLOGY),
				new NotCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.FACTORY))));
	}

	private void registerCoreRecipes(RecipeDataHelper helper) {
		Consumer<IFinishedRecipe> consumer = helper.getConsumer();

		//don't need conditions here generally since core is always enabled
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.BASE.get(BlockTypeCoreTesr.ANALYZER).block())
			.key('T', CoreItems.PORTABLE_ALYZER.item())
			.key('X', ForestryTags.Items.INGOTS_BRONZE)
			.key('Y', CoreItems.STURDY_CASING.item())
			.patternLine("XTX").patternLine(" Y ").patternLine("X X")
			.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item())).build(consumer);
		//TODO how to deal with variable output. Options: wrapper recipe, custom recipe type, leave up to data packs.
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.APATITE).block())
			.key('#', ForestryTags.Items.GEMS_APATITE)
			.patternLine("###").patternLine("###").patternLine("###")
			.addCriterion("has_apatite", this.hasItem(ForestryTags.Items.GEMS_APATITE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.BRONZE).block())
			.key('#', ForestryTags.Items.INGOTS_BRONZE)
			.patternLine("###").patternLine("###").patternLine("###")
			.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOTS_BRONZE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.COPPER).block())
			.key('#', ForestryTags.Items.INGOTS_COPPER)
			.patternLine("###").patternLine("###").patternLine("###")
			.addCriterion("has_copper", this.hasItem(ForestryTags.Items.INGOTS_COPPER)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreBlocks.RESOURCE_STORAGE.get(EnumResourceType.TIN).block())
			.key('#', ForestryTags.Items.INGOTS_TIN)
			.patternLine("###").patternLine("###").patternLine("###")
			.addCriterion("has_apatite", this.hasItem(ForestryTags.Items.INGOTS_TIN)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.BRONZE_PICKAXE.item())
			.key('#', ForestryTags.Items.INGOTS_BRONZE)
			.key('X', Tags.Items.RODS_WOODEN)
			.patternLine("###").patternLine(" X ").patternLine(" X ")
			.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOTS_BRONZE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.BRONZE_SHOVEL.item())
			.key('#', ForestryTags.Items.INGOTS_BRONZE)
			.key('X', Tags.Items.RODS_WOODEN)
			.patternLine(" # ").patternLine(" X ").patternLine(" X ")
			.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOTS_BRONZE)).build(consumer);
		helper.simpleConditionalRecipe(
			ShapedRecipeBuilder.shapedRecipe(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.CAMOUFLAGED_PANELING).item())
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
				ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_BRONZE.item())
					.key('#', ForestryTags.Items.INGOTS_BRONZE)
					.key('X', ForestryTags.Items.GEARS_STONE)
					.patternLine(" # ").patternLine("#X#").patternLine(" # ")
					.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOTS_BRONZE))::build)
			.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
			.addRecipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_BRONZE.item())
				.key('#', ForestryTags.Items.INGOTS_BRONZE)
				.key('X', ForestryTags.Items.INGOTS_COPPER)
				.patternLine(" # ").patternLine("#X#").patternLine(" # ")
				.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOTS_BRONZE))::build)
			.build(helper.getConsumer(), new ResourceLocation(Constants.MOD_ID, "gear_bronze"));
		ConditionalRecipe.builder()
			.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
			.addRecipe(
				ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_COPPER.item())
					.key('#', ForestryTags.Items.INGOTS_COPPER)
					.key('X', ForestryTags.Items.GEARS_STONE)
					.patternLine(" # ").patternLine("#X#").patternLine(" # ")
					.addCriterion("has_copper", this.hasItem(ForestryTags.Items.INGOTS_COPPER))::build)
			.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
			.addRecipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_COPPER.item())
				.key('#', ForestryTags.Items.INGOTS_COPPER)
				.key('X', ForestryTags.Items.INGOTS_COPPER)
				.patternLine(" # ").patternLine("#X#").patternLine(" # ")
				.addCriterion("has_copper", this.hasItem(ForestryTags.Items.INGOTS_COPPER))::build)
			.build(helper.getConsumer(), new ResourceLocation(Constants.MOD_ID, "gear_copper"));
		ConditionalRecipe.builder()
			.addCondition(new NotCondition(new TagEmptyCondition("forge", "gears/stone")))
			.addRecipe(
				ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_TIN.item())
					.key('#', ForestryTags.Items.INGOTS_TIN)
					.key('X', ForestryTags.Items.GEARS_STONE)
					.patternLine(" # ").patternLine("#X#").patternLine(" # ")
					.addCriterion("has_tin", this.hasItem(ForestryTags.Items.INGOTS_TIN))::build)
			.addCondition(new TagEmptyCondition("forge", "gears/stone"))    //TODO can this be replaced with true since the array is scanned in order?
			.addRecipe(ShapedRecipeBuilder.shapedRecipe(CoreItems.GEAR_TIN.item())
				.key('#', ForestryTags.Items.INGOTS_TIN)
				.key('X', ForestryTags.Items.INGOTS_COPPER)
				.patternLine(" # ").patternLine("#X#").patternLine(" # ")
				.addCriterion("has_tin", this.hasItem(ForestryTags.Items.INGOTS_TIN))::build)
			.build(helper.getConsumer(), new ResourceLocation(Constants.MOD_ID, "gear_tin"));

		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.INGOT_BRONZE.item())
			.addIngredient(ForestryTags.Items.INGOTS_TIN)
			.addIngredient(ForestryTags.Items.INGOTS_COPPER)
			.addIngredient(ForestryTags.Items.INGOTS_COPPER)
			.addIngredient(ForestryTags.Items.INGOTS_COPPER)
			.addCriterion("has_tin", this.hasItem(ForestryTags.Items.INGOTS_TIN))
			.build(consumer, new ResourceLocation(Constants.MOD_ID, "ingot_bronze_alloying"));

		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.APATITE.item(), 9)
			.addIngredient(ForestryTags.Items.STORAGE_BLOCKS_APATITE)
			.addCriterion("has_block", this.hasItem(ForestryTags.Items.STORAGE_BLOCKS_APATITE)).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.INGOT_BRONZE.item(), 9)
			.addIngredient(ForestryTags.Items.STORAGE_BLOCKS_BRONZE)
			.addCriterion("has_block", this.hasItem(ForestryTags.Items.STORAGE_BLOCKS_BRONZE)).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.INGOT_COPPER.item(), 9)
			.addIngredient(ForestryTags.Items.STORAGE_BLOCKS_COPPER)
			.addCriterion("has_block", this.hasItem(ForestryTags.Items.STORAGE_BLOCKS_COPPER)).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.INGOT_TIN.item(), 9)
			.addIngredient(ForestryTags.Items.STORAGE_BLOCKS_TIN)
			.addCriterion("has_block", this.hasItem(ForestryTags.Items.STORAGE_BLOCKS_TIN)).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.KIT_PICKAXE.item())
			.addIngredient(CoreItems.BRONZE_PICKAXE.item())
			.addIngredient(CoreItems.CARTON.item())
			.addCriterion("has_pickaxe", this.hasItem(CoreItems.BRONZE_PICKAXE.item())).build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(CoreItems.KIT_SHOVEL.item())
			.addIngredient(CoreItems.BRONZE_SHOVEL.item())
			.addIngredient(CoreItems.CARTON.item())
			.addCriterion("has_shovel", this.hasItem(CoreItems.BRONZE_SHOVEL.item())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.SPECTACLES.item())
			.key('X', ForestryTags.Items.INGOTS_BRONZE)
			.key('Y', Tags.Items.GLASS_PANES)
			.patternLine(" X ").patternLine("Y Y")
			.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOTS_BRONZE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.PIPETTE.item())
			.key('#', ItemTags.WOOL)
			.key('X', Tags.Items.GLASS_PANES)
			.patternLine("  #").patternLine(" X ").patternLine("X  ")
			.addCriterion("has_wool", this.hasItem(ItemTags.WOOL)).build(consumer);
		helper.simpleConditionalRecipe(
			ShapedRecipeBuilder.shapedRecipe(CoreItems.PORTABLE_ALYZER.item())
				.key('#', Tags.Items.GLASS_PANES)
				.key('X', ForestryTags.Items.INGOTS_TIN)
				.key('R', Tags.Items.DUSTS_REDSTONE)
				.key('D', Tags.Items.GEMS_DIAMOND)
				.patternLine("X#X").patternLine("X#X").patternLine("RDR")
				.addCriterion("has_diamond", this.hasItem(Tags.Items.GEMS_DIAMOND))::build,
			new NotCondition(new ModuleEnabledCondition(Constants.MOD_ID, ForestryModuleUids.FACTORY)));
		ShapedRecipeBuilder.shapedRecipe(Items.STRING)
			.key('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).item())
			.patternLine(" # ").patternLine(" # ").patternLine(" # ")
			.addCriterion("has_wisp", this.hasItem(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).item())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.STURDY_CASING.item())
			.key('#', ForestryTags.Items.INGOTS_BRONZE)
			.patternLine("###").patternLine("# #").patternLine("###")
			.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOTS_BRONZE)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(Items.COBWEB, 4)
			.key('#', CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).item())
			.patternLine("# #").patternLine(" # ").patternLine("# #")
			.addCriterion("has_wisp", this.hasItem(CoreItems.CRAFTING_MATERIALS.get(EnumCraftingMaterial.SILK_WISP).item())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(CoreItems.WRENCH.item())
			.key('#', ForestryTags.Items.INGOTS_BRONZE)
			.patternLine("# #").patternLine(" # ").patternLine(" # ")
			.addCriterion("has_bronze", this.hasItem(ForestryTags.Items.INGOTS_BRONZE)).build(consumer);

	}

	private void registerBookRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
			ShapelessRecipeBuilder.shapelessRecipe(BookItems.BOOK.item())
				.addIngredient(Items.BOOK)
				.addIngredient(ApicultureItems.HONEY_DROPS.get(EnumHoneyDrop.HONEY).item())
				.addCriterion("has_book", this.hasItem(Items.BOOK))::build,
			new ResourceLocation(Constants.MOD_ID, "book_forester_drop"),
			ForestryModuleUids.BOOK, ForestryModuleUids.APICULTURE);
		helper.moduleConditionRecipe(
			ShapelessRecipeBuilder.shapelessRecipe(BookItems.BOOK.item())
				.addIngredient(Items.BOOK)
				.addIngredient(ItemTags.SAPLINGS)
				.addCriterion("has_book", this.hasItem(Items.BOOK))::build,
			new ResourceLocation(Constants.MOD_ID, "book_forester_sapling"),
			ForestryModuleUids.BOOK);
		helper.moduleConditionRecipe(
			ShapelessRecipeBuilder.shapelessRecipe(BookItems.BOOK.item())
				.addIngredient(Items.BOOK)
				.addIngredient(LepidopterologyItems.BUTTERFLY_GE.item())
				.addCriterion("has_book", this.hasItem(Items.BOOK))::build,
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
				ShapedRecipeBuilder.shapedRecipe(managed)
					.key('G', Tags.Items.GLASS)
					.key('T', CoreItems.ELECTRON_TUBES.get(getElectronTube(planter)).item())
					.key('C', CoreItems.FLEXIBLE_CASING.item())
					.key('B', CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.BASIC).item())
					.patternLine("GTG").patternLine("TCT").patternLine("GBG")
					.addCriterion("has_casing", this.hasItem(CoreItems.FLEXIBLE_CASING.item()))::build,
				ForestryModuleUids.CULTIVATION);

			helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapelessRecipe(manual)
					.addIngredient(managed)
					.addCriterion("has_managed", this.hasItem(managed))::build,
				ForestryModuleUids.CULTIVATION);
			helper.moduleConditionRecipe(
				ShapelessRecipeBuilder.shapelessRecipe(managed)
					.addIngredient(manual)
					.addCriterion("has_manual", this.hasItem(manual))::build,
				new ResourceLocation(Constants.MOD_ID, managed.getRegistryName().getPath() + "_from_manual"),
				ForestryModuleUids.CULTIVATION);
		}
	}

	private void registerDatabaseRecipes(RecipeDataHelper helper) {
		//TODO create FallbackIngredient implementation
		List<FeatureBlock<?, ?>> features = Lists.newArrayList(ApicultureBlocks.BEE_CHEST, ArboricultureBlocks.TREE_CHEST, LepidopterologyBlocks.BUTTERFLY_CHEST);
		List<Ingredient> possibleSpecials = Lists.newArrayList(Ingredient.fromItems(ApicultureItems.ROYAL_JELLY.getItem()), Ingredient.fromItems(CoreItems.FRUITS.get(ItemFruit.EnumFruit.PLUM).getItem()), Ingredient.fromTag(Tags.Items.CHESTS_WOODEN));
		Ingredient possibleSpecial = Ingredient.merge(possibleSpecials);
		for (FeatureBlock<?, ?> featureBlock1 : features) {
			for (FeatureBlock<?, ?> featureBlock2 : features) {
				if (featureBlock1.equals(featureBlock2)) {
					continue;
				}

				helper.moduleConditionRecipe(
					ShapedRecipeBuilder.shapedRecipe(DatabaseBlocks.DATABASE.block())
						.key('#', CoreItems.PORTABLE_ALYZER.item())
						.key('C', possibleSpecial)
						.key('S', featureBlock1.block())
						.key('F', featureBlock2.block())
						.key('W', ItemTags.PLANKS)
						.key('I', ForestryTags.Items.INGOTS_BRONZE)
						.key('Y', CoreItems.STURDY_CASING.item())
						.patternLine("I#I").patternLine("FYS").patternLine("WCW")
						.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
					new ResourceLocation(Constants.MOD_ID, "database_" + featureBlock1.getIdentifier() + "_" + featureBlock2.getIdentifier()),
					ForestryModuleUids.DATABASE, featureBlock1.getModuleId(), featureBlock2.getModuleId());
			}
		}
	}

	private void registerEnergyRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(EnergyBlocks.ENGINES.get(BlockTypeEngine.BIOGAS).block())
				.key('#', ForestryTags.Items.INGOTS_BRONZE)
				.key('V', Items.PISTON)
				.key('X', Tags.Items.GLASS)
				.key('Y', ForestryTags.Items.GEARS_BRONZE)
				.patternLine("###").patternLine(" X ").patternLine("YVY")
				.addCriterion("has_piston", this.hasItem(Items.PISTON))::build,
			ForestryModuleUids.ENERGY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(EnergyBlocks.ENGINES.get(BlockTypeEngine.CLOCKWORK).block())
				.key('#', ItemTags.PLANKS)
				.key('V', Items.PISTON)
				.key('X', Tags.Items.GLASS)
				.key('Y', Items.CLOCK)
				.key('Z', ForestryTags.Items.GEARS_COPPER)
				.patternLine("###").patternLine(" X ").patternLine("ZVY")
				.addCriterion("has_piston", this.hasItem(Items.PISTON))::build,
			ForestryModuleUids.ENERGY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(EnergyBlocks.ENGINES.get(BlockTypeEngine.PEAT).block())
				.key('#', ForestryTags.Items.INGOTS_COPPER)
				.key('V', Items.PISTON)
				.key('X', Tags.Items.GLASS)
				.key('Y', ForestryTags.Items.GEARS_COPPER)
				.patternLine("###").patternLine(" X ").patternLine("YVY")
				.addCriterion("has_piston", this.hasItem(Items.PISTON))::build,
			ForestryModuleUids.ENERGY);
	}

	private void registerFactoryRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.BOTTLER).block())
				.key('#', Tags.Items.GLASS)
				.key('X', FluidsItems.CONTAINERS.get(EnumContainerType.CAN).item())
				.key('Y', CoreItems.STURDY_CASING.item())
				.patternLine("X#X").patternLine("#Y#").patternLine("X#X")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CARPENTER).block())
				.key('#', Tags.Items.GLASS)
				.key('X', ForestryTags.Items.INGOTS_BRONZE)
				.key('Y', CoreItems.STURDY_CASING.item())
				.patternLine("X#X").patternLine("XYX").patternLine("X#X")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CENTRIFUGE).block())
				.key('#', Tags.Items.GLASS)
				.key('X', ForestryTags.Items.INGOTS_COPPER)
				.key('Y', CoreItems.STURDY_CASING.item())
				.patternLine("X#X").patternLine("XYX").patternLine("X#X")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.FABRICATOR).block())
				.key('#', Tags.Items.GLASS)
				.key('X', Tags.Items.INGOTS_GOLD)
				.key('Y', CoreItems.STURDY_CASING.item())
				.key('Z', Tags.Items.CHESTS_WOODEN)
				.patternLine("X#X").patternLine("#Y#").patternLine("XZX")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.FERMENTER).block())
				.key('#', Tags.Items.GLASS)
				.key('X', ForestryTags.Items.GEARS_BRONZE)
				.key('Y', CoreItems.STURDY_CASING.item())
				.patternLine("X#X").patternLine("#Y#").patternLine("X#X")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.MOISTENER).block())
				.key('#', Tags.Items.GLASS)
				.key('X', ForestryTags.Items.GEARS_COPPER)
				.key('Y', CoreItems.STURDY_CASING.item())
				.patternLine("X#X").patternLine("#Y#").patternLine("X#X")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.RAINMAKER).block())
				.key('#', Tags.Items.GLASS)
				.key('X', ForestryTags.Items.GEARS_TIN)
				.key('Y', CoreItems.STURDY_CASING.item())
				.patternLine("X#X").patternLine("#Y#").patternLine("X#X")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FactoryBlocks.PLAIN.get(BlockTypeFactoryPlain.RAINTANK).block())
				.key('#', Tags.Items.GLASS)
				.key('X', Tags.Items.INGOTS_IRON)
				.key('Y', CoreItems.STURDY_CASING.item())
				.patternLine("X#X").patternLine("XYX").patternLine("X#X")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.SQUEEZER).block())
				.key('#', Tags.Items.GLASS)
				.key('X', ForestryTags.Items.INGOTS_TIN)
				.key('Y', CoreItems.STURDY_CASING.item())
				.patternLine("X#X").patternLine("XYX").patternLine("X#X")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.FACTORY);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.STILL).block())
				.key('#', Tags.Items.GLASS)
				.key('X', Tags.Items.DUSTS_REDSTONE)
				.key('Y', CoreItems.STURDY_CASING.item())
				.patternLine("X#X").patternLine("#Y#").patternLine("X#X")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.FACTORY);
	}

	private void registerFarmingRecipes(RecipeDataHelper helper) {
		for (EnumFarmMaterial material : EnumFarmMaterial.values()) {
			Item base = material.getBase().getItem();
			helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(FarmingBlocks.FARM.get(EnumFarmBlockType.PLAIN, material).block())
					.key('#', base)
					.key('C', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.TIN).item())
					.key('W', ItemTags.WOODEN_SLABS)
					.key('I', ForestryTags.Items.INGOTS_COPPER)
					.patternLine("I#I").patternLine("WCW")
					.addCriterion("has_copper", this.hasItem(ForestryTags.Items.INGOTS_COPPER))::build,
				ForestryModuleUids.FARMING);
			helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(FarmingBlocks.FARM.get(EnumFarmBlockType.GEARBOX, material).block())
					.key('#', base)
					.key('T', ForestryTags.Items.GEARS_TIN)
					.patternLine(" # ").patternLine("TTT")
					.addCriterion("has_tin_gear", this.hasItem(ForestryTags.Items.GEARS_TIN))::build,
				ForestryModuleUids.FARMING);
			helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(FarmingBlocks.FARM.get(EnumFarmBlockType.HATCH, material).block())
					.key('#', base)
					.key('T', ForestryTags.Items.GEARS_TIN)
					.key('D', ItemTags.WOODEN_TRAPDOORS)
					.patternLine(" # ").patternLine("TDT")
					.addCriterion("has_tin_gear", this.hasItem(ForestryTags.Items.GEARS_TIN))::build,
				ForestryModuleUids.FARMING);
			helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(FarmingBlocks.FARM.get(EnumFarmBlockType.VALVE, material).block())
					.key('#', base)
					.key('T', ForestryTags.Items.GEARS_TIN)
					.key('X', Tags.Items.GLASS)
					.patternLine(" # ").patternLine("XTX")
					.addCriterion("has_tin_gear", this.hasItem(ForestryTags.Items.GEARS_TIN))::build,
				ForestryModuleUids.FARMING);
			helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(FarmingBlocks.FARM.get(EnumFarmBlockType.CONTROL, material).block())
					.key('#', base)
					.key('T', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.GOLD).item())
					.key('X', Tags.Items.DUSTS_REDSTONE)
					.patternLine(" # ").patternLine("XTX")
					.addCriterion("has_tin_gear", this.hasItem(ForestryTags.Items.GEARS_TIN))::build,
				ForestryModuleUids.FARMING);
		}
	}

	//TODO maybe I'm missing something, but this seems like the only reasonable way to do it...
	private NBTIngredient createNbtIngredient(ItemStack stack) {
		Constructor<NBTIngredient> constructor;
		try {
			constructor = NBTIngredient.class.getDeclaredConstructor(ItemStack.class);
			constructor.setAccessible(true);
			return constructor.newInstance(stack);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}


	private void registerFluidsRecipes(RecipeDataHelper helper) {
		ForestryFluids milk = ForestryFluids.MILK;
		for (EnumContainerType containerType : EnumContainerType.values()) {
			if (containerType == EnumContainerType.JAR || containerType == EnumContainerType.GLASS) {
				continue;
			}
			ItemStack filled = FluidsItems.getContainer(containerType, milk);
			Ingredient ingredientNBT = createNbtIngredient(filled);
			helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(Items.CAKE)
					.key('A', ingredientNBT)
					.key('B', Items.SUGAR)
					.key('C', Items.WHEAT)
					.key('E', Items.EGG)
					.patternLine("AAA").patternLine("BEB").patternLine("CCC")
					.addCriterion("has_wheat", this.hasItem(Items.WHEAT))::build,
				new ResourceLocation(Constants.MOD_ID, "cake_" + containerType.getString()),
				ForestryModuleUids.FLUIDS);
		}
	}

	private void registerLepidopterologyRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(LepidopterologyBlocks.BUTTERFLY_CHEST.block())
				.key('#', Tags.Items.GLASS)
				.key('X', LepidopterologyItems.BUTTERFLY_GE.item())    //TODO tag?
				.key('Y', Tags.Items.CHESTS_WOODEN)
				.patternLine(" # ").patternLine("XYX").patternLine("XXX")
				.addCriterion("has_butterfly", this.hasItem(LepidopterologyItems.BUTTERFLY_GE.item()))::build,
			ForestryModuleUids.LEPIDOPTEROLOGY);
	}

	private void registerMailRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
			ShapelessRecipeBuilder.shapelessRecipe(MailItems.CATALOGUE.item())
				.addIngredient(Items.BOOK)
				.addIngredient(ForestryTags.Items.STAMPS)
				.addCriterion("has_book", this.hasItem(Items.BOOK))::build,
			ForestryModuleUids.MAIL);

		//TODO fallback ingredient
		helper.moduleConditionRecipe(
			ShapelessRecipeBuilder.shapelessRecipe(MailItems.LETTERS.get(ItemLetter.Size.EMPTY, ItemLetter.State.FRESH).item())
				.addIngredient(Items.PAPER)
				.addIngredient(Ingredient.merge(Lists.newArrayList(Ingredient.fromTag(ForestryTags.Items.PROPOLIS), Ingredient.fromTag(Tags.Items.SLIMEBALLS))))
				.addCriterion("has_paper", this.hasItem(Items.PAPER))::build,
			ForestryModuleUids.MAIL);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(MailBlocks.BASE.get(BlockTypeMail.MAILBOX).block())
				.key('#', ForestryTags.Items.INGOTS_TIN)
				.key('X', Tags.Items.CHESTS_WOODEN)
				.key('Y', CoreItems.STURDY_CASING.item())
				.patternLine(" # ").patternLine("#Y#").patternLine("XXX")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.MAIL);
		Item[] emptiedLetter = MailItems.LETTERS.getRowFeatures(ItemLetter.Size.EMPTY).stream().map(FeatureItem::getItem).toArray(Item[]::new);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(Items.PAPER)
				.key('#', Ingredient.fromItems(emptiedLetter))
				.patternLine(" # ").patternLine(" # ").patternLine(" # ")
				.addCriterion("has_paper", this.hasItem(Items.PAPER))::build,
			ForestryModuleUids.MAIL);
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(MailBlocks.BASE.get(BlockTypeMail.TRADE_STATION).block())
				.key('#', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.BRONZE).getItem())
				.key('X', Tags.Items.CHESTS_WOODEN)
				.key('Y', CoreItems.STURDY_CASING.item())
				.key('Z', CoreItems.ELECTRON_TUBES.get(EnumElectronTube.IRON).item())
				.key('W', createNbtIngredient(ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[]{})))
				.patternLine("Z#Z").patternLine("#Y#").patternLine("XWX")
				.addCriterion("has_casing", this.hasItem(CoreItems.STURDY_CASING.item()))::build,
			ForestryModuleUids.MAIL);

		//TODO fallback
		Ingredient glue = Ingredient.merge(Lists.newArrayList(Ingredient.fromTag(ForestryTags.Items.DROP_HONEY), Ingredient.fromItems(Items.SLIME_BALL)));
		for (EnumStampDefinition stampDefinition : EnumStampDefinition.VALUES) {
			helper.moduleConditionRecipe(
				ShapedRecipeBuilder.shapedRecipe(MailItems.STAMPS.get(stampDefinition).item(), 9)
					.key('X', stampDefinition.getCraftingIngredient())
					.key('#', Items.PAPER)
					.key('Z', glue)
					.patternLine("XXX").patternLine("###").patternLine("ZZZ")
					.addCriterion("has_paper", this.hasItem(Items.PAPER))::build,
				ForestryModuleUids.MAIL);
		}
	}

	private void registerSortingRecipes(RecipeDataHelper helper) {
		Ingredient ing = Ingredient.merge(Lists.newArrayList(Ingredient.fromItems(LepidopterologyItems.CATERPILLAR_GE.item(), ApicultureItems.PROPOLIS.get(EnumPropolis.NORMAL).item()), Ingredient.fromTag(ForestryTags.Items.FRUITS)));

		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(SortingBlocks.FILTER.block(), 2)
				.key('B', ForestryTags.Items.GEARS_BRONZE)
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('F', ing)
				.key('W', ItemTags.PLANKS)
				.key('G', Tags.Items.GLASS)
				.patternLine("WDW").patternLine("FGF").patternLine("BDB")
				.addCriterion("has_diamond", this.hasItem(Tags.Items.GEMS_DIAMOND))::build,
			ForestryModuleUids.SORTING);
	}

	private void registerWorktableRecipes(RecipeDataHelper helper) {
		helper.moduleConditionRecipe(
			ShapedRecipeBuilder.shapedRecipe(WorktableBlocks.WORKTABLE.block())
				.key('B', Items.BOOK)
				.key('C', Tags.Items.CHESTS_WOODEN)
				.key('W', Items.CRAFTING_TABLE)
				.patternLine("B").patternLine("W").patternLine("C")
				.addCriterion("has_crafting_table", this.hasItem(Items.CRAFTING_TABLE))::build,
			ForestryModuleUids.WORKTABLE);
	}

	@Override
	protected void saveRecipeAdvancement(DirectoryCache cache, JsonObject advancementJson, Path pathIn) {
		//NOOP - We dont replace any of the advancement things yet...
	}

	@Override
	public String getName() {
		return "Forestry Recipes";
	}
}
