package forestry.core.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.event.RegistryEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod.EventBusSubscriber
public class MigrationHelper {
	private static Map<String, String> blockRemappings = new HashMap<>();
	private static Map<String, String> itemRemappings = new HashMap<>();

	private static Set<String> ignoredMappings = new HashSet<>();

	static {
		ignoredMappings.add("greenhouse.sprinkler");

		itemRemappings.put("greenhouse.plain", "greenhouse");
		blockRemappings.put("greenhouse.plain", "greenhouse");
		itemRemappings.put("greenhouse.gearbox", "greenhouse");
		blockRemappings.put("greenhouse.gearbox", "greenhouse");
		itemRemappings.put("greenhouse.control", "greenhouse");
		blockRemappings.put("greenhouse.control", "greenhouse");
		itemRemappings.put("greenhouse.fan", "climatiser.fan");
		blockRemappings.put("greenhouse.fan", "climatiser.fan");
		blockRemappings.put("greenhouse.heater", "climatiser.heater");
		itemRemappings.put("greenhouse.heater", "climatiser.heater");
		blockRemappings.put("greenhouse.dehumidifier", "climatiser.dehumidifier");
		itemRemappings.put("greenhouse.dehumidifier", "climatiser.dehumidifier");
		blockRemappings.put("greenhouse.humidifier", "climatiser.humidifier");
		itemRemappings.put("greenhouse.humidifier", "climatiser.humidifier");
		itemRemappings.put("greenhouse.dryer", "climatiser.dehumidifier");
		blockRemappings.put("greenhouse.dryer", "climatiser.dehumidifier");
		itemRemappings.put("pile_dirt", "loam");
		blockRemappings.put("pile_dirt", "loam");
		itemRemappings.put("pile_wood", "wood_pile");
		blockRemappings.put("pile_wood", "wood_pile");
		blockRemappings.put("pile_ash", "ash_block");
	}

	public static Pattern underscores = Pattern.compile("_");

	public static void addBlockName(String blockName) {
		add(blockName, blockRemappings);
	}

	public static void addItemName(String itemName) {
		add(itemName, itemRemappings);
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

			String resourcePath = resourceLocation.getResourcePath();
			if (ignoredMappings.contains(resourcePath)) {
				missingMapping.ignore();
			} else if (blockRemappings.containsKey(resourcePath)) {
				ResourceLocation remappedResourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), blockRemappings.get(resourcePath));
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

			String resourcePath = resourceLocation.getResourcePath();
			if (ignoredMappings.contains(resourcePath)) {
				missingMapping.ignore();
			} else if (itemRemappings.containsKey(resourcePath)) {
				ResourceLocation remappedResourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), itemRemappings.get(resourcePath));
				if (ForgeRegistries.ITEMS.containsKey(remappedResourceLocation)) {
					Item remappedItem = ForgeRegistries.ITEMS.getValue(remappedResourceLocation);
					if (remappedItem != null && remappedItem != Items.AIR) {
						missingMapping.remap(remappedItem);
					}
				}
			}
		}
	}

	private MigrationHelper() {
	}
}
