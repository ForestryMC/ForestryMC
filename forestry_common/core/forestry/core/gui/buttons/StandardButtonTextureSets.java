/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui.buttons;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum StandardButtonTextureSets implements IButtonTextureSet{

	LARGE_BUTTON(0, 0, 20, 200),
	SMALL_BUTTON(0, 80, 15, 200),
    LOCKED_BUTTON(224, 0, 16, 16),
    UNLOCKED_BUTTON(240, 0, 16, 16),
    LEFT_BUTTON(204, 0, 16, 10),
    RIGHT_BUTTON(214, 0, 16, 10),
    LEFT_BUTTON_SMALL(238, 220, 12, 9),
    RIGHT_BUTTON_SMALL(247, 220, 12, 9);
    private final int x, y, height, width;

    private StandardButtonTextureSets(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

}
