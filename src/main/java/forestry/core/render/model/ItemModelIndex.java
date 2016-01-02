/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.render.model;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class ItemModelIndex {

	public final ModelResourceLocation modelLocation;
	public final ItemRenderer model;
	public final Item item;

	public ItemModelIndex(ModelResourceLocation modelLocation, ItemRenderer model, Item item) {
		this.modelLocation = modelLocation;
		this.model = model;
		this.item = item;
	}

}
