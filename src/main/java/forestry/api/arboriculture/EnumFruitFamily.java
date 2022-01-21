/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.TranslatableComponent;

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
	public BaseComponent getName() {
		return new TranslatableComponent("for.family." + uid);
	}

	@Override
	public BaseComponent getDescription() {
		return new TranslatableComponent("for.family." + uid + ".description");
	}

}
