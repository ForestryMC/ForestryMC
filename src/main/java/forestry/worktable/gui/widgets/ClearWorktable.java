/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.worktable.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.SoundUtil;
import forestry.worktable.gui.ContainerWorktable;

public class ClearWorktable extends Widget {

    public ClearWorktable(WidgetManager manager, int xPos, int yPos) {
        super(manager, xPos, yPos);
        width = 7;
        height = 7;
    }

    @Override
    public void draw(MatrixStack transform, int startY, int startX) {
    }

    @Override
    public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
        ContainerWorktable.clearRecipe();
        SoundUtil.playButtonClick();
    }
}
