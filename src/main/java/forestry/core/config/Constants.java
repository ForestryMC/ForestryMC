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
package forestry.core.config;

import forestry.api.core.IForestryConstants;

public class Constants implements IForestryConstants {
	// System
	public static final String MOD = "Forestry";
	public static final String ID = "forestry";
	public static final String URL = "http://forestry.sengir.net/";

	public static final int FLAG_BLOCK_UPDATE = 1;
	public static final int FLAG_BLOCK_SYNCH = 2;
	public static final int FLAG_BLOCK_SYNCH_AND_UPDATE = FLAG_BLOCK_UPDATE + FLAG_BLOCK_SYNCH;

	public static final int BUCKET_VOLUME = 1000;
	public static final int FLUID_PER_HONEY_DROP = 100;

	public static final int[] FACINGS = {0, 1, 2, 3, 4, 5};
	public static final int[] SLOTS_NONE = new int[0];
	public static final String[] EMPTY_STRINGS = new String[0];

	// Textures
	public static final String TEXTURE_PATH_GUI = "textures/gui";
	public static final String TEXTURE_PATH_BLOCKS = "textures/blocks";
	public static final String TEXTURE_PATH_ITEMS = "textures/items";
	public static final String TEXTURE_PATH_ENTITIES = "textures/entity";

	public static final String TEXTURE_APIARIST_ARMOR_PRIMARY = TEXTURE_PATH_ITEMS + "/apiarist_armor_1.png";
	public static final String TEXTURE_APIARIST_ARMOR_SECONDARY = TEXTURE_PATH_ITEMS + "/apiarist_armor_2.png";
	public static final String TEXTURE_NATURALIST_ARMOR_PRIMARY = TEXTURE_PATH_ITEMS + "/naturalist_armor_1.png";
	public static final String TEXTURE_SKIN_BEEKPEEPER = TEXTURE_PATH_ENTITIES + "/beekeeper.png";
	public static final String TEXTURE_SKIN_LUMBERJACK = TEXTURE_PATH_ENTITIES + "/lumberjack.png";

	// Villagers
	public static final int ID_VILLAGER_BEEKEEPER = 80;
	public static final int ID_VILLAGER_LUMBERJACK = 81;

	// Village Chest Gen hook
	public static final String CHEST_GEN_HOOK_NATURALIST_CHEST = "naturalistChest";

	// Item Ids

	public static final int SLOTS_BACKPACK_DEFAULT = 15;
	public static final int SLOTS_BACKPACK_T2 = 45;
	public static final int SLOTS_BACKPACK_APIARIST = 125;

	// Food stuff
	public static final int FOOD_AMBROSIA_HEAL = 8;
	public static final int FOOD_JUICE_HEAL = 2;
	public static final float FOOD_JUICE_SATURATION = 0.2f;
	public static final int FOOD_HONEY_HEAL = 2;
	public static final float FOOD_HONEY_SATURATION = 0.2f;

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
	public static final int ENGINE_FUEL_VALUE_WATER = 10;
	public static final int ENGINE_FUEL_VALUE_JUICE = 10;
	public static final int ENGINE_FUEL_VALUE_HONEY = 20;
	public static final int ENGINE_FUEL_VALUE_MILK = 10;
	public static final int ENGINE_FUEL_VALUE_SEED_OIL = 30;
	public static final int ENGINE_FUEL_VALUE_BIOMASS = 50;
	public static final int ENGINE_HEAT_VALUE_LAVA = 20;

	public static final float ENGINE_PISTON_SPEED_MAX = 0.08f;

	public static final int ENGINE_BRONZE_HEAT_MAX = 10000;
	public static final int ENGINE_BRONZE_HEAT_LOSS_COOL = 2;
	public static final int ENGINE_BRONZE_HEAT_LOSS_OPERATING = 1;
	public static final int ENGINE_BRONZE_HEAT_LOSS_OVERHEATING = 5;
	public static final int ENGINE_BRONZE_HEAT_GENERATION_ENERGY = 1;

	public static final int ENGINE_COPPER_CYCLE_DURATION_PEAT = 5000;
	public static final int ENGINE_COPPER_FUEL_VALUE_PEAT = 10;
	public static final int ENGINE_COPPER_CYCLE_DURATION_BITUMINOUS_PEAT = 6000;
	public static final int ENGINE_COPPER_FUEL_VALUE_BITUMINOUS_PEAT = 20;
	public static final int ENGINE_COPPER_HEAT_MAX = 10000;
	public static final int ENGINE_COPPER_ASH_FOR_ITEM = 7500;

	public static final int ENGINE_ELECTRIC_HEAT_MAX = 3000;
	public static final int ENGINE_TIN_EU_FOR_CYCLE = 5; //Reduced from 6 to 5, as per the 4 RF to 1 EU ratio
	public static final int ENGINE_TIN_ENERGY_PER_CYCLE = 20;
	public static final int ENGINE_TIN_MAX_EU_STORED = 2 * ENGINE_TIN_EU_FOR_CYCLE;
	public static final int ENGINE_TIN_MAX_EU_BATTERY = 100;

	// Factory
	public static final int PROCESSOR_TANK_CAPACITY = 10 * BUCKET_VOLUME;

	public static final int MACHINE_MAX_ENERGY = 5000;

	public static final int RAINMAKER_RAIN_DURATION_IODINE = 10000;

	public static final int STILL_DESTILLATION_DURATION = 100;
	public static final int STILL_DESTILLATION_INPUT = 10;
	public static final int STILL_DESTILLATION_OUTPUT = 3;

	public static final int BOTTLER_FILLING_TIME = 20;
	public static final int BOTTLER_FUELCAN_VOLUME = 2000;

	public static final float ICE_COOLING_MULTIPLIER = 6f; // multiple of water's cooling value

	// Storage
	public static final int RAINTANK_TANK_CAPACITY = 30 * BUCKET_VOLUME;
	public static final int RAINTANK_AMOUNT_PER_UPDATE = 10;
	public static final int RAINTANK_FILLING_TIME = 12;
	public static final int CARPENTER_CRATING_CYCLES = 5;
	public static final int CARPENTER_UNCRATING_CYCLES = 5;
	public static final int CARPENTER_CRATING_LIQUID_QUANTITY = 100;

	@Override
	public int getApicultureVillagerID() {
		return ID_VILLAGER_BEEKEEPER;
	}

	@Override
	public int getArboricultureVillagerID() {
		return ID_VILLAGER_LUMBERJACK;
	}

	@Override
	public String getVillagerChestGenKey() {
		return CHEST_GEN_HOOK_NATURALIST_CHEST;
	}
}
