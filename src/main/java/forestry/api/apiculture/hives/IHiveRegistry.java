/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.hives;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

import net.minecraft.util.IStringSerializable;

import forestry.api.apiculture.IHiveDrop;

public interface IHiveRegistry {

	/* Forestry Hive Names */
	enum HiveType implements IStringSerializable {
		FOREST("forestry:forest", "forestry.speciesForest"),
		MEADOWS("forestry:meadows", "forestry.speciesMeadows"),
		DESERT("forestry:desert", "forestry.speciesModest"),
		JUNGLE("forestry:jungle", "forestry.speciesTropical"),
		END("forestry:end", "forestry.speciesEnded"),
		SNOW("forestry:snow", "forestry.speciesWintry"),
		SWAMP("forestry:swamp", "forestry.speciesMarshy"),
		SWARM("forestry:swarm", "forestry.speciesForest");
		
		public static final HiveType[] VALUES = values();

		@Nonnull
		private final String hiveUid;
		@Nonnull
		private final String speciesUid;

		HiveType(@Nonnull String hiveUid, @Nonnull String speciesUid) {
			this.hiveUid = hiveUid;
			this.speciesUid = speciesUid;
		}

		@Nonnull
		public String getHiveUid() {
			return hiveUid;
		}

		@Nonnull
		public String getSpeciesUid() {
			return speciesUid;
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}

		public int getMeta() {
			return ordinal();
		}
		
	}

	/**
	 * Adds a new hive to be generated in the world.
	 */
	void registerHive(String hiveName, IHiveDescription hiveDescription);

	/**
	 * Add drops to a registered hive.
	 */
	void addDrops(String hiveName, IHiveDrop... drops);

	void addDrops(String hiveName, List<IHiveDrop> drop);
}
