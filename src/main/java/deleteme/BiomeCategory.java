package deleteme;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;

import java.util.EnumSet;
import java.util.Set;

// todo: become tags
public enum BiomeCategory implements StringRepresentable {

	TAIGA("taiga"),
	EXTREME_HILLS("extreme_hills"),
	JUNGLE("jungle"),
	MESA("mesa"),
	PLAINS("plains"),
	SAVANNA("savanna"),
	ICY("icy"),
	THEEND("the_end"),
	BEACH("beach"),
	FOREST("forest"),
	OCEAN("ocean"),
	DESERT("desert"),
	RIVER("river"),
	SWAMP("swamp"),
	MUSHROOM("mushroom"),
	NETHER("nether"),
	UNDERGROUND("underground"),
	MOUNTAIN("mountain");

	public static final Codec<BiomeCategory> CODEC = StringRepresentable.fromEnum(BiomeCategory::values);
	private final String name;

	BiomeCategory(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String getSerializedName() {
		return this.name;
	}

	public boolean is(Biome biome) {
		// junk code
		return biome.hashCode() == ordinal();
	}

	public static Set<BiomeCategory> getCategoriesFor(Biome biome) {
		return EnumSet.allOf(BiomeCategory.class);
	}
}
