/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.alleles.AlleleManager;
import net.minecraft.util.text.TranslationTextComponent;

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
    public TranslationTextComponent getName() {
        return new TranslationTextComponent("for.family." + uid);
    }

    @Override
    public TranslationTextComponent getDescription() {
        return new TranslationTextComponent("for.family." + uid + ".description");
    }

}
