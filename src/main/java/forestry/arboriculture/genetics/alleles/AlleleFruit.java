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
package forestry.arboriculture.genetics.alleles;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IFruitProvider;
import forestry.core.genetics.alleles.AlleleCategorized;

public class AlleleFruit extends AlleleCategorized implements IAlleleFruit {

	public static IAlleleFruit fruitNone;
	public static IAlleleFruit fruitApple;
	public static IAlleleFruit fruitCocoa;
	public static IAlleleFruit fruitChestnut;
	public static IAlleleFruit fruitCoconut;
	public static IAlleleFruit fruitWalnut;
	public static IAlleleFruit fruitCherry;
	public static IAlleleFruit fruitDates;
	public static IAlleleFruit fruitPapaya;
	public static IAlleleFruit fruitLemon;
	public static IAlleleFruit fruitPlum;
	public static IAlleleFruit fruitJujube;

	private final IFruitProvider provider;

	public AlleleFruit(String name, IFruitProvider provider) {
		this(name, provider, false);
	}

	public AlleleFruit(String name, IFruitProvider provider, boolean isDominant) {
		super("forestry", "fruit", name, isDominant);
		this.provider = provider;
	}

	@Override
	public IFruitProvider getProvider() {
		return this.provider;
	}
	
	@Override
	public String getName() {
		return getProvider().getDescription();
	}

}
