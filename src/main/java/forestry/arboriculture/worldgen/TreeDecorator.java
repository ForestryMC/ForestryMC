/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.arboriculture.worldgen;

import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.TreeConfig;
import forestry.arboriculture.commands.TreeGenHelper;
import forestry.core.config.Config;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.Log;
import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class TreeDecorator extends Feature<NoFeatureConfig> {
    public TreeDecorator() {
        super(NoFeatureConfig.field_236558_a_);
    }

    private static final List<IAlleleTreeSpecies> SPECIES = new ArrayList<>();
    private static final Map<ResourceLocation, Set<ITree>> biomeCache = new HashMap<>();

    @Override
    public boolean generate(
            ISeedReader seedReader,
            ChunkGenerator generator,
            Random rand,
            BlockPos pos,
            NoFeatureConfig config
    ) {
        float globalRarity = TreeConfig.getSpawnRarity(null);
        if (globalRarity <= 0.0F || !TreeConfig.isValidDimension(null, seedReader.getWorld().getDimensionKey())) {
            return false;
        }

        if (biomeCache.isEmpty()) {
            generateBiomeCache(seedReader, rand);
        }

        for (int tries = 0; tries < 4 + rand.nextInt(2); tries++) {
            int x = pos.getX() + rand.nextInt(16);
            int z = pos.getZ() + rand.nextInt(16);

            Biome biome = seedReader.getBiome(pos);
            ResourceLocation biomeName = seedReader.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(biome);
            Set<ITree> trees = biomeCache.computeIfAbsent(biomeName, k -> new HashSet<>());

            for (ITree tree : trees) {
                ResourceLocation treeUID = tree.getGenome().getPrimary().getRegistryName();
                if (!TreeConfig.isValidDimension(treeUID, seedReader.getWorld().getDimensionKey())) {
                    continue;
                }

                IAlleleTreeSpecies species = tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES);
                if (TreeConfig.getSpawnRarity(species.getRegistryName()) * globalRarity >= rand.nextFloat()) {
                    BlockPos validPos = getValidPos(seedReader, x, z, tree);
                    if (validPos == null) {
                        continue;
                    }

                    if (species.getGrowthProvider().canSpawn(tree, seedReader.getWorld(), validPos)) {
                        if (TreeGenHelper.generateTree(tree, seedReader, validPos)) {
                            if (Config.logTreePlacement) {
                                Log.info("Placed {} at {}", treeUID.toString(), pos.getCoordinatesAsString());
                            }

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Nullable
    private static BlockPos getValidPos(ISeedReader world, int x, int z, ITree tree) {
        // get to the ground
        final BlockPos topPos = world.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(x, 0, z));
        if (topPos.getY() == 0) {
            return null;
        }

        final BlockPos.Mutable pos = new BlockPos.Mutable(topPos.getX(), topPos.getY(), topPos.getZ());

        BlockState blockState = world.getBlockState(pos);
        while (BlockUtil.canReplace(blockState, world, pos)) {
            pos.move(Direction.DOWN);
            if (pos.getY() <= 0) {
                return null;
            }

            blockState = world.getBlockState(pos);
        }

        if (tree instanceof IPlantable && blockState.getBlock().canSustainPlant(
                blockState,
                world,
                pos,
                Direction.UP,
                (IPlantable) tree
        )) {
            return pos.up();
        }

        return null;
    }

    private static List<IAlleleTreeSpecies> getSpecies() {
        if (!SPECIES.isEmpty()) {
            return SPECIES;
        }

        for (IAllele allele : GeneticsAPI.apiInstance.getAlleleRegistry()
                                                     .getRegisteredAlleles(TreeChromosomes.SPECIES)
        ) {
            if (allele instanceof IAlleleTreeSpecies) {
                IAlleleTreeSpecies alleleTreeSpecies = (IAlleleTreeSpecies) allele;
                if (TreeConfig.getSpawnRarity(alleleTreeSpecies.getRegistryName()) > 0) {
                    SPECIES.add(alleleTreeSpecies);
                }
            }
        }

        return SPECIES;
    }

    private static void generateBiomeCache(ISeedReader world, Random rand) {
        for (IAlleleTreeSpecies species : getSpecies()) {
            IAllele[] template = TreeManager.treeRoot.getTemplate(species.getRegistryName().toString());
            IGenome genome = TreeManager.treeRoot.templateAsIndividual(template).getGenome();
            ITree tree = TreeManager.treeRoot.getTree(world.getWorld(), genome);
            ResourceLocation treeUID = genome.getPrimary().getRegistryName();
            IGrowthProvider growthProvider = species.getGrowthProvider();
            for (Biome biome : ForgeRegistries.BIOMES) {
                Set<ITree> trees = biomeCache.computeIfAbsent(WorldGenRegistries.BIOME.getKey(biome), k -> new HashSet<>());
                if (growthProvider.isBiomeValid(tree, biome) && TreeConfig.isValidBiome(treeUID, biome)) {
                    trees.add(tree);
                }
            }
        }
    }
}
