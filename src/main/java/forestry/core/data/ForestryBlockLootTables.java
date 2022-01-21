package forestry.core.data;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeavesFruit;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.loot.CountBlockFunction;
import forestry.core.blocks.EnumResourceType;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.loot.OrganismFunction;
import forestry.core.utils.Log;
import forestry.lepidopterology.features.LepidopterologyBlocks;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.modules.ModuleManager;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.FeatureType;
import forestry.modules.features.IModFeature;

/**
 * Data generator class that generates the block drop loot tables for forestry blocks.
 */
public class ForestryBlockLootTables extends BlockLoot {
	private final Set<Block> knownBlocks = new HashSet<>();

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			for (BlockDecorativeLeaves leaves : ArboricultureBlocks.LEAVES_DECORATIVE.getBlocks()) {
				this.add(leaves, (block) -> droppingWithChances(block, leaves.getDefinition(), NORMAL_LEAVES_SAPLING_CHANCES));
			}
			for (BlockDefaultLeaves leaves : ArboricultureBlocks.LEAVES_DEFAULT.getBlocks()) {
				this.add(leaves, (block) -> droppingWithChances(block, leaves.getTreeDefinition(), NORMAL_LEAVES_SAPLING_CHANCES));
			}
			for (Map.Entry<TreeDefinition, FeatureBlock<BlockDefaultLeavesFruit, BlockItem>> entry : ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.getFeatureByType().entrySet()) {
				FeatureBlock<BlockDefaultLeaves, BlockItem> defaultLeaves = ArboricultureBlocks.LEAVES_DEFAULT.get(entry.getKey());
				Block defaultLeavesBlock = defaultLeaves.block();
				Block fruitLeavesBlock = entry.getValue().block();
				this.add(fruitLeavesBlock, (block) -> droppingWithChances(defaultLeavesBlock, entry.getKey(), NORMAL_LEAVES_SAPLING_CHANCES));
			}
			registerLootTable(CharcoalBlocks.ASH, (block) -> LootTable.lootTable().setParamSet(LootContextParamSets.BLOCK)
					.withPool(LootPool.lootPool().add(LootItem.lootTableItem(CoreItems.ASH)).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(2, 1.0f / 3.0f))))
					.withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.COAL)).apply(CountBlockFunction.builder()).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 23.0f / 40, 2))));
		}
		registerLootTable(CoreBlocks.PEAT, (block) -> LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Blocks.DIRT))).withPool(LootPool.lootPool().apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))).add(LootItem.lootTableItem(CoreItems.PEAT.item()))));
		registerDropping(CoreBlocks.HUMUS, Blocks.DIRT);

		dropSelf(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN).block());
		dropSelf(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER).block());

		registerEmptyTables(ArboricultureBlocks.PODS); // Handled by internal logic
		registerEmptyTables(ArboricultureBlocks.SAPLING_GE); // Handled by internal logic
		registerEmptyTables(ArboricultureBlocks.LEAVES);  // Handled by internal logic
		registerEmptyTables(LepidopterologyBlocks.COCOON);
		registerEmptyTables(LepidopterologyBlocks.COCOON_SOLID);
		registerLootTable(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE), (p_218548_0_) -> createSilkTouchDispatchTable(p_218548_0_, applyExplosionDecay(p_218548_0_, LootItem.lootTableItem(CoreItems.APATITE.item()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 7.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 2)))));
		//TODO: Hives

		Set<ResourceLocation> visited = Sets.newHashSet();
		for (IModFeature feature : ModuleManager.moduleHandler.getFeatures((type) -> type.equals(FeatureType.BLOCK))) {
			if (!(feature instanceof FeatureBlock)) {
				Log.error("Found feature of the type block that does not extends the \"FeatureBlock\" class.");
				continue;
			}
			Block block = ((FeatureBlock<?, ?>) feature).block();
			ResourceLocation resourcelocation = block.getLootTable();
			if (resourcelocation != BuiltInLootTables.EMPTY && visited.add(resourcelocation)) {
				LootTable.Builder builder = this.map.remove(resourcelocation);

				if (builder == null) {
					builder = createSingleItemTable(block);
					//throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourcelocation, Registry.BLOCK.getKey(block)));
				}

				consumer.accept(resourcelocation, builder);
			}
		}

		if (!this.map.isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
		}
	}

	@Override
	public void otherWhenSilkTouch(Block blockIn, Block drop) {
		this.knownBlocks.add(blockIn);
		super.otherWhenSilkTouch(blockIn, drop);
	}

	@Override
	public void add(Block blockIn, Function<Block, LootTable.Builder> builderFunction) {
		this.knownBlocks.add(blockIn);
		super.add(blockIn, builderFunction);
	}

	@Override
	public void add(Block blockIn, LootTable.Builder builder) {
		this.knownBlocks.add(blockIn);
		super.add(blockIn, builder);
	}

	public static LootTable.Builder droppingWithChances(Block block, TreeDefinition definition, float... chances) {
		return createSilkTouchOrShearsDispatchTable(block,
				applyExplosionCondition(block, LootItem.lootTableItem(ArboricultureItems.SAPLING)
						.apply(OrganismFunction.fromDefinition(definition)))
						.when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, chances)));
	}

	public void registerSilkTouch(FeatureBlock<?, ?> featureBlock, Block drop) {
		otherWhenSilkTouch(featureBlock.block(), drop);
	}

	public void registerSilkTouch(FeatureBlock<?, ?> featureBlock, FeatureBlock<?, ?> drop) {
		otherWhenSilkTouch(featureBlock.block(), drop.block());
	}

	public void registerLootTable(FeatureBlock<?, ?> featureBlock, Function<Block, LootTable.Builder> builderFunction) {
		add(featureBlock.block(), builderFunction);
	}

	public void registerDropping(FeatureBlock<?, ?> featureBlock, FeatureBlock<?, ?> drop) {
		dropOther(featureBlock.block(), drop.block());
	}

	public void registerLootTable(FeatureBlock<?, ?> featureBlock, LootTable.Builder builder) {
		add(featureBlock.block(), builder);
	}

	public void registerDropping(FeatureBlock<?, ?> featureBlock, ItemLike drop) {
		dropOther(featureBlock.block(), drop);
	}

	public void registerEmptyTables(FeatureBlockGroup<?, ?> blockGroup) {
		registerEmptyTables(blockGroup.blockArray());
	}

	public void registerEmptyTables(FeatureBlock<?, ?> featureBlock) {
		registerEmptyTables(featureBlock.block());
	}

	public void registerEmptyTables(Block... blocks) {
		for (Block block : blocks) {
			add(block, noDrop());
		}
	}
}
