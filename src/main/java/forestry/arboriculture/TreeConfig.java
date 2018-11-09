package forestry.arboriculture;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.BiomeDictionary;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.utils.Log;

public class TreeConfig {
	public static final String CONFIG_CATEGORY_TREE = "trees";
	public static final String CONFIG_COMMENT =
		"This config can be used to customise the world generation for all trees that where added by forestry or\n" +
			"by an addon mod like extra trees.\n" +
			"\n" +
			"# The spawn rarity of the tree species in the world. [range: 0.0 ~ 1.0]\n" +
			"S:rarity=1.0\n" +
			"\n" +
			"# Dimension ids can be added to these lists to blacklist or whitelist them. \n" +
			"dimensions {\n" +
			"\tI:blacklist <\n" +
			"\t\t1\n" +
			"\t >\n" +
			"\tI:whitelist <\n" +
			"\t\t-1\n" +
			"\t >\n" +
			"}\n" +
			"\n" +
			"# Biome types or registry names can be added to these lists to blacklist them. \n" +
			"biomes {\n" +
			"\tblacklist {\n" +
			"\t\tS:names <\n" +
			"\t\t\tminecraft:plains\n" +
			"\t\t >\n" +
			"\t\tS:types <\n" +
			"\t\t\tforest\n" +
			"\t\t >\n" +
			"\t}\n" +
			"}";
	private static final Map<String, TreeConfig> configs = new HashMap<>();
	private static final TreeConfig GLOBAL = new TreeConfig("global", 1.0F);

	private final String treeName;
	private final float defaultRarity;
	private final Set<Integer> blacklistedDimensions = new HashSet<>();
	private final Set<Integer> whitelistedDimensions = new HashSet<>();
	private final Set<BiomeDictionary.Type> blacklistedBiomeTypes = new HashSet<>();
	private final Set<Biome> blacklistedBiomes = new HashSet<>();
	private float spawnRarity;

	public static void parse(LocalizedConfiguration config) {
		config.setCategoryComment(CONFIG_CATEGORY_TREE, CONFIG_COMMENT);
		config.setCategoryComment(CONFIG_CATEGORY_TREE + ".global", "All options defined in the global category are used for all trees.");
		GLOBAL.parseConfig(config);
		for (IAllele treeAllele : AlleleManager.alleleRegistry.getRegisteredAlleles(EnumTreeChromosome.SPECIES)) {
			if (!(treeAllele instanceof IAlleleTreeSpecies)) {
				continue;
			}
			IAlleleTreeSpecies treeSpecies = (IAlleleTreeSpecies) treeAllele;
			configs.put(treeSpecies.getUID(), new TreeConfig(treeSpecies.getUID(), treeSpecies.getRarity()).parseConfig(config));
		}
	}

	private TreeConfig(String treeName, float defaultRarity) {
		this.treeName = treeName;
		this.defaultRarity = defaultRarity;
		this.spawnRarity = defaultRarity;
	}

	private TreeConfig parseConfig(LocalizedConfiguration config) {
		for (int dimId : config.get(CONFIG_CATEGORY_TREE + "." + treeName + ".dimensions", "blacklist", new int[0]).getIntList()) {
			blacklistedDimensions.add(dimId);
		}
		for (int dimId : config.get(CONFIG_CATEGORY_TREE + "." + treeName + ".dimensions", "whitelist", new int[0]).getIntList()) {
			whitelistedDimensions.add(dimId);
		}
		for (String typeName : config.get(CONFIG_CATEGORY_TREE + "." + treeName + ".biomes.blacklist", "types", new String[0]).getStringList()) {
			blacklistedBiomeTypes.add(BiomeDictionary.Type.getType(typeName));
		}
		for (String biomeName : config.get(CONFIG_CATEGORY_TREE + "." + treeName + ".biomes.blacklist", "names", new String[0]).getStringList()) {
			Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName));
			if (biome != null) {
				blacklistedBiomes.add(biome);
			} else {
				Log.error("Failed to identify biome for the config property for the tree with the uid '" + treeName + "'. No biome is registered under the registry name '" + biomeName + "'.");
			}
		}
		spawnRarity = (float) config.get(CONFIG_CATEGORY_TREE + "." + treeName, "rarity", defaultRarity).setMinValue(0.0F).setMaxValue(1.0F).getDouble();
		return this;
	}

	public static void blacklistTreeDim(@Nullable String treeUID, int dimID) {
		TreeConfig treeConfig = configs.get(treeUID);
		if (treeUID == null) {
			treeConfig = GLOBAL;
		}
		treeConfig.blacklistedDimensions.add(dimID);
	}

	public static void whitelistTreeDim(@Nullable String treeUID, int dimID) {
		TreeConfig treeConfig = configs.get(treeUID);
		if (treeUID == null) {
			treeConfig = GLOBAL;
		}
		treeConfig.whitelistedDimensions.add(dimID);
	}

	public static boolean isValidDimension(@Nullable String treeUID, int dimID) {
		TreeConfig treeConfig = configs.get(treeUID);
		return GLOBAL.isValidDimension(dimID) && (treeConfig == null || treeConfig.isValidDimension(dimID));
	}

	private boolean isValidDimension(int dimID) { //blacklist has priority
		if (blacklistedDimensions.isEmpty() || !blacklistedDimensions.contains(dimID)) {
			return whitelistedDimensions.isEmpty() || whitelistedDimensions.contains(dimID);
		}
		return false;
	}

	public static boolean isValidBiome(@Nullable String treeUID, Biome biome) {
		TreeConfig treeConfig = configs.get(treeUID);
		return GLOBAL.isValidBiome(biome) && (treeConfig == null || treeConfig.isValidBiome(biome));
	}

	private boolean isValidBiome(Biome biome) {
		if (blacklistedBiomes.contains(biome)) {
			return false;
		}
		return BiomeDictionary.getTypes(biome).stream().noneMatch(blacklistedBiomeTypes::contains);
	}

	public static float getSpawnRarity(@Nullable String treeUID) {
		TreeConfig treeConfig = configs.get(treeUID);
		if (treeUID == null) {
			treeConfig = GLOBAL;
		}
		return treeConfig.spawnRarity;
	}
}

