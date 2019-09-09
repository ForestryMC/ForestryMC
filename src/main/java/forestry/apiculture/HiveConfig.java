package forestry.apiculture;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.core.config.LocalizedConfiguration;

public class HiveConfig {
	private static final Map<IHiveRegistry.HiveType, HiveConfig> configs = new EnumMap<>(IHiveRegistry.HiveType.class);
	private static final String CATEGORY = "world.generate.beehives.blacklist";

	private final Set<BiomeDictionary.Type> blacklistedTypes = new HashSet<>();
	private final Set<Biome> blacklistedBiomes = new HashSet<>();

	private static final Set<ResourceLocation> blacklistedDims = new HashSet<>();

	private static final Set<ResourceLocation> whitelistedDims = new HashSet<>();

	@Nullable
	private static HiveConfig GLOBAL;

	public static void parse(LocalizedConfiguration config) {
		config.addCategoryCommentLocalized(CATEGORY);
		for (String dimId : config.get("world.generate.beehives", "dimBlacklist", new String[0]).getStringList()) {
			blacklistedDims.add(new ResourceLocation(dimId));
		}
		for (String dimId : config.get("world.generate.beehives", "dimWhitelist", new String[0]).getStringList()) {
			whitelistedDims.add(new ResourceLocation(dimId));
		}
		for (IHiveRegistry.HiveType type : IHiveRegistry.HiveType.values()) {
			String[] entries = config.get(CATEGORY, type.getName(), new String[0]).getStringList();
			configs.put(type, new HiveConfig(entries));
		}
		String[] globalEntries = config.get(CATEGORY, "global", new String[0]).getStringList();
		GLOBAL = new HiveConfig(globalEntries);
	}

	public HiveConfig(String[] entries) {
		for (String entry : entries) {
			BiomeDictionary.Type type = BiomeDictionary.Type.getType(entry);
			Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(entry));
			if (type != null) {
				blacklistedTypes.add(type);
			} else if (biome != null) {
				blacklistedBiomes.add(biome);
			}
		}
	}

	public static boolean isBlacklisted(IHiveRegistry.HiveType type, Biome biome) {
		if (GLOBAL != null && GLOBAL.isBlacklisted(biome)) {
			return true;
		}
		HiveConfig config = configs.get(type);
		if (config == null) {
			return false;
		}
		return config.isBlacklisted(biome);
	}

	private boolean isBlacklisted(Biome biome) {
		if (GLOBAL != null && this != GLOBAL && GLOBAL.isBlacklisted(biome)) {
			return true;
		}
		if (blacklistedBiomes.contains(biome)) {
			return true;
		}
		return BiomeDictionary.getTypes(biome).stream().anyMatch(blacklistedTypes::contains);
	}

	public static boolean isDimAllowed(ResourceLocation dimId) {        //blacklist has priority
		if (blacklistedDims.isEmpty() || !blacklistedDims.contains(dimId)) {
			return whitelistedDims.isEmpty() || whitelistedDims.contains(dimId);
		}
		return false;
	}

	public static void addBlacklistedDim(ResourceLocation dimId) {
		blacklistedDims.add(dimId);
	}

	public static void addWhitelistedDim(ResourceLocation dimId) {
		whitelistedDims.add(dimId);
	}
}
