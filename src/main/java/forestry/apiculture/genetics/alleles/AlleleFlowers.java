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

import javax.annotation.Nonnull;

import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IFlowerProvider;
import forestry.core.genetics.alleles.AlleleCategorized;

public class AlleleFlowers extends AlleleCategorized implements IAlleleFlowers {
	@Nonnull
	private final IFlowerProvider provider;

	public AlleleFlowers(@Nonnull String modId, @Nonnull String category, @Nonnull String name, @Nonnull IFlowerProvider provider, boolean isDominant) {
		super(modId, category, name, isDominant);
		this.provider = provider;
	}

	@Nonnull
	@Override
	public IFlowerProvider getProvider() {
		return provider;
	}

	@Nonnull
	@Override
	public String getName() {
		return getProvider().getDescription();
	}

}
