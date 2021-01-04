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
package forestry.apiculture.worldgen;

import forestry.api.apiculture.hives.IHiveDescription;
import forestry.api.apiculture.hives.IHiveDrop;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.apiculture.HiveConfig;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Hive {

    private final IHiveDescription hiveDescription;
    private final List<IHiveDrop> drops = new ArrayList<>();

    public Hive(IHiveDescription hiveDescription) {
        this.hiveDescription = hiveDescription;
    }

    public BlockState getHiveBlockState() {
        return hiveDescription.getBlockState();
    }

    public void addDrops(List<IHiveDrop> drops) {
        this.drops.addAll(drops);
    }

    public List<IHiveDrop> getDrops() {
        return drops;
    }

    public float genChance() {
        return hiveDescription.getGenChance();
    }

    public void postGen(ISeedReader world, Random rand, BlockPos pos) {
        hiveDescription.postGen(world, rand, pos);
    }

    public boolean isGoodBiome(Biome biome) {
        return hiveDescription.isGoodBiome(biome);
    }

    public boolean isGoodHumidity(EnumHumidity humidity) {
        return hiveDescription.isGoodHumidity(humidity);
    }

    public boolean isGoodTemperature(EnumTemperature temperature) {
        return hiveDescription.isGoodTemperature(temperature);
    }

    public boolean isValidLocation(ISeedReader world, BlockPos pos) {
        if (!HiveConfig.isDimAllowed(world.getWorld().getDimensionKey().getLocation())) {
            return false;
        }

        return hiveDescription.getHiveGen().isValidLocation(world, pos);
    }

    public boolean canReplace(ISeedReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return hiveDescription.getHiveGen().canReplace(blockState, world, pos);
    }

    @Nullable
    public BlockPos getPosForHive(ISeedReader world, int x, int z) {
        return hiveDescription.getHiveGen().getPosForHive(world, x, z);
    }

    @Override
    public String toString() {
        return hiveDescription + " hive";
    }
}
