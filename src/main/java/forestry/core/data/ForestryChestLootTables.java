package forestry.core.data;

import java.util.function.BiConsumer;

import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;

public class ForestryChestLootTables extends ChestLootTables {

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {

	}
}
