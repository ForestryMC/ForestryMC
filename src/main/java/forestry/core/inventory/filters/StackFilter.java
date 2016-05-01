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
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.item.ItemStack;

/**
 * This class is used to provide a convenient means of dealing with entire classes of items without having to specify each item individually.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unused")
public class StackFilter implements IStackFilter {
	/**
	 * Railcraft adds the following IItemTypes during preInit: ALL, FUEL, TRACK, MINECART, BALLAST, FEED
	 * <p/>
	 * Feel free to grab them from here or define your own.
	 */
	public static final Map<String, IStackFilter> standardFilters = new HashMap<>();

	@Override
	public boolean apply(@Nullable final ItemStack input) {
		return true;
	}

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
				return StackFilter.this.apply(stack);
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
				return StackFilter.this.apply(stack);
			}
		};
	}

	@Override
	public final StackFilter negate() {
		return new StackFilter() {
			@Override
			public boolean apply(ItemStack stack) {
				return !StackFilter.this.apply(stack);
			}
		};
	}

	public static StackFilter buildAnd(@Nonnull final Predicate<? super ItemStack>... filters) {
		return new StackFilter() {
			@Override
			public boolean apply(ItemStack stack) {
				Objects.requireNonNull(stack);
				for (Predicate<? super ItemStack> filter : filters) {
					if (!filter.apply(stack)) {
						return false;
					}
				}
				return true;
			}
		};
	}

	public static StackFilter buildOr(@Nonnull final Predicate<? super ItemStack>... filters) {
		return new StackFilter() {
			@Override
			public boolean apply(ItemStack stack) {
				Objects.requireNonNull(stack);
				for (Predicate<? super ItemStack> filter : filters) {
					if (filter.apply(stack)) {
						return true;
					}
				}
				return false;
			}
		};
	}

	public static StackFilter invert(@Nonnull final Predicate<? super ItemStack> filter) {
		return new StackFilter() {
			@Override
			public boolean apply(ItemStack stack) {
				return !filter.apply(stack);
			}
		};
	}

}
