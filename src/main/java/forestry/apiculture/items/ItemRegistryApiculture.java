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
package forestry.apiculture.items;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.core.ItemGroups;
import forestry.core.items.ItemForestry;
import forestry.core.items.ItemOverlay;
import forestry.core.items.ItemRegistry;
import forestry.core.items.ItemScoop;

public class ItemRegistryApiculture extends ItemRegistry {
	public final ItemBeeGE beeQueenGE;
	public final ItemBeeGE beeDroneGE;
	public final ItemBeeGE beePrincessGE;
	public final ItemBeeGE beeLarvaeGE;

	public final ItemHabitatLocator habitatLocator;
	public final ItemImprinter imprinter;

	public final ItemMinecartBeehouse minecartBeehouse;

	public final ItemHiveFrame frameUntreated;
	public final ItemHiveFrame frameImpregnated;
	public final ItemHiveFrame frameProven;

	public final Map<EnumHoneyDrop, ItemOverlay> honeyDrops = new EnumMap<>(EnumHoneyDrop.class);
	public final Map<EnumPollenCluster, ItemPollenCluster> pollenClusters = new EnumMap<>(EnumPollenCluster.class);
	public final Map<EnumPropolis, ItemPropolis> propolis = new EnumMap<>(EnumPropolis.class);
	public final ItemForestry honeydew;
	public final ItemForestry royalJelly;
	public final ItemForestry waxCast;
	public final Map<EnumHoneyComb, ItemHoneyComb> beeCombs = new EnumMap<>(EnumHoneyComb.class);

	public final ItemArmorApiarist apiaristHat;
	public final ItemArmorApiarist apiaristChest;
	public final ItemArmorApiarist apiaristLegs;
	public final ItemArmorApiarist apiaristBoots;

	public final ItemScoop scoop;
	public final ItemSmoker smoker;

	public ItemRegistryApiculture() {
		// / BEES
		beeQueenGE = registerItem(new ItemBeeGE(EnumBeeType.QUEEN), "bee_queen_ge");
		beeDroneGE = registerItem(new ItemBeeGE(EnumBeeType.DRONE), "bee_drone_ge");
		beePrincessGE = registerItem(new ItemBeeGE(EnumBeeType.PRINCESS), "bee_princess_ge");
		beeLarvaeGE = registerItem(new ItemBeeGE(EnumBeeType.LARVAE), "bee_larvae_ge");

		habitatLocator = registerItem(new ItemHabitatLocator(), "habitat_locator");
		imprinter = registerItem(new ItemImprinter(), "imprinter");

		minecartBeehouse = registerItem(new ItemMinecartBeehouse(), "cart.beehouse");

		// / COMB FRAMES
		frameUntreated = registerItem(new ItemHiveFrame(80, 0.9f), "frame_untreated");
		frameImpregnated = registerItem(new ItemHiveFrame(240, 0.4f), "frame_impregnated");
		frameProven = registerItem(new ItemHiveFrame(720, 0.3f), "frame_proven");

		// / BEE RESOURCES
		for (EnumHoneyDrop drop : EnumHoneyDrop.VALUES) {
			ItemOverlay honeyDrop = new ItemOverlay(ItemGroups.tabApiculture, drop);
			registerItem(honeyDrop, "honey_drop_" + drop.getName());
			honeyDrops.put(drop, honeyDrop);
		}    //TODO tag
		//		OreDictionary.registerOre(OreDictUtil.DROP_HONEY, honeyDrop);

		for (EnumPropolis type : EnumPropolis.VALUES) {
			ItemPropolis prop = new ItemPropolis(type);
			registerItem(prop, "propolis_" + type.getName());
			propolis.put(type, prop);
		}    //TODO tag


		honeydew = registerItem(new ItemForestry(ItemGroups.tabApiculture), "honeydew");
		//		OreDictionary.registerOre(OreDictUtil.DROP_HONEYDEW, honeydew);

		royalJelly = registerItem(new ItemForestry(ItemGroups.tabApiculture), "royal_jelly");
		//		OreDictionary.registerOre(OreDictUtil.DROP_ROYAL_JELLY, royalJelly);

		waxCast = registerItem(new ItemWaxCast(), "wax_cast");

		for (EnumPollenCluster type : EnumPollenCluster.VALUES) {
			ItemPollenCluster pollen = new ItemPollenCluster(type);
			registerItem(pollen, "pollen_cluster_" + type.getName());
			pollenClusters.put(type, pollen);
		}    //TODO tag


		// / BEE COMBS
		for (EnumHoneyComb type : EnumHoneyComb.VALUES) {
			ItemHoneyComb comb = new ItemHoneyComb(type);
			registerItem(comb, "bee_comb_" + type.getName());
			beeCombs.put(type, comb);
		}
		//		OreDictionary.registerOre(OreDictUtil.BEE_COMB, beeComb.getWildcard());
		//TODO - tags

		// / APIARIST'S CLOTHES
		apiaristHat = registerItem(new ItemArmorApiarist(EquipmentSlotType.HEAD), "apiarist_helmet");
		apiaristChest = registerItem(new ItemArmorApiarist(EquipmentSlotType.CHEST), "apiarist_chest");
		apiaristLegs = registerItem(new ItemArmorApiarist(EquipmentSlotType.LEGS), "apiarist_legs");
		apiaristBoots = registerItem(new ItemArmorApiarist(EquipmentSlotType.FEET), "apiarist_boots");

		// TOOLS
		scoop = registerItem(new ItemScoop(), "scoop");
		smoker = registerItem(new ItemSmoker(), "smoker");

		// register some common oreDict names for our recipes
		//		OreDictionary.registerOre(OreDictUtil.BLOCK_WOOL, new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));
	}

	public ItemStack getRandomComb(int amount, Random random, boolean includeSecret) {
		EnumHoneyComb honeyComb = ItemHoneyComb.getRandomCombType(random, includeSecret);
		if (honeyComb == null) {
			return ItemStack.EMPTY;
		}
		return getComb(honeyComb, amount);

	}

	public ItemStack getComb(EnumHoneyComb honeyComb, int amount) {
		return new ItemStack(beeCombs.get(honeyComb), amount);
	}

	public ItemStack getPollen(EnumPollenCluster pollenCluster, int amount) {
		return new ItemStack(pollenClusters.get(pollenCluster), amount);
	}

	public ItemStack getPropolis(EnumPropolis prop, int amount) {
		return new ItemStack(propolis.get(prop), amount);
	}

	public ItemStack getHoneyDrop(EnumHoneyDrop drop, int amount) {
		return new ItemStack(honeyDrops.get(drop), amount);
	}
}
