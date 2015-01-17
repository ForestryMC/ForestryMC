package forestry.apiculture;

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

	private final static FlowerRegistry instance;

	private final Map<String, List<Flower>> registeredFlowers;
	private final Map<String, List<IFlowerGrowthRule>> growthRules;

	private boolean hasDepricatedFlowersImported;

	static {
		instance = new FlowerRegistry();
	}

	private FlowerRegistry() {
		this.registeredFlowers = new HashMap<String, List<Flower>>();
		this.growthRules = new HashMap<String, List<IFlowerGrowthRule>>();

		this.hasDepricatedFlowersImported = false;

		registerVanillaFlowers();
		registerVanillaGrowthRules();
	}

	public static FlowerRegistry getInstance() {
		return instance;
	}

	@Override
	public void registerAccecptableFlower(ItemStack is, String... flowerTypes) {
		registerFlower(is, null, false, flowerTypes);
	}

	@Override
	public void registerPlantableFlower(ItemStack is, double weight, String... flowerTypes) {
		registerFlower(is, weight, true, flowerTypes);
	}

	private void registerFlower(ItemStack is, Double weight, boolean isPlantable, String... flowerTypes) {
		if (is == null || is.getItem() == null)
			return;
		if (is.getItemDamage() == Short.MAX_VALUE || is.getItemDamage() == -1)
			return;
		if (weight == null || weight <= 0.0)
			weight = 0.0;
		if (weight >= 1.0)
			weight = 1.0;

		Flower newFlower = new Flower(is, weight, isPlantable);
		for (String flowerType : flowerTypes) {
			if (!this.registeredFlowers.containsKey(flowerType)) {
				this.registeredFlowers.put(flowerType, new ArrayList<Flower>());
			}

			if (!this.registeredFlowers.get(flowerType).contains(newFlower)) {
				this.registeredFlowers.get(flowerType).add(newFlower);

				Collections.sort(this.registeredFlowers.get(flowerType));
			}
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
			if (StackUtils.isIdenticalItem(f.Item, is))
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
		return this.registeredFlowers.get(flowerType).toArray(new ItemStack[] {});
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
		TreeMap<Double, Flower> tm = new TreeMap<Double, Flower>();
		double count = 0.0;
		for (Flower f : this.registeredFlowers.get(flowerType)) {
			if (f.isPlantable) {
				tm.put(count, f);
				count += f.Weight;
			}
		}

		return tm.get(tm.lowerKey(rand.nextDouble() * count)).Item;
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
		registerAccecptableFlower(new ItemStack(Blocks.flower_pot, 1, 1), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAccecptableFlower(new ItemStack(Blocks.flower_pot, 1, 2), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAccecptableFlower(new ItemStack(Blocks.flower_pot, 1, 7), FlowerManager.FlowerTypeMushrooms);
		registerAccecptableFlower(new ItemStack(Blocks.flower_pot, 1, 8), FlowerManager.FlowerTypeMushrooms);
		registerAccecptableFlower(new ItemStack(Blocks.flower_pot, 1, 9), FlowerManager.FlowerTypeCacti);
		registerAccecptableFlower(new ItemStack(Blocks.flower_pot, 1, 11), FlowerManager.FlowerTypeJungle);

		registerAccecptableFlower(new ItemStack(Blocks.dragon_egg), FlowerManager.FlowerTypeEnd);
		registerAccecptableFlower(new ItemStack(Blocks.vine), FlowerManager.FlowerTypeJungle);
		registerAccecptableFlower(new ItemStack(Blocks.tallgrass, 1, 2), FlowerManager.FlowerTypeJungle);
		registerAccecptableFlower(new ItemStack(Blocks.wheat, 1, 8), FlowerManager.FlowerTypeWheat);
		registerAccecptableFlower(new ItemStack(Blocks.pumpkin_stem), FlowerManager.FlowerTypeGourd);
		registerAccecptableFlower(new ItemStack(Blocks.melon_stem), FlowerManager.FlowerTypeGourd);
		registerAccecptableFlower(new ItemStack(Blocks.nether_wart), FlowerManager.FlowerTypeNether);

		registerAccecptableFlower(new ItemStack(Blocks.double_plant, 1, 0), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAccecptableFlower(new ItemStack(Blocks.double_plant, 1, 1), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAccecptableFlower(new ItemStack(Blocks.double_plant, 1, 4), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		registerAccecptableFlower(new ItemStack(Blocks.double_plant, 1, 5), FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
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