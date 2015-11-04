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
package forestry.core.genetics.alleles;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllelePlantType;

public class AllelePlantType extends AlleleCategorized implements IAllelePlantType {

	public static IAllelePlantType plantTypeNone;
	public static IAllelePlantType plantTypePlains;
	public static IAllelePlantType plantTypeDesert;
	public static IAllelePlantType plantTypeBeach;
	public static IAllelePlantType plantTypeCave;
	public static IAllelePlantType plantTypeWater;
	public static IAllelePlantType plantTypeNether;
	public static IAllelePlantType plantTypeCrop;

	public static void createAlleles() {
		List<IAllelePlantType> alleles = Arrays.asList(
				plantTypeNone = new AllelePlantType("none", EnumSet.noneOf(EnumPlantType.class), true),
				plantTypePlains = new AllelePlantType(EnumPlantType.Plains),
				plantTypeDesert = new AllelePlantType(EnumPlantType.Desert),
				plantTypeBeach = new AllelePlantType(EnumPlantType.Beach),
				plantTypeCave = new AllelePlantType(EnumPlantType.Cave),
				plantTypeWater = new AllelePlantType(EnumPlantType.Water),
				plantTypeNether = new AllelePlantType(EnumPlantType.Nether),
				plantTypeCrop = new AllelePlantType(EnumPlantType.Crop)
		);

		for (IAllelePlantType allele : alleles) {
			AlleleManager.alleleRegistry.registerAllele(allele, EnumTreeChromosome.PLANT);
		}
	}

	private final EnumSet<EnumPlantType> types;

	protected AllelePlantType(EnumPlantType type) {
		this(type.toString().toLowerCase(Locale.ENGLISH), EnumSet.of(type), false);
	}

	protected AllelePlantType(String name, EnumSet<EnumPlantType> types, boolean isDominant) {
		super("forestry", "plantType", name, isDominant);
		this.types = types;
	}

	public EnumSet<EnumPlantType> getPlantTypes() {
		return types;
	}
}
