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
package forestry.core.models;

import net.minecraft.block.Block;

//import forestry.arboriculture.IWoodTyped;

/**
 * Only to storage data's to register a model later.
 */
public class WoodModelEntry<T extends Block> {//& IWoodTyped> {

	public final T woodTyped;
	public final boolean withVariants;

	public WoodModelEntry(T woodTyped, boolean withVariants) {
		this.woodTyped = woodTyped;
		this.withVariants = withVariants;
	}

}
