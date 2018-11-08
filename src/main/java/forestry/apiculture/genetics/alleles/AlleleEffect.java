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

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.genetics.IEffectData;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleCategorized;
import forestry.core.render.ParticleRender;
import forestry.core.utils.VectUtil;

public abstract class AlleleEffect extends AlleleCategorized implements IAlleleBeeEffect {
	protected AlleleEffect(String valueName, boolean isDominant) {
		super(Constants.MOD_ID, "effect", valueName, isDominant);
	}

	@Override
	public IEffectData validateStorage(IEffectData storedData) {
		return storedData;
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
	@SideOnly(Side.CLIENT)
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		IBeekeepingLogic beekeepingLogic = housing.getBeekeepingLogic();
		List<BlockPos> flowerPositions = beekeepingLogic.getFlowerPositions();

		ParticleRender.addBeeHiveFX(housing, genome, flowerPositions);
		return storedData;
	}

	public static Vec3i getModifiedArea(IBeeGenome genome, IBeeHousing housing) {
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
