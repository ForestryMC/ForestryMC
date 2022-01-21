package forestry.core.worldgen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.gen.feature.structure.*;

import java.util.List;

import net.minecraft.data.worldgen.DesertVillagePools;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.data.worldgen.SavannaVillagePools;
import net.minecraft.data.worldgen.SnowyVillagePools;
import net.minecraft.data.worldgen.TaigaVillagePools;

public class VillagerJigsaw {
	public static void init()
	{
		PlainVillagePools.bootstrap();
		SnowyVillagePools.bootstrap();
		SavannaVillagePools.bootstrap();
		DesertVillagePools.bootstrap();
		TaigaVillagePools.bootstrap();

		addVillagerHouse("apiarist", "plains", 6);
		addVillagerHouse("apiarist", "snowy", 4);
		addVillagerHouse("apiarist", "savanna", 6);
		addVillagerHouse("apiarist", "desert", 5);
		addVillagerHouse("apiarist", "taiga", 7);
	}

	private static void addVillagerHouse(String type, String biome, int weight) {
		addToJigsawPattern(new ResourceLocation("village/" + biome + "/houses"), StructurePoolElement.legacy("forestry" + ":village/" + type + "_house_" + biome + "_1").apply(StructureTemplatePool.Projection.RIGID), weight);
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
	public static void addToJigsawPattern(ResourceLocation toAdd, StructurePoolElement newPiece, int weight) {
		StructureTemplatePool oldPool = (StructureTemplatePool)BuiltinRegistries.TEMPLATE_POOL.get(toAdd);
		if (oldPool != null) {
			oldPool.rawTemplates.add(Pair.of(newPiece, weight));
			List<StructurePoolElement> jigsawPieces = oldPool.templates;

			for(int i = 0; i < weight; ++i) {
				jigsawPieces.add(newPiece);
			}
		}
	}
}
