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


import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.core.items.ItemRegistry;

public class ItemRegistryLepidopterology extends ItemRegistry {
	public final ItemButterflyGE butterflyGE;
	public final ItemButterflyGE serumGE;
	public final ItemButterflyGE caterpillarGE;
	public final ItemButterflyGE cocoonGE;

	public ItemRegistryLepidopterology() {
		butterflyGE = registerItem(new ItemButterflyGE(EnumFlutterType.BUTTERFLY), "butterfly_ge");
		serumGE = registerItem(new ItemButterflyGE(EnumFlutterType.SERUM), "serum_ge");
		caterpillarGE = registerItem(new ItemButterflyGE(EnumFlutterType.CATERPILLAR), "caterpillar_ge");
		cocoonGE = registerItem(new ItemButterflyGE(EnumFlutterType.COCOON), "cocoon_ge");
	}
}
