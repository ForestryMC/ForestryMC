package forestry.apiculture;

import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.core.config.LocalizedConfiguration;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class HiveConfig {
    private static final Map<IHiveRegistry.HiveType, HiveConfig> configs = new EnumMap<>(IHiveRegistry.HiveType.class);
    private static final String CATEGORY = "world.generate.beehives.blacklist";

    private final Set<Biome.Category> blacklistedTypes = new HashSet<>();
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
            String[] entries = config.get(CATEGORY, type.getString(), new String[0]).getStringList();
            configs.put(type, new HiveConfig(entries));
        }

        String[] globalEntries = config.get(CATEGORY, "global", new String[0]).getStringList();
        GLOBAL = new HiveConfig(globalEntries);
    }

    public HiveConfig(String[] entries) {
        for (String entry : entries) {
            Biome.Category category = Biome.Category.valueOf(entry);
            Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(entry));
            if (category != null) {
                blacklistedTypes.add(category);
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

        return Arrays.stream(Biome.Category.values()).anyMatch(blacklistedTypes::contains);
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
