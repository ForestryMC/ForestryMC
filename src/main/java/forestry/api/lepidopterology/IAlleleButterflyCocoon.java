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
package forestry.api.lepidopterology;

import java.util.Map;

import forestry.api.genetics.IAlleleProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public interface IAlleleButterflyCocoon extends IAlleleProperty<IAlleleButterflyCocoon> {
	
	ModelResourceLocation getCocoonItemModel(int age);
	
	String getCocoonName();
	
	Map<ItemStack, Float> getCocoonLoot();
	
}
