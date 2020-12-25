/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.genetics;

import net.minecraft.util.text.TranslationTextComponent;

public interface IFruitFamily {

    /**
     * @return Unique String identifier.
     */
    String getUID();

    /**
     * @return Localized family name for user display.
     */
    TranslationTextComponent getName();

    /**
     * A scientific-y name for this fruit family
     *
     * @return flavour text (may be null)
     */
    String getScientific();

    /**
     * @return Localized description of this fruit family. (May be null.)
     */
    TranslationTextComponent getDescription();

}
