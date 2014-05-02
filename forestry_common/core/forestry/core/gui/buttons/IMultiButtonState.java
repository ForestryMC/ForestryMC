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
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IMultiButtonState {

    public String getLabel();

    public String name();

    public IButtonTextureSet getTextureSet();

    public ToolTip getToolTip();

}
