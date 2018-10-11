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
	private static final Map<String, TreeConfig> configs = new HashMap<>();
	private static final TreeConfig GLOBAL = new TreeConfig("global");

	private final String treeName;
	private final Set<Integer> blacklistedDimensions = new HashSet<>();
	private final Set<Integer> whitelistedDimensions = new HashSet<>();
	private final Set<BiomeDictionary.Type> blacklistedBiomeTypes = new HashSet<>();
	private final Set<Biome> blacklistedBiomes = new HashSet<>();

	public static void parse(LocalizedConfiguration config) {
		GLOBAL.parseConfig(config);
		for(IAllele treeAllele : AlleleManager.alleleRegistry.getRegisteredAlleles(EnumTreeChromosome.SPECIES)){
			if(!(treeAllele instanceof IAlleleTreeSpecies)){
				continue;
			}
			IAlleleTreeSpecies treeSpecies = (IAlleleTreeSpecies) treeAllele;
			configs.put(treeSpecies.getUID(), new TreeConfig(treeSpecies.getUID()).parseConfig(config));
		}
	}

	private TreeConfig(String treeName){
		this.treeName = treeName;
	}

	private TreeConfig parseConfig(LocalizedConfiguration config){
		for (int dimId : config.get(CONFIG_CATEGORY_TREE + "." + treeName + ".dimensions", "blacklist", new int[0]).getIntList()) {
			blacklistedDimensions.add(dimId);
		}
		for (int dimId : config.get(CONFIG_CATEGORY_TREE + "." + treeName + ".dimensions", "whitelist", new int[0]).getIntList()) {
			whitelistedDimensions.add(dimId);
		}
		for(String typeName : config.get(CONFIG_CATEGORY_TREE + "." + treeName + ".biomes.blacklist", "types", new String[0]).getStringList()){
			blacklistedBiomeTypes.add(BiomeDictionary.Type.getType(typeName));
		}
		for(String biomeName : config.get(CONFIG_CATEGORY_TREE + "." + treeName + ".biomes.blacklist", "names", new String[0]).getStringList()){
			Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName));
			if(biome != null) {
				blacklistedBiomes.add(biome);
			}else{
				Log.error("Failed to identify biome for the config property for the tree with the uid '"+ treeName + "'. No biome is registered under the registry name '"+ biomeName + "'.");
			}
		}
		return this;
	}

	public static void blacklistTreeDim(@Nullable String treeUID, int dimID) {
		TreeConfig treeConfig = configs.get(treeUID);
		if(treeUID == null){
			treeConfig = GLOBAL;
		}
		treeConfig.blacklistedDimensions.add(dimID);
	}

	public static void whitelistTreeDim(@Nullable String treeUID, int dimID) {
		TreeConfig treeConfig = configs.get(treeUID);
		if(treeUID == null){
			treeConfig = GLOBAL;
		}
		treeConfig.whitelistedDimensions.add(dimID);
	}

	public static boolean isValidDimension(@Nullable String treeUID, int dimID) {
		TreeConfig treeConfig = configs.get(treeUID);
		return GLOBAL.isValidDimension(dimID) && (treeConfig == null || treeConfig.isValidDimension(dimID));
	}

	private boolean isValidDimension(int dimID){ //blacklist has priority
		if (blacklistedDimensions.isEmpty() || !blacklistedDimensions.contains(dimID)) {
			return whitelistedDimensions.isEmpty() || whitelistedDimensions.contains(dimID);
		}
		return false;
	}

	public static boolean isValidBiome(@Nullable String treeUID, Biome biome) {
		TreeConfig treeConfig = configs.get(treeUID);
		return GLOBAL.isValidBiome(biome) && (treeConfig == null || treeConfig.isValidBiome(biome));
	}

	private boolean isValidBiome(Biome biome){
		if (blacklistedBiomes.contains(biome)) {
			return false;
		}
		return BiomeDictionary.getTypes(biome).stream().noneMatch(blacklistedBiomeTypes::contains);
	}

}

