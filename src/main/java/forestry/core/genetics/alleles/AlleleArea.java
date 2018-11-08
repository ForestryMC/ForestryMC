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

import net.minecraft.util.math.Vec3i;

import forestry.api.genetics.IAlleleArea;

public class AlleleArea extends AlleleCategorized implements IAlleleArea {

	private final Vec3i area;

	public AlleleArea(String modId, String category, String name, Vec3i value, boolean isDominant) {
		super(modId, category, name, isDominant);
		this.area = value;
	}

	@Override
	public Vec3i getValue() {
		return area;
	}

	public Vec3i getArea() {
		return area;
	}
}
