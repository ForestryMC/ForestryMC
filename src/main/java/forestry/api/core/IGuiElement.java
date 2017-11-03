/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiElement {
	int getX();

	int getY();

	int getWidth();

	int getHeight();

	void setXOffset(int xOffset);

	void setYOffset(int yOffset);

	@Nullable
	IGuiElement getParent();

	void setParent(@Nullable IGuiElement parent);

	void draw(int startX, int startY);

	@SideOnly(Side.CLIENT)
	default List<String> getToolTip(int mouseX, int mouseY){
		return Collections.emptyList();
	}

	@SideOnly(Side.CLIENT)
	boolean isMouseOver(int mouseX, int mouseY);
}
