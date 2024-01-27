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
package forestry.core.circuits;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.core.utils.Translator;

public abstract class Circuit implements ICircuit {
	private final String uid;

	protected Circuit(String uid) {
		this.uid = uid;

		ChipsetManager.circuitRegistry.registerCircuit(this);
	}

	@Override
	public String getUID() {
		return "forestry." + this.uid;
	}

	@Override
	public String getTranslationKey() {
		return "for.circuit." + this.uid;
	}

	@Override
	public void addTooltip(List<Component> list) {
		list.add(Component.translatable(getTranslationKey()).withStyle(ChatFormatting.GRAY));

		int i = 1;
		while (true) {
			String unlocalizedDescription = getTranslationKey() + ".description." + i;
			Component component = Component.translatable(unlocalizedDescription);
			if (!Translator.canTranslate(component)) {
				break;
			}
			list.add(Component.literal(" - ").append(component).withStyle(ChatFormatting.GRAY));
			i++;
		}
	}
}
