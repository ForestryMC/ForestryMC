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
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerAcceptableRule;
import forestry.api.genetics.IFlowerGrowthHelper;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;
import forestry.core.config.Constants;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.Log;

public final class FlowerRegistry implements IFlowerRegistry, IFlowerGrowthHelper {

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

	private final HashMultimap<String, IFlowerAcceptableRule> registeredRules; // custom check
	private final HashMultimap<String, Block> registeredBlocks; // quick first check
	private final HashMultimap<String, Flower> registeredFlowers; // full check

	private final ArrayListMultimap<String, IFlowerGrowthRule> growthRules;
	private final Map<String, TreeMap<Double, Flower>> chances;

	public FlowerRegistry() {
		this.registeredRules = HashMultimap.create();
		this.registeredBlocks = HashMultimap.create();
		this.registeredFlowers = HashMultimap.create();
		this.growthRules = ArrayListMultimap.create();
		this.chances = new HashMap<>();

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
	public void registerAcceptableFlowerRule(IFlowerAcceptableRule acceptableFlower, String... flowerTypes) {
		for (String flowerType : flowerTypes) {
			if (defaultFlowerTypes.contains(flowerType)) {
				Log.severe("IFlowerAcceptableRules are too slow to be applied to Forestry's built-in flower type: " + flowerType + ".");
			} else {
				registeredRules.put(flowerType, acceptableFlower);
			}
		}
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
			if (flowerType == null) {
				throw new NullPointerException("Tried to register flower with null type. " + block);
			}

			Set<Flower> flowers = this.registeredFlowers.get(flowerType);
			flowers.add(newFlower);

			Set<Block> blocks = this.registeredBlocks.get(flowerType);
			blocks.add(block);

			if (this.chances.containsKey(flowerType)) {
				this.chances.remove(flowerType);
			}
		}
	}

	private static BlockPos getArea(IBeeGenome genome, IBeeModifier beeModifier) {
		int[] genomeTerritory = genome.getTerritory();
		float housingModifier = beeModifier.getTerritoryModifier(genome, 1f);
		return BlockUtil.multiply(new BlockPos(genomeTerritory[0], genomeTerritory[1], genomeTerritory[2]), housingModifier * 3.0f);
	}

	@Override
	public BlockPos getAcceptedFlowerCoordinates(IBeeHousing beeHousing, IBee bee, String flowerType) {
		if (!this.registeredFlowers.containsKey(flowerType)) {
			return null;
		}

		Set<IFlowerAcceptableRule> acceptableRules = this.registeredRules.get(flowerType);
		Set<Block> acceptedBlocks = this.registeredBlocks.get(flowerType);
		Set<Flower> acceptedFlowers = this.registeredFlowers.get(flowerType);
		World world = beeHousing.getWorld();

		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(beeHousing);

		BlockPos area = getArea(bee.getGenome(), beeModifier);
		BlockPos housingPos = new BlockPos(beeHousing.getCoordinates()).add(-area.getX() / 2, -area.getY() / 2, -area.getZ() / 2);

		BlockPos posCurrent = new BlockPos(0, 0, 0);
		while (BlockUtil.advancePositionInArea(posCurrent, area)) {

			BlockPos posBlock = BlockUtil.add(housingPos, posCurrent);

			for (IFlowerAcceptableRule acceptableRule : acceptableRules) {
				if (acceptableRule.isAcceptableFlower(flowerType, world, posBlock)) {
					return new BlockPos(posBlock);
				}
			}

			if (isAcceptedFlower(flowerType, acceptedBlocks, acceptedFlowers, world, posBlock)) {
				return new BlockPos(posBlock);
			}
		}

		return null;
	}

	@Override
	public boolean isAcceptedFlower(String flowerType, World world, BlockPos pos) {
		if (!this.registeredFlowers.containsKey(flowerType)) {
			return false;
		}

		Set<IFlowerAcceptableRule> acceptedCustom = this.registeredRules.get(flowerType);
		for (IFlowerAcceptableRule acceptableFlower : acceptedCustom) {
			if (acceptableFlower.isAcceptableFlower(flowerType, world, pos)) {
				return true;
			}
		}

		Set<Block> acceptedBlocks = this.registeredBlocks.get(flowerType);
		Set<Flower> acceptedFlowers = this.registeredFlowers.get(flowerType);

		return isAcceptedFlower(flowerType, acceptedBlocks, acceptedFlowers, world, pos);
	}

	private static boolean isAcceptedFlower(String flowerType, Set<Block> acceptedBlocks, Set<Flower> acceptedFlowers, World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		final int meta;

		if (block instanceof BlockFlowerPot) {
			TileEntity tile = world.getTileEntity(pos);
			TileEntityFlowerPot tileFlowerPot = (TileEntityFlowerPot) tile;
			Item item = tileFlowerPot.getFlowerPotItem();
			block = Block.getBlockFromItem(item);
			meta = tileFlowerPot.getFlowerPotData();
		} else {
			if (!acceptedBlocks.contains(block)) {
				return false;
			}
			meta = block.getMetaFromState(state);
		}

		/*if (PluginManager.Module.AGRICRAFT.isEnabled()) {
			Block cropBlock = GameRegistry.findBlock("AgriCraft", "crops");
			if (block == cropBlock) {
				if(block instanceof IPlantable) {
					//For agricraft versions which implement IPlantable in the BlockCrop class
					block = ((IPlantable) block).getPlant(world, x, y, z);
				} else {
					//For earlier versions of AgriCraft
					ArrayList<ItemStack> drops = block.getDrops(world, x, y, z, 7, 0);
					if (drops.get(1).getItem() == Items.wheat_seeds && flowerType.equals(FlowerManager.FlowerTypeWheat)) {
						return true;
					}
					if (drops.get(1).getItem() == Items.nether_wart && flowerType.equals(FlowerManager.FlowerTypeNether)) {
						return true;
					}
				}
			}
		}*/

		Flower flower = new Flower(block, meta, 0);
		return acceptedFlowers.contains(flower);
	}

	@Override
	public boolean growFlower(String flowerType, World world, IIndividual individual, BlockPos pos) {
		if (!this.growthRules.containsKey(flowerType)) {
			return false;
		}

		for (IFlowerGrowthRule rule : this.growthRules.get(flowerType)) {
			boolean success;
			success = rule.growFlower(this, flowerType, world, pos);

			if (success) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Set<IFlower> getAcceptableFlowers(String flowerType) {
		return ImmutableSet.<IFlower>copyOf(this.registeredFlowers.get(flowerType));
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
		TreeMap<Double, Flower> chancesMap = getChancesMap(flowerType);
		double maxKey = chancesMap.lastKey() + 1.0;
		return chancesMap.get(chancesMap.lowerKey(rand.nextDouble() * maxKey));
	}

	@Override
	public Collection<String> getFlowerTypes() {
		return new ArrayList<>(Sets.union(defaultFlowerTypes, registeredFlowers.keySet()));
	}

	private TreeMap<Double, Flower> getChancesMap(String flowerType) {
		if (!this.chances.containsKey(flowerType)) {
			TreeMap<Double, Flower> flowerChances = new TreeMap<>();
			double count = 0.0;
			for (Flower flower : this.registeredFlowers.get(flowerType)) {
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
		registerGrowthRule(new GrowthRuleDirtGrass(), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerGrowthRule(new GrowthRuleSnow(), FlowerManager.FlowerTypeSnow);
		registerGrowthRule(new GrowthRuleFlowerPot(), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow, FlowerManager.FlowerTypeMushrooms, FlowerManager.FlowerTypeCacti,
				FlowerManager.FlowerTypeJungle);
		registerGrowthRule(new GrowthRuleMycelium(), FlowerManager.FlowerTypeMushrooms);
		registerGrowthRule(new GrowthRuleNone(), FlowerManager.FlowerTypeEnd);
		registerGrowthRule(new GrowthRuleFertilize(Blocks.melon_stem, Blocks.pumpkin_stem), FlowerManager.FlowerTypeGourd);
		registerGrowthRule(new GrowthRuleFertilize(Blocks.wheat), FlowerManager.FlowerTypeWheat);
	}

	@Override
	public boolean plantRandomFlower(String flowerType, World world, BlockPos pos) {
		IFlower flower = getRandomPlantableFlower(flowerType, world.rand);
		return world.setBlockState(pos, flower.getBlock().getStateFromMeta(flower.getMeta()), Constants.FLAG_BLOCK_SYNCH);
	}
}
