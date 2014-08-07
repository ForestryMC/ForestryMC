/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.config;

import java.util.Locale;

import net.minecraftforge.oredict.OreDictionary;

public class Defaults {
	// System
	public static final String MOD = "Forestry";
	public static final String ID = MOD.toLowerCase(Locale.ENGLISH);
	public static final String URL = "http://forestry.sengir.net/";

	public static final int WILDCARD = OreDictionary.WILDCARD_VALUE;

	public static final int WORLD_HEIGHT = 256;

	public static final int FLAG_BLOCK_UPDATE = 1;
	public static final int FLAG_BLOCK_SYNCH = 2;

	public static final int NET_MAX_UPDATE_DISTANCE = 50;

	public static final String DEFAULT_POWER_FRAMEWORK = "forestry.energy.BioPowerFramework";
	public static final int BUCKET_VOLUME = 1000;

	public static final int[] FACINGS = { 0, 1, 2, 3, 4, 5 };
	public static final int FACING_UP = 1;
	public static final int FACING_DOWN = 0;
	public static final int FACING_NORTH = 2;
	public static final int FACING_SOUTH = 3;
	public static final int FACING_WEST = 4;
	public static final int FACING_EAST = 5;
	public static final int[] FACING_SIDES = { FACING_NORTH, FACING_SOUTH, FACING_WEST, FACING_EAST };
	public static final int[] FACING_NORTHSOUTH = { FACING_NORTH, FACING_SOUTH };
	public static final int[] FACING_WESTEAST = { FACING_WEST, FACING_EAST };
	public static final int[] FACINGS_NONE = new int[0];

	public static final String LIQUID_WATER = "water";
	public static final String LIQUID_LAVA = "lava";
	public static final String LIQUID_MILK = "milk";
	public static final String LIQUID_BIOMASS = "biomass";
	public static final String LIQUID_ETHANOL = "bioethanol";
	public static final String LIQUID_SEEDOIL = "seedoil";
	public static final String LIQUID_HONEY = "honey";
	public static final String LIQUID_JUICE = "juice";
	public static final String LIQUID_ICE = "ice";
	public static final String LIQUID_MEAD = "short.mead";
	public static final String LIQUID_GLASS = "glass";
	public static final String LIQUID_OIL = "oil";
	public static final String LIQUID_FUEL = "fuel";

	// Textures
	public static final String TEXTURE_PATH_GUI = "textures/gui";
	public static final String TEXTURE_PATH_BLOCKS = "textures/blocks";
	public static final String TEXTURE_PATH_ITEMS = "textures/items";
	public static final String TEXTURE_PATH_ENTITIES = "textures/entity";

	public static final String TEXTURE_BLOCKS = TEXTURE_PATH_BLOCKS + "/blocks.png";
	public static final String TEXTURE_ARBORICULTURE = TEXTURE_PATH_BLOCKS + "/arboriculture.png";
	public static final String TEXTURE_PODS = TEXTURE_PATH_BLOCKS + "/pods.png";
	public static final String TEXTURE_FARM = TEXTURE_PATH_BLOCKS + "/farm.png";

	public static final String TEXTURE_APIARIST_ARMOR_PRIMARY = TEXTURE_PATH_ITEMS + "/apiarist_armor_1.png";
	public static final String TEXTURE_APIARIST_ARMOR_SECONDARY = TEXTURE_PATH_ITEMS + "/apiarist_armor_2.png";
	public static final String TEXTURE_NATURALIST_ARMOR_PRIMARY = TEXTURE_PATH_ITEMS + "/naturalist_armor_1.png";
	public static final String TEXTURE_SKIN_BEEKPEEPER = TEXTURE_PATH_ENTITIES + "/beekeeper.png";
	public static final String TEXTURE_SKIN_LUMBERJACK = TEXTURE_PATH_ENTITIES + "/lumberjack.png";

	public static final String TEXTURE_ICONS_MINECRAFT = "/gui/items.png";
	public static final String TEXTURE_BLOCKS_MINECRAFT = "/terrain.png";

	// Villagers
	public static final int ID_VILLAGER_BEEKEEPER = 80;
	public static final int ID_VILLAGER_LUMBERJACK = 81;

	// Village Chest Gen hook
	public static final String CHEST_GEN_HOOK_NATURALIST_CHEST = "naturalistChest";

	// Definition IDs
	public static final int DEFINITION_ANALYZER_META = 0;
	public static final int DEFINITION_ESCRITOIRE_META = 1;

	public static final int DEFINITION_APIARY_META = 0;
	public static final int DEFINITION_APIARISTCHEST_META = 1;
	public static final int DEFINITION_BEEHOUSE_META = 2;

	public static final int DEFINITION_ENGINETIN_META = 0;
	public static final int DEFINITION_ENGINECOPPER_META = 1;
	public static final int DEFINITION_ENGINEBRONZE_META = 2;
	public static final int DEFINITION_GENERATOR_META = 3;
	public static final int DEFINITION_ENGINECLOCKWORK_META = 4;

	public static final int DEFINITION_MAILBOX_META = 0;
	public static final int DEFINITION_TRADESTATION_META = 1;
	public static final int DEFINITION_PHILATELIST_META = 2;

	public static final int DEFINITION_BOTTLER_META = 0;
	public static final int DEFINITION_CARPENTER_META = 1;
	public static final int DEFINITION_CENTRIFUGE_META = 2;
	public static final int DEFINITION_FERMENTER_META = 3;
	public static final int DEFINITION_MOISTENER_META = 4;
	public static final int DEFINITION_SQUEEZER_META = 5;
	public static final int DEFINITION_STILL_META = 6;
	public static final int DEFINITION_RAINMAKER_META = 7;

	public static final int DEFINITION_FABRICATOR_META = 0;
	public static final int DEFINITION_RAINTANK_META = 1;
	public static final int DEFINITION_WORKTABLE_META = 2;

	public static final int DEFINITION_LEPICHEST_META = 0;

	public static final int DEFINITION_ARBCHEST_META = 0;

	// Package Ids
	public static final int ID_PACKAGE_MACHINE_FERMENTER = 0;
	public static final int ID_PACKAGE_MACHINE_STILL = 1;
	public static final int ID_PACKAGE_MACHINE_BOTTLER = 2;
	public static final int ID_PACKAGE_MACHINE_RAINTANK = 3;
	public static final int ID_PACKAGE_MACHINE_CARPENTER = 5;
	public static final int ID_PACKAGE_MACHINE_MOISTENER = 6;
	public static final int ID_PACKAGE_MACHINE_APIARY = 7;
	public static final int ID_PACKAGE_MACHINE_CENTRIFUGE = 8;
	public static final int ID_PACKAGE_MACHINE_SQUEEZER = 9;
	public static final int ID_PACKAGE_MACHINE_ALVEARY = 10;
	public static final int ID_PACKAGE_MACHINE_FABRICATOR = 11;

	public static final int ID_PACKAGE_MILL_RAINMAKER = 1;
	public static final int ID_PACKAGE_MILL_APIARIST_CHEST = 3;
	public static final int ID_PACKAGE_MILL_ANALYZER = 4;
	public static final int ID_PACKAGE_MILL_MAILBOX = 5;
	public static final int ID_PACKAGE_MILL_TRADER = 6;
	public static final int ID_PACKAGE_MILL_PHILATELIST = 7;

	// Item Ids

	public static final int SLOTS_BACKPACK_DEFAULT = 15;
	public static final int SLOTS_BACKPACK_T2 = 45;
	public static final int SLOTS_BACKPACK_APIARIST = 125;

	// Bee ids
	public static final int ID_BEE_SPECIES_REDDENED = 31;
	public static final int ID_BEE_SPECIES_DARKENED = 32;
	public static final int ID_BEE_SPECIES_OMEGA = 33;

	// Food stuff
	public static final int FOOD_AMBROSIA_HEAL = 8;
	public static final int FOOD_JUICE_HEAL = 2;
	public static final float FOOD_JUICE_SATURATION = 0.2f;
	public static final int FOOD_HONEY_HEAL = 2;
	public static final float FOOD_HONEY_SATURATION = 0.2f;

	// IndustrialCraft 2
	public static final int ID_IC2_FUELCAN_DAMAGE = 6480;

	// BuildCraft
	public static final int BUILDCRAFT_BLOCKID_ENGINE = 161;
	public static final int BUILDCRAFT_BLOCKID_PIPE = 166;

	public static final int APIARY_MIN_LEVEL_LIGHT = 11;
	public static final int APIARY_BREEDING_TIME = 100;

	// Energy
	public static final int ENGINE_TANK_CAPACITY = 10 * BUCKET_VOLUME;
	public static final int ENGINE_CYCLE_DURATION_WATER = 1000;
	public static final int ENGINE_CYCLE_DURATION_JUICE = 2500;
	public static final int ENGINE_CYCLE_DURATION_HONEY = 2500;
	public static final int ENGINE_CYCLE_DURATION_MILK = 10000;
	public static final int ENGINE_CYCLE_DURATION_SEED_OIL = 2500;
	public static final int ENGINE_CYCLE_DURATION_BIOMASS = 2500;
	public static final int ENGINE_CYCLE_DURATION_ETHANOL = 15000;
	public static final int ENGINE_FUEL_VALUE_WATER = 1;
	public static final int ENGINE_FUEL_VALUE_JUICE = 1;
	public static final int ENGINE_FUEL_VALUE_HONEY = 2;
	public static final int ENGINE_FUEL_VALUE_MILK = 1;
	public static final int ENGINE_FUEL_VALUE_SEED_OIL = 3;
	public static final int ENGINE_FUEL_VALUE_BIOMASS = 5;
	public static final int ENGINE_HEAT_VALUE_LAVA = 20;

	public static final float ENGINE_PISTON_SPEED_MAX = 0.08f;

	public static final int ENGINE_BRONZE_HEAT_MAX = 10000;
	public static final int ENGINE_BRONZE_HEAT_LOSS_COOL = 2;
	public static final int ENGINE_BRONZE_HEAT_LOSS_OPERATING = 1;
	public static final int ENGINE_BRONZE_HEAT_LOSS_OVERHEATING = 5;
	public static final int ENGINE_BRONZE_HEAT_GENERATION_ENERGY = 1;

	public static final int ENGINE_COPPER_CYCLE_DURATION_PEAT = 5000;
	public static final int ENGINE_COPPER_FUEL_VALUE_PEAT = 1;
	public static final int ENGINE_COPPER_CYCLE_DURATION_BITUMINOUS_PEAT = 6000;
	public static final int ENGINE_COPPER_FUEL_VALUE_BITUMINOUS_PEAT = 2;
	public static final int ENGINE_COPPER_HEAT_MAX = 10000;
	public static final int ENGINE_COPPER_ASH_FOR_ITEM = 7500;

	public static final int ENGINE_TIN_HEAT_MAX = 3000;
	public static final int ENGINE_TIN_EU_FOR_CYCLE = 6;
	public static final int ENGINE_TIN_ENERGY_PER_CYCLE = 2;
	public static final int ENGINE_TIN_MAX_EU_STORED = 2 * ENGINE_TIN_EU_FOR_CYCLE;
	public static final int ENGINE_TIN_MAX_EU_BATTERY = 100;

	// Factory
	public static final int PROCESSOR_TANK_CAPACITY = 10 * BUCKET_VOLUME;

	public static final int MACHINE_LATENCY = 1000;
	public static final int MACHINE_MIN_ENERGY_RECEIVED = 5;
	public static final int MACHINE_MAX_ENERGY_RECEIVED = 40;
	public static final int MACHINE_MIN_ACTIVATION_ENERGY = 15;
	public static final int MACHINE_MAX_ENERGY = 500;

	public static final int RAINMAKER_RAIN_DURATION_IODINE = 10000;

	public static final int STILL_DESTILLATION_DURATION = 100;
	public static final int STILL_DESTILLATION_INPUT = 10;
	public static final int STILL_DESTILLATION_OUTPUT = 3;

	public static final int BOTTLER_FILLING_TIME = 20;
	public static final int BOTTLER_FUELCAN_VOLUME = 2000;

	// Storage
	public static final int RAINTANK_TANK_CAPACITY = 30 * BUCKET_VOLUME;
	public static final int RAINTANK_AMOUNT_PER_UPDATE = 10;
	public static final int RAINTANK_FILLING_TIME = 12;
	public static final int CARPENTER_CRATING_CYCLES = 5;
	public static final int CARPENTER_UNCRATING_CYCLES = 5;
	public static final int CARPENTER_CRATING_LIQUID_QUANTITY = 100;

	// SMP gui ids
	public static final int ID_GUI_ARBORETUM = 90;
	public static final int ID_GUI_BOTTLER = 91;
	public static final int ID_GUI_ENGINE_BRONZE = 92;
	public static final int ID_GUI_FARM = 93;
	public static final int ID_GUI_FERMENTER = 94;
	public static final int ID_GUI_FORESTER = 95;
	public static final int ID_GUI_PLANTATION = 96;
	public static final int ID_GUI_PUMPKIN_FARM = 97;
	public static final int ID_GUI_PEAT_BOG = 98;
	public static final int ID_GUI_STILL = 99;
	public static final int ID_GUI_ENGINE_COPPER = 89;
	public static final int ID_GUI_RAINTANK = 88;
	public static final int ID_GUI_GENERATOR = 87;
	public static final int ID_GUI_CARPENTER = 86;
	public static final int ID_GUI_MOISTENER = 85;
	public static final int ID_GUI_MUSHROOM_FARM = 84;
	public static final int ID_GUI_APIARY = 83;
	public static final int ID_GUI_CENTRIFUGE = 82;
	public static final int ID_GUI_APIARIST_INVENTORY = 81;
	public static final int ID_GUI_BACKPACK = 80;
	public static final int ID_GUI_SQUEEZER = 79;
	public static final int ID_GUI_NETHER_FARM = 78;
	public static final int ID_GUI_BEEALYZER = 100;
	public static final int ID_GUI_BACKPACK_T2 = 101;
	public static final int ID_GUI_BIOME_FINDER = 102;
	// ID 100 used by bucket filler.
}
