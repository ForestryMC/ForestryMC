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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;
import forestry.core.utils.StackUtils;

public final class FlowerRegistry implements IFlowerRegistry {

	private final Map<String, List<Flower>> registeredFlowers;
	private final Map<String, List<IFlowerGrowthRule>> growthRules;
	private final Map<String, TreeMap<Double, Flower>> chances;

	private boolean hasDepricatedFlowersImported;

	public FlowerRegistry() {
		this.registeredFlowers = new HashMap<String, List<Flower>>();
		this.growthRules = new HashMap<String, List<IFlowerGrowthRule>>();
		this.chances =  new HashMap<String, TreeMap<Double,Flower>>();

		this.hasDepricatedFlowersImported = false;

		registerVanillaFlowers();
		registerVanillaGrowthRules();
	}

	@Override
	public void registerAcceptableFlower(ItemStack is, String... flowerTypes) {
		registerFlower(is, null, flowerTypes);
	}

	@Override
	public void registerPlantableFlower(ItemStack is, double weight, String... flowerTypes) {
		registerFlower(is, weight, flowerTypes);
	}

	private void registerFlower(ItemStack is, Double weight, String... flowerTypes) {
		if (is == null || is.getItem() == null)
			return;
		if (is.getItemDamage() == Short.MAX_VALUE || is.getItemDamage() == -1)
			return;
		if (weight == null || weight <= 0.0)
			weight = 0.0;
		if (weight >= 1.0)
			weight = 1.0;

		Flower newFlower = new Flower(is, weight);
		Integer index;
		
		for (String flowerType : flowerTypes) {
			if (!this.registeredFlowers.containsKey(flowerType)) {
				this.registeredFlowers.put(flowerType, new ArrayList<Flower>());
			}
			
			index = this.registeredFlowers.get(flowerType).indexOf(flowerType);
			if (index == -1) {
				this.registeredFlowers.get(flowerType).add(newFlower);
			}
			else if (this.registeredFlowers.get(flowerType).get(index).weight > newFlower.weight) {
				this.registeredFlowers.get(flowerType).get(index).weight = newFlower.weight;
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
		if (!this.registeredFlowers.containsKey(flowerType))
			return false;

		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack is = new ItemStack(b, 1, meta);

		for (Flower f : this.registeredFlowers.get(flowerType))
			if (StackUtils.isIdenticalItem(f.item, is))
				return true;

		return false;
	}

	@Override
	public boolean growFlower(String flowerType, World world, IIndividual individual, int x, int y, int z) {
		internalInitialize();
		if (!this.growthRules.containsKey(flowerType))
			return false;

		for (IFlowerGrowthRule r : this.growthRules.get(flowerType))
			if (r.growFlower(this, flowerType, world, individual, x, y, z))
				return true;

		return false;
	}

	@Override
	public ItemStack[] getAcceptableFlowers(String flowerType) {
		internalInitialize();
		List<Flower> flowers = this.registeredFlowers.get(flowerType);

		ItemStack[] acceptableFlowers = new ItemStack[flowers.size()];
		for (int i = 0; i < acceptableFlowers.length; i++)
			acceptableFlowers[i] = flowers.get(i).item;

		return acceptableFlowers;
	}

	@Override
	public void registerGrowthRule(IFlowerGrowthRule rule, String... flowerTypes) {
		if (rule == null)
			return;

		for (String flowerType : flowerTypes) {
			if (!this.growthRules.containsKey(flowerType))
				this.growthRules.put(flowerType, new ArrayList<IFlowerGrowthRule>());

			this.growthRules.get(flowerType).add(rule);
		}
	}

	@Override
	public ItemStack getRandomPlantableFlower(String flowerType, Random rand) {
		TreeMap<Double, Flower> tm = getChancesMap(flowerType);
		double maxKey = tm.lastKey() + 1.0;
		return tm.get(tm.lowerKey(rand.nextDouble() * maxKey)).item;
	}

	private TreeMap<Double, Flower> getChancesMap(String flowerType) {
		if (!this.chances.containsKey(flowerType)) {
			TreeMap<Double, Flower> tm = new TreeMap<Double, Flower>();
			double count = 0.0;
			for (Flower f : this.registeredFlowers.get(flowerType)) {
				if (f.isPlantable()) {
					tm.put(count, f);
					count += f.weight;
				}
			}
			this.chances.put(flowerType, tm);
		}
		return this.chances.get(flowerType);
	}

	/*
	 * Method to support deprecated FlowerManager.plainFlowers
	 */
	@SuppressWarnings("deprecation")
	private void internalInitialize() {
		if (!hasDepricatedFlowersImported) {
			for (ItemStack f : FlowerManager.plainFlowers)
				registerPlantableFlower(f, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);

			hasDepricatedFlowersImported = true;
		}
	}

	private void registerVanillaFlowers() {
		// Register plantable plants
		for (int sc = 0; sc <= 8; sc++)
			registerPlantableFlower(new ItemStack(Blocks.red_flower, 1, sc), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);

		registerPlantableFlower(new ItemStack(Blocks.yellow_flower), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerPlantableFlower(new ItemStack(Blocks.brown_mushroom), 1.0, FlowerManager.FlowerTypeMushrooms);
		registerPlantableFlower(new ItemStack(Blocks.red_mushroom), 1.0, FlowerManager.FlowerTypeMushrooms);
		registerPlantableFlower(new ItemStack(Blocks.cactus), 1.0, FlowerManager.FlowerTypeCacti);

		// Register acceptable plants
		registerAcceptableFlower(new ItemStack(Blocks.flower_pot, 1, 1), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(new ItemStack(Blocks.flower_pot, 1, 2), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(new ItemStack(Blocks.flower_pot, 1, 7), FlowerManager.FlowerTypeMushrooms);
		registerAcceptableFlower(new ItemStack(Blocks.flower_pot, 1, 8), FlowerManager.FlowerTypeMushrooms);
		registerAcceptableFlower(new ItemStack(Blocks.flower_pot, 1, 9), FlowerManager.FlowerTypeCacti);
		registerAcceptableFlower(new ItemStack(Blocks.flower_pot, 1, 11), FlowerManager.FlowerTypeJungle);

		registerAcceptableFlower(new ItemStack(Blocks.dragon_egg), FlowerManager.FlowerTypeEnd);
		registerAcceptableFlower(new ItemStack(Blocks.vine), FlowerManager.FlowerTypeJungle);
		registerAcceptableFlower(new ItemStack(Blocks.tallgrass, 1, 2), FlowerManager.FlowerTypeJungle);
		registerAcceptableFlower(new ItemStack(Blocks.wheat, 1, 8), FlowerManager.FlowerTypeWheat);
		registerAcceptableFlower(new ItemStack(Blocks.pumpkin_stem), FlowerManager.FlowerTypeGourd);
		registerAcceptableFlower(new ItemStack(Blocks.melon_stem), FlowerManager.FlowerTypeGourd);
		registerAcceptableFlower(new ItemStack(Blocks.nether_wart), FlowerManager.FlowerTypeNether);

		registerAcceptableFlower(new ItemStack(Blocks.double_plant, 1, 0), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(new ItemStack(Blocks.double_plant, 1, 1), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(new ItemStack(Blocks.double_plant, 1, 4), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAcceptableFlower(new ItemStack(Blocks.double_plant, 1, 5), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
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
