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
package forestry.core.inventory.filters;

import com.google.common.base.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * This interface is used to provide a convenient means of dealing with entire classes of items without having to specify each item individually.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IStackFilter extends Predicate<ItemStack> {

	StackFilter and(@Nonnull Predicate<? super ItemStack>... other);

	StackFilter or(@Nonnull Predicate<? super ItemStack>... other);

	StackFilter negate();
}
