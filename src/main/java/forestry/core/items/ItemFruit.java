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

package forestry.core.items;

import java.util.Locale;

import forestry.api.core.IItemSubtype;

import net.minecraft.item.Item;

public class ItemFruit extends ItemForestryFood {

	public enum EnumFruit implements IItemSubtype {
		CHERRY,
		WALNUT,
		CHESTNUT,
		LEMON,
		PLUM,
		DATES,
		PAPAYA,
		COCONUT;

		private final String name;

		EnumFruit() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	private final EnumFruit type;

	public ItemFruit(EnumFruit type) {
		super(1, 0.2f, (new Item.Properties()));
		this.type = type;
	}

	public EnumFruit getType() {
		return type;
	}

	@Override
	public boolean canBeDepleted() {
		return false;
	}
}
