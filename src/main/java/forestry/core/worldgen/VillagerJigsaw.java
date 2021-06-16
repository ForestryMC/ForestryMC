package forestry.core.worldgen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.*;

import java.util.List;

public class VillagerJigsaw {
	public static void init()
	{
		PlainsVillagePools.bootstrap();
		SnowyVillagePools.bootstrap();
		SavannaVillagePools.bootstrap();
		DesertVillagePools.bootstrap();
		TaigaVillagePools.bootstrap();

		addVillagerHouse("apiarist", "plains", 2);
		addVillagerHouse("apiarist", "snowy", 4);
		addVillagerHouse("apiarist", "savanna", 6);
		addVillagerHouse("apiarist", "desert", 5);
		addVillagerHouse("apiarist", "taiga", 7);
	}

	private static void addVillagerHouse(String type, String biome, int weight) {
		addToJigsawPattern(new ResourceLocation("village/" + biome + "/houses"), JigsawPiece.single("forestry" + ":village/" + type + "_house_" + biome + "_1").apply(JigsawPattern.PlacementBehaviour.RIGID), weight);
	}

	/**
	 * Adds a new {@link JigsawPiece} to a pre-existing {@link JigsawPattern}.
	 *
	 * @param toAdd The {@link ResourceLocation} of the pattern to insert the new piece into.
	 * @param newPiece The {@link JigsawPiece} to insert into {@code toAdd}.
	 * @param weight The probability weight of {@code newPiece}.
	 *
	 * @author abigailfails / abnormals
	 */
	public static void addToJigsawPattern(ResourceLocation toAdd, JigsawPiece newPiece, int weight) {
		JigsawPattern oldPool = (JigsawPattern)WorldGenRegistries.TEMPLATE_POOL.get(toAdd);
		if (oldPool != null) {
			oldPool.rawTemplates.add(Pair.of(newPiece, weight));
			List<JigsawPiece> jigsawPieces = oldPool.templates;

			for(int i = 0; i < weight; ++i) {
				jigsawPieces.add(newPiece);
			}
		}

	}
}
