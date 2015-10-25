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

import forestry.api.genetics.IAlleleArea;
import forestry.core.vect.Vect;
import net.minecraft.util.BlockPos;

public class AlleleArea extends AlleleCategorized implements IAlleleArea {

	private final BlockPos area;

	public AlleleArea(String modId, String category, String name, BlockPos value, boolean isDominant) {
		super(modId, category, name, isDominant);
		this.area = value;
	}

	@Override
	public BlockPos getValue() {
		return area;
	}

	public Vect getArea() {
		return new Vect(area);
	}
}
