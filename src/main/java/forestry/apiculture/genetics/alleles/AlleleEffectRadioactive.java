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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import genetics.api.individual.IGenome;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.blocks.BlockAlveary;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.DamageSourceForestry;
import forestry.core.utils.VectUtil;

public class AlleleEffectRadioactive extends AlleleEffectThrottled {

	private static final DamageSource damageSourceBeeRadioactive = new DamageSourceForestry("bee.radioactive");

	public AlleleEffectRadioactive() {
		super("radioactive", true, 40, false, true);
	}

	@Override
	public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		harmEntities(genome, housing);

		return destroyEnvironment(genome, storedData, housing);
	}

	private void harmEntities(IGenome genome, IBeeHousing housing) {
		List<LivingEntity> entities = getEntitiesInRange(genome, housing, LivingEntity.class);
		for (LivingEntity entity : entities) {
			int damage = 8;

			// Entities are not attacked if they wear a full set of apiarist's armor.
			int count = BeeManager.armorApiaristHelper.wearsItems(entity, getRegistryName(), true);
			damage -= count * 2;
			if (damage <= 0) {
				continue;
			}

			entity.attackEntityFrom(damageSourceBeeRadioactive, damage);
		}
	}

	private static IEffectData destroyEnvironment(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		World world = housing.getWorldObj();
		Random rand = world.rand;

		Vec3i area = VectUtil.scale(genome.getActiveValue(BeeChromosomes.TERRITORY), 2);
		Vec3i offset = VectUtil.scale(area, -1 / 2.0f);
		BlockPos posHousing = housing.getCoordinates();

		for (int i = 0; i < 20; i++) {
			BlockPos randomPos = VectUtil.getRandomPositionInArea(rand, area);
			BlockPos posBlock = randomPos.add(posHousing);
			posBlock = posBlock.add(offset);

			if (posBlock.getY() <= 1 || posBlock.getY() >= world.getActualHeight()) {
				continue;
			}

			// Don't destroy ourselves or blocks below us.
			if (posBlock.getX() == posHousing.getX() && posBlock.getZ() == posHousing.getZ() && posBlock.getY() <= posHousing.getY()) {
				continue;
			}

			if (!world.isBlockLoaded(posBlock) || world.isAirBlock(posBlock)) {
				continue;
			}

			BlockState blockState = world.getBlockState(posBlock);
			Block block = blockState.getBlock();

			if (block instanceof BlockAlveary) {
				continue;
			}

			TileEntity tile = TileUtil.getTile(world, posBlock);
			if (tile instanceof IBeeHousing) {
				continue;
			}

			if (blockState.getBlockHardness(world, posBlock) < 0) {
				continue;
			}

			BlockUtil.setBlockToAirWithSound(world, posBlock, blockState);
			break;
		}

		return storedData;
	}
}
