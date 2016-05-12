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
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IEffectData;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleCategorized;
import forestry.core.proxy.Proxies;
import forestry.core.utils.VectUtil;

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
				effectBeatific = new AlleleEffectPotion("beatific", false, MobEffects.REGENERATION, 100),
				effectMiasmic = new AlleleEffectPotion("miasmic", false, MobEffects.POISON, 600, 100, 0.1f),
				effectMisanthrope = new AlleleEffectMisanthrope(),
				effectGlacial = new AlleleEffectGlacial(),
				effectRadioactive = new AlleleEffectRadioactive(),
				effectCreeper = new AlleleEffectCreeper(),
				effectIgnition = new AlleleEffectIgnition(),
				effectExploration = new AlleleEffectExploration(),
				effectFestiveEaster = new AlleleEffectNone("festiveEaster", true),
				effectSnowing = new AlleleEffectSnowing(),
				effectDrunkard = new AlleleEffectPotion("drunkard", false, MobEffects.NAUSEA, 100),
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
		super(Constants.MOD_ID, "effect", valueName, isDominant);
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
		Vec3d origin = housing.getBeeFXCoordinates();
		IBeekeepingLogic beekeepingLogic = housing.getBeekeepingLogic();
		List<BlockPos> flowerPositions = beekeepingLogic.getFlowerPositions();

		int beeColor = genome.getPrimary().getSpriteColour(0);
		Proxies.render.addBeeHiveFX(housing.getWorldObj(), origin.xCoord, origin.yCoord, origin.zCoord, beeColor, flowerPositions);
		return storedData;
	}

	protected Vec3i getModifiedArea(IBeeGenome genome, IBeeHousing housing) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		float territoryModifier = beeModifier.getTerritoryModifier(genome, 1f);

		Vec3i area = VectUtil.scale(genome.getTerritory(), territoryModifier);
		int x = area.getX();
		int y = area.getY();
		int z = area.getZ();

		if (x < 1) {
			x = 1;
		}
		if (y < 1) {
			y = 1;
		}
		if (z < 1) {
			z = 1;
		}

		return new Vec3i(x, y, z);
	}

	public static AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing) {
		IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
		float territoryModifier = beeModifier.getTerritoryModifier(genome, 1.0f);

		Vec3i area = VectUtil.scale(genome.getTerritory(), territoryModifier);
		Vec3i offset = VectUtil.scale(area, -1 / 2.0f);

		BlockPos min = housing.getCoordinates().add(offset);
		BlockPos max = min.add(area);

		return new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
	}

	public static <T extends Entity> List<T> getEntitiesInRange(IBeeGenome genome, IBeeHousing housing, Class<T> entityClass) {
		AxisAlignedBB boundingBox = getBounding(genome, housing);
		return housing.getWorldObj().getEntitiesWithinAABB(entityClass, boundingBox);
	}
}
