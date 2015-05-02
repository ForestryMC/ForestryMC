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
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

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
	public void registerAcceptableFlower(Block block, String... flowerTypes) {
		registerFlower(block, OreDictionary.WILDCARD_VALUE, 0.0, flowerTypes);
	}

	@Override
	public void registerAcceptableFlower(Block block, int meta, String... flowerTypes) {
		registerFlower(block, meta, 0.0, flowerTypes);
	}

	@Override
	public void registerPlantableFlower(Block block, int meta, double weight, String... flowerTypes) {
		registerFlower(block, meta, weight, flowerTypes);
	}

	private void registerFlower(Block block, int meta, double weight, String... flowerTypes) {
		if (block == null) {
			return;
		}
		if (weight <= 0.0) {
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
	public boolean isAcceptedFlower(String flowerType, World world, IIndividual individual, int x, int y, int z) {
		internalInitialize();
		if (!this.registeredFlowers.containsKey(flowerType)) {
			return false;
		}

		Block block = world.getBlock(x, y, z);

		if (block.isAir(world, x, y, z) || block.equals(Blocks.bedrock) || block.equals(Blocks.dirt) || block.equals(Blocks.grass)) {
			return false;
		}

		int meta;

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityFlowerPot) {
			TileEntityFlowerPot tileFlowerPot = (TileEntityFlowerPot) tile;
			Item item = tileFlowerPot.getFlowerPotItem();
			block = Block.getBlockFromItem(item);
			meta = tileFlowerPot.getFlowerPotData();
		} else {
			meta = world.getBlockMetadata(x, y, z);
		}

		Flower flower = new Flower(block, meta, 0);
		List<IFlower> acceptedFlowers = this.registeredFlowers.get(flowerType);
		return acceptedFlowers.contains(flower);
	}

	@Override
	public boolean growFlower(String flowerType, World world, IIndividual individual, int x, int y, int z) {
		internalInitialize();
		if (!this.growthRules.containsKey(flowerType)) {
			return false;
		}

		for (IFlowerGrowthRule rule : this.growthRules.get(flowerType)) {
			if (rule.growFlower(this, flowerType, world, individual, x, y, z)) {
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
		// Register acceptable plants
		registerAcceptableFlower(Blocks.red_flower, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(Blocks.yellow_flower, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(Blocks.brown_mushroom, FlowerManager.FlowerTypeMushrooms);
		registerAcceptableFlower(Blocks.red_mushroom, FlowerManager.FlowerTypeMushrooms);
		registerAcceptableFlower(Blocks.cactus, FlowerManager.FlowerTypeCacti);

		registerAcceptableFlower(Blocks.dragon_egg, FlowerManager.FlowerTypeEnd);
		registerAcceptableFlower(Blocks.vine, FlowerManager.FlowerTypeJungle);
		registerAcceptableFlower(Blocks.tallgrass, FlowerManager.FlowerTypeJungle);
		registerAcceptableFlower(Blocks.wheat, FlowerManager.FlowerTypeWheat);
		registerAcceptableFlower(Blocks.pumpkin_stem, FlowerManager.FlowerTypeGourd);
		registerAcceptableFlower(Blocks.melon_stem, FlowerManager.FlowerTypeGourd);
		registerAcceptableFlower(Blocks.nether_wart, FlowerManager.FlowerTypeNether);

		registerAcceptableFlower(Blocks.double_plant, 0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(Blocks.double_plant, 1, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(Blocks.double_plant, 4, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(Blocks.double_plant, 5, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);

		// Register plantable plants
		for (int meta = 0; meta <= 8; meta++) {
			registerPlantableFlower(Blocks.red_flower, meta, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		}

		registerPlantableFlower(Blocks.yellow_flower, 0, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerPlantableFlower(Blocks.brown_mushroom, 0, 1.0, FlowerManager.FlowerTypeMushrooms);
		registerPlantableFlower(Blocks.red_mushroom, 0, 1.0, FlowerManager.FlowerTypeMushrooms);
		registerPlantableFlower(Blocks.cactus, 0, 1.0, FlowerManager.FlowerTypeCacti);

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
