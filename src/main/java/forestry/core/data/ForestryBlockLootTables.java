package forestry.core.data;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.functions.ApplyBonus;
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
				this.registerLootTable(leaves, (block) -> func_218540_a(block, Blocks.AIR, field_218579_g));
			}
		}
		registerLootTable(CoreBlocks.PEAT.block(), (block) -> new LootTable.Builder().addLootPool(new LootPool.Builder().addEntry(ItemLootEntry.builder(Blocks.DIRT))).addLootPool(new LootPool.Builder().acceptFunction(SetCount.func_215932_a(ConstantRange.of(2))).addEntry(ItemLootEntry.builder(CoreItems.PEAT.item()))));
		func_218564_a(CoreBlocks.HUMUS.block(), Blocks.DIRT);

		func_218492_c(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.TIN).block());
		func_218492_c(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.COPPER).block());
		registerLootTable(CoreBlocks.RESOURCE_ORE.get(EnumResourceType.APATITE).block(), (block) -> func_218519_a(block, func_218552_a(block, ItemLootEntry.builder(CoreItems.APATITE.item()).acceptFunction(SetCount.func_215932_a(RandomValueRange.func_215837_a(2, 7))).acceptFunction(ApplyBonus.func_215865_a(Enchantments.FORTUNE, 2)))));

		Set<ResourceLocation> visited = Sets.newHashSet();

		for (Block block : this.knownBlocks) {
			ResourceLocation lootTable = block.getLootTable();
			if (lootTable != LootTables.EMPTY && visited.add(lootTable)) {
				LootTable.Builder builder = this.field_218581_i.remove(lootTable);
				if (builder == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", lootTable, block.getRegistryName()));
				}

				consumer.accept(lootTable, builder);
			}
		}

		if (!this.field_218581_i.isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + this.field_218581_i.keySet());
		}
	}

	@Override
	public void func_218564_a(Block blockIn, Block droppedBlockIn) {
		this.knownBlocks.add(blockIn);
		super.func_218564_a(blockIn, droppedBlockIn);
	}

	@Override
	public void func_218492_c(Block block) {
		this.knownBlocks.add(block);
		super.func_218492_c(block);
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
