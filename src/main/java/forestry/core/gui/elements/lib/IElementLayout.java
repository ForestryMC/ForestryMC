/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.core.gui.elements.lib;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IElementLayout extends IElementGroup {
    /**
     * @param distance
     * @return
     */
    IElementLayout setDistance(int distance);

    /**
     * @return The distance between the different elements of this layout.
     */
    int getDistance();

    int getSize();
}
