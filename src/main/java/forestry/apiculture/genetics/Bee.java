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
package forestry.apiculture.genetics;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.apiculture.BeeChromosome;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.arboriculture.ITree;
import forestry.api.core.BiomeHelper;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorState;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IPollinatable;
import forestry.arboriculture.genetics.pollination.FakePollinatable;
import forestry.arboriculture.genetics.pollination.ICheckPollinatable;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.genetics.GenericRatings;
import forestry.core.genetics.IndividualLiving;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.Log;
import forestry.core.utils.StringUtil;
import forestry.core.utils.vect.Vect;

public class Bee extends IndividualLiving<BeeChromosome> implements IBee {

	private int generation;
	private boolean isNatural = true;

	@Nonnull
	private final IBeeGenome genome;
	@Nullable
	private IBeeGenome mate;

	/* CONSTRUCTOR */
	public Bee(@Nonnull NBTTagCompound nbt) {
		super(nbt);

		if (nbt.hasKey("NA")) {
			isNatural = nbt.getBoolean("NA");
		}

		if (nbt.hasKey("GEN")) {
			generation = nbt.getInteger("GEN");
		}

		if (nbt.hasKey("Genome")) {
			genome = BeeGenome.fromNBT(nbt.getCompoundTag("Genome"));
		} else {
			genome = BeeDefinition.FOREST.getGenome();
		}

		if (nbt.hasKey("Mate")) {
			mate = BeeGenome.fromNBT(nbt.getCompoundTag("Mate"));
		}
	}

	public Bee(@Nonnull IBeeGenome genome, IBee mate) {
		this(genome);
		this.mate = mate.getGenome();
	}

	public Bee(@Nonnull IBeeGenome genome) {
		this(genome, true, 0);
	}

	private Bee(@Nonnull IBeeGenome genome, boolean isNatural, int generation) {
		super(genome.getLifespan());
		this.genome = genome;
		this.isNatural = isNatural;
		this.generation = generation;
	}

	@Override
	public void writeToNBT(@Nonnull NBTTagCompound nbttagcompound) {

		super.writeToNBT(nbttagcompound);

		if (!isNatural) {
			nbttagcompound.setBoolean("NA", false);
		}

		if (generation > 0) {
			nbttagcompound.setInteger("GEN", generation);
		}
	}

	@Override
	public void setIsNatural(boolean flag) {
		this.isNatural = flag;
	}

	@Override
	public boolean isNatural() {
		return this.isNatural;
	}

	@Override
	public int getGeneration() {
		return generation;
	}

	@Override
	public void mate(IIndividual individual) {
		if (!(individual instanceof IBee)) {
			return;
		}

		IBee drone = (IBee) individual;
		mate = drone.getGenome();
	}

	/* EFFECTS */
	@Override
	public IEffectData[] doEffect(IEffectData[] storedData, IBeeHousing housing) {
		IAlleleBeeEffect effect = genome.getEffect();

		if (effect == null) {
			return null;
		}

		storedData[0] = doEffect(effect, storedData[0], housing);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable()) {
			return storedData;
		}

		IAlleleBeeEffect secondary = (IAlleleBeeEffect) genome.getInactiveAllele(BeeChromosome.EFFECT);
		if (!secondary.isCombinable()) {
			return storedData;
		}

		storedData[1] = doEffect(secondary, storedData[1], housing);

		return storedData;
	}

	private IEffectData doEffect(IAlleleBeeEffect effect, IEffectData storedData, IBeeHousing housing) {
		storedData = effect.validateStorage(storedData);
		return effect.doEffect(genome, storedData, housing);
	}

	@Override
	public IEffectData[] doFX(IEffectData[] storedData, IBeeHousing housing) {
		IAlleleBeeEffect effect = genome.getEffect();

		if (effect == null) {
			return null;
		}

		storedData[0] = doFX(effect, storedData[0], housing);

		// Return here if the primary can already not be combined
		if (!effect.isCombinable()) {
			return storedData;
		}

		IAlleleBeeEffect secondary = (IAlleleBeeEffect) genome.getInactiveAllele(BeeChromosome.EFFECT);
		if (!secondary.isCombinable()) {
			return storedData;
		}

		storedData[1] = doFX(secondary, storedData[1], housing);

		return storedData;
	}

	private IEffectData doFX(IAlleleBeeEffect effect, IEffectData storedData, IBeeHousing housing) {
		return effect.doFX(genome, storedData, housing);
	}

	// / INFORMATION
	@Nonnull
	@Override
	public IBeeGenome getGenome() {
		return genome;
	}

	@Nullable
	@Override
	public IBeeGenome getMate() {
		return mate;
	}

	@Override
	public IBee copy() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new Bee(nbttagcompound);

	}

	@Override
	public boolean canSpawn() {
		return mate != null;
	}

	@Override
	public Set<IErrorState> getCanWork(IBeeHousing housing) {
		World world = housing.getWorld();
		BiomeGenBase biome = housing.getBiome();

		Set<IErrorState> errorStates = new HashSet<>();

		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

		// / Rain needs tolerant flyers
		if (world.isRaining() && BiomeHelper.canRainOrSnow(biome) && !canFlyInRain(beeModifier)) {
			errorStates.add(EnumErrorCode.IS_RAINING);
		}

		// / Night or darkness requires nocturnal species
		if (world.isDaytime()) {
			if (!canWorkDuringDay()) {
				errorStates.add(EnumErrorCode.NOT_NIGHT);
			}
		} else {
			if (!canWorkAtNight(beeModifier)) {
				errorStates.add(EnumErrorCode.NOT_DAY);
			}
		}

		if (housing.getBlockLightValue() > Constants.APIARY_MIN_LEVEL_LIGHT) {
			if (!canWorkDuringDay()) {
				errorStates.add(EnumErrorCode.NOT_GLOOMY);
			}
		} else {
			if (!canWorkAtNight(beeModifier)) {
				errorStates.add(EnumErrorCode.NOT_BRIGHT);
			}
		}

		// / Check for the sky, except if in hell
		if (biome != null && !BiomeHelper.isBiomeHellish(biome)) {
			if (!housing.canBlockSeeTheSky() && !canWorkUnderground(beeModifier)) {
				errorStates.add(EnumErrorCode.NO_SKY);
			}
		}

		// / And finally climate check
		IAlleleBeeSpecies species = genome.getPrimary();
		{
			EnumTemperature actualTemperature = housing.getTemperature();
			EnumTemperature beeBaseTemperature = species.getTemperature();
			EnumTolerance beeToleranceTemperature = genome.getToleranceTemp();

			if (!AlleleManager.climateHelper.isWithinLimits(actualTemperature, beeBaseTemperature, beeToleranceTemperature)) {
				if (beeBaseTemperature.ordinal() > actualTemperature.ordinal()) {
					errorStates.add(EnumErrorCode.TOO_COLD);
				} else {
					errorStates.add(EnumErrorCode.TOO_HOT);
				}
			}
		}

		{
			EnumHumidity actualHumidity = housing.getHumidity();
			EnumHumidity beeBaseHumidity = species.getHumidity();
			EnumTolerance beeToleranceHumidity = genome.getToleranceHumid();

			if (!AlleleManager.climateHelper.isWithinLimits(actualHumidity, beeBaseHumidity, beeToleranceHumidity)) {
				if (beeBaseHumidity.ordinal() > actualHumidity.ordinal()) {
					errorStates.add(EnumErrorCode.TOO_ARID);
				} else {
					errorStates.add(EnumErrorCode.TOO_HUMID);
				}
			}
		}

		return errorStates;
	}

	private boolean canWorkAtNight(IBeeModifier beeModifier) {
		return genome.getPrimary().isNocturnal() || genome.getNocturnal() || beeModifier.isSelfLighted();
	}

	private boolean canWorkDuringDay() {
		return !genome.getPrimary().isNocturnal() || genome.getNocturnal();
	}

	private boolean canWorkUnderground(IBeeModifier beeModifier) {
		return genome.getCaveDwelling() || beeModifier.isSunlightSimulated();
	}

	private boolean canFlyInRain(IBeeModifier beeModifier) {
		return genome.getTolerantFlyer() || beeModifier.isSealed();
	}

	private boolean isSuitableBiome(BiomeGenBase biome) {
		if (biome == null) {
			return false;
		}

		EnumTemperature temperature = EnumTemperature.getFromBiome(biome);
		EnumHumidity humidity = EnumHumidity.getFromValue(biome.rainfall);
		return isSuitableClimate(temperature, humidity);
	}

	private boolean isSuitableClimate(EnumTemperature temperature, EnumHumidity humidity) {
		return AlleleManager.climateHelper.isWithinLimits(temperature, humidity,
				genome.getPrimary().getTemperature(), genome.getToleranceTemp(),
				genome.getPrimary().getHumidity(), genome.getToleranceHumid());
	}

	@Override
	public ArrayList<BiomeGenBase> getSuitableBiomes() {
		ArrayList<BiomeGenBase> suitableBiomes = new ArrayList<>();
		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
			if (isSuitableBiome(biome)) {
				suitableBiomes.add(biome);
			}
		}

		return suitableBiomes;
	}

	@Override
	public void addTooltip(List<String> list) {

		// No info 4 u!
		if (!isAnalyzed) {
			list.add("<" + StringUtil.localize("gui.unknown") + ">");
			return;
		}

		// You analyzed it? Juicy tooltip coming up!
		IAlleleBeeSpecies primary = genome.getPrimary();
		IAlleleBeeSpecies secondary = genome.getSecondary();
		if (!isPureBred(BeeChromosome.SPECIES)) {
			list.add(EnumChatFormatting.BLUE + StringUtil.localize("bees.hybrid").replaceAll("%PRIMARY", primary.getName()).replaceAll("%SECONDARY", secondary.getName()));
		}

		if (generation > 0) {
			EnumRarity rarity;
			if (generation >= 1000) {
				rarity = EnumRarity.EPIC;
			} else if (generation >= 100) {
				rarity = EnumRarity.RARE;
			} else if (generation >= 10) {
				rarity = EnumRarity.UNCOMMON;
			} else {
				rarity = EnumRarity.COMMON;
			}

			String generationString = rarity.rarityColor + StringUtil.localizeAndFormat("gui.beealyzer.generations", generation);
			list.add(generationString);
		}

		IAllele speedAllele = genome.getActiveAllele(BeeChromosome.SPEED);
		IAlleleTolerance tempToleranceAllele = (IAlleleTolerance) getGenome().getActiveAllele(BeeChromosome.TEMPERATURE_TOLERANCE);
		IAlleleTolerance humidToleranceAllele = (IAlleleTolerance) getGenome().getActiveAllele(BeeChromosome.HUMIDITY_TOLERANCE);

		String unlocalizedCustomSpeed = "tooltip.worker." + speedAllele.getUnlocalizedName().replaceAll("(.*)\\.", "");
		String speed;
		if (StringUtil.canTranslate(unlocalizedCustomSpeed)) {
			speed = StringUtil.localize(unlocalizedCustomSpeed);
		} else {
			speed = speedAllele.getName() + ' ' + StringUtil.localize("gui.worker");
		}

		String lifespan = genome.getActiveAllele(BeeChromosome.LIFESPAN).getName() + ' ' + StringUtil.localize("gui.life");
		String tempTolerance = EnumChatFormatting.GREEN + "T: " + AlleleManager.climateHelper.toDisplay(genome.getPrimary().getTemperature()) + " / " + tempToleranceAllele.getName();
		String humidTolerance = EnumChatFormatting.GREEN + "H: " + AlleleManager.climateHelper.toDisplay(genome.getPrimary().getHumidity()) + " / " + humidToleranceAllele.getName();
		String flowers = genome.getFlowerProvider().getDescription();

		list.add(lifespan);
		list.add(speed);
		list.add(tempTolerance);
		list.add(humidTolerance);
		list.add(flowers);

		if (genome.getNocturnal()) {
			list.add(EnumChatFormatting.RED + GenericRatings.rateActivityTime(genome.getNocturnal(), false));
		}

		if (genome.getTolerantFlyer()) {
			list.add(EnumChatFormatting.WHITE + StringUtil.localize("gui.flyer.tooltip"));
		}
	}

	@Override
	public void age(World world, float housingLifespanModifier) {
		IBeekeepingMode mode = BeeManager.beeRoot.getMode(world);
		IBeeModifier beeModifier = mode.getBeeModifier();
		float finalModifier = housingLifespanModifier * beeModifier.getLifespanModifier(genome, mate, housingLifespanModifier);

		super.age(world, finalModifier);
	}

	// / PRODUCTION
	@Override
	public ItemStack[] getProduceList() {
		ArrayList<ItemStack> products = new ArrayList<>();

		IAlleleBeeSpecies primary = genome.getPrimary();
		IAlleleBeeSpecies secondary = genome.getSecondary();

		products.addAll(primary.getProductChances().keySet());

		Set<ItemStack> secondaryProducts = secondary.getProductChances().keySet();
		// Remove duplicates
		for (ItemStack second : secondaryProducts) {
			boolean skip = false;

			for (ItemStack compare : products) {
				if (second.isItemEqual(compare)) {
					skip = true;
					break;
				}
			}

			if (!skip) {
				products.add(second);
			}

		}

		return products.toArray(new ItemStack[products.size()]);
	}

	@Override
	public ItemStack[] getSpecialtyList() {
		Set<ItemStack> specialties = genome.getPrimary().getSpecialtyChances().keySet();
		return specialties.toArray(new ItemStack[specialties.size()]);
	}

	@Override
	public ItemStack[] produceStacks(IBeeHousing housing) {
		if (housing == null) {
			Log.warning("Failed to produce in an apiary because the beehousing was null.");
			return null;
		}
		IBeekeepingMode mode = BeeManager.beeRoot.getMode(housing.getWorld());
		if (mode == null) {
			Log.warning("Failed to produce in an apiary because the beekeeping mode was null.");
			return null;
		}

		List<ItemStack> products = new ArrayList<>();

		IAlleleBeeSpecies primary = genome.getPrimary();
		IAlleleBeeSpecies secondary = genome.getSecondary();

		// All work and no play makes queen a dull girl.
		if (mode.isOverworked(this, housing)) {
			setIsNatural(false);
		}

		IBeeModifier beeHousingModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		IBeeModifier beeModeModifier = mode.getBeeModifier();

		// Bee genetic speed * beehousing * beekeeping mode
		float speed = genome.getSpeed() * beeHousingModifier.getProductionModifier(genome, 1f) * beeModeModifier.getProductionModifier(genome, 1f);

		// / Primary Products
		for (Map.Entry<ItemStack, Float> entry : primary.getProductChances().entrySet()) {
			if (housing.getWorld().rand.nextFloat() < entry.getValue() * speed) {
				products.add(entry.getKey().copy());
			}
		}
		// / Secondary Products
		for (Map.Entry<ItemStack, Float> entry : secondary.getProductChances().entrySet()) {
			if (housing.getWorld().rand.nextFloat() < Math.round(entry.getValue() / 2) * speed) {
				products.add(entry.getKey().copy());
			}
		}

		// / Specialty products
		if (primary.isJubilant(genome, housing) && secondary.isJubilant(genome, housing)) {
			for (Map.Entry<ItemStack, Float> entry : primary.getSpecialtyChances().entrySet()) {
				if (housing.getWorld().rand.nextFloat() < entry.getValue() * speed) {
					products.add(entry.getKey().copy());
				}
			}
		}

		ItemStack[] productsArray = products.toArray(new ItemStack[products.size()]);
		BlockPos housingCoordinates = housing.getCoordinates();
		return genome.getFlowerProvider().affectProducts(housing.getWorld(), this, housingCoordinates, productsArray);
	}

	/* REPRODUCTION */
	@Nullable
	@Override
	public IBee spawnPrincess(IBeeHousing housing) {
		return createOffspring(housing, getGeneration() + 1);
	}

	@Nullable
	@Override
	public IBee[] spawnDrones(IBeeHousing housing) {

		World world = housing.getWorld();

		// We need a mated queen to produce offspring.
		if (mate == null) {
			return null;
		}

		List<IBee> bees = new ArrayList<>();

		BlockPos housingPos = housing.getCoordinates();
		int toCreate = BeeManager.beeRoot.getMode(world).getFinalFertility(this, world, housingPos);
		if (toCreate <= 0) {
			toCreate = 1;
		}

		for (int i = 0; i < toCreate; i++) {
			IBee offspring = createOffspring(housing, 0);
			if (offspring != null) {
				offspring.setIsNatural(true);
				bees.add(offspring);
			}
		}

		if (bees.size() > 0) {
			return bees.toArray(new IBee[bees.size()]);
		} else {
			return null;
		}
	}

	@Nullable
	private IBee createOffspring(IBeeHousing housing, int generation) {
		// We need a mated queen to produce offspring.
		if (mate == null) {
			return null;
		}

		// Fatigued queens do not produce princesses.
		if (BeeManager.beeRoot.getMode(housing.getWorld()).isFatigued(this, housing)) {
			return null;
		}

		ImmutableMap<BeeChromosome, IChromosome> chromosomes = createOffspringChromosomes(housing.getWorld(), housing.getOwner(), housing.getCoordinates(), genome, mate);
		IBeeGenome genome = BeeManager.beeRoot.chromosomesAsGenome(chromosomes);
		return new Bee(genome, isNatural, generation);
	}

	/* FLOWERS */
	@Override
	public ITree retrievePollen(IBeeHousing housing) {

		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

		int chance = Math.round(genome.getFlowering() * beeModifier.getFloweringModifier(getGenome(), 1f));

		World world = housing.getWorld();
		Random random = world.rand;

		// Correct speed
		if (random.nextInt(100) >= chance) {
			return null;
		}

		Vect area = getArea(genome, beeModifier);
		Vect offset = new Vect(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
		Vect housingPos = new Vect(housing.getCoordinates());

		ITree pollen = null;

		for (int i = 0; i < 20; i++) {
			Vect randomPos = Vect.getRandomPositionInArea(random, area);
			Vect blockPos = Vect.add(housingPos, randomPos, offset);
			TileEntity tile = world.getTileEntity(blockPos);

			if (tile instanceof IPollinatable) {
				IPollinatable pitcher = (IPollinatable) tile;
				if (genome.getFlowerProvider().isAcceptedPollinatable(world, pitcher)) {
					pollen = pitcher.getPollen();
				}
			} else {
				pollen = GeneticsUtil.getErsatzPollen(world, blockPos);
			}

			if (pollen != null) {
				return pollen;
			}
		}

		return null;
	}

	@Override
	public boolean pollinateRandom(IBeeHousing housing, ITree pollen) {

		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

		int chance = (int) (genome.getFlowering() * beeModifier.getFloweringModifier(getGenome(), 1f));

		World world = housing.getWorld();
		Random random = world.rand;

		// Correct speed
		if (random.nextInt(100) >= chance) {
			return false;
		}

		Vect area = getArea(genome, beeModifier);
		Vect offset = new Vect(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
		Vect housingPos = new Vect(housing.getCoordinates());

		for (int i = 0; i < 30; i++) {

			Vect randomPos = Vect.getRandomPositionInArea(random, area);
			Vect posBlock = Vect.add(housingPos, randomPos, offset);

			ICheckPollinatable checkPollinatable = GeneticsUtil.getCheckPollinatable(world, posBlock);
			if (checkPollinatable == null) {
				continue;
			}

			if (!genome.getFlowerProvider().isAcceptedPollinatable(world, new FakePollinatable(checkPollinatable))) {
				continue;
			}
			if (!checkPollinatable.canMateWith(pollen)) {
				continue;
			}

			IPollinatable realPollinatable = GeneticsUtil.getOrCreatePollinatable(housing.getOwner(), world, posBlock);

			if (realPollinatable != null) {
				realPollinatable.mateWith(pollen);
				return true;
			}
		}

		return false;
	}

	@Override
	public void plantFlowerRandom(IBeeHousing housing) {

		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

		int chance = Math.round(genome.getFlowering() * beeModifier.getFloweringModifier(getGenome(), 1f));

		World world = housing.getWorld();
		Random random = world.rand;

		// Correct speed
		if (random.nextInt(100) >= chance) {
			return;
		}

		// Gather required info
		IFlowerProvider provider = genome.getFlowerProvider();
		Vect area = getArea(genome, beeModifier);
		Vect offset = new Vect(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
		Vect housingPos = new Vect(housing.getCoordinates());

		for (int i = 0; i < 10; i++) {
			Vect randomPos = Vect.getRandomPositionInArea(random, area);
			Vect posBlock = Vect.add(housingPos, randomPos, offset);

			if (FlowerManager.flowerRegistry.growFlower(provider.getFlowerType(), world, this, posBlock)) {
				break;
			}
		}
	}

	private static Vect getArea(IBeeGenome genome, IBeeModifier beeModifier) {
		int[] genomeTerritory = genome.getTerritory();
		float housingModifier = beeModifier.getTerritoryModifier(genome, 1f);
		return new Vect(genomeTerritory).multiply(housingModifier * 3.0f);
	}
}
