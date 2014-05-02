/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui.buttons;

import forestry.core.gui.tooltips.ToolTip;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum LockButtonState implements IMultiButtonState {

    UNLOCKED(new ButtonTextureSet(224, 0, 16, 16)),
    LOCKED(new ButtonTextureSet(240, 0, 16, 16));
    public static final LockButtonState[] VALUES = values();
    private final IButtonTextureSet texture;

    private LockButtonState(IButtonTextureSet texture) {
        this.texture = texture;
    }

    @Override
    public String getLabel() {
        return "";
    }

    @Override
    public IButtonTextureSet getTextureSet() {
        return texture;
    }

    @Override
    public ToolTip getToolTip() {
        return null;
    }

}
