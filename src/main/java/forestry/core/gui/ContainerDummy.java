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
package forestry.core.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class ContainerDummy extends Container {
    public static final ContainerDummy instance = new ContainerDummy();

    private ContainerDummy() {
        super(null, 0);
    }

    @Override
    public boolean canInteractWith(PlayerEntity PlayerEntity) {
        return true;
    }

}
