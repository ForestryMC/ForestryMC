/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui.buttons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiToggleButtonSmall extends GuiToggleButton {

    public GuiToggleButtonSmall(int i, int j, int k, String s, boolean active) {
        this(i, j, k, 200, s, active);
    }

    public GuiToggleButtonSmall(int i, int x, int y, int w, String s, boolean active) {
        super(i, x, y, w, StandardButtonTextureSets.SMALL_BUTTON, s, active);
        this.active = active;
    }

}
