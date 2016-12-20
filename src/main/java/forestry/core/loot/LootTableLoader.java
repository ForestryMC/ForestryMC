package forestry.core.loot;

import javax.annotation.Nullable;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.net.URL;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

import forestry.core.utils.Log;

/**
 * Copy of {@link LootTableManager} that can load Forestry's loot table additions.
 * This is a workaround so I can load loot table jsons that have pools to be added to vanilla's chests.
 * During {@link net.minecraftforge.event.LootTableLoadEvent} the world's lootTable is not set yet.
 */
public class LootTableLoader {
	private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();

	@Nullable
	public static LootTable loadBuiltinLootTable(ResourceLocation resource) {
		URL url = LootTableLoader.class.getResource("/assets/" + resource.getResourceDomain() + "/loot_tables/" + resource.getResourcePath() + ".json");

		if (url != null) {
			String s;

			try {
				s = Resources.toString(url, Charsets.UTF_8);
			} catch (IOException ioexception) {
				Log.warning("Couldn\'t load loot table " + resource + " from " + url, ioexception);
				return LootTable.EMPTY_LOOT_TABLE;
			}

			try {
				return net.minecraftforge.common.ForgeHooks.loadLootTable(GSON_INSTANCE, resource, s, false);
			} catch (JsonParseException jsonparseexception) {
				Log.error("Couldn\'t load loot table " + resource + " from " + url, jsonparseexception);
				return LootTable.EMPTY_LOOT_TABLE;
			}
		} else {
			return null;
		}
	}
}
