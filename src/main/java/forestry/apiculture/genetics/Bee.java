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
package forestry.apiculture.genetics;

import com.mojang.authlib.GameProfile;
import forestry.api.apiculture.*;
import forestry.api.apiculture.genetics.*;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorState;
import forestry.api.core.tooltips.ToolTip;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IPollinatable;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.genetics.flowers.IFlowerProvider;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.errors.EnumErrorCode;
import forestry.core.genetics.GenericRatings;
import forestry.core.genetics.IndividualLiving;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.ResourceUtil;
import forestry.core.utils.VectUtil;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.components.ComponentKeys;
import genetics.individual.Genome;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class Bee extends IndividualLiving implements IBee {
    private static final String NBT_NATURAL = "NA";
    private static final String NBT_GENERATION = "GEN";

    private int generation;
    private boolean isNatural = true;

    /* CONSTRUCTOR */
    public Bee(CompoundNBT nbt) {
        super(nbt);

        if (nbt.contains(NBT_NATURAL)) {
            isNatural = nbt.getBoolean(NBT_NATURAL);
        }

        if (nbt.contains(NBT_GENERATION)) {
            generation = nbt.getInt(NBT_GENERATION);
        }
    }

    public Bee(IGenome genome) {
        this(genome, (IGenome) null);
    }

    public Bee(IGenome genome, IBee mate) {
        this(genome, mate.getGenome());
    }

    public Bee(IGenome genome, @Nullable IGenome mate) {
        this(genome, mate, true, 0);
    }

    private Bee(IGenome genome, @Nullable IGenome mate, boolean isNatural, int generation) {
        super(genome, mate, genome.getActiveValue(BeeChromosomes.LIFESPAN));
        this.isNatural = isNatural;
        this.generation = generation;
    }

    @Override
    public IIndividualRoot getRoot() {
        return BeeManager.beeRoot;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {

        compound = super.write(compound);

        if (!isNatural) {
            compound.putBoolean(NBT_NATURAL, false);
        }

        if (generation > 0) {
            compound.putInt(NBT_GENERATION, generation);
        }
        return compound;
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

    /* EFFECTS */
    @Override
    public IEffectData[] doEffect(IEffectData[] storedData, IBeeHousing housing) {
        IAlleleBeeEffect effect = genome.getActiveAllele(BeeChromosomes.EFFECT);

        storedData[0] = doEffect(effect, storedData[0], housing);

        // Return here if the primary can already not be combined
        if (!effect.isCombinable()) {
            return storedData;
        }

        IAlleleBeeEffect secondary = genome.getInactiveAllele(BeeChromosomes.EFFECT);
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
    @OnlyIn(Dist.CLIENT)
    public IEffectData[] doFX(IEffectData[] storedData, IBeeHousing housing) {
        IAlleleBeeEffect effect = genome.getActiveAllele(BeeChromosomes.EFFECT);

        storedData[0] = doFX(effect, storedData[0], housing);

        // Return here if the primary can already not be combined
        if (!effect.isCombinable()) {
            return storedData;
        }

        IAlleleBeeEffect secondary = genome.getInactiveAllele(BeeChromosomes.EFFECT);
        if (!secondary.isCombinable()) {
            return storedData;
        }

        storedData[1] = doFX(secondary, storedData[1], housing);

        return storedData;
    }

    @OnlyIn(Dist.CLIENT)
    private IEffectData doFX(IAlleleBeeEffect effect, IEffectData storedData, IBeeHousing housing) {
        return effect.doFX(genome, storedData, housing);
    }

    // INFORMATION
    @Override
    public IBee copy() {
        CompoundNBT compound = new CompoundNBT();
        this.write(compound);
        return new Bee(compound);
    }

    @Override
    public boolean canSpawn() {
        return mate != null;
    }

    @Override
    public Set<IErrorState> getCanWork(IBeeHousing housing) {
        World world = housing.getWorldObj();

        Set<IErrorState> errorStates = new HashSet<>();

        IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

        // Rain needs tolerant flyers
        if (housing.isRaining() && !canFlyInRain(beeModifier)) {
            errorStates.add(EnumErrorCode.IS_RAINING);
        }

        // Night or darkness requires nocturnal species
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

        // Check for the sky, except if in hell
        if (world.getBiome(housing.getCoordinates()).getCategory() != Biome.Category.NETHER) {
            if (!housing.canBlockSeeTheSky() && !canWorkUnderground(beeModifier)) {
                errorStates.add(EnumErrorCode.NO_SKY);
            }
        }

        // And finally climate check
        IAlleleBeeSpecies species = genome.getActiveAllele(BeeChromosomes.SPECIES);
        {
            EnumTemperature actualTemperature = housing.getTemperature();
            EnumTemperature beeBaseTemperature = species.getTemperature();
            EnumTolerance beeToleranceTemperature = genome.getActiveValue(BeeChromosomes.TEMPERATURE_TOLERANCE);

            if (!AlleleManager.climateHelper.isWithinLimits(
                    actualTemperature,
                    beeBaseTemperature,
                    beeToleranceTemperature
            )) {
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
            EnumTolerance beeToleranceHumidity = genome.getActiveValue(BeeChromosomes.HUMIDITY_TOLERANCE);

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
        return genome.getActiveAllele(BeeChromosomes.SPECIES)
                     .isNocturnal() || genome.getActiveValue(BeeChromosomes.NEVER_SLEEPS) ||
               beeModifier.isSelfLighted();
    }

    private boolean canWorkDuringDay() {
        return !genome.getActiveAllele(BeeChromosomes.SPECIES)
                      .isNocturnal() || genome.getActiveValue(BeeChromosomes.NEVER_SLEEPS);
    }

    private boolean canWorkUnderground(IBeeModifier beeModifier) {
        return genome.getActiveValue(BeeChromosomes.CAVE_DWELLING) || beeModifier.isSunlightSimulated();
    }

    private boolean canFlyInRain(IBeeModifier beeModifier) {
        return genome.getActiveValue(BeeChromosomes.TOLERATES_RAIN) || beeModifier.isSealed();
    }

    private boolean isSuitableBiome(Biome biome) {
        EnumTemperature temperature = EnumTemperature.getFromBiome(biome);
        EnumHumidity humidity = EnumHumidity.getFromValue(biome.getDownfall());
        return isSuitableClimate(temperature, humidity);
    }

    private boolean isSuitableClimate(EnumTemperature temperature, EnumHumidity humidity) {
        return AlleleManager.climateHelper.isWithinLimits(
                temperature,
                humidity,
                genome.getActiveAllele(BeeChromosomes.SPECIES).getTemperature(),
                genome.getActiveValue(BeeChromosomes.TEMPERATURE_TOLERANCE),
                genome.getActiveAllele(BeeChromosomes.SPECIES).getHumidity(),
                genome.getActiveValue(BeeChromosomes.HUMIDITY_TOLERANCE)
        );
    }

    @Override
    public List<Biome> getSuitableBiomes() {
        List<Biome> suitableBiomes = new ArrayList<>();
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (isSuitableBiome(biome)) {
                suitableBiomes.add(biome);
            }
        }

        return suitableBiomes;
    }

    @Override
    public void addTooltip(List<ITextComponent> list) {
        ToolTip toolTip = new ToolTip();

        // No info 4 u!
        if (!isAnalyzed) {
            toolTip.singleLine()
                   .text(new StringTextComponent("<"))
                   .translated("for.gui.unknown")
                   .text(new StringTextComponent(">"))
                   .style(TextFormatting.GRAY).create();
            return;
        }

        // You analyzed it? Juicy tooltip coming up!
        IAlleleBeeSpecies primary = genome.getActiveAllele(BeeChromosomes.SPECIES);
        IAlleleBeeSpecies secondary = genome.getInactiveAllele(BeeChromosomes.SPECIES);
        if (!isPureBred(BeeChromosomes.SPECIES)) {
            toolTip.translated("for.bees.hybrid", primary.getDisplayName(), secondary.getDisplayName())
                   .style(TextFormatting.BLUE);
        }

        if (generation > 0) {
            Rarity rarity;
            if (generation >= 1000) {
                rarity = Rarity.EPIC;
            } else if (generation >= 100) {
                rarity = Rarity.RARE;
            } else if (generation >= 10) {
                rarity = Rarity.UNCOMMON;
            } else {
                rarity = Rarity.COMMON;
            }
            toolTip.translated("for.gui.beealyzer.generations", generation).style(rarity.color);
        }

        toolTip.singleLine()
               .add(genome.getActiveAllele(BeeChromosomes.LIFESPAN).getDisplayName())
               .text(new StringTextComponent(" "))
               .translated("for.gui.life")
               .style(TextFormatting.GRAY)
               .create();

        IAllele speedAllele = genome.getActiveAllele(BeeChromosomes.SPEED);

        TranslationTextComponent customSpeed = new TranslationTextComponent(
                "for.tooltip.worker." + speedAllele.getLocalisationKey()
                                                   .replaceAll("(.*)\\.", "")
        );
        if (ResourceUtil.canTranslate(customSpeed)) {
            toolTip.singleLine()
                   .add(customSpeed)
                   .style(TextFormatting.GRAY)
                   .create();
        } else {
            toolTip.singleLine()
                   .add(speedAllele.getDisplayName())
                   .text(new StringTextComponent(" "))
                   .translated("for.gui.worker")
                   .style(TextFormatting.GRAY)
                   .create();
        }

        IAlleleValue<EnumTolerance> tempToleranceAllele = getGenome().getActiveAllele(BeeChromosomes.TEMPERATURE_TOLERANCE);
        IAlleleValue<EnumTolerance> humidToleranceAllele = getGenome().getActiveAllele(BeeChromosomes.HUMIDITY_TOLERANCE);

        toolTip.singleLine()
               .text(new StringTextComponent("T: "))
               .add(AlleleManager.climateHelper.toDisplay(primary.getTemperature()))
               .text(new StringTextComponent(" / "))
               .add(tempToleranceAllele.getDisplayName())
               .style(TextFormatting.GREEN)
               .create();
        toolTip.singleLine().text(new StringTextComponent("H: "))
               .add(AlleleManager.climateHelper.toDisplay(primary.getHumidity())).text(new StringTextComponent(" / "))
               .add(humidToleranceAllele.getDisplayName()).style(TextFormatting.GREEN).create();


        toolTip.add(
                genome.getActiveAllele(BeeChromosomes.FLOWER_PROVIDER).getProvider().getDescription(),
                TextFormatting.GRAY
        );

        if (genome.getActiveValue(BeeChromosomes.NEVER_SLEEPS)) {
            toolTip.text(GenericRatings.rateActivityTime(true, false)).style(TextFormatting.RED);
        }

        if (genome.getActiveValue(BeeChromosomes.TOLERATES_RAIN)) {
            toolTip.translated("for.gui.flyer.tooltip").style(TextFormatting.WHITE);
        }
        list.addAll(toolTip.getLines());
    }

    @Override
    public void age(World world, float housingLifespanModifier) {
        IBeekeepingMode mode = BeeManager.beeRoot.getBeekeepingMode(world);
        IBeeModifier beeModifier = mode.getBeeModifier();
        float finalModifier = housingLifespanModifier * beeModifier.getLifespanModifier(
                genome,
                mate,
                housingLifespanModifier
        );

        super.age(world, finalModifier);
    }

    // PRODUCTION
    @Override
    public NonNullList<ItemStack> getProduceList() {
        NonNullList<ItemStack> products = NonNullList.create();

        IAlleleBeeSpecies primary = genome.getActiveAllele(BeeChromosomes.SPECIES);
        IAlleleBeeSpecies secondary = genome.getInactiveAllele(BeeChromosomes.SPECIES);

        products.addAll(primary.getProducts().getPossibleStacks());

        NonNullList<ItemStack> secondaryProducts = secondary.getProducts().getPossibleStacks();
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

        return products;
    }

    @Override
    public NonNullList<ItemStack> getSpecialtyList() {
        return genome.getActiveAllele(BeeChromosomes.SPECIES).getSpecialties().getPossibleStacks();
    }

    @Override
    public NonNullList<ItemStack> produceStacks(IBeeHousing housing) {
        World world = housing.getWorldObj();
        IBeekeepingMode mode = BeeManager.beeRoot.getBeekeepingMode(world);

        NonNullList<ItemStack> products = NonNullList.create();

        IAlleleBeeSpecies primary = genome.getActiveAllele(BeeChromosomes.SPECIES);
        IAlleleBeeSpecies secondary = genome.getInactiveAllele(BeeChromosomes.SPECIES);

        IBeeModifier beeHousingModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
        IBeeModifier beeModeModifier = mode.getBeeModifier();

        // Bee genetic speed * beehousing * beekeeping mode
        float speed = genome.getActiveValue(BeeChromosomes.SPEED) * beeHousingModifier.getProductionModifier(
                genome,
                1f
        ) * beeModeModifier.getProductionModifier(genome, 1f);

        // Primary Products
        primary.getProducts()
               .addProducts(
                       world,
                       housing.getCoordinates(),
                       products,
                       (product) -> product.getChance() * speed,
                       world.rand
               );
        // Secondary Products
        secondary.getProducts()
                 .addProducts(
                         world,
                         housing.getCoordinates(),
                         products,
                         (product) -> Math.round(product.getChance() / 2) * speed,
                         world.rand
                 );

        // Specialty products
        if (primary.isJubilant(genome, housing) && secondary.isJubilant(genome, housing)) {
            primary.getSpecialties()
                   .addProducts(
                           world,
                           housing.getCoordinates(),
                           products,
                           (product) -> product.getChance() * speed,
                           world.rand
                   );
        }

        BlockPos housingCoordinates = housing.getCoordinates();
        return genome.getActiveAllele(BeeChromosomes.FLOWER_PROVIDER)
                     .getProvider()
                     .affectProducts(world, this, housingCoordinates, products);
    }

    /* REPRODUCTION */
    @Override
    @Nullable
    public Optional<IBee> spawnPrincess(IBeeHousing housing) {

        // We need a mated queen to produce offspring.
        if (mate == null) {
            return Optional.empty();
        }

        // Fatigued queens do not produce princesses.
        if (BeeManager.beeRoot.getBeekeepingMode(housing.getWorldObj()).isFatigued(this, housing)) {
            return Optional.empty();
        }

        return Optional.ofNullable(createOffspring(housing, mate, getGeneration() + 1));
    }

    @Override
    public List<IBee> spawnDrones(IBeeHousing housing) {
        World world = housing.getWorldObj();

        // We need a mated queen to produce offspring.
        if (mate == null) {
            return Collections.emptyList();
        }

        List<IBee> bees = new ArrayList<>();

        BlockPos housingPos = housing.getCoordinates();
        int toCreate = BeeManager.beeRoot.getBeekeepingMode(world).getFinalFertility(this, world, housingPos);

        if (toCreate <= 0) {
            toCreate = 1;
        }

        for (int i = 0; i < toCreate; i++) {
            IBee offspring = createOffspring(housing, mate, 0);
            offspring.setIsNatural(true);
            bees.add(offspring);
        }

        return bees;
    }

    private IBee createOffspring(IBeeHousing housing, IGenome mate, int generation) {

        World world = housing.getWorldObj();

        IChromosome[] chromosomes = new IChromosome[genome.getChromosomes().length];
        IChromosome[] parent1 = genome.getChromosomes();
        IChromosome[] parent2 = mate.getChromosomes();

        // Check for mutation. Replace one of the parents with the mutation
        // template if mutation occurred.
        IChromosome[] mutated1 = mutateSpecies(housing, genome, mate);
        if (mutated1 != null) {
            parent1 = mutated1;
        }
        IChromosome[] mutated2 = mutateSpecies(housing, mate, genome);
        if (mutated2 != null) {
            parent2 = mutated2;
        }

        for (int i = 0; i < parent1.length; i++) {
            if (parent1[i] != null && parent2[i] != null) {
                chromosomes[i] = parent1[i].inheritChromosome(world.rand, parent2[i]);
            }
        }

        IBeekeepingMode mode = BeeManager.beeRoot.getBeekeepingMode(world);
        return new Bee(
                new Genome(BeeManager.beeRoot.getKaryotype(), chromosomes),
                null,
                mode.isNaturalOffspring(this),
                generation
        );
    }

    @Nullable
    private static IChromosome[] mutateSpecies(IBeeHousing housing, IGenome genomeOne, IGenome genomeTwo) {

        World world = housing.getWorldObj();

        IChromosome[] parent1 = genomeOne.getChromosomes();
        IChromosome[] parent2 = genomeTwo.getChromosomes();

        IGenome genome0;
        IGenome genome1;

        IAlleleBeeSpecies allele0;
        IAlleleBeeSpecies allele1;

        if (world.rand.nextBoolean()) {
            allele0 = (IAlleleBeeSpecies) parent1[BeeChromosomes.SPECIES.ordinal()].getActiveAllele();
            allele1 = (IAlleleBeeSpecies) parent2[BeeChromosomes.SPECIES.ordinal()].getInactiveAllele();

            genome0 = genomeOne;
            genome1 = genomeTwo;
        } else {
            allele0 = (IAlleleBeeSpecies) parent2[BeeChromosomes.SPECIES.ordinal()].getActiveAllele();
            allele1 = (IAlleleBeeSpecies) parent1[BeeChromosomes.SPECIES.ordinal()].getInactiveAllele();

            genome0 = genomeTwo;
            genome1 = genomeOne;
        }

        GameProfile playerProfile = housing.getOwner();
        IApiaristTracker breedingTracker = BeeManager.beeRoot.getBreedingTracker(world, playerProfile);

        IMutationContainer<IBee, ? extends IMutation> container = BeeManager.beeRoot.getComponent(ComponentKeys.MUTATIONS);
        List<? extends IMutation> combinations = container.getCombinations(allele0, allele1, true);
        for (IMutation mutation : combinations) {
            IBeeMutation beeMutation = (IBeeMutation) mutation;

            float chance = beeMutation.getChance(housing, allele0, allele1, genome0, genome1);
            if (chance <= 0) {
                continue;
            }

            // boost chance for researched mutations
            if (breedingTracker.isResearched(beeMutation)) {
                float mutationBoost = chance * (Config.researchMutationBoostMultiplier - 1.0f);
                mutationBoost = Math.min(Config.maxResearchMutationBoostPercent, mutationBoost);
                chance += mutationBoost;
            }

            if (chance > world.rand.nextFloat() * 100) {
                breedingTracker.registerMutation(mutation);
                return BeeManager.beeRoot.getKaryotype().templateAsChromosomes(mutation.getTemplate());
            }
        }

        return null;
    }

    /* FLOWERS */
    @Override
    @Nullable
    public Optional<IIndividual> retrievePollen(IBeeHousing housing) {
        IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

        int chance = Math.round(genome.getActiveValue(BeeChromosomes.FLOWERING) * beeModifier.getFloweringModifier(
                getGenome(),
                1f
        ));

        World world = housing.getWorldObj();
        Random random = world.rand;

        // Correct speed
        if (random.nextInt(100) >= chance) {
            return Optional.empty();
        }

        Vector3i area = getArea(genome, beeModifier);
        Vector3i offset = new Vector3i(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
        BlockPos housingPos = housing.getCoordinates();

        IIndividual pollen = null;

        for (int i = 0; i < 20; i++) {
            BlockPos randomPos = VectUtil.getRandomPositionInArea(random, area);
            BlockPos blockPos = VectUtil.add(housingPos, randomPos, offset);
            ICheckPollinatable pitcher = TileUtil.getTile(world, blockPos, ICheckPollinatable.class);
            if (pitcher != null) {
                if (genome.getActiveAllele(BeeChromosomes.FLOWER_PROVIDER)
                          .getProvider()
                          .isAcceptedPollinatable(world, pitcher)) {
                    pollen = pitcher.getPollen();
                }
            } else {
                pollen = GeneticsUtil.getPollen(world, blockPos).orElse(null);
            }

            if (pollen != null) {
                return Optional.of(pollen);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean pollinateRandom(IBeeHousing housing, IIndividual pollen) {

        IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

        int chance = (int) (
                genome.getActiveValue(BeeChromosomes.FLOWERING) * beeModifier.getFloweringModifier(
                        getGenome(),
                        1f
                ));

        World world = housing.getWorldObj();
        Random random = world.rand;

        // Correct speed
        if (random.nextInt(100) >= chance) {
            return false;
        }

        Vector3i area = getArea(genome, beeModifier);
        Vector3i offset = new Vector3i(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
        BlockPos housingPos = housing.getCoordinates();

        for (int i = 0; i < 30; i++) {

            BlockPos randomPos = VectUtil.getRandomPositionInArea(random, area);
            BlockPos posBlock = VectUtil.add(housingPos, randomPos, offset);

            ICheckPollinatable checkPollinatable = GeneticsUtil.getCheckPollinatable(world, posBlock);
            if (checkPollinatable == null) {
                continue;
            }

            if (!genome.getActiveAllele(BeeChromosomes.FLOWER_PROVIDER)
                       .getProvider()
                       .isAcceptedPollinatable(world, checkPollinatable)) {
                continue;
            }
            if (!checkPollinatable.canMateWith(pollen)) {
                continue;
            }

            IPollinatable realPollinatable = GeneticsUtil.getOrCreatePollinatable(
                    housing.getOwner(),
                    world,
                    posBlock,
                    Config.pollinateVanillaTrees
            );

            if (realPollinatable != null) {
                realPollinatable.mateWith(pollen);
                return true;
            }
        }

        return false;
    }

    @Override
    public Optional<BlockPos> plantFlowerRandom(IBeeHousing housing, List<BlockState> potentialFlowers) {
        IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

        int chance = Math.round(genome.getActiveValue(BeeChromosomes.FLOWERING) * beeModifier.getFloweringModifier(
                getGenome(),
                1f
        ));

        World world = housing.getWorldObj();
        Random random = world.rand;

        // Correct speed
        if (random.nextInt(100) >= chance) {
            return Optional.empty();
        }

        // Gather required info
        IFlowerProvider provider = genome.getActiveAllele(BeeChromosomes.FLOWER_PROVIDER).getProvider();
        Vector3i area = getArea(genome, beeModifier);
        Vector3i offset = new Vector3i(-area.getX() / 2, -area.getY() / 4, -area.getZ() / 2);
        BlockPos housingPos = housing.getCoordinates();

        for (int i = 0; i < 10; i++) {
            BlockPos randomPos = VectUtil.getRandomPositionInArea(random, area);
            BlockPos posBlock = VectUtil.add(housingPos, randomPos, offset);

            if (FlowerManager.flowerRegistry.growFlower(
                    provider.getFlowerType(),
                    world,
                    this,
                    posBlock,
                    potentialFlowers
            )) {
                return Optional.of(posBlock);
            }
        }

        return Optional.empty();
    }

    private static Vector3i getArea(IGenome genome, IBeeModifier beeModifier) {
        Vector3i genomeTerritory = genome.getActiveValue(BeeChromosomes.TERRITORY);
        float housingModifier = beeModifier.getTerritoryModifier(genome, 1f);
        return VectUtil.scale(genomeTerritory, housingModifier * 3.0f);
    }
}
