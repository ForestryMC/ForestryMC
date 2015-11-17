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
package forestry.apiculture.genetics.alleles;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IEffectData;
import forestry.core.genetics.alleles.AlleleCategorized;
import forestry.core.proxy.Proxies;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.vect.MutableVect;
import forestry.core.utils.vect.Vect;

public abstract class AlleleEffect extends AlleleCategorized implements IAlleleBeeEffect {

	public static IAlleleBeeEffect effectNone;
	public static IAlleleBeeEffect effectAggressive;
	public static IAlleleBeeEffect effectHeroic;
	public static IAlleleBeeEffect effectBeatific;
	public static IAlleleBeeEffect effectMiasmic;
	public static IAlleleBeeEffect effectMisanthrope;
	public static IAlleleBeeEffect effectGlacial;
	public static IAlleleBeeEffect effectRadioactive;
	public static IAlleleBeeEffect effectCreeper;
	public static IAlleleBeeEffect effectIgnition;
	public static IAlleleBeeEffect effectExploration;
	public static IAlleleBeeEffect effectFestiveEaster;
	public static IAlleleBeeEffect effectSnowing;
	public static IAlleleBeeEffect effectDrunkard;
	public static IAlleleBeeEffect effectReanimation;
	public static IAlleleBeeEffect effectResurrection;
	public static IAlleleBeeEffect effectRepulsion;
	public static IAlleleBeeEffect effectFertile;
	public static IAlleleBeeEffect effectMycophilic;

	public static void createAlleles() {
		List<IAlleleBeeEffect> beeEffects = Arrays.asList(
				effectNone = new AlleleEffectNone("none", true),
				effectAggressive = new AlleleEffectAggressive(),
				effectHeroic = new AlleleEffectHeroic(),
				effectBeatific = new AlleleEffectPotion("beatific", false, Potion.regeneration, 100),
				effectMiasmic = new AlleleEffectPotion("miasmic", false, Potion.poison, 600, 100, 0.1f),
				effectMisanthrope = new AlleleEffectMisanthrope(),
				effectGlacial = new AlleleEffectGlacial(),
				effectRadioactive = new AlleleEffectRadioactive(),
				effectCreeper = new AlleleEffectCreeper(),
				effectIgnition = new AlleleEffectIgnition(),
				effectExploration = new AlleleEffectExploration(),
				effectFestiveEaster = new AlleleEffectNone("festiveEaster", true),
				effectSnowing = new AlleleEffectSnowing(),
				effectDrunkard = new AlleleEffectPotion("drunkard", false, Potion.confusion, 100),
				effectReanimation = new AlleleEffectResurrection("reanimation", AlleleEffectResurrection.getReanimationList()),
				effectResurrection = new AlleleEffectResurrection("resurrection", AlleleEffectResurrection.getResurrectionList()),
				effectRepulsion = new AlleleEffectRepulsion(),
				effectFertile = new AlleleEffectFertile(),
				effectMycophilic = new AlleleEffectFungification()
		);

		for (IAlleleBeeEffect beeEffect : beeEffects) {
			AlleleManager.alleleRegistry.registerAllele(beeEffect, EnumBeeChromosome.EFFECT);
		}
	}

	protected AlleleEffect(String valueName, boolean isDominant) {
		super("forestry", "effect", valueName, isDominant);
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		return null;
	}

	@Override
	public boolean isCombinable() {
		return false;
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		return storedData;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		Vec3 beeFXCoordinates = getFXCoordinates(housing);
		Proxies.render.addBeeHiveFX("particles/swarm_bee", housing.getWorld(), beeFXCoordinates.xCoord, beeFXCoordinates.yCoord, beeFXCoordinates.zCoord, genome.getPrimary().getIconColour(0));
		return storedData;
	}

	protected Vec3 getFXCoordinates(IBeeHousing housing) {
		try {
			return housing.getBeeFXCoordinates();
		} catch (Throwable error) {
			// getBeeFXCoordinates() is only newly added to the API, fall back on getCoordinates()
			ChunkCoordinates coordinates = housing.getCoordinates();
			return Vec3.createVectorHelper(coordinates.posX + 0.5D, coordinates.posY + 0.5D, coordinates.posZ + 0.5D);
		}
	}

	protected Vect getModifiedArea(IBeeGenome genome, IBeeHousing housing) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		float territoryModifier = beeModifier.getTerritoryModifier(genome, 1f);

		MutableVect area = new MutableVect(genome.getTerritory());
		area.multiply(territoryModifier);

		if (area.x < 1) {
			area.x = 1;
		}
		if (area.y < 1) {
			area.y = 1;
		}
		if (area.z < 1) {
			area.z = 1;
		}

		return new Vect(area);
	}

	public static AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		float territoryModifier = beeModifier.getTerritoryModifier(genome, 1.0f);

		MutableVect area = new MutableVect(genome.getTerritory());
		area.multiply(territoryModifier);
		Vect offset = new Vect(area).multiply(-1 / 2.0f);

		Vect min = new Vect(housing.getCoordinates()).add(offset);
		Vect max = min.add(area);

		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	public static <T extends Entity> List<T> getEntitiesInRange(IBeeGenome genome, IBeeHousing housing, Class<T> entityClass) {
		AxisAlignedBB boundingBox = getBounding(genome, housing);
		return EntityUtil.getEntitiesWithinAABB(housing.getWorld(), entityClass, boundingBox);
	}
}
