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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.core.utils.StringUtil;
import forestry.plugins.PluginManager;
import forestry.plugins.PluginManager.Stage;

/**
 * Allows direct access to Forestry's items. Will be populated during
 * preInit and Init.
 *
 * Most items with the exception of bees do not care for damage values.
 *
 * Make sure to only reference it in postInit or later.
 *
 * @author SirSengir
 */
public enum ForestryItem {

	fertilizerBio,
	fertilizerCompound,
	apatite,
	// Ingots
	ingotCopper,
	ingotTin,
	ingotBronze,
	// Tools
	wrench,
	pipette,
	bucketBiomass,
	bucketEthanol,
	bucketGlass,
	bucketHoney,
	bucketIce,
	bucketJuice,
	bucketSeedoil,
	bucketShortMead,
	// Crafting
	impregnatedCasing,
	sturdyCasing,
	hardenedCasing,
	craftingMaterial,
	// Rainmaker
	iodineCharge,
	// Gears
	gearBronze,
	gearCopper,
	gearTin,
	// Chipsets
	circuitboards,
	solderingIron,
	tubes,
	// Mail
	stamps,
	letters,
	catalogue,
	// mailIndicator,

	// Carpenter
	stickImpregnated,
	woodPulp,
	carton,
	crate,
	// Tools
	bronzePickaxe,
	brokenBronzePickaxe,
	kitPickaxe,
	bronzeShovel,
	brokenBronzeShovel,
	kitShovel,
	// Do not touch - contagious!
	tent,
	habitatLocator,
	// Moistener
	mouldyWheat,
	decayingWheat,
	mulch,
	// Peat
	peat,
	bituminousPeat,
	ash,
	// Bees
	beeQueenGE,
	beeDroneGE,
	beePrincessGE,
	beeLarvaeGE,
	beealyzer,
	imprinter,
	honeyDrop,
	scoop,
	beeswax,
	pollenCluster,
	propolis,
	honeydew,
	royalJelly,
	honeyedSlice,
	ambrosia,
	honeyPot,
	phosphor,
	refractoryWax,
	waxCast,
	frameUntreated,
	frameImpregnated,
	frameProven,
	minecartBeehouse,
	// Trees
	sapling,
	pollenFertile,
	treealyzer,
	grafter,
	grafterProven,
	// Butterflies
	butterflyGE,
	flutterlyzer,
	serumGE,
	caterpillarGE,
	researchNote,
	// Beverages
	beverage,
	infuser,
	// Naturalist's Armor
	naturalistHat,
	// Apiarist's Armor
	apiaristHat,
	apiaristChest,
	apiaristLegs,
	apiaristBoots,
	// Combs
	beeComb,
	// Fruits
	fruits,
	// Backpacks
	apiaristBackpack,
	lepidopteristBackpack,
	minerBackpack,
	diggerBackpack,
	foresterBackpack,
	hunterBackpack,
	builderBackpack,
	adventurerBackpack,
	// T2
	minerBackpackT2,
	diggerBackpackT2,
	foresterBackpackT2,
	hunterBackpackT2,
	builderBackpackT2,
	adventurerBackpackT2,
	// Capsules
	waxCapsule,
	waxCapsuleWater,
	waxCapsuleBiomass,
	waxCapsuleEthanol,
	waxCapsuleOil,
	waxCapsuleFuel,
	waxCapsuleSeedOil,
	waxCapsuleHoney,
	waxCapsuleJuice,
	waxCapsuleIce,
	// Refractory Capsules
	refractoryEmpty,
	refractoryWater,
	refractoryBiomass,
	refractoryEthanol,
	refractoryOil,
	refractoryFuel,
	refractoryLava,
	refractorySeedOil,
	refractoryHoney,
	refractoryJuice,
	refractoryIce,
	// Cans
	canWater,
	canEmpty,
	canBiomass,
	canEthanol,
	canOil,
	canFuel,
	canLava,
	canSeedOil,
	canHoney,
	canJuice,
	canIce;
	private Item item;

	public void registerItem(Item item, String name) {
		if (PluginManager.getStage() != Stage.SETUP) {
			throw new RuntimeException("Tried to register Item outside of Setup");
		}
		this.item = item;
		item.setUnlocalizedName("for." + name);
		GameRegistry.registerItem(item, StringUtil.cleanItemName(item));
	}

	public boolean isItemEqual(ItemStack stack) {
		return stack != null && this.item == stack.getItem();
	}

	public boolean isItemEqual(Item i) {
		return i != null && this.item == i;
	}

	public Item item() {
		return item;
	}

	public ItemStack getWildcard() {
		return getItemStack(1, OreDictionary.WILDCARD_VALUE);
	}

	public ItemStack getItemStack() {
		return getItemStack(1, 0);
	}

	public ItemStack getItemStack(int qty) {
		return getItemStack(qty, 0);
	}

	public ItemStack getItemStack(int qty, int meta) {
		if (item == null) {
			return null;
		}
		return new ItemStack(item, qty, meta);
	}
}
