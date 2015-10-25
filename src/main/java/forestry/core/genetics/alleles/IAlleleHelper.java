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
import forestry.api.genetics.IChromosomeType;

public interface IAlleleHelper {

	<T extends Enum<T> & IChromosomeType> void set(IAllele[] alleles, T chromosomeType, IAllele allele);

	<T extends Enum<T> & IChromosomeType> void set(IAllele[] alleles, T chromosomeType, IAlleleValue value);

	<T extends Enum<T> & IChromosomeType> void set(IAllele[] alleles, T chromosomeType, boolean value);

	<T extends Enum<T> & IChromosomeType> void set(IAllele[] alleles, T chromosomeType, int value);

}
