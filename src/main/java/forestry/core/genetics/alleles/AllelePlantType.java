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

	@Override
	public EnumSet<EnumPlantType> getPlantTypes() {
		return types;
	}
}
