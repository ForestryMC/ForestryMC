package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;

import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.event.RegistryEvent;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import forestry.core.config.Constants;
import forestry.core.tiles.TileEntityDataFixable;

@Mod.EventBusSubscriber
public class MigrationHelper {
	private static Map<String, String> blockRemappings = new HashMap<>();
	private static Map<String, String> itemRemappings = new HashMap<>();
	private static Map<String, String> tileRemappings = new HashMap<>();

	private static Set<String> ignoredMappings = new HashSet<>();

	static {
		//Greenhouse
		ignoredMappings.add("greenhouse.sprinkler");

		itemRemappings.put("greenhouse.plain", "greenhouse");
		blockRemappings.put("greenhouse.plain", "greenhouse");
		itemRemappings.put("greenhouse.gearbox", "greenhouse");
		blockRemappings.put("greenhouse.gearbox", "greenhouse");
		itemRemappings.put("greenhouse.control", "greenhouse");
		blockRemappings.put("greenhouse.control", "greenhouse");
		itemRemappings.put("greenhouse.fan", "climatiser");
		blockRemappings.put("greenhouse.fan", "climatiser");
		blockRemappings.put("greenhouse.heater", "climatiser");
		itemRemappings.put("greenhouse.heater", "climatiser");
		blockRemappings.put("greenhouse.dehumidifier", "climatiser");
		itemRemappings.put("greenhouse.dehumidifier", "climatiser");
		blockRemappings.put("greenhouse.humidifier", "climatiser");
		itemRemappings.put("greenhouse.humidifier", "climatiser");
		itemRemappings.put("greenhouse.dryer", "climatiser");
		blockRemappings.put("greenhouse.dryer", "climatiser");
		//Arboriculture
		itemRemappings.put("pile_dirt", "loam");
		blockRemappings.put("pile_dirt", "loam");
		itemRemappings.put("pile_wood", "wood_pile");
		blockRemappings.put("pile_wood", "wood_pile");
		blockRemappings.put("pile_ash", "ash_block_0");
		blockRemappings.put("ash_block", "ash_block_0");

		//Apiculture
		addTileRemappingName("Alveary", "alveary_plain");
		addTileRemappingName("Swarm", "hive_wild");
		addTileRemappingName("AlvearySwarmer", "alveary_swarmer");
		addTileRemappingName("AlvearyHeater", "alveary_heater");
		addTileRemappingName("AlvearyFan", "alveary_fan");
		addTileRemappingName("AlvearyHygro", "alveary_hygro");
		addTileRemappingName("AlvearyStabiliser", "alveary_stabiliser");
		addTileRemappingName("AlvearySieve", "alveary_sieve");
		addTileRemappingName("Candle", "candle");
		//Arboriculture
		addTileRemappingName("Sapling", "sapling");
		addTileRemappingName("Leaves", "leaves");
		addTileRemappingName("Pods", "pods");
		//Farming
		addTileRemappingName("Farm", "farm");
		addTileRemappingName("FarmGearbox", "farm_gearbox");
		addTileRemappingName("FarmHatch", "farm_hatch");
		addTileRemappingName("FarmValve", "farm_valve");
		addTileRemappingName("FarmControl", "farm_control");
		//Lepidopterology
		addTileRemappingName("Cocoon", "cocoon");
		//Sorting
		addTileRemappingName("GeneticFilter", "genetic_filter");

	}

	public static Pattern underscores = Pattern.compile("_");

	public static void registerFixable() {
		TileEntityDataFixable tileFixable = new TileEntityDataFixable();
		CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
		ModFixs modFixs = fixer.init(Constants.MOD_ID, tileFixable.getFixVersion());    //is there a current save format version?
		modFixs.registerFix(FixTypes.BLOCK_ENTITY, tileFixable);
	}

	public static void addBlockName(String blockName) {
		add(blockName, blockRemappings);
	}

	public static void addItemName(String itemName) {
		add(itemName, itemRemappings);
	}

	public static void addTileName(String tileName) {
		add(tileName, tileRemappings);
	}

	private static void add(String name, Map<String, String> remappings) {
		String nameWithoutUnderscores = underscores.matcher(name).replaceAll("");
		if (!name.equals(nameWithoutUnderscores) && !remappings.containsKey(nameWithoutUnderscores)) {
			remappings.put(nameWithoutUnderscores, name);
		}
	}

	@SubscribeEvent
	public static void onMissingBlockMappings(RegistryEvent.MissingMappings<Block> event) {
		for (RegistryEvent.MissingMappings.Mapping<Block> missingMapping : event.getMappings()) {
			ResourceLocation resourceLocation = missingMapping.key;

			String resourcePath = resourceLocation.getPath();
			if (ignoredMappings.contains(resourcePath)) {
				missingMapping.ignore();
			} else if (blockRemappings.containsKey(resourcePath)) {
				ResourceLocation remappedResourceLocation = new ResourceLocation(resourceLocation.getNamespace(), blockRemappings.get(resourcePath));
				if (ForgeRegistries.BLOCKS.containsKey(remappedResourceLocation)) {
					Block remappedBlock = ForgeRegistries.BLOCKS.getValue(remappedResourceLocation);
					if (remappedBlock != null && remappedBlock != Blocks.AIR) {
						missingMapping.remap(remappedBlock);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onMissingItemMappings(RegistryEvent.MissingMappings<Item> event) {
		for (RegistryEvent.MissingMappings.Mapping<Item> missingMapping : event.getMappings()) {
			ResourceLocation resourceLocation = missingMapping.key;

			String resourcePath = resourceLocation.getPath();
			if (ignoredMappings.contains(resourcePath)) {
				missingMapping.ignore();
			} else if (itemRemappings.containsKey(resourcePath)) {
				ResourceLocation remappedResourceLocation = new ResourceLocation(resourceLocation.getNamespace(), itemRemappings.get(resourcePath));
				if (ForgeRegistries.ITEMS.containsKey(remappedResourceLocation)) {
					Item remappedItem = ForgeRegistries.ITEMS.getValue(remappedResourceLocation);
					if (remappedItem != null && remappedItem != Items.AIR) {
						missingMapping.remap(remappedItem);
					}
				}
			}
		}
	}

	@Nullable
	public static String getRemappedTileName(String resourcePath) {
		if (tileRemappings.containsKey(resourcePath)) {
			return Constants.MOD_ID + ":" + tileRemappings.get(resourcePath);
		}
		return null;
	}

	private MigrationHelper() {
	}

	public static void addTileRemappingName(String oldName, String remappedName) {
		tileRemappings.put("forestry." + WordUtils.uncapitalize(oldName), remappedName);
	}
}
