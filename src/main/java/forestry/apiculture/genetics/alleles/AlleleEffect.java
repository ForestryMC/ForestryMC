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
import net.minecraft.util.BlockPos;
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
import forestry.core.utils.BlockUtil;
import forestry.core.utils.EntityUtil;

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
		Proxies.render.addBeeHiveFX("particles/swarm_bee", housing.getWorld(), beeFXCoordinates.xCoord, beeFXCoordinates.yCoord, beeFXCoordinates.zCoord, genome.getPrimary().getSpriteColour(0));
		return storedData;
	}

	protected Vec3 getFXCoordinates(IBeeHousing housing) {
		try {
			return housing.getBeeFXCoordinates();
		} catch (Throwable error) {
			// getBeeFXCoordinates() is only newly added to the API, fall back on getCoordinates()
			BlockPos coordinates = housing.getCoordinates();
			return new Vec3(coordinates.getX() + 0.5D, coordinates.getY() + 0.5D, coordinates.getZ() + 0.5D);
		}
	}

	protected BlockPos getModifiedArea(IBeeGenome genome, IBeeHousing housing) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		float territoryModifier = beeModifier.getTerritoryModifier(genome, 1f);

		BlockPos area = new BlockPos(genome.getTerritory()[0], genome.getTerritory()[1], genome.getTerritory()[2]);
		area = BlockUtil.multiply(area, territoryModifier);

		if (area.getX() < 1) {
			area = new BlockPos(1, area.getY(), area.getZ());
		}
		if (area.getY() < 1) {
			area = new BlockPos(area.getX(), 1, area.getZ());
		}
		if (area.getZ() < 1) {
			area= new BlockPos(area.getX(), area.getY(), 1);
		}

		return new BlockPos(area);
	}

	public static AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		float territoryModifier = beeModifier.getTerritoryModifier(genome, 1.0f);

		BlockPos area = new BlockPos(genome.getTerritory()[0], genome.getTerritory()[1], genome.getTerritory()[2]);
		area = BlockUtil.multiply(area, territoryModifier);
		BlockPos offset = BlockUtil.multiply(new BlockPos(area), -1 / 2.0f);

		BlockPos min = new BlockPos(housing.getCoordinates()).add(offset);
		BlockPos max = min.add(area);

		return AxisAlignedBB.fromBounds(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
	}

	public static <T extends Entity> List<T> getEntitiesInRange(IBeeGenome genome, IBeeHousing housing, Class<T> entityClass) {
		AxisAlignedBB boundingBox = getBounding(genome, housing);
		return EntityUtil.getEntitiesWithinAABB(housing.getWorld(), entityClass, boundingBox);
	}
}
