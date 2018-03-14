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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.IBlockPosPredicate;
import forestry.api.genetics.IFlowerAcceptableRule;
import forestry.api.genetics.IFlowerGrowthHelper;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;
import forestry.core.utils.BlockStateSet;
import forestry.core.utils.VectUtil;
import forestry.core.utils.WeightedCollection;

public final class FlowerRegistry implements IFlowerRegistry, IFlowerGrowthHelper {
	private final HashMultimap<String, IFlowerAcceptableRule> registeredRules;
	private final HashMultimap<String, Block> acceptableBlocks;
	private final Map<String, BlockStateSet> acceptableBlockStates;
	private final HashMultimap<String, Flower> plantableFlowers;

	private final ArrayListMultimap<String, IFlowerGrowthRule> growthRules;
	private final Map<String, WeightedCollection<Flower>> chances;

	public FlowerRegistry() {
		this.registeredRules = HashMultimap.create();
		this.acceptableBlocks = HashMultimap.create();
		this.acceptableBlockStates = new HashMap<>();
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
	public void registerAcceptableFlower(IBlockState blockState, String... flowerTypes) {
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
	public void registerPlantableFlower(IBlockState blockState, double weight, String... flowerTypes) {
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

			Set<IBlockState> blocks = this.getAcceptedBlockStates(flowerType);
			blocks.add(blockState);

			if (this.chances.containsKey(flowerType)) {
				this.chances.remove(flowerType);
			}
		}
	}

	private static Vec3i getArea(IBeeGenome genome, IBeeModifier beeModifier) {
		Vec3i genomeTerritory = genome.getTerritory();
		float housingModifier = beeModifier.getTerritoryModifier(genome, 1f);
		return VectUtil.scale(genomeTerritory, housingModifier * 3.0f);
	}

	@Override
	public List<BlockPos> getAcceptedFlowerCoordinates(IBeeHousing beeHousing, IBee bee, String flowerType, int maxFlowers) {
		List<BlockPos> flowerCoords = new ArrayList<>();

		World world = beeHousing.getWorldObj();
		IBlockPosPredicate acceptedFlowerPredicate = createAcceptedFlowerPredicate(flowerType);
		Iterator<BlockPos.MutableBlockPos> areaIterator = getAreaIterator(beeHousing, bee);
		while (areaIterator.hasNext() && flowerCoords.size() < maxFlowers) {
			BlockPos.MutableBlockPos pos = areaIterator.next();
			if (acceptedFlowerPredicate.test(world, pos)) {
				flowerCoords.add(pos.toImmutable());
			}
		}

		return flowerCoords;
	}

	@Override
	public Iterator<BlockPos.MutableBlockPos> getAreaIterator(IBeeHousing beeHousing, IBee bee) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(beeHousing);
		Vec3i area = getArea(bee.getGenome(), beeModifier);
		BlockPos minPos = beeHousing.getCoordinates().add(-area.getX() / 2, -area.getY() / 2, -area.getZ() / 2);
		BlockPos maxPos = minPos.add(area);
		World world = beeHousing.getWorldObj();
		return VectUtil.getAllInBoxFromCenterMutable(world, minPos, beeHousing.getCoordinates(), maxPos);
	}

	@Override
	@Deprecated
	public boolean isAcceptedFlower(String flowerType, World world, BlockPos pos) {
		if (!world.isBlockLoaded(pos)) {
			return true; // Avoid actually checking until the flower's position is loaded.
		}
		IBlockState blockState = world.getBlockState(pos);
		Set<IFlowerAcceptableRule> acceptedCustom = this.registeredRules.get(flowerType);
		for (IFlowerAcceptableRule acceptableFlower : acceptedCustom) {
			if (acceptableFlower.isAcceptableFlower(blockState, world, pos, flowerType)) {
				return true;
			}
		}

		Set<IBlockState> acceptedBlockStates = this.getAcceptedBlockStates(flowerType);
		Set<Block> acceptedBlocks = this.acceptableBlocks.get(flowerType);

		return isAcceptedFlower(blockState, acceptedBlocks, acceptedBlockStates);
	}

	private Set<IBlockState> getAcceptedBlockStates(String flowerType) {
		return this.acceptableBlockStates.computeIfAbsent(flowerType, k -> new BlockStateSet());
	}

	@Override
	public IBlockPosPredicate createAcceptedFlowerPredicate(String flowerType) {
		Set<IFlowerAcceptableRule> acceptableRules = this.registeredRules.get(flowerType);
		Set<IBlockState> acceptedBlockStates = this.getAcceptedBlockStates(flowerType);
		Set<Block> acceptedBlocks = this.acceptableBlocks.get(flowerType);

		return new AcceptedFlowerPredicate(flowerType, acceptableRules, acceptedBlocks, acceptedBlockStates);
	}

	private static boolean isAcceptedFlower(IBlockState blockState, Set<Block> acceptedBlocks, Set<IBlockState> acceptedBlockStates) {
		Block block = blockState.getBlock();
		return acceptedBlocks.contains(block) || acceptedBlockStates.contains(blockState);
	}

	@Override
	@Deprecated
	public boolean growFlower(String flowerType, World world, IIndividual individual, BlockPos pos) {
		if (!this.growthRules.containsKey(flowerType)) {
			return false;
		}

		for (IFlowerGrowthRule rule : this.growthRules.get(flowerType)) {
			if (rule.growFlower(this, flowerType, world, pos)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean growFlower(String flowerType, World world, IIndividual individual, BlockPos pos, Collection<IBlockState> potentialFlowers) {
		if (!this.growthRules.containsKey(flowerType)) {
			return false;
		}

		List<IFlowerGrowthRule> growthRules = this.growthRules.get(flowerType);
		Collections.shuffle(growthRules);
		for (IFlowerGrowthRule rule : growthRules) {
			if (rule.growFlower(this, flowerType, world, pos, potentialFlowers)) {
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
	public boolean plantRandomFlower(String flowerType, World world, BlockPos pos) {
		Flower flower = getRandomPlantableFlower(flowerType, world.rand);
		return flower != null && world.setBlockState(pos, flower.getBlockState());
	}

	@Override
	public boolean plantRandomFlower(String flowerType, World world, BlockPos pos, Collection<IBlockState> potentialFlowers) {
		WeightedCollection<Flower> chances = getFlowerChances(flowerType);
		WeightedCollection<IBlockState> potentialChances = new WeightedCollection<>();

		for (Map.Entry<Double, Flower> entry : chances.entrySet()) {
			Flower flower = entry.getValue();
			IBlockState blockState = flower.getBlockState();
			if (potentialFlowers.contains(blockState)) {
				potentialChances.put(entry.getKey(), blockState);
			}
		}

		final IBlockState blockState = potentialChances.getRandom(world.rand);
		return blockState != null && world.setBlockState(pos, blockState);
	}

	private static class AcceptedFlowerPredicate implements IBlockPosPredicate {
		private final String flowerType;
		private final Set<IFlowerAcceptableRule> acceptableRules;
		private final Set<Block> acceptedBlocks;
		private final Set<IBlockState> acceptedBlockStates;

		public AcceptedFlowerPredicate(String flowerType, Set<IFlowerAcceptableRule> acceptableRules, Set<Block> acceptedBlocks, Set<IBlockState> acceptedBlockStates) {
			this.flowerType = flowerType;
			this.acceptableRules = acceptableRules;
			this.acceptedBlocks = acceptedBlocks;
			this.acceptedBlockStates = acceptedBlockStates;
		}

		@Override
		public boolean test(World world, BlockPos blockPos) {
			if (world.isBlockLoaded(blockPos)) {
				IBlockState blockState = world.getBlockState(blockPos);
				blockState = blockState.getBlock().getActualState(blockState, world, blockPos);
				if (!blockState.getBlock().isAir(blockState, world, blockPos)) {
					for (IFlowerAcceptableRule acceptableRule : acceptableRules) {
						if (acceptableRule.isAcceptableFlower(blockState, world, blockPos, flowerType)) {
							return true;
						}
					}

					if (isAcceptedFlower(blockState, acceptedBlocks, acceptedBlockStates)) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
