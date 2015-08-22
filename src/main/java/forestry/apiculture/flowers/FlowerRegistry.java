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

import com.google.common.collect.ArrayListMultimap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;

public final class FlowerRegistry implements IFlowerRegistry {

	private final ArrayListMultimap<String, IFlower> registeredFlowers;
	private final ArrayListMultimap<String, IFlowerGrowthRule> growthRules;
	private final Map<String, TreeMap<Double, IFlower>> chances;

	private boolean hasDeprecatedFlowersImported;

	public FlowerRegistry() {
		this.registeredFlowers = ArrayListMultimap.create();
		this.growthRules = ArrayListMultimap.create();
		this.chances = new HashMap<String, TreeMap<Double, IFlower>>();

		this.hasDeprecatedFlowersImported = false;

		registerVanillaFlowers();
		registerVanillaGrowthRules();
	}

	@Override
	public void registerAcceptableFlower(Block block, int meta, String... flowerTypes) {
		registerFlower(block, meta, null, flowerTypes);
	}

	@Override
	public void registerPlantableFlower(Block block, int meta, double weight, String... flowerTypes) {
		registerFlower(block, meta, weight, flowerTypes);
	}

	private void registerFlower(Block block, int meta, Double weight, String... flowerTypes) {
		if (block == null) {
			return;
		}
		if (meta == Short.MAX_VALUE || meta == -1) {
			return;
		}
		if (weight == null || weight <= 0.0) {
			weight = 0.0;
		}
		if (weight >= 1.0) {
			weight = 1.0;
		}

		Flower newFlower = new Flower(block, meta, weight);
		Integer index;
		
		for (String flowerType : flowerTypes) {
			List<IFlower> flowers = this.registeredFlowers.get(flowerType);

			index = flowers.indexOf(newFlower);
			if (index == -1) {
				flowers.add(newFlower);
			} else if (flowers.get(index).getWeight() > newFlower.getWeight()) {
				flowers.get(index).setWeight(newFlower.getWeight());
			}

			if (this.chances.containsKey(flowerType)) {
				this.chances.remove(flowerType);
			}

			Collections.sort(this.registeredFlowers.get(flowerType));
		}
	}

	@Override
	public boolean isAcceptedFlower(String flowerType, World world, IIndividual individual, BlockPos pos) {
		internalInitialize();
		if (!this.registeredFlowers.containsKey(flowerType)) {
			return false;
		}

		IBlockState state = world.getBlockState(pos);
		Block block = world.getBlockState(pos).getBlock();
		int meta = block.getMetaFromState(state);

		if (world.isAirBlock(pos) || block.equals(Blocks.bedrock) || block.equals(Blocks.dirt) || block.equals(Blocks.grass)) {
			return false;
		}

		for (IFlower flower : this.registeredFlowers.get(flowerType)) {
			if (Block.isEqualTo(flower.getBlock(), block) && flower.getMeta() == meta) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean growFlower(String flowerType, World world, IIndividual individual, BlockPos pos) {
		internalInitialize();
		if (!this.growthRules.containsKey(flowerType)) {
			return false;
		}

		for (IFlowerGrowthRule rule : this.growthRules.get(flowerType)) {
			if (rule.growFlower(this, flowerType, world, individual, pos)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<IFlower> getAcceptableFlowers(String flowerType) {
		internalInitialize();

		return this.registeredFlowers.get(flowerType);
	}

	@Override
	public void registerGrowthRule(IFlowerGrowthRule rule, String... flowerTypes) {
		if (rule == null) {
			return;
		}

		for (String flowerType : flowerTypes) {
			this.growthRules.get(flowerType).add(rule);
		}
	}

	@Override
	public IFlower getRandomPlantableFlower(String flowerType, Random rand) {
		TreeMap<Double, IFlower> chancesMap = getChancesMap(flowerType);
		double maxKey = chancesMap.lastKey() + 1.0;
		return chancesMap.get(chancesMap.lowerKey(rand.nextDouble() * maxKey));
	}

	private TreeMap<Double, IFlower> getChancesMap(String flowerType) {
		if (!this.chances.containsKey(flowerType)) {
			TreeMap<Double, IFlower> flowerChances = new TreeMap<Double, IFlower>();
			double count = 0.0;
			for (IFlower flower : this.registeredFlowers.get(flowerType)) {
				if (flower.isPlantable()) {
					flowerChances.put(count, flower);
					count += flower.getWeight();
				}
			}
			this.chances.put(flowerType, flowerChances);
		}
		return this.chances.get(flowerType);
	}

	/*
	 * Method to support deprecated FlowerManager.plainFlowers
	 */
	@SuppressWarnings("deprecation")
	private void internalInitialize() {
		if (!hasDeprecatedFlowersImported) {
			for (ItemStack plainFlower : FlowerManager.plainFlowers) {
				Block flowerBlock = Block.getBlockFromItem(plainFlower.getItem());
				int meta = plainFlower.getItemDamage();
				registerPlantableFlower(flowerBlock, meta, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
			}

			hasDeprecatedFlowersImported = true;
		}
	}

	private void registerVanillaFlowers() {
		// Register plantable plants
		for (int meta = 0; meta <= 8; meta++) {
			registerPlantableFlower(Blocks.red_flower, meta, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		}

		registerPlantableFlower(Blocks.yellow_flower, 0, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerPlantableFlower(Blocks.brown_mushroom, 0, 1.0, FlowerManager.FlowerTypeMushrooms);
		registerPlantableFlower(Blocks.red_mushroom, 0, 1.0, FlowerManager.FlowerTypeMushrooms);
		registerPlantableFlower(Blocks.cactus, 0, 1.0, FlowerManager.FlowerTypeCacti);

		// Register acceptable plants
		registerAcceptableFlower(Blocks.flower_pot, 1, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(Blocks.flower_pot, 2, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(Blocks.flower_pot, 7, FlowerManager.FlowerTypeMushrooms);
		registerAcceptableFlower(Blocks.flower_pot, 8, FlowerManager.FlowerTypeMushrooms);
		registerAcceptableFlower(Blocks.flower_pot, 9, FlowerManager.FlowerTypeCacti);
		registerAcceptableFlower(Blocks.flower_pot, 11, FlowerManager.FlowerTypeJungle);

		registerAcceptableFlower(Blocks.dragon_egg, 0, FlowerManager.FlowerTypeEnd);
		registerAcceptableFlower(Blocks.vine, 0, FlowerManager.FlowerTypeJungle);
		registerAcceptableFlower(Blocks.tallgrass, 2, FlowerManager.FlowerTypeJungle);
		registerAcceptableFlower(Blocks.wheat, 7, FlowerManager.FlowerTypeWheat);
		registerAcceptableFlower(Blocks.pumpkin_stem, 0, FlowerManager.FlowerTypeGourd);
		registerAcceptableFlower(Blocks.melon_stem, 0, FlowerManager.FlowerTypeGourd);
		registerAcceptableFlower(Blocks.nether_wart, 0, FlowerManager.FlowerTypeNether);

		registerAcceptableFlower(Blocks.double_plant, 0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(Blocks.double_plant, 1, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(Blocks.double_plant, 4, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(Blocks.double_plant, 5, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
	}

	private void registerVanillaGrowthRules() {
		registerGrowthRule(new VanillaDirtGrassGrowthRule(), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerGrowthRule(new VanillaSnowGrowthRule(), FlowerManager.FlowerTypeSnow);
		registerGrowthRule(new VanillaFlowerPotGrowthRule(), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow, FlowerManager.FlowerTypeMushrooms, FlowerManager.FlowerTypeCacti,
				FlowerManager.FlowerTypeJungle);
		registerGrowthRule(new VanillaMyceliumGrowthRule(), FlowerManager.FlowerTypeMushrooms);
		registerGrowthRule(new VanillaDefaultGrowthRule(), FlowerManager.FlowerTypeEnd);
		registerGrowthRule(new VanillaFertilizeGrowthRule(Blocks.melon_stem, Blocks.pumpkin_stem), FlowerManager.FlowerTypeGourd);
		registerGrowthRule(new VanillaFertilizeGrowthRule(Blocks.wheat), FlowerManager.FlowerTypeWheat);
	}
}
