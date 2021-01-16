/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

import java.util.List;

import net.minecraft.util.text.ITextComponent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.INbtWritable;

public interface ICircuitBoard extends INbtWritable {
	@OnlyIn(Dist.CLIENT)
	int getPrimaryColor();

	@OnlyIn(Dist.CLIENT)
	int getSecondaryColor();

	@OnlyIn(Dist.CLIENT)
	void addTooltip(List<ITextComponent> list);

	void onInsertion(Object tile);

	void onLoad(Object tile);

	void onRemoval(Object tile);

	void onTick(Object tile);

	ICircuit[] getCircuits();

	/**
	 * Specifies where a circuit can be used.
	 */
	ICircuitSocketType getSocketType();

}
