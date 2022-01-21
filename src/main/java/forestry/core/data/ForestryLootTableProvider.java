package forestry.core.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.resources.ResourceLocation;

import com.mojang.datafixers.util.Pair;

import forestry.core.utils.Log;

public class ForestryLootTableProvider implements DataProvider {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator dataGenerator;
	private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables =
			ImmutableList.of(Pair.of(ForestryBlockLootTables::new, LootContextParamSets.BLOCK), Pair.of(ForestryChestLootTables::new, LootContextParamSets.CHEST));

	public ForestryLootTableProvider(DataGenerator dataGeneratorIn) {
		this.dataGenerator = dataGeneratorIn;
	}

	/**
	 * Performs this provider's action.
	 */
	@Override
	public void run(HashCache cache) {
		Path path = this.dataGenerator.getOutputFolder();
		Map<ResourceLocation, LootTable> map = Maps.newHashMap();
		tables.forEach((entry) -> entry.getFirst().get().accept((location, builder) -> {
			if (map.put(location, builder.setParamSet(entry.getSecond()).build()) != null) {
				throw new IllegalStateException("Duplicate loot table " + location);
			}
		}));
		ValidationContext validationtracker = new ValidationContext(LootContextParamSets.ALL_PARAMS, (location) -> null, map::get);

		validate(map, validationtracker);

		Multimap<String, String> multimap = validationtracker.getProblems();
		if (!multimap.isEmpty()) {
			multimap.forEach((location, message) -> Log.warning("Found validation problem in " + location + ": " + message));
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		} else {
			map.forEach((location, table) -> {
				Path path1 = getPath(path, location);

				try {
					DataProvider.save(GSON, cache, LootTables.serialize(table), path1);
				} catch (IOException ioexception) {
					LOGGER.error("Couldn't save loot table {}", path1, ioexception);
				}

			});
		}
	}

	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext tracker) {
		/*for (ResourceLocation resourcelocation : Sets.difference(LootTables.all(), map.keySet())) {
			validationtracker.reportProblem("Missing built-in table: " + resourcelocation);
		}*/

		map.forEach((location, loot) -> LootTables.validate(tracker, location, loot));
	}

	private static Path getPath(Path path, ResourceLocation id) {
		return path.resolve("data/" + id.getNamespace() + "/loot_tables/" + id.getPath() + ".json");
	}

	/**
	 * Gets a name for this provider, to use in logging.
	 */
	@Override
	public String getName() {
		return "Forestry LootTables";
	}
}
