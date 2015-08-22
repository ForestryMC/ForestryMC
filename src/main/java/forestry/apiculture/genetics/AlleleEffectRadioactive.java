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
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.apiculture.gadgets.BlockAlveary;
import forestry.apiculture.gadgets.TileAlveary;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.core.utils.DamageSourceForestry;
import forestry.core.vect.Vect;

public class AlleleEffectRadioactive extends AlleleEffectThrottled {

	public static final DamageSource damageSourceBeeRadioactive = new DamageSourceForestry("bee.radioactive");

	public AlleleEffectRadioactive(String uid) {
		super(uid, "radioactive", true, 40, false, true);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorld();

		if (isHalted(storedData, housing)) {
			return storedData;
		}

		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0] * 2, areaAr[1] * 2, areaAr[2] * 2);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		// Radioactivity hurts players and mobs
		Vect min = new Vect(housing.getCoords().getX() + offset.x, housing.getCoords().getY() + offset.y, housing.getCoords().getZ() + offset.z);
		Vect max = new Vect(housing.getCoords().getX() + offset.x + area.x, housing.getCoords().getY() + offset.y + area.y, housing.getCoords().getZ() + offset.z + area.z);

		AxisAlignedBB hurtBox = AxisAlignedBB.fromBounds(min.x, min.y, min.z, max.x, max.y, max.z);

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

			Vect randomPos = new Vect(rand.nextInt(area.x), rand.nextInt(area.y), rand.nextInt(area.z));

			Vect posBlock = randomPos.add(new Vect(housing.getCoords().getX(), housing.getCoords().getY(), housing.getCoords().getZ()));
			posBlock = posBlock.add(offset);

			if (posBlock.y <= 1 || posBlock.y >= housing.getWorld().getActualHeight()) {
				continue;
			}

			// Don't destroy ourself and blocks below us.
			if (posBlock.x == housing.getCoords().getX() && posBlock.z == housing.getCoords().getZ() && posBlock.y <= housing.getCoords().getY()) {
				continue;
			}

			if (world.isAirBlock(new BlockPos(posBlock.x, posBlock.y, posBlock.z))) {
				continue;
			}

			Block block = world.getBlockState(new BlockPos(posBlock.x, posBlock.y, posBlock.z)).getBlock();

			if (block instanceof BlockAlveary) {
				continue;
			}

			TileEntity tile = world.getTileEntity(new BlockPos(posBlock.x, posBlock.y, posBlock.z));
			if (tile instanceof IBeeHousing) {
				continue;
			}
			if (tile instanceof TileAlveary) {
				continue;
			}

			if (block.getBlockHardness(world, new BlockPos(posBlock.x, posBlock.y, posBlock.z)) < 0) {
				continue;
			}

			world.setBlockToAir(new BlockPos(posBlock.x, posBlock.y, posBlock.z));
			break;
		}

		return storedData;
	}
}
