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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;

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
		list.add(new TranslatableComponent(getTranslationKey()).withStyle(ChatFormatting.GRAY));

		int i = 1;
		while (true) {
			String unlocalizedDescription = getTranslationKey() + ".description." + i;
			TranslatableComponent component = new TranslatableComponent(unlocalizedDescription);
			if (!Translator.canTranslate(component)) {
				break;
			}
			list.add(new TextComponent(" - ").append(component).withStyle(ChatFormatting.GRAY));
			i++;
		}
	}
}
