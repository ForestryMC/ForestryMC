/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.apiculture.hives;

import forestry.api.core.IBlockSubtype;

import java.util.List;
import java.util.Locale;

public interface IHiveRegistry {

    /**
     * Adds a new hive to be generated in the world.
     */
    void registerHive(String hiveName, IHiveDescription hiveDescription);

    /**
     * Add drops to a registered hive.
     */
    void addDrops(String hiveName, IHiveDrop... drops);

    void addDrops(String hiveName, List<IHiveDrop> drop);

    /* Forestry Hive Names */
    enum HiveType implements IBlockSubtype {
        FOREST("forestry:forest", "forestry:species_forest"),
        MEADOWS("forestry:meadows", "forestry:species_meadows"),
        DESERT("forestry:desert", "forestry:species_modest"),
        JUNGLE("forestry:jungle", "forestry:species_tropical"),
        END("forestry:end", "forestry:species_ended"),
        SNOW("forestry:snow", "forestry:species_wintry"),
        SWAMP("forestry:swamp", "forestry:species_marshy"),
        SWARM("forestry:swarm", "forestry:species_forest");

        public static final HiveType[] VALUES = values();

        private final String hiveUid;
        private final String speciesUid;

        HiveType(String hiveUid, String speciesUid) {
            this.hiveUid = hiveUid;
            this.speciesUid = speciesUid;
        }


        public String getHiveUid() {
            return hiveUid;
        }


        public String getSpeciesUid() {
            return speciesUid;
        }

        @Override
        public String getString() {
            return name().toLowerCase(Locale.ENGLISH);
        }

        public int getMeta() {
            return ordinal();
        }
    }
}
