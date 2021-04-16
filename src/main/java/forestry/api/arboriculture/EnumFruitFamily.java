/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.alleles.AlleleManager;

public enum EnumFruitFamily implements IFruitFamily {
	PRUNES("prunes", "Prunus domestica"),
	POMES("pomes", "Pomum"),
	JUNGLE("jungle", "Tropicus"),
	NUX("nuts", "Nux"),
	NONE("none", "None");

	private final String uid;
	private final String scientific;

	EnumFruitFamily(String uid, String scientific) {
		this.uid = uid;
		this.scientific = scientific;
		AlleleManager.geneticRegistry.registerFruitFamily(this);
	}

	@Override
	public String getUID() {
		return "forestry." + uid;
	}

	@Override
	public String getScientific() {
		return this.scientific;
	}

	@Override
	public TextComponent getName() {
		return new TranslationTextComponent("for.family." + uid);
	}

	@Override
	public TextComponent getDescription() {
		return new TranslationTextComponent("for.family." + uid + ".description");
	}

}
