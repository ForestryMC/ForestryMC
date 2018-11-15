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
package forestry.arboriculture.blocks;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.block.properties.PropertyHelper;

import forestry.arboriculture.genetics.TreeDefinition;

public class PropertyTreeTypeFruit extends PropertyHelper<PropertyTreeTypeFruit.LeafVariant> {
	@Nullable
	private static List<LeafVariant> definitions = null;

	public static List<LeafVariant> getDefinitions() {
		if (definitions == null) {
			definitions = new ArrayList<>();
			int metadata = 0;
			for (TreeDefinition definition : TreeDefinition.VALUES) {
				if (definition.hasFruitLeaves()) {
					definitions.add(new LeafVariant(definition, metadata++));
				}
			}
		}
		return definitions;
	}

	public static LeafVariant getVariant(int metadata) {
		return getDefinitions().get(metadata);
	}

	public static int getBlockCount(int variantsPerBlock) {
		return (int) Math.ceil((float) getDefinitions().size() / variantsPerBlock);
	}

	public static PropertyTreeTypeFruit create(String name, int blockNumber, int variantsPerBlock) {
		LeafPredicate filter = new LeafPredicate(blockNumber, variantsPerBlock);
		Collection<LeafVariant> allowedValues = Collections2.filter(getDefinitions(), filter);

		return new PropertyTreeTypeFruit(name, allowedValues);
	}

	private final Collection<LeafVariant> allowedValues;
	private final Map<String, LeafVariant> nameToValue = new HashMap<>();

	protected PropertyTreeTypeFruit(String name, Collection<LeafVariant> allowedValues) {
		super(name, LeafVariant.class);
		this.allowedValues = allowedValues;
		for (LeafVariant t : allowedValues) {
			String s = t.definition.getName();

			if (this.nameToValue.containsKey(s)) {
				throw new IllegalArgumentException("Multiple values have the same name '" + s + "'");
			}

			this.nameToValue.put(s, t);
		}
	}

	@Override
	public Collection<LeafVariant> getAllowedValues() {
		return this.allowedValues;
	}

	@Override
	public Optional<LeafVariant> parseValue(String value) {
		return Optional.fromNullable(this.nameToValue.get(value));
	}

	/**
	 * Get the name for the given value.
	 */
	@Override
	public String getName(LeafVariant value) {
		return value.definition.getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof PropertyTreeTypeFruit && super.equals(o)) {
			PropertyTreeTypeFruit property = (PropertyTreeTypeFruit) o;
			return this.allowedValues.equals(property.allowedValues) && this.nameToValue.equals(property.nameToValue);
		} else {
			return false;
		}
	}

	public LeafVariant getFirstType() {
		return getAllowedValues().iterator().next();
	}

	public static class LeafVariant implements Comparable<LeafVariant> {
		public final TreeDefinition definition;
		public final int metadata;

		public LeafVariant(TreeDefinition definition, int metadata) {
			this.definition = definition;
			this.metadata = metadata;
		}

		@Override
		public int compareTo(LeafVariant o) {
			return metadata - o.metadata;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			LeafVariant that = (LeafVariant) o;
			return metadata == that.metadata &&
				definition == that.definition;
		}

		@Override
		public int hashCode() {
			return Objects.hash(definition, metadata);
		}
	}

	private static class LeafPredicate implements Predicate<LeafVariant> {
		private final int minMeta;
		private final int maxMeta;

		public LeafPredicate(int blockNumber, int variantsPerBlock) {
			this.minMeta = blockNumber * variantsPerBlock;
			this.maxMeta = minMeta + variantsPerBlock - 1;
		}

		@Override
		public boolean apply(@Nullable LeafVariant variant) {
			return variant != null && variant.metadata >= minMeta && variant.metadata <= maxMeta;
		}
	}

}
