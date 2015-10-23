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

import java.util.EnumSet;
import java.util.Locale;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.genetics.IAllelePlantType;

public class AllelePlantType extends AlleleCategorized implements IAllelePlantType {

	public static final Allele plantTypeNone = new AllelePlantType("none", EnumSet.noneOf(EnumPlantType.class), true);
	public static final Allele plantTypePlains = new AllelePlantType(EnumPlantType.Plains);
	public static final Allele plantTypeDesert = new AllelePlantType(EnumPlantType.Desert);
	public static final Allele plantTypeBeach = new AllelePlantType(EnumPlantType.Beach);
	public static final Allele plantTypeCave = new AllelePlantType(EnumPlantType.Cave);
	public static final Allele plantTypeWater = new AllelePlantType(EnumPlantType.Water);
	public static final Allele plantTypeNether = new AllelePlantType(EnumPlantType.Nether);
	public static final Allele plantTypeCrop = new AllelePlantType(EnumPlantType.Crop);

	private final EnumSet<EnumPlantType> types;

	protected AllelePlantType(EnumPlantType type) {
		this(type, false);
	}

	protected AllelePlantType(EnumPlantType type, boolean isDominant) {
		this(type.toString().toLowerCase(Locale.ENGLISH), EnumSet.of(type), isDominant);
	}

	protected AllelePlantType(String name, EnumSet<EnumPlantType> types, boolean isDominant) {
		super("forestry", "plantType", name, isDominant);
		this.types = types;
	}

	public EnumSet<EnumPlantType> getPlantTypes() {
		return types;
	}
}
