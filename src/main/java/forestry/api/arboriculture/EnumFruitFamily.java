/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import net.minecraft.util.StatCollector;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IFruitFamily;

public enum EnumFruitFamily implements IFruitFamily {
	PRUNES("prunes", "Prunus domestica"),
	POMES("pomes", "Pomum"),
	JUNGLE("jungle", "Tropicus"),
	NUX("nuts", "Nux");

	private final String uid;
	private final String scientific;

	EnumFruitFamily(String uid, String scientific) {
		this.uid = uid;
		this.scientific = scientific;
		AlleleManager.alleleRegistry.registerFruitFamily(this);
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
	public String getName() {
		return StatCollector.translateToLocal("for.family." + uid);
	}

	@Override
	public String getDescription() {
		return StatCollector.translateToLocal("for.family." + uid + ".description");
	}

}
