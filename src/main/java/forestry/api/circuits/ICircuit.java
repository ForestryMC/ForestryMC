/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

import java.util.List;

import net.minecraft.network.chat.Component;

public interface ICircuit {
	String getUID();

	String getTranslationKey();

	default Component getDisplayName() {
		return Component.translatable(getTranslationKey());
	}

	boolean isCircuitable(Object tile);

	void onInsertion(int slot, Object tile);

	void onLoad(int slot, Object tile);

	void onRemoval(int slot, Object tile);

	void onTick(int slot, Object tile);

	void addTooltip(List<Component> list);
}
