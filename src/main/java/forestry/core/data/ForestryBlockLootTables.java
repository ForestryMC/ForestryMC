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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;

import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public class ForestryBlockLootTables extends BlockLootTables {
	private Set<Block> knownBlocks = new HashSet<>();

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			BlockRegistryArboriculture registry = ModuleArboriculture.getBlocks();
			for (Map.Entry<TreeDefinition, BlockDecorativeLeaves> leaves : registry.leavesDecorative.entrySet()) {
				this.registerLootTable(leaves.getValue(), (block) -> func_218540_a(block, Blocks.AIR, field_218579_g));
			}
		}

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
