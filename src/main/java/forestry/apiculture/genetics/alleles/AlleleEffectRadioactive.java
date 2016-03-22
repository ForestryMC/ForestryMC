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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.blocks.BlockAlveary;
import forestry.core.utils.DamageSourceForestry;
import forestry.core.utils.vect.Vect;

public class AlleleEffectRadioactive extends AlleleEffectThrottled {

	private static final DamageSource damageSourceBeeRadioactive = new DamageSourceForestry("bee.radioactive");

	public AlleleEffectRadioactive() {
		super("radioactive", true, 40, false, true);
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		harmEntities(genome, housing);

		return destroyEnvironment(genome, storedData, housing);
	}

	private void harmEntities(IBeeGenome genome, IBeeHousing housing) {
		List<EntityLivingBase> entities = getEntitiesInRange(genome, housing, EntityLivingBase.class);
		for (EntityLivingBase entity : entities) {
			int damage = 8;

			// Entities are not attacked if they wear a full set of apiarist's armor.
			int count = BeeManager.armorApiaristHelper.wearsItems(entity, getUID(), true);
			damage -= (count * 2);
			if (damage <= 0) {
				continue;
			}

			entity.attackEntityFrom(damageSourceBeeRadioactive, damage);
		}
	}

	private static IEffectData destroyEnvironment(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		World world = housing.getWorld();
		Random rand = world.rand;

		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr).multiply(2);
		Vect offset = area.multiply(-1 / 2.0f);
		Vect posHousing = new Vect(housing.getCoordinates());

		for (int i = 0; i < 20; i++) {
			Vect randomPos = Vect.getRandomPositionInArea(rand, area);
			Vect posBlock = randomPos.add(posHousing);
			posBlock = posBlock.add(offset);

			if (posBlock.y <= 1 || posBlock.y >= housing.getWorld().getActualHeight()) {
				continue;
			}

			// Don't destroy ourselves or blocks below us.
			if (posBlock.x == posHousing.x && posBlock.z == posHousing.z && posBlock.y <= posHousing.y) {
				continue;
			}

			if (world.isAirBlock(posBlock.x, posBlock.y, posBlock.z)) {
				continue;
			}

			Block block = world.getBlock(posBlock.x, posBlock.y, posBlock.z);

			if (block instanceof BlockAlveary) {
				continue;
			}

			TileEntity tile = world.getTileEntity(posBlock.x, posBlock.y, posBlock.z);
			if (tile instanceof IBeeHousing) {
				continue;
			}

			if (block.getBlockHardness(world, posBlock.x, posBlock.y, posBlock.z) < 0) {
				continue;
			}

			world.setBlockToAir(posBlock.x, posBlock.y, posBlock.z);
			break;
		}

		return storedData;
	}
}
