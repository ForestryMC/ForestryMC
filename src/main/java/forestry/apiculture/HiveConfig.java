package forestry.apiculture;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.BiomeDictionary;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.core.config.LocalizedConfiguration;

public class HiveConfig {
	private static final Map<IHiveRegistry.HiveType, HiveConfig> configs = new EnumMap<IHiveRegistry.HiveType, HiveConfig>(IHiveRegistry.HiveType.class);
	private static final String CATEGORY = "world.generate.beehives.blacklist";

	private final Set<BiomeDictionary.Type> blacklistedTypes = new HashSet<>();
	private final Set<Biome> blacklistedBiomes = new HashSet<>();

	public static void parse(LocalizedConfiguration config){
		config.addCategoryCommentLocalized(CATEGORY);
		for(IHiveRegistry.HiveType type : IHiveRegistry.HiveType.values()){
			String[] entries = config.get(CATEGORY, type.getName(), new String[0]).getStringList();
			configs.put(type, new HiveConfig(entries));
		}
	}

	public HiveConfig(String[] entries){
		for(String entry : entries){
			BiomeDictionary.Type type = BiomeDictionary.Type.getType(entry);
			Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(entry));
			if(type != null){
				blacklistedTypes.add(type);
			}else if(biome != null){
				blacklistedBiomes.add(biome);
			}
		}
	}

	public static boolean isBlacklisted(IHiveRegistry.HiveType type, Biome biome){
		HiveConfig config = configs.get(type);
		if(config == null){
			return false;
		}
		return config.isBlacklisted(biome);
	}

	private boolean isBlacklisted(Biome biome){
		if(blacklistedBiomes.contains(biome)){
			return true;
		}
		return BiomeDictionary.getTypes(biome).stream().anyMatch(blacklistedTypes::contains);
	}
}
