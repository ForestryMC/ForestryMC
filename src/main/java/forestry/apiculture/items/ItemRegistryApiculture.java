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

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.core.Tabs;
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
	public final ItemBeealyzer beealyzer;
	public final ItemImprinter imprinter;

	public final ItemMinecartBeehouse minecartBeehouse;

	public final ItemHiveFrame frameUntreated;
	public final ItemHiveFrame frameImpregnated;
	public final ItemHiveFrame frameProven;

	public final ItemOverlay honeyDrop;
	public final ItemPollenCluster pollenCluster;
	public final ItemPropolis propolis;
	public final ItemForestry honeydew;
	public final ItemForestry royalJelly;
	public final ItemForestry waxCast;
	public final ItemHoneycomb beeComb;

	public final ItemArmorApiarist apiaristHat;
	public final ItemArmorApiarist apiaristChest;
	public final ItemArmorApiarist apiaristLegs;
	public final ItemArmorApiarist apiaristBoots;

	public final ItemScoop scoop;

	public ItemRegistryApiculture() {
		// / BEES
		beeQueenGE = registerItem(new ItemBeeGE(EnumBeeType.QUEEN), "beeQueenGE");
		beeDroneGE = registerItem(new ItemBeeGE(EnumBeeType.DRONE), "beeDroneGE");
		beePrincessGE = registerItem(new ItemBeeGE(EnumBeeType.PRINCESS), "beePrincessGE");
		beeLarvaeGE = registerItem(new ItemBeeGE(EnumBeeType.LARVAE), "beeLarvaeGE");
		
		beealyzer = registerItem(new ItemBeealyzer(), "beealyzer");
		habitatLocator = registerItem(new ItemHabitatLocator(), "habitatLocator");
		imprinter = registerItem(new ItemImprinter(), "imprinter");
		
		minecartBeehouse = registerItem(new ItemMinecartBeehouse(), "cart.beehouse");
		
		// / COMB FRAMES
		frameUntreated = registerItem(new ItemHiveFrame(80, 0.9f), "frameUntreated");
		frameImpregnated = registerItem(new ItemHiveFrame(240, 0.4f), "frameImpregnated");
		frameProven = registerItem(new ItemHiveFrame(720, 0.3f), "frameProven");
		
		// / BEE RESOURCES
		honeyDrop = registerItem(new ItemOverlay(Tabs.tabApiculture, EnumHoneyDrop.VALUES), "honeyDrop");
		OreDictionary.registerOre("dropHoney", honeyDrop);

		pollenCluster = registerItem(new ItemPollenCluster(), "pollen");
		OreDictionary.registerOre("itemPollen", pollenCluster);

		propolis = registerItem(new ItemPropolis(), "propolis");

		honeydew = registerItem(new ItemForestry(Tabs.tabApiculture), "honeydew");
		OreDictionary.registerOre("dropHoneydew", honeydew);

		royalJelly = registerItem(new ItemForestry(Tabs.tabApiculture), "royalJelly");
		OreDictionary.registerOre("dropRoyalJelly", royalJelly);
		
		waxCast = registerItem(new ItemWaxCast(), "waxCast");
		
		// / BEE COMBS
		beeComb = registerItem(new ItemHoneycomb(), "beeCombs");
		OreDictionary.registerOre("beeComb", beeComb.getWildcard());
		
		// / APIARIST'S CLOTHES
		apiaristHat = registerItem(new ItemArmorApiarist(0), "apiaristHelmet");
		apiaristChest = registerItem(new ItemArmorApiarist(1), "apiaristChest");
		apiaristLegs = registerItem(new ItemArmorApiarist(2), "apiaristLegs");
		apiaristBoots = registerItem(new ItemArmorApiarist(3), "apiaristBoots");
		
		// TOOLS
		scoop = registerItem(new ItemScoop(), "scoop");
		scoop.setHarvestLevel("scoop", 3);
	}
}
