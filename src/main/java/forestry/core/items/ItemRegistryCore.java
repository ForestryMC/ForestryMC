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
package forestry.core.items;

import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.Tabs;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.genetics.ItemResearchNote;

public class ItemRegistryCore extends ItemRegistry {
	/* Fertilizer */
	public final ItemForestry fertilizerBio;
	public final ItemForestryBonemeal fertilizerCompound;

	/* Gems */
	public final ItemForestry apatite;

	/* Research */
	public final ItemResearchNote researchNote;

	/* Ingots */
	public final ItemForestry ingotCopper;
	public final ItemForestry ingotTin;
	public final ItemForestry ingotBronze;

	/* Tools */
	public final ItemWrench wrench;
	public final ItemPipette pipette;

	/* Packaged Tools */
	public final ItemForestry carton;
	public final ItemForestry brokenBronzePickaxe;
	public final ItemForestry brokenBronzeShovel;
	public final ItemForestryTool bronzePickaxe;
	public final ItemForestryTool bronzeShovel;
	public final ItemAssemblyKit kitShovel;
	public final ItemAssemblyKit kitPickaxe;

	/* Machine Parts */
	public final ItemForestry sturdyCasing;
	public final ItemForestry hardenedCasing;
	public final ItemForestry impregnatedCasing;
	public final ItemForestry gearBronze;
	public final ItemForestry gearCopper;
	public final ItemForestry gearTin;

	/* Soldering */
	public final ItemSolderingIron solderingIron;
	public final ItemCircuitBoard circuitboards;
	public final ItemElectronTube tubes;

	/* Armor */
	public final ItemArmorNaturalist spectacles;

	/* Peat */
	public final ItemForestry peat;
	public final ItemForestry ash;
	public final ItemForestry bituminousPeat;

	/* Moistener */
	public final ItemForestry mouldyWheat;
	public final ItemForestry decayingWheat;
	public final ItemForestry mulch;

	/* Rainmaker */
	public final ItemForestry iodineCharge;
	public final ItemForestry phosphor;

	/* Misc */
	public final ItemCraftingMaterial craftingMaterial;
	public final ItemForestry stickImpregnated;
	public final ItemForestry woodPulp;
	public final ItemForestry beeswax;
	public final ItemForestry refractoryWax;
	public final ItemFruit fruits;
	
	public ItemRegistryCore() {
		fertilizerBio = registerItem(new ItemForestry(), "fertilizerBio");
		fertilizerCompound = registerItem(new ItemForestryBonemeal(), "fertilizerCompound");

		apatite = registerItem(new ItemForestry(), "apatite");
		OreDictionary.registerOre("gemApatite", apatite);

		researchNote = registerItem(new ItemResearchNote(), "researchNote");

		ingotCopper = registerItem(new ItemForestry(), "ingotCopper");
		OreDictionary.registerOre("ingotCopper", ingotCopper);
		
		ingotTin = registerItem(new ItemForestry(), "ingotTin");
		OreDictionary.registerOre("ingotTin", ingotTin);
		
		ingotBronze = registerItem(new ItemForestry(), "ingotBronze");
		OreDictionary.registerOre("ingotBronze", ingotBronze);

		wrench = registerItem(new ItemWrench(), "wrench");
		pipette = registerItem(new ItemPipette(), "pipette");

		sturdyCasing = registerItem(new ItemForestry(), "sturdyMachine");
		hardenedCasing = registerItem(new ItemForestry(), "hardenedMachine");
		impregnatedCasing = registerItem(new ItemForestry(), "impregnatedCasing");

		craftingMaterial = registerItem(new ItemCraftingMaterial(), "craftingMaterial");

		spectacles = registerItem(new ItemArmorNaturalist(), "naturalistHelmet");

		peat = registerItem(new ItemForestry(), "peat");
		OreDictionary.registerOre("brickPeat", peat);

		ash = registerItem(new ItemForestry(), "ash");
		OreDictionary.registerOre("dustAsh", ash);

		bituminousPeat = registerItem(new ItemForestry(), "bituminousPeat");

		gearBronze = registerItem(new ItemForestry(), "gearBronze");
		OreDictionary.registerOre("gearBronze", gearBronze);
		gearCopper = registerItem(new ItemForestry(), "gearCopper");
		OreDictionary.registerOre("gearCopper", gearCopper);
		gearTin = registerItem(new ItemForestry(), "gearTin");
		OreDictionary.registerOre("gearTin", gearTin);

		circuitboards = registerItem(new ItemCircuitBoard(), "chipsets");

		solderingIron = new ItemSolderingIron();
		solderingIron.setMaxDamage(5).setFull3D();
		registerItem(solderingIron, "solderingIron");

		tubes = registerItem(new ItemElectronTube(), "thermionicTubes");

		// / CARTONS
		carton = registerItem(new ItemForestry(), "carton");

		// / CRAFTING CARPENTER
		stickImpregnated = registerItem(new ItemForestry(), "oakStick");
		woodPulp = registerItem(new ItemForestry(), "woodPulp");
		OreDictionary.registerOre("pulpWood", woodPulp);

		// / RECLAMATION
		brokenBronzePickaxe = registerItem(new ItemForestry(), "brokenBronzePickaxe");
		brokenBronzeShovel = registerItem(new ItemForestry(), "brokenBronzeShovel");

		// / TOOLS
		bronzePickaxe = new ItemForestryTool(new ItemStack(brokenBronzePickaxe));
		bronzePickaxe.setHarvestLevel("pickaxe", 3);
		registerItem(bronzePickaxe, "bronzePickaxe");

		bronzeShovel = new ItemForestryTool(new ItemStack(brokenBronzeShovel));
		bronzeShovel.setHarvestLevel("shovel", 3);
		registerItem(bronzeShovel, "bronzeShovel");

		// / ASSEMBLY KITS
		kitShovel = new ItemAssemblyKit(new ItemStack(bronzeShovel));
		registerItem(kitShovel, "kitShovel");

		kitPickaxe = new ItemAssemblyKit(new ItemStack(bronzePickaxe));
		registerItem(kitPickaxe, "kitPickaxe");

		// / MOISTENER RESOURCES
		mouldyWheat = registerItem(new ItemForestry(), "mouldyWheat");
		decayingWheat = registerItem(new ItemForestry(), "decayingWheat");
		mulch = registerItem(new ItemForestry(), "mulch");

		// / RAINMAKER SUBSTRATES
		iodineCharge = registerItem(new ItemForestry(), "iodineCapsule");

		phosphor = registerItem(new ItemForestry(), "phosphor");

		// / BEE RESOURCES
		beeswax = registerItem(new ItemForestry(), "beeswax");
		beeswax.setCreativeTab(Tabs.tabApiculture);
		OreDictionary.registerOre("itemBeeswax", beeswax);

		refractoryWax = registerItem(new ItemForestry(), "refractoryWax");

		// FRUITS
		fruits = registerItem(new ItemFruit(), "fruits");
		for (ItemFruit.EnumFruit def : ItemFruit.EnumFruit.values()) {
			ItemStack fruit = new ItemStack(fruits, 1, def.ordinal());
			OreDictionary.registerOre(def.getOreDict(), fruit);
		}
	}
}
