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

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.Tabs;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.genetics.ItemResearchNote;
import forestry.core.utils.OreDictUtil;

public class ItemRegistryCore extends ItemRegistry {
	/* Fertilizer */
	public final ItemForestry fertilizerBio;
	public final ItemFertilizer fertilizerCompound;

	/* Gems */
	public final ItemForestry apatite;

	/* Research */
	public final ItemResearchNote researchNote;
	
	/* Alyzer */
	public final ItemAlyzer portableAlyzer;

	/* Ingots */
	public final ItemStack ingotCopper;
	public final ItemStack ingotTin;
	public final ItemStack ingotBronze;

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
	public final ItemStack gearBronze;
	public final ItemStack gearCopper;
	public final ItemStack gearTin;

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
		fertilizerCompound = registerItem(new ItemFertilizer(), "fertilizerCompound");

		apatite = registerItem(new ItemForestry(), "apatite");
		OreDictionary.registerOre(OreDictUtil.GEM_APATITE, apatite);

		researchNote = registerItem(new ItemResearchNote(), "researchNote");
		
		portableAlyzer = registerItem(new ItemAlyzer(), "portableAlyzer");

		ingotCopper = createItemForOreName(OreDictUtil.INGOT_COPPER);
		ingotTin = createItemForOreName(OreDictUtil.INGOT_TIN);
		ingotBronze = createItemForOreName(OreDictUtil.INGOT_BRONZE);

		wrench = registerItem(new ItemWrench(), "wrench");
		pipette = registerItem(new ItemPipette(), "pipette");

		sturdyCasing = registerItem(new ItemForestry(), "sturdyMachine");
		hardenedCasing = registerItem(new ItemForestry(), "hardenedMachine");
		impregnatedCasing = registerItem(new ItemForestry(), "impregnatedCasing");

		craftingMaterial = registerItem(new ItemCraftingMaterial(), "craftingMaterial");

		spectacles = registerItem(new ItemArmorNaturalist(), "naturalistHelmet");

		peat = registerItem(new ItemForestry(), "peat");
		OreDictionary.registerOre(OreDictUtil.BRICK_PEAT, peat);

		ash = registerItem(new ItemForestry(), "ash");
		OreDictionary.registerOre(OreDictUtil.DUST_ASH, ash);

		bituminousPeat = registerItem(new ItemForestry(), "bituminousPeat");

		gearBronze = createItemForOreName(OreDictUtil.GEAR_BRONZE);
		gearCopper = createItemForOreName(OreDictUtil.GEAR_COPPER);
		gearTin = createItemForOreName(OreDictUtil.GEAR_TIN);

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
		OreDictionary.registerOre(OreDictUtil.PULP_WOOD, woodPulp);

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
		OreDictionary.registerOre(OreDictUtil.ITEM_BEESWAX, beeswax);

		refractoryWax = registerItem(new ItemForestry(), "refractoryWax");

		// FRUITS
		fruits = registerItem(new ItemFruit(), "fruits");
		for (ItemFruit.EnumFruit def : ItemFruit.EnumFruit.values()) {
			ItemStack fruit = new ItemStack(fruits, 1, def.ordinal());
			OreDictionary.registerOre(def.getOreDict(), fruit);
		}
	}

}
