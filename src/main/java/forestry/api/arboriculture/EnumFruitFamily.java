/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.client.resources.I18n;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFruitFamily;

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

	//TODO - these must only be called on client. Or Should I return TranslationTextComponent
	@Override
	public String getName() {
		return I18n.format("for.family." + uid);
	}

	@Override
	public String getDescription() {
		return I18n.format("for.family." + uid + ".description");
	}

}
