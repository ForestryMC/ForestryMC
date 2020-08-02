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
package forestry.apiculture.flowers;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.IBlockPosPredicate;
import forestry.api.genetics.flowers.IFlowerAcceptableRule;
import forestry.api.genetics.flowers.IFlowerGrowthHelper;
import forestry.api.genetics.flowers.IFlowerGrowthRule;
import forestry.api.genetics.flowers.IFlowerRegistry;
import forestry.core.utils.VectUtil;
import forestry.core.utils.datastructures.BlockStateSet;
import forestry.core.utils.datastructures.WeightedCollection;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

public final class FlowerRegistry implements IFlowerRegistry, IFlowerGrowthHelper {
    private final HashMultimap<String, IFlowerAcceptableRule> registeredRules;
    private final HashMultimap<String, Block> acceptableBlocks;
    private final Map<String, BlockStateSet> acceptableBlockStates;
    private final HashMultimap<String, ITag<Block>> acceptableBlockTags;
    private final HashMultimap<String, Flower> plantableFlowers;

    private final ArrayListMultimap<String, IFlowerGrowthRule> growthRules;
    private final Map<String, WeightedCollection<Flower>> chances;

    public FlowerRegistry() {
        this.registeredRules = HashMultimap.create();
        this.acceptableBlocks = HashMultimap.create();
        this.acceptableBlockStates = new HashMap<>();
        this.acceptableBlockTags = HashMultimap.create();
        this.plantableFlowers = HashMultimap.create();
        this.growthRules = ArrayListMultimap.create();
        this.chances = new HashMap<>();

        registerVanillaGrowthRules();
    }

    @Override
    public void registerAcceptableFlower(Block block, String... flowerTypes) {
        for (String flowerType : flowerTypes) {
            if (flowerType == null) {
                throw new NullPointerException("Tried to register flower with null type. " + block);
            }

            this.acceptableBlocks.get(flowerType).add(block);
        }
    }

    @Override
    public void registerAcceptableFlower(ITag<Block> block, String... flowerTypes) {
        for (String flowerType : flowerTypes) {
            if (flowerType == null) {
                throw new NullPointerException("Tried to register flower with null type. " + block);
            }

            this.acceptableBlockTags.get(flowerType).add(block);
        }
    }

    @Override
    public void registerAcceptableFlower(BlockState blockState, String... flowerTypes) {
        for (String flowerType : flowerTypes) {
            if (flowerType == null) {
                throw new NullPointerException("Tried to register flower with null type. " + blockState);
            }

            this.getAcceptedBlockStates(flowerType).add(blockState);
        }
    }

    @Override
    public void registerAcceptableFlowerRule(IFlowerAcceptableRule acceptableFlower, String... flowerTypes) {
        for (String flowerType : flowerTypes) {
            if (flowerType == null) {
                throw new NullPointerException("Tried to register flower with null type. " + acceptableFlower);
            }

            registeredRules.put(flowerType, acceptableFlower);
        }
    }

    @Override
    public void registerPlantableFlower(BlockState blockState, double weight, String... flowerTypes) {
        Preconditions.checkNotNull(blockState);
        Preconditions.checkArgument(blockState.getBlock() != Blocks.AIR, "Tried to register AIR as a flower. Bad idea.");

        if (weight <= 0.0) {
            weight = 0.0;
        }
        if (weight >= 1.0) {
            weight = 1.0;
        }

        Flower newFlower = new Flower(blockState, weight);

        for (String flowerType : flowerTypes) {
            Preconditions.checkNotNull(flowerType, "Tried to register flower with null type. " + blockState);

            Set<Flower> flowers = this.plantableFlowers.get(flowerType);
            flowers.add(newFlower);

            Set<BlockState> blocks = this.getAcceptedBlockStates(flowerType);
            blocks.add(blockState);

            this.chances.remove(flowerType);
        }
    }

    private static Vector3i getArea(IGenome genome, IBeeModifier beeModifier) {
        Vector3i genomeTerritory = genome.getActiveValue(BeeChromosomes.TERRITORY);
        float housingModifier = beeModifier.getTerritoryModifier(genome, 1f);
        return VectUtil.scale(genomeTerritory, housingModifier * 3.0f);
    }

    @Override
    public Iterator<BlockPos.Mutable> getAreaIterator(IBeeHousing beeHousing, IBee bee) {
        IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(beeHousing);
        Vector3i area = getArea(bee.getGenome(), beeModifier);
        BlockPos minPos = beeHousing.getCoordinates().add(-area.getX() / 2, -area.getY() / 2, -area.getZ() / 2);
        BlockPos maxPos = minPos.add(area);
        World world = beeHousing.getWorldObj();
        return VectUtil.getAllInBoxFromCenterMutable(world, minPos, beeHousing.getCoordinates(), maxPos);
    }

    private Set<BlockState> getAcceptedBlockStates(String flowerType) {
        return this.acceptableBlockStates.computeIfAbsent(flowerType, k -> new BlockStateSet());
    }

    @Override
    public IBlockPosPredicate createAcceptedFlowerPredicate(String flowerType) {
        Set<IFlowerAcceptableRule> acceptableRules = this.registeredRules.get(flowerType);
        Set<BlockState> acceptedBlockStates = this.getAcceptedBlockStates(flowerType);
        Set<Block> acceptedBlocks = this.acceptableBlocks.get(flowerType);
        Set<ITag<Block>> acceptedBlockTags = this.acceptableBlockTags.get(flowerType);

        return new AcceptedFlowerPredicate(flowerType, acceptableRules, acceptedBlocks, acceptedBlockStates, acceptedBlockTags);
    }

    private static boolean isAcceptedFlower(BlockState blockState, Set<Block> acceptedBlocks, Set<BlockState> acceptedBlockStates, Set<ITag<Block>> acceptedBlockTags) {
        Block block = blockState.getBlock();
        return acceptedBlocks.contains(block) || acceptedBlockStates.contains(blockState) || acceptedBlockTags.stream().anyMatch(blockTag -> blockTag.contains(block));
    }

    @Override
    public boolean growFlower(String flowerType, World world, IIndividual individual, BlockPos pos, Collection<BlockState> potentialFlowers) {
        if (!this.growthRules.containsKey(flowerType)) {
            return false;
        }

        List<IFlowerGrowthRule> growthRules = this.growthRules.get(flowerType);
        Collections.shuffle(growthRules);
        for (IFlowerGrowthRule rule : growthRules) {
            if (rule.growFlower(this, flowerType, (ServerWorld) world, pos, potentialFlowers)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void registerGrowthRule(IFlowerGrowthRule rule, String... flowerTypes) {
        Preconditions.checkNotNull(rule);

        for (String flowerType : flowerTypes) {
            this.growthRules.get(flowerType).add(rule);
        }
    }

    @Nullable
    public Flower getRandomPlantableFlower(String flowerType, Random rand) {
        WeightedCollection<Flower> chancesMap = getFlowerChances(flowerType);
        return chancesMap.getRandom(rand);
    }

    private WeightedCollection<Flower> getFlowerChances(String flowerType) {
        if (!this.chances.containsKey(flowerType)) {
            WeightedCollection<Flower> flowerChances = new WeightedCollection<>();
            for (Flower flower : this.plantableFlowers.get(flowerType)) {
                if (flower.isPlantable()) {
                    flowerChances.put(flower.getWeight(), flower);
                }
            }
            this.chances.put(flowerType, flowerChances);
        }
        return this.chances.get(flowerType);
    }

    private void registerVanillaGrowthRules() {
        registerGrowthRule(new GrowthRuleDirtGrass(), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
        registerGrowthRule(new GrowthRuleSnow(), FlowerManager.FlowerTypeSnow);
        registerGrowthRule(new GrowthRuleMycelium(), FlowerManager.FlowerTypeMushrooms);
        registerGrowthRule(new GrowthRuleNone(), FlowerManager.FlowerTypeEnd);
        registerGrowthRule(new GrowthRuleFertilize(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM), FlowerManager.FlowerTypeGourd);
        registerGrowthRule(new GrowthRuleFertilize(Blocks.WHEAT), FlowerManager.FlowerTypeWheat);
    }

    @Override
    public boolean plantRandomFlower(String flowerType, World world, BlockPos pos, Collection<BlockState> potentialFlowers) {
        WeightedCollection<Flower> chances = getFlowerChances(flowerType);
        WeightedCollection<BlockState> potentialChances = new WeightedCollection<>();

        for (Map.Entry<Double, Flower> entry : chances.entrySet()) {
            Flower flower = entry.getValue();
            BlockState blockState = flower.getBlockState();
            if (potentialFlowers.contains(blockState)) {
                potentialChances.put(entry.getKey(), blockState);
            }
        }

        final BlockState blockState = potentialChances.getRandom(world.rand);
        return blockState != null && world.setBlockState(pos, blockState);
    }

    private static class AcceptedFlowerPredicate implements IBlockPosPredicate {
        private final String flowerType;
        private final Set<IFlowerAcceptableRule> acceptableRules;
        private final Set<Block> acceptedBlocks;
        private final Set<BlockState> acceptedBlockStates;
        private final Set<ITag<Block>> acceptedBlockTags;

        public AcceptedFlowerPredicate(String flowerType, Set<IFlowerAcceptableRule> acceptableRules, Set<Block> acceptedBlocks, Set<BlockState> acceptedBlockStates, Set<ITag<Block>> acceptedBlockTags) {
            this.flowerType = flowerType;
            this.acceptableRules = acceptableRules;
            this.acceptedBlocks = acceptedBlocks;
            this.acceptedBlockStates = acceptedBlockStates;
            this.acceptedBlockTags = acceptedBlockTags;
        }

        @Override
        public boolean test(World world, BlockPos blockPos) {
            if (world.isBlockLoaded(blockPos)) {
                BlockState blockState = world.getBlockState(blockPos);
                if (!blockState.getBlock().isAir(blockState, world, blockPos)) {
                    for (IFlowerAcceptableRule acceptableRule : acceptableRules) {
                        if (acceptableRule.isAcceptableFlower(blockState, world, blockPos, flowerType)) {
                            return true;
                        }
                    }

                    return isAcceptedFlower(blockState, acceptedBlocks, acceptedBlockStates, acceptedBlockTags);
                }
            }
            return false;
        }
    }
}
