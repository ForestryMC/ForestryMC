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
package forestry.storage.gui;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiNaturalistBackpack extends GuiForestry<ContainerNaturalistBackpack> {

    public GuiNaturalistBackpack(ContainerNaturalistBackpack container, PlayerInventory inv, ITextComponent title) {
        super(getTextureString(container), container, inv, title);
    }

    private static String getTextureString(ContainerNaturalistBackpack container) {
        return Constants.TEXTURE_PATH_GUI + "backpack.png";
    }

    @Override
    protected void addLedgers() {

    }
}
