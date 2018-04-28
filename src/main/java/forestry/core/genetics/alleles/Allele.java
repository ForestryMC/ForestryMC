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

import forestry.api.genetics.IAllele;
import forestry.core.utils.Translator;

public abstract class Allele implements IAllele {
	private final String modId;
	private final String uid;
	private final boolean isDominant;
	private final String unlocalizedName;

	protected Allele(String modId, String uid, String unlocalizedName, boolean isDominant) {
		this.modId = modId;
		this.uid = uid;
		this.isDominant = isDominant;
		this.unlocalizedName = unlocalizedName;
	}

	@Override
	public String getUID() {
		return uid;
	}

	@Override
	public String getModID() {
		return modId;
	}

	@Override
	public boolean isDominant() {
		return isDominant;
	}

	@Override
	@Deprecated
	public String getName() {
		return Translator.translateToLocal(getUnlocalizedName());
	}

	@Override
	public String getAlleleName() {
		return Translator.translateToLocal(getUnlocalizedName());
	}

	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public String toString() {
		return uid;
	}
}
