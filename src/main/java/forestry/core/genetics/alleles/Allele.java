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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.StatCollector;

import forestry.api.genetics.IAllele;

public abstract class Allele implements IAllele {

	@Nonnull
	private final String uid;
	private final boolean isDominant;
	@Nonnull
	private final String unlocalizedName;

	protected Allele(@Nonnull String uid, @Nonnull String unlocalizedName, boolean isDominant) {
		this.uid = uid;
		this.isDominant = isDominant;
		this.unlocalizedName = unlocalizedName;
	}

	@Nonnull
	@Override
	public String getUID() {
		return uid;
	}

	@Override
	public boolean isDominant() {
		return isDominant;
	}

	@Nonnull
	@Override
	public String getName() {
		return StatCollector.translateToLocal(getUnlocalizedName());
	}

	@Nonnull
	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public String toString() {
		return uid;
	}

	public static boolean equals(@Nullable IAllele allele1, @Nullable IAllele allele2) {
		if (allele1 == allele2) {
			return true;
		}
		if (allele1 == null || allele2 == null) {
			return false;
		}

		return allele1.getUID().equals(allele2.getUID());
	}
}
