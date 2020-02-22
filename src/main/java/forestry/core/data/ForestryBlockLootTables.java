package forestry.core.data;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.functions.SetCount;

import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.core.blocks.EnumResourceType;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public class ForestryBlockLootTables extends BlockLootTables {
	private Set<Block> knownBlocks = new HashSet<>();

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			for (BlockDecorativeLeaves leaves : ArboricultureBlocks.LEAVES_DECORATIVE.getBlocks()) {
				this.registerLootTable(leaves, (block) -> droppingWithChancesAndSticks(block, Blocks.AIR, DEFAULT_SAPLING_DROP_RATES));
			}
		}
		registerLootTable(CoreBlocks.PEAT.block(), (block) -> new LootTable.Builder().addLootPool(new LootPool.Builder().addEntry(ItemLootEntry.builder(Blocks.DIRT))).addLootPool(new LootPool.Builder().acceptFunction(SetCount.builder(ConstantRange.of(2))).addEntry(ItemLootEntry.builder(CoreItems.PEAT.item()))));
		registerDropping(CoreBlocks.HUMUS.block(), Blocks.DIRT);

		registerDropSelfLootTable(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN).block());
		registerDropSelfLootTable(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER).block());
		//TODO: APATITE drops
		//registerLootTable(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE).block(), (block) -> withExplosionDecay((IItemProvider) block, ItemLootEntry.builder(CoreItems.APATITE.item()).acceptFunction(SetCount.builder(RandomValueRange.of(2, 7))).acceptFunction(ApplyBonus.uniformBonusCount(Enchantments.FORTUNE, 2))));

		Set<ResourceLocation> visited = Sets.newHashSet();

		for (Block block : getKnownBlocks()) {
			ResourceLocation resourcelocation = block.getLootTable();
			if (resourcelocation != LootTables.EMPTY && visited.add(resourcelocation)) {
				LootTable.Builder loottable$builder = this.lootTables.remove(resourcelocation);
				if (loottable$builder == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourcelocation, Registry.BLOCK.getKey(block)));
				}

				consumer.accept(resourcelocation, loottable$builder);
			}
		}

		if (!this.lootTables.isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
		}
	}

	@Override
	public void registerSilkTouch(Block blockIn, Block drop) {
		this.knownBlocks.add(blockIn);
		super.registerSilkTouch(blockIn, drop);
	}

	@Override
	public void registerLootTable(Block blockIn, Function<Block, LootTable.Builder> builderFunction) {
		this.knownBlocks.add(blockIn);
		super.registerLootTable(blockIn, builderFunction);
	}

	@Override
	public void registerLootTable(Block blockIn, LootTable.Builder builder) {
		this.knownBlocks.add(blockIn);
		super.registerLootTable(blockIn, builder);
	}
}
