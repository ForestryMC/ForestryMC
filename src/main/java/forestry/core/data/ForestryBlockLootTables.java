package forestry.core.data;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BlockItem;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.conditions.TableBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import forestry.apiculture.features.ApicultureBlocks;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeavesFruit;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.blocks.EnumResourceType;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.loot.OrganismFunction;
import forestry.lepidopterology.features.LepidopterologyBlocks;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.modules.ModuleManager;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.FeatureType;
import forestry.modules.features.IModFeature;

import genetics.Log;

public class ForestryBlockLootTables extends BlockLootTables {
	private Set<Block> knownBlocks = new HashSet<>();

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
		}
		registerLootTable(CoreBlocks.PEAT, (block) -> new LootTable.Builder().withPool(new LootPool.Builder().add(ItemLootEntry.lootTableItem(Blocks.DIRT))).withPool(new LootPool.Builder().apply(SetCount.setCount(ConstantRange.exactly(2))).add(ItemLootEntry.lootTableItem(CoreItems.PEAT.item()))));
		registerDropping(CoreBlocks.HUMUS, Blocks.DIRT);

		dropSelf(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN).block());
		dropSelf(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER).block());

		//Apiculture
		registerEmptyTables(ApicultureBlocks.CANDLE); // Handled by internal logic
		registerEmptyTables(ApicultureBlocks.CANDLE_WALL); // Handled by internal logic
		registerDropping(ApicultureBlocks.STUMP_WALL, ApicultureBlocks.STUMP);

		registerEmptyTables(ArboricultureBlocks.PODS); // Handled by internal logic
		registerEmptyTables(ArboricultureBlocks.SAPLING_GE); // Handled by internal logic
		registerEmptyTables(LepidopterologyBlocks.COCOON);
		registerEmptyTables(LepidopterologyBlocks.COCOON_SOLID);
		//TODO: Hives
		//TODO: APATITE drops
		//registerLootTable(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE).block(), (block) -> withExplosionDecay((IItemProvider) block, ItemLootEntry.builder(CoreItems.APATITE.item()).acceptFunction(SetCount.builder(RandomValueRange.of(2, 7))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE, 2))));

		Set<ResourceLocation> visited = Sets.newHashSet();
		for (IModFeature feature : ModuleManager.moduleHandler.getFeatures((type) -> type.equals(FeatureType.BLOCK))) {
			if (!(feature instanceof FeatureBlock)) {
				Log.error("Error"); //TODO: Better error description
				continue;
			}
			Block block = ((FeatureBlock) feature).block();
			ResourceLocation resourcelocation = block.getLootTable();
			if (resourcelocation != LootTables.EMPTY && visited.add(resourcelocation)) {
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
				applyExplosionCondition(block, ItemLootEntry.lootTableItem(ArboricultureItems.SAPLING)
						.apply(OrganismFunction.fromDefinition(definition)))
						.when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, chances)));
	}

	public void registerSilkTouch(FeatureBlock featureBlock, Block drop) {
		otherWhenSilkTouch(featureBlock.block(), drop);
	}

	public void registerSilkTouch(FeatureBlock featureBlock, FeatureBlock drop) {
		otherWhenSilkTouch(featureBlock.block(), drop.block());
	}

	public void registerLootTable(FeatureBlock featureBlock, Function<Block, LootTable.Builder> builderFunction) {
		add(featureBlock.block(), builderFunction);
	}

	public void registerDropping(FeatureBlock featureBlock, FeatureBlock frop) {
		dropOther(featureBlock.block(), frop.block());
	}

	public void registerLootTable(FeatureBlock featureBlock, LootTable.Builder builder) {
		add(featureBlock.block(), builder);
	}

	public void registerDropping(FeatureBlock featureBlock, IItemProvider drop) {
		dropOther(featureBlock.block(), drop);
	}

	public void registerEmptyTables(FeatureBlockGroup blockGroup) {
		registerEmptyTables(blockGroup.blockArray());
	}

	public void registerEmptyTables(FeatureBlock featureBlock) {
		registerEmptyTables(featureBlock.block());
	}

	public void registerEmptyTables(Block... blocks) {
		for (Block block : blocks) {
			add(block, noDrop());
		}
	}
}
