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

import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;
import java.lang.reflect.Field;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MutationEMC extends MutationReqRes {

	private final int emcRequired;
	private final Class<?> condenserClass;
	private final Field emcField;

	public MutationEMC(IAllele allele0, IAllele allele1, IAllele[] template, int chance, ItemStack blockRequired, Class<?> condenserClass, Field emcField,
			int emcRequired) {
		super(allele0, allele1, template, chance, blockRequired);

		this.condenserClass = condenserClass;
		this.emcField = emcField;
		this.emcRequired = emcRequired;
	}

	@Override
	public float getChance(IBeeHousing housing, IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1) {
		float chance = super.getChance(housing, allele0, allele1, genome0, genome1);

		// If we don't have any chance, return at once.
		if (chance <= 0)
			return 0;

		if (emcRequired <= 0)
			return chance;

		World world = housing.getWorld();

		Vect[] possibleTargets = new Vect[] { new Vect(housing.getXCoord() + 1, housing.getYCoord(), housing.getZCoord()),
				new Vect(housing.getXCoord() - 1, housing.getYCoord(), housing.getZCoord()),
				new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord() + 1),
				new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord() - 1) };

		for (Vect target : possibleTargets) {
			if (!world.blockExists(target.x, target.y, target.z))
				continue;

			TileEntity entity = world.getTileEntity(target.x, target.y, target.z);
			if (entity == null)
				continue;

			if (!condenserClass.isInstance(entity)) {
				Proxies.log.warning("Did not find a relay at " + target.toString());
				continue;
			}

			int emc = 0;
			try {
				emc = emcField.getInt(entity);
			} catch (Exception ex) {
				Proxies.log.warning("Failed to fetch EMC information.");
			}

			if (emc < emcRequired * 80)
				continue;

			boolean removedEMC = false;
			try {
				emcField.set(entity, emc - (emcRequired * 80));
				removedEMC = true;
			} catch (Exception ex) {
				Proxies.log.warning("Failed to set EMC information.");
			}

			if (removedEMC)
				return chance;

		}

		return 0;
	}
}
