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

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleArea;
import forestry.api.genetics.IAlleleBoolean;
import forestry.api.genetics.IAlleleFactory;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IFlowerProvider;
import forestry.apiculture.genetics.alleles.AlleleFlowers;

public class AlleleFactory implements IAlleleFactory {

	@Override
	public IAlleleFloat createFloat(String modId, String category, String valueName, float value, boolean isDominant, IChromosomeType... types) {
		IAlleleFloat alleleFloat = new AlleleFloat(modId, category, valueName, value, isDominant);
		AlleleManager.alleleRegistry.registerAllele(alleleFloat, types);
		return alleleFloat;
	}

	@Override
	public IAlleleArea createArea(String modId, String category, String valueName, int xDimValue, int yDimValue, int zDimValue, boolean isDominant, IChromosomeType... types) {
		IAlleleArea alleleArea = new AlleleArea(modId, category, valueName, new int[]{xDimValue, yDimValue, zDimValue}, isDominant);
		AlleleManager.alleleRegistry.registerAllele(alleleArea, types);
		return alleleArea;
	}

	@Override
	public IAlleleInteger createInteger(String modId, String category, String valueName, int value, boolean isDominant, IChromosomeType... types) {
		IAlleleInteger alleleInteger = new AlleleInteger(modId, category, valueName, value, isDominant);
		AlleleManager.alleleRegistry.registerAllele(alleleInteger, types);
		return alleleInteger;
	}

	@Override
	public IAlleleBoolean createBoolean(String modId, String category, boolean value, boolean isDominant, IChromosomeType... types) {
		IAlleleBoolean alleleBoolean = new AlleleBoolean(modId, category, value, isDominant);
		AlleleManager.alleleRegistry.registerAllele(alleleBoolean, types);
		return alleleBoolean;
	}

	@Override
	public IAlleleFlowers createFlowers(String modId, String category, String valueName, IFlowerProvider value, boolean isDominant, IChromosomeType... types) {
		IAlleleFlowers alleleFlowers = new AlleleFlowers(modId, category, valueName, value, isDominant);
		AlleleManager.alleleRegistry.registerAllele(alleleFlowers, types);
		return alleleFlowers;
	}

	@Override
	public IAlleleFloat createFloat(String modId, String category, String name, float value, boolean isDominant) {
		IAlleleFloat alleleFloat = new AlleleFloat(modId, category, name, value, isDominant);
		AlleleManager.alleleRegistry.registerAllele(alleleFloat);
		return alleleFloat;
	}

	@Override
	public IAlleleArea createArea(String modId, String category, String name, int xDim, int yDim, int zDim, boolean isDominant) {
		IAlleleArea alleleArea = new AlleleArea(modId, category, name, new int[]{xDim, yDim, zDim}, isDominant);
		AlleleManager.alleleRegistry.registerAllele(alleleArea);
		return alleleArea;
	}

	@Override
	public IAlleleInteger createInteger(String modId, String category, String valueName, int value, boolean isDominant) {
		IAlleleInteger alleleInteger = new AlleleInteger(modId, category, valueName, value, isDominant);
		AlleleManager.alleleRegistry.registerAllele(alleleInteger);
		return alleleInteger;
	}

	@Override
	public IAlleleBoolean createBoolean(String modId, String category, boolean value, boolean isDominant) {
		IAlleleBoolean alleleBoolean = new AlleleBoolean(modId, category, value, isDominant);
		AlleleManager.alleleRegistry.registerAllele(alleleBoolean);
		return alleleBoolean;
	}

	@Override
	public IAlleleFlowers createFlowers(String modId, String category, String name, IFlowerProvider flowerProvider, boolean isDominant) {
		IAlleleFlowers alleleFlowers = new AlleleFlowers(modId, category, name, flowerProvider, isDominant);
		AlleleManager.alleleRegistry.registerAllele(alleleFlowers);
		return alleleFlowers;
	}
}
