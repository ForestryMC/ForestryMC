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

import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.IWoodTyped;
import net.minecraft.block.Block;

/**
 * Only to storage data's to register a model later.
 */
public class WoodModelEntry<T extends Block & IWoodTyped> {
	
	public final T woodTyped;
	public final WoodBlockKind woodKind;
	public final boolean withVariants;

	public WoodModelEntry(T woodTyped, WoodBlockKind woodKind, boolean withVariants) {
		this.woodTyped = woodTyped;
		this.woodKind = woodKind;
		this.withVariants = withVariants;
	}

}
