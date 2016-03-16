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
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityFurnace;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * This interface is used with several of the functions in IItemTransfer to
 * provide a convenient means of dealing with entire classes of items without
 * having to specify each item individually.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum StandardStackFilters implements IStackFilter {

	ALL {
		@Override
		public boolean apply(ItemStack stack) {
			return true;
		}
	},
	FUEL {
		@Override
		public boolean apply(ItemStack stack) {
			return TileEntityFurnace.getItemBurnTime(stack) > 0;
		}
	},
	FEED {
		@Override
		public boolean apply(ItemStack stack) {
			return stack.getItem() instanceof ItemFood || stack.getItem() == Items.wheat || stack.getItem() instanceof ItemSeeds;
		}
	};

    public static void initialize() {
        for (StandardStackFilters type : StandardStackFilters.values()) {
            StackFilter.standardFilters.put(type.name(), type);
        }
    }

    @Override
    public abstract boolean apply(ItemStack stack);

    @Override
    public final StackFilter and(@Nonnull final Predicate<? super ItemStack>... other) {
        Objects.requireNonNull(other);
        return new StackFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                for (Predicate<? super ItemStack> filter : other) {
                    if (!filter.apply(stack)) {
						return false;
					}
                }
                return StandardStackFilters.this.apply(stack);
            }
        };
    }

    @Override
    public final StackFilter or(@Nonnull final Predicate<? super ItemStack>... other) {
        Objects.requireNonNull(other);
        return new StackFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                for (Predicate<? super ItemStack> filter : other) {
                    if (filter.apply(stack)) {
						return true;
					}
                }
                return StandardStackFilters.this.apply(stack);
            }
        };
    }

    @Override
    public final StackFilter negate() {
        return new StackFilter() {
            @Override
            public boolean apply(ItemStack stack) {
                return !StandardStackFilters.this.apply(stack);
            }
        };
    }

}
