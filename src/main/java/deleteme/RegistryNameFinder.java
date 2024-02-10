package deleteme;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryNameFinder {

	public static ResourceLocation getRegistryName(Fluid o) {
		return ForgeRegistries.FLUIDS.getKey(o);
	}

	public static ResourceLocation getRegistryName(Block o) {
		return ForgeRegistries.BLOCKS.getKey(o);
	}

	public static ResourceLocation getRegistryName(Item o) {
		return ForgeRegistries.ITEMS.getKey(o);
	}

	public static ResourceLocation getRegistryName(ParticleType<?> o) {
		return ForgeRegistries.PARTICLE_TYPES.getKey(o);
	}
}
