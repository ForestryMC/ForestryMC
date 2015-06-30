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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;
import forestry.apiculture.BeeHousingModifier;
import forestry.core.vect.MutableVect;
import forestry.core.vect.Vect;
import forestry.plugins.PluginManager;

public final class FlowerRegistry implements IFlowerRegistry {

	private final Set<String> defaultFlowerTypes = ImmutableSet.of(
			FlowerManager.FlowerTypeVanilla,
			FlowerManager.FlowerTypeNether,
			FlowerManager.FlowerTypeCacti,
			FlowerManager.FlowerTypeMushrooms,
			FlowerManager.FlowerTypeEnd,
			FlowerManager.FlowerTypeJungle,
			FlowerManager.FlowerTypeSnow,
			FlowerManager.FlowerTypeWheat,
			FlowerManager.FlowerTypeGourd
	);

	private final HashMultimap<String, Block> registeredBlocks; // quick first check
	private final HashMultimap<String, IFlower> registeredFlowers; // full check

	private final ArrayListMultimap<String, IFlowerGrowthRule> growthRules;
	private final Map<String, TreeMap<Double, IFlower>> chances;

	public FlowerRegistry() {
		this.registeredBlocks = HashMultimap.create();
		this.registeredFlowers = HashMultimap.create();
		this.growthRules = ArrayListMultimap.create();
		this.chances = new HashMap<String, TreeMap<Double, IFlower>>();

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

		for (String flowerType : flowerTypes) {
			Set<IFlower> flowers = this.registeredFlowers.get(flowerType);

			if (flowers.contains(newFlower)) {
				throw new IllegalArgumentException("Flower type already exists: " + flowerType + " for flower " + block);
			}
			flowers.add(newFlower);

			Set<Block> blocks = this.registeredBlocks.get(flowerType);
			blocks.add(block);

			if (this.chances.containsKey(flowerType)) {
				this.chances.remove(flowerType);
			}
		}
	}

	private static Vect getArea(IBeeGenome genome, IBeeModifier beeModifier) {
		int[] genomeTerritory = genome.getTerritory();
		float housingModifier = beeModifier.getTerritoryModifier(genome, 1f);
		return new Vect(genomeTerritory).multiply(housingModifier * 3.0f);
	}

	@Override
	public ChunkCoordinates getAcceptedFlowerCoordinates(IBeeHousing beeHousing, IBee bee, String flowerType) {
		if (!this.registeredFlowers.containsKey(flowerType)) {
			return null;
		}

		Set<Block> acceptedBlocks = this.registeredBlocks.get(flowerType);
		Set<IFlower> acceptedFlowers = this.registeredFlowers.get(flowerType);
		World world = beeHousing.getWorld();

		IBeeModifier beeModifier = new BeeHousingModifier(beeHousing);

		Vect area = getArea(bee.getGenome(), beeModifier);
		Vect housingPos = new Vect(beeHousing.getCoordinates()).add(-area.x / 2, -area.y / 2, -area.z / 2);

		MutableVect posCurrent = new MutableVect(0, 0, 0);
		while (posCurrent.advancePositionInArea(area)) {

			Vect posBlock = Vect.add(housingPos, posCurrent);

			if (isAcceptedFlower(flowerType, acceptedBlocks, acceptedFlowers, world, posBlock.x, posBlock.y, posBlock.z)) {
				return new ChunkCoordinates(posBlock.x, posBlock.y, posBlock.z);
			}
		}

		return null;
	}

	@Override
	public boolean isAcceptedFlower(String flowerType, World world, int x, int y, int z) {
		if (!this.registeredFlowers.containsKey(flowerType)) {
			return false;
		}

		Set<Block> acceptedBlocks = this.registeredBlocks.get(flowerType);
		Set<IFlower> acceptedFlowers = this.registeredFlowers.get(flowerType);

		return isAcceptedFlower(flowerType, acceptedBlocks, acceptedFlowers, world, x, y, z);
	}

	private static boolean isAcceptedFlower(String flowerType, Set<Block> acceptedBlocks, Set<IFlower> acceptedFlowers, World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);

		final int meta;

		if (block instanceof BlockFlowerPot) {
			TileEntity tile = world.getTileEntity(x, y, z);
			TileEntityFlowerPot tileFlowerPot = (TileEntityFlowerPot) tile;
			Item item = tileFlowerPot.getFlowerPotItem();
			block = Block.getBlockFromItem(item);
			meta = tileFlowerPot.getFlowerPotData();
		} else {
			if (!acceptedBlocks.contains(block)) {
				return false;
			}
			meta = world.getBlockMetadata(x, y, z);
		}

		if (PluginManager.Module.AGRICRAFT.isEnabled() && (flowerType.equals(FlowerManager.FlowerTypeWheat) || flowerType.equals(FlowerManager.FlowerTypeNether))) {
			Block cropBlock = GameRegistry.findBlock("AgriCraft", "crops");
			if (block == cropBlock) {
				ArrayList<ItemStack> drops = block.getDrops(world, x, y, z, 7, 0);
				if (drops.get(1).getItem() == Items.wheat_seeds && flowerType.equals(FlowerManager.FlowerTypeWheat)) {
					return true;
				}
				if (drops.get(1).getItem() == Items.nether_wart && flowerType.equals(FlowerManager.FlowerTypeNether)) {
					return true;
				}
			}
		}

		Flower flower = new Flower(block, meta, 0);
		return acceptedFlowers.contains(flower);
	}

	@Override
	public boolean growFlower(String flowerType, World world, IIndividual individual, int x, int y, int z) {
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
	public Set<IFlower> getAcceptableFlowers(String flowerType) {
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

	@Override
	public Collection<String> getFlowerTypes() {
		return Sets.union(defaultFlowerTypes, registeredFlowers.keySet());
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
