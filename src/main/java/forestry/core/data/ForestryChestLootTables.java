package forestry.core.data;

import java.util.function.BiConsumer;

import net.minecraft.data.loot.ChestLoot;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceLocation;

import forestry.core.config.Constants;

public class ForestryChestLootTables extends ChestLoot {

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/village_naturalist"), LootTable.lootTable());
		for (LootTableHelper.Entry entry : LootTableHelper.getInstance().entries.values()) {
			consumer.accept(entry.getLocation(), entry.builder);
		}
	}

}
