package forestry.core.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MigrationHelper {
	private static Map<String, String> blockRemappings = new HashMap<>();
	private static Map<String, String> itemRemappings = new HashMap<>();

	private static Set<String> ignoredMappings = new HashSet<>();
	static {
		ignoredMappings.add("greenhouse.sprinkler");

		itemRemappings.put("greenhouse.dryer", "greenhouse.dehumidifier");
		blockRemappings.put("greenhouse.dryer", "greenhouse.dehumidifier");
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

	public static void onMissingMappings(FMLMissingMappingsEvent event) {
		for (FMLMissingMappingsEvent.MissingMapping missingMapping : event.get()) {
			ResourceLocation resourceLocation = missingMapping.resourceLocation;

			String resourcePath = resourceLocation.getResourcePath();
			if (ignoredMappings.contains(resourcePath)) {
				missingMapping.ignore();
			} else {
				switch (missingMapping.type) {
					case BLOCK:
						if (blockRemappings.containsKey(resourcePath)) {
							ResourceLocation remappedResourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), blockRemappings.get(resourcePath));
							if (ForgeRegistries.BLOCKS.containsKey(remappedResourceLocation)) {
								Block remappedBlock = ForgeRegistries.BLOCKS.getValue(remappedResourceLocation);
								missingMapping.remap(remappedBlock);
							}
						}
						break;
					case ITEM:
						if (itemRemappings.containsKey(resourcePath)) {
							ResourceLocation remappedResourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), itemRemappings.get(resourcePath));
							if (ForgeRegistries.ITEMS.containsKey(remappedResourceLocation)) {
								Item remappedItem = ForgeRegistries.ITEMS.getValue(remappedResourceLocation);
								missingMapping.remap(remappedItem);
							}
						}
						break;
				}
			}
		}
	}

	private MigrationHelper() {}
}
