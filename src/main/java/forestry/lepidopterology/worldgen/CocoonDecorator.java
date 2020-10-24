/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.lepidopterology.worldgen;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.Log;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.features.LepidopterologyBlocks;
import forestry.lepidopterology.tiles.TileCocoon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.*;

public class CocoonDecorator extends Feature<NoFeatureConfig> {
    public CocoonDecorator() {
        super(NoFeatureConfig.field_236558_a_);
    }

    @Override
    public boolean func_241855_a(
            ISeedReader seedReader,
            ChunkGenerator generator,
            Random rand,
            BlockPos pos,
            NoFeatureConfig config
    ) {
        ArrayList<IButterfly> butterflys = new ArrayList<IButterfly>(ButterflyManager.butterflyRoot.getIndividualTemplates());

        Collections.shuffle(butterflys, rand);
        for (IButterfly butterfly : butterflys) {
            if (genCocoon(seedReader, rand, pos, butterfly)) {
                return true;
            }
        }

        return false;
    }

    public static boolean genCocoon(ISeedReader world, Random rand, BlockPos pos, IButterfly butterfly) {
        if (butterfly.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getRarity()
            * ModuleLepidopterology.getGenerateCocoonsAmount() < rand.nextFloat() * 100.0f
        ) {
            return false;
        }

        Biome biome = world.getBiome(new BlockPos(pos.getX(), 0, pos.getZ()));

        Set<Biome.Category> speciesCategories = butterfly.getGenome()
                                                         .getActiveAllele(ButterflyChromosomes.SPECIES)
                                                         .getSpawnBiomes();

        boolean biomeTypesGood = false;
        for (Biome.Category category : speciesCategories) {
            if (category == biome.getCategory()) {
                biomeTypesGood = true;
            }
        }

        if (!biomeTypesGood) {
            return false;
        }

        for (int tries = 0; tries < 4; tries++) {
            int x = pos.getX() + rand.nextInt(16);
            int z = pos.getZ() + rand.nextInt(16);

            if (tryGenCocoon(world, x, z, butterfly)) {
                return true;
            }
        }

        return false;
    }

    private static boolean tryGenCocoon(ISeedReader world, int x, int z, IButterfly butterfly) {
        int y = getYForCocoon(world, x, z);
        if (y < 0) {
            return false;
        }

        if (!isValidLocation(world, new BlockPos(x, y, z))) {
            return false;
        }

        return setCocoon(world, new BlockPos(x, y, z), butterfly);
    }

    private static boolean setCocoon(ISeedReader world, BlockPos pos, IButterfly butterfly) {
        Block cocoonBlock = LepidopterologyBlocks.COCOON_SOLID.getBlock();
        boolean placed = world.setBlockState(pos, cocoonBlock.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
        if (!placed) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        if (cocoonBlock != state.getBlock()) {
            return false;
        }

        TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
        if (cocoon != null) {
            cocoon.setCaterpillar(butterfly);
        } else {
            return false;
        }

        cocoonBlock.onBlockAdded(state, world.getWorld(), pos, cocoonBlock.getDefaultState(), false);
        world.getWorld().markBlockRangeForRenderUpdate(pos, state, cocoonBlock.getDefaultState());

        if (Config.logCoconPlacement) {
            Log.info("Placed {} at {}", cocoonBlock.toString(), pos.getCoordinatesAsString());
        }

        return true;
    }

    private static int getYForCocoon(ISeedReader world, int x, int z) {
        int y = world.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(x, 0, z)).getY() - 1;
        BlockPos pos = new BlockPos(x, y, z);
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getMaterial() != Material.LEAVES) {
            return -1;
        }

        do {
            pos = pos.down();
            blockState = world.getBlockState(pos);
        } while (blockState.getMaterial() == Material.LEAVES);

        return y;
    }

    public static boolean isValidLocation(ISeedReader world, BlockPos pos) {
        BlockPos posAbove = pos.up();
        BlockState blockStateAbove = world.getBlockState(posAbove);
        Block blockAbove = blockStateAbove.getBlock();
        if (blockStateAbove.getMaterial() != Material.LEAVES) {
            return false;
        }

        BlockPos posBelow = pos.down();
        BlockState blockStateBelow = world.getBlockState(posBelow);
        return BlockUtil.canReplace(blockStateBelow, world, posBelow);
    }
}
