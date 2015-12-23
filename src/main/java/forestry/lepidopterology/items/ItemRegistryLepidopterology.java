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
package forestry.lepidopterology.items;

import forestry.api.lepidopterology.EnumFlutterType;
import forestry.core.items.ItemRegistry;

public class ItemRegistryLepidopterology extends ItemRegistry {
	public final ItemFlutterlyzer flutterlyzer;
	public final ItemButterflyGE butterflyGE;
	public final ItemButterflyGE serumGE;
	public final ItemButterflyGE caterpillarGE;

	public ItemRegistryLepidopterology() {
		flutterlyzer = registerItem(new ItemFlutterlyzer(), "flutterlyzer");
		butterflyGE = registerItem(new ItemButterflyGE(EnumFlutterType.BUTTERFLY), "butterflyGE");
		serumGE = registerItem(new ItemButterflyGE(EnumFlutterType.SERUM), "serumGE");
		caterpillarGE = registerItem(new ItemButterflyGE(EnumFlutterType.CATERPILLAR), "caterpillarGE");
	}
}
