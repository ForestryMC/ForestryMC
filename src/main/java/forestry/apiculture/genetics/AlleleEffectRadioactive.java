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

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.gadgets.BlockAlveary;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.core.utils.DamageSourceForestry;
import forestry.core.vect.Vect;

public class AlleleEffectRadioactive extends AlleleEffectThrottled {

	private static final DamageSource damageSourceBeeRadioactive = new DamageSourceForestry("bee.radioactive");

	public AlleleEffectRadioactive() {
		super("radioactive", true, 40, false, true);
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorld();

		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr).multiply(2);
		Vect offset = area.multiply(-1 / 2.0f);

		// Radioactivity hurts players and mobs
		Vect min = new Vect(housing.getCoordinates()).add(offset);
		Vect max = min.add(area);

		AxisAlignedBB hurtBox = AxisAlignedBB.fromBounds(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());

		@SuppressWarnings("rawtypes")
		List list = housing.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, hurtBox);

		for (Object obj : list) {
			EntityLivingBase entity = (EntityLivingBase) obj;

			int damage = 8;

			// Players are not attacked if they wear a full set of apiarist's
			// armor.
			if (entity instanceof EntityPlayer) {
				int count = ItemArmorApiarist.wearsItems((EntityPlayer) entity, getUID(), true);
				// Full set, no damage/effect
				if (count > 3) {
					continue;
				} else if (count > 2) {
					damage = 1;
				} else if (count > 1) {
					damage = 2;
				} else if (count > 0) {
					damage = 3;
				}
			}

			entity.attackEntityFrom(damageSourceBeeRadioactive, damage);

		}

		Random rand = housing.getWorld().rand;
		// Radioactivity destroys environment
		for (int i = 0; i < 20; i++) {

			Vect randomPos = Vect.getRandomPositionInArea(rand, area);

			Vect posHousing = new Vect(housing.getCoordinates());
			Vect posBlock = randomPos.add(posHousing);
			posBlock = posBlock.add(offset);

			if (posBlock.getY() <= 1 || posBlock.getY() >= housing.getWorld().getActualHeight()) {
				continue;
			}

			// Don't destroy ourselves or blocks below us.
			if (posBlock.getX() == posHousing.getX() && posBlock.getY() == posHousing.getZ() && posBlock.getZ() <= posHousing.getZ()) {
				continue;
			}

			if (world.isAirBlock(posBlock.getPos())) {
				continue;
			}

			Block block = world.getBlockState(posBlock.getPos()).getBlock();

			if (block instanceof BlockAlveary) {
				continue;
			}

			TileEntity tile = world.getTileEntity(posBlock.getPos());
			if (tile instanceof IBeeHousing) {
				continue;
			}
			if (tile instanceof TileAlveary) {
				continue;
			}

			if (block.getBlockHardness(world, posBlock.getPos()) < 0) {
				continue;
			}

			world.setBlockToAir(posBlock.getPos());
			break;
		}

		return storedData;
	}
}
