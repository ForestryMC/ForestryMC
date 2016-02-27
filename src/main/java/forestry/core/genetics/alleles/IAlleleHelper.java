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

import java.util.Map;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;

public interface IAlleleHelper {

	<C extends Enum<C> & IChromosomeType<C>> void set(Map<C, IAllele> alleles, C chromosomeType, IAllele allele);

	<C extends Enum<C> & IChromosomeType<C>> void set(Map<C, IAllele> alleles, C chromosomeType, IAlleleValue value);

	<C extends Enum<C> & IChromosomeType<C>> void set(Map<C, IAllele> alleles, C chromosomeType, boolean value);

	<C extends Enum<C> & IChromosomeType<C>> void set(Map<C, IAllele> alleles, C chromosomeType, int value);

}
