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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.apiculture.BeeChromosome;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IFlowerProvider;
import forestry.core.genetics.Genome;
import forestry.core.genetics.alleles.AlleleArea;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.genetics.alleles.AlleleTolerance;
import forestry.core.utils.vect.Vect;

public class BeeGenome extends Genome<BeeChromosome> implements IBeeGenome {
	/**
	 * 0 - Species (determines product)
	 * 1 - Speed
	 * 2 - Lifespan
	 * 3 - Fertility (Maximum number of offspring)
	 * 4 - Preferred temperature Icy: Snow biomes Cold: Tundra/Steppe, Extreme Mountains/Hills? Normal: Plains, Forests, Mountains Hot: Desert Hellish: Nether
	 * 5 - Temperature tolerance (Range +/-)
	 * 6 - Nocturnal
	 * 7 - Preferred humidity (Arid - Normal - Damp)
	 * 8 - Humidity tolerance (Range +/-)
	 * 9 - Flight interference tolerance (stuff falling from the sky/other hindrances -> tolerates dampness + flight interference tolerance => rain resistance)
	 * 10 - Cave dwelling
	 * 11 - Required flowers
	 * 12 - Flower plant chance
	 * 13 - Territory
	 */

	private static final LoadingCache<NBTTagCompound, BeeGenome> beeGenomeCache = CacheBuilder.newBuilder()
			.maximumSize(128)
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build(new CacheLoader<NBTTagCompound, BeeGenome>() {
				@Override
				public BeeGenome load(@Nonnull NBTTagCompound tagCompound) {
					return new BeeGenome(tagCompound);
				}
			});

	public static BeeGenome fromNBT(NBTTagCompound nbtTagCompound) {
		if (nbtTagCompound == null) {
			return null;
		}

		return beeGenomeCache.getUnchecked(nbtTagCompound);
	}

	/* CONSTRUCTOR */
	private BeeGenome(@Nonnull NBTTagCompound nbttagcompound) {
		super(nbttagcompound);
	}

	public BeeGenome(@Nonnull ImmutableMap<BeeChromosome, IChromosome> chromosomes) {
		super(chromosomes);
	}

	// NBT RETRIEVAL
	public static IAlleleBeeSpecies getSpecies(ItemStack itemStack) {
		if (!BeeManager.beeRoot.isMember(itemStack)) {
			return null;
		}

		IAlleleSpecies species = getSpeciesDirectly(itemStack);
		if (species instanceof IAlleleBeeSpecies) {
			return (IAlleleBeeSpecies) species;
		}

		return (IAlleleBeeSpecies) getActiveAllele(itemStack, BeeChromosome.SPECIES, BeeManager.beeRoot);
	}

	// / INFORMATION RETRIEVAL
	@Nonnull
	@Override
	public IAlleleBeeSpecies getPrimary() {
		return (IAlleleBeeSpecies) getActiveAllele(BeeChromosome.SPECIES);
	}

	@Nonnull
	@Override
	public IAlleleBeeSpecies getSecondary() {
		return (IAlleleBeeSpecies) getInactiveAllele(BeeChromosome.SPECIES);
	}

	@Override
	public float getSpeed() {
		return ((IAlleleFloat) getActiveAllele(BeeChromosome.SPEED)).getValue();
	}

	@Override
	public int getLifespan() {
		return ((IAlleleInteger) getActiveAllele(BeeChromosome.LIFESPAN)).getValue();
	}

	@Override
	public int getFertility() {
		return ((IAlleleInteger) getActiveAllele(BeeChromosome.FERTILITY)).getValue();
	}

	@Nonnull
	@Override
	public EnumTolerance getToleranceTemp() {
		return ((AlleleTolerance) getActiveAllele(BeeChromosome.TEMPERATURE_TOLERANCE)).getValue();
	}

	@Override
	public boolean getNocturnal() {
		return ((AlleleBoolean) getActiveAllele(BeeChromosome.NEVER_SLEEPS)).getValue();
	}

	@Nonnull
	@Override
	public EnumTolerance getToleranceHumid() {
		return ((AlleleTolerance) getActiveAllele(BeeChromosome.HUMIDITY_TOLERANCE)).getValue();
	}

	@Override
	public boolean getTolerantFlyer() {
		return ((AlleleBoolean) getActiveAllele(BeeChromosome.TOLERANT_FLYER)).getValue();
	}

	@Override
	public boolean getCaveDwelling() {
		return ((AlleleBoolean) getActiveAllele(BeeChromosome.CAVE_DWELLING)).getValue();
	}

	@Nonnull
	@Override
	public IFlowerProvider getFlowerProvider() {
		return ((IAlleleFlowers) getActiveAllele(BeeChromosome.FLOWER_PROVIDER)).getProvider();
	}

	@Override
	public int getFlowering() {
		return ((IAlleleInteger) getActiveAllele(BeeChromosome.FLOWERING)).getValue();
	}

	@Override
	public int[] getTerritory() {
		Vect area = ((AlleleArea) getActiveAllele(BeeChromosome.TERRITORY)).getArea();
		return area.toArray();
	}

	@Nonnull
	@Override
	public IAlleleBeeEffect getEffect() {
		return (IAlleleBeeEffect) getActiveAllele(BeeChromosome.EFFECT);
	}

	@Nonnull
	@Override
	public IBeeRoot getSpeciesRoot() {
		return BeeManager.beeRoot;
	}
}
