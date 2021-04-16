package forestry.core.data;

import java.util.function.BiConsumer;

import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;

import forestry.core.config.Constants;

public class ForestryChestLootTables extends ChestLootTables {

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/village_naturalist"), LootTable.lootTable());
		for (LootTableHelper.Entry entry : LootTableHelper.getInstance().entries.values()) {
			consumer.accept(entry.getLocation(), entry.builder);
		}
	}

}
