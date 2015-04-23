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
package forestry.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.IIcon;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.apiculture.GuiHandlerApiculture;
import forestry.apiculture.SaveEventHandlerApiculture;
import forestry.apiculture.VillageHandlerApiculture;
import forestry.apiculture.commands.CommandBee;
import forestry.apiculture.flowers.FlowerProvider;
import forestry.apiculture.flowers.FlowerRegistry;
import forestry.apiculture.gadgets.BlockAlveary;
import forestry.apiculture.gadgets.BlockBeehives;
import forestry.apiculture.gadgets.BlockCandle;
import forestry.apiculture.gadgets.BlockStump;
import forestry.apiculture.gadgets.StructureLogicAlveary;
import forestry.apiculture.gadgets.TileAlvearyFan;
import forestry.apiculture.gadgets.TileAlvearyHeater;
import forestry.apiculture.gadgets.TileAlvearyHygroregulator;
import forestry.apiculture.gadgets.TileAlvearyPlain;
import forestry.apiculture.gadgets.TileAlvearySieve;
import forestry.apiculture.gadgets.TileAlvearyStabiliser;
import forestry.apiculture.gadgets.TileAlvearySwarmer;
import forestry.apiculture.gadgets.TileApiaristChest;
import forestry.apiculture.gadgets.TileApiary;
import forestry.apiculture.gadgets.TileBeehouse;
import forestry.apiculture.gadgets.TileCandle;
import forestry.apiculture.gadgets.TileSwarm;
import forestry.apiculture.genetics.AlleleEffectAggressive;
import forestry.apiculture.genetics.AlleleEffectCreeper;
import forestry.apiculture.genetics.AlleleEffectExploration;
import forestry.apiculture.genetics.AlleleEffectFertile;
import forestry.apiculture.genetics.AlleleEffectFungification;
import forestry.apiculture.genetics.AlleleEffectGlacial;
import forestry.apiculture.genetics.AlleleEffectHeroic;
import forestry.apiculture.genetics.AlleleEffectIgnition;
import forestry.apiculture.genetics.AlleleEffectMiasmic;
import forestry.apiculture.genetics.AlleleEffectMisanthrope;
import forestry.apiculture.genetics.AlleleEffectNone;
import forestry.apiculture.genetics.AlleleEffectPotion;
import forestry.apiculture.genetics.AlleleEffectRadioactive;
import forestry.apiculture.genetics.AlleleEffectRepulsion;
import forestry.apiculture.genetics.AlleleEffectResurrection;
import forestry.apiculture.genetics.AlleleEffectSnowing;
import forestry.apiculture.genetics.AlleleFlowers;
import forestry.apiculture.genetics.BeeHelper;
import forestry.apiculture.genetics.BeekeepingMode;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.apiculture.genetics.BeeBranchDefinition;
import forestry.apiculture.genetics.HiveDrop;
import forestry.apiculture.items.ItemAlvearyBlock;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.apiculture.items.ItemBeeGE;
import forestry.apiculture.items.ItemBeealyzer;
import forestry.apiculture.items.ItemCandleBlock;
import forestry.apiculture.items.ItemHabitatLocator;
import forestry.apiculture.items.ItemHiveFrame;
import forestry.apiculture.items.ItemHoneycomb;
import forestry.apiculture.items.ItemImprinter;
import forestry.apiculture.items.ItemWaxCast;
import forestry.apiculture.network.PacketHandlerApiculture;
import forestry.apiculture.proxy.ProxyApiculture;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.apiculture.worldgen.HiveDecorator;
import forestry.apiculture.worldgen.HiveDescription;
import forestry.apiculture.worldgen.HiveGenHelper;
import forestry.apiculture.worldgen.HiveRegistry;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.Property;
import forestry.core.fluids.Fluids;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.gadgets.TileAnalyzer;
import forestry.core.genetics.Allele;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.items.ItemForestry;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemOverlay;
import forestry.core.items.ItemOverlay.OverlayInfo;
import forestry.core.items.ItemScoop;
import forestry.core.proxy.Proxies;
import forestry.core.render.EntitySnowFX;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.StackUtils;

@Plugin(pluginID = "Apiculture", name = "Apiculture", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.apiculture.description")
public class PluginApiculture extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.apiculture.proxy.ClientProxyApiculture", serverSide = "forestry.apiculture.proxy.ProxyApiculture")
	public static ProxyApiculture proxy;
	private static final String CONFIG_CATEGORY = "apiculture";
	private Configuration apicultureConfig;
	public static String beekeepingMode = "NORMAL";
	private static int secondPrincessChance = 0;
	public static int ticksPerBeeWorkCycle = 550;
	public static boolean apiarySideSensitive = false;
	public static boolean fancyRenderedBees = false;

	/**
	 * See {@link IBeeRoot} for details
	 */
	public static IBeeRoot beeInterface;
	public static HiveRegistry hiveRegistry;

	public static MachineDefinition definitionApiary;
	public static MachineDefinition definitionChest;
	public static MachineDefinition definitionBeehouse;
	public static MachineDefinition definitionAnalyzer;

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void setupAPI() {
		super.setupAPI();

		HiveManager.hiveRegistry = hiveRegistry = new HiveRegistry();
		HiveManager.genHelper = new HiveGenHelper();

		FlowerManager.flowerRegistry = new FlowerRegistry();

		BeeManager.villageBees = new ArrayList[]{new ArrayList<IBeeGenome>(), new ArrayList<IBeeGenome>()};

		// Init bee interface
		AlleleManager.alleleRegistry.registerSpeciesRoot(PluginApiculture.beeInterface = new BeeHelper());

		// Modes
		PluginApiculture.beeInterface.registerBeekeepingMode(BeekeepingMode.easy);
		PluginApiculture.beeInterface.registerBeekeepingMode(BeekeepingMode.normal);
		PluginApiculture.beeInterface.registerBeekeepingMode(BeekeepingMode.hard);
		PluginApiculture.beeInterface.registerBeekeepingMode(BeekeepingMode.hardcore);
		PluginApiculture.beeInterface.registerBeekeepingMode(BeekeepingMode.insane);
	}

	@Override
	public void preInit() {
		super.preInit();

		MinecraftForge.EVENT_BUS.register(this);

		ForestryBlock.apiculture.registerBlock(new BlockBase(Material.iron), ItemForestryBlock.class, "apiculture");
		ForestryBlock.apiculture.block().setCreativeTab(Tabs.tabApiculture);
		ForestryBlock.apiculture.block().setHarvestLevel("axe", 0);

		definitionApiary = ((BlockBase) ForestryBlock.apiculture.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_APIARY_META, "forestry.Apiary", TileApiary.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.apiculture.getItemStack(1, Defaults.DEFINITION_APIARY_META),
						"XXX",
						"#C#",
						"###",
						'X', "slabWood",
						'#', "plankWood",
						'C', ForestryItem.impregnatedCasing))
				.setFaces(0, 1, 2, 2, 4, 4, 0, 7));

		definitionChest = ((BlockBase) ForestryBlock.apiculture.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_APIARISTCHEST_META, "forestry.ApiaristChest", TileApiaristChest.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.apiculture.getItemStack(1, Defaults.DEFINITION_APIARISTCHEST_META),
						" # ",
						"XYX",
						"XXX",
						'#', Blocks.glass,
						'X', "beeComb",
						'Y', Blocks.chest))
				.setFaces(0, 1, 2, 3, 4, 4));

		definitionBeehouse = ((BlockBase) ForestryBlock.apiculture.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_BEEHOUSE_META, "forestry.Beehouse", TileBeehouse.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.apiculture.getItemStack(1, Defaults.DEFINITION_BEEHOUSE_META),
						"XXX",
						"#C#",
						"###",
						'X', "slabWood",
						'#', "plankWood",
						'C', "beeComb"))
				.setFaces(0, 1, 2, 2, 4, 4, 0, 7));

		definitionAnalyzer = ((BlockBase) ForestryBlock.core.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_ANALYZER_META, "forestry.Analyzer", TileAnalyzer.class,
				PluginApiculture.proxy.getRendererAnalyzer(Defaults.TEXTURE_PATH_BLOCKS + "/analyzer_")));

		ForestryBlock.beehives.registerBlock(new BlockBeehives(), ItemForestryBlock.class, "beehives");

		// Candles
		ForestryBlock.candle.registerBlock(new BlockCandle(), ItemCandleBlock.class, "candle");
		ForestryBlock.stump.registerBlock(new BlockStump(), ItemForestryBlock.class, "stump");

		// Alveary and Components
		ForestryBlock.alveary.registerBlock(new BlockAlveary(), ItemAlvearyBlock.class, "alveary");
		ForestryBlock.alveary.block().setHarvestLevel("axe", 0);

		// Add triggers
		if (PluginManager.Module.BUILDCRAFT_STATEMENTS.isEnabled()) {
			ApicultureTriggers.initialize();
		}

		if (Config.enableVillager) {
			// Register village components with the Structure registry.
			VillageHandlerApiculture.registerVillageComponents();
		}

		// Commands
		PluginCore.rootCommand.addChildCommand(new CommandBee());
	}

	@Override
	public void doInit() {
		super.doInit();

		// Config
		apicultureConfig = new Configuration();

		Property property = apicultureConfig.get("apiary.sidesensitive", CONFIG_CATEGORY, apiarySideSensitive);
		property.comment = "set to false if apiaries should output all items regardless of side a pipe is attached to";
		apiarySideSensitive = Boolean.parseBoolean(property.value);

		property = apicultureConfig.get("render.bees.fancy", CONFIG_CATEGORY, fancyRenderedBees);
		property.comment = "set to true to enable a fancy butterfly-like renderer for bees. (experimental!)";
		fancyRenderedBees = Boolean.parseBoolean(property.value);

		property = apicultureConfig.get("beekeeping.mode", CONFIG_CATEGORY, "NORMAL");
		property.comment = "change beekeeping modes here. possible values EASY, NORMAL, HARD, HARDCORE, INSANE. mods may add additional modes.";
		beekeepingMode = property.value.trim();
		Proxies.log.finer("Beekeeping mode read from config: " + beekeepingMode);

		Property secondPrincess = apicultureConfig.get("beekeeping.secondprincess", CONFIG_CATEGORY, secondPrincessChance);
		secondPrincess.comment = "percent chance of second princess drop, for limited/skyblock maps.";
		secondPrincessChance = Integer.parseInt(secondPrincess.value);

		property = apicultureConfig.get("beekeeping.flowers.custom", CONFIG_CATEGORY, "");
		property.comment = "add additional flower blocks for apiaries here in the format modid:name or modid:name:meta. separate blocks using ';'. wildcard for metadata: '*'. will be treated like vanilla flowers. not recommended for flowers implemented as tile entities.";
		parseAdditionalFlowers(property.value, FlowerManager.plainFlowers);

		property = apicultureConfig.get("species.blacklist", CONFIG_CATEGORY, "");
		property.comment = "add species to blacklist identified by their uid and seperated with ';'.";
		parseBeeBlacklist(property.value);

		apicultureConfig.save();

		// Genetics
		createAlleles();
		BeeDefinition.initBees();

		// Hives
		createHives();
		registerBeehiveDrops();

		// Inducers for swarmer
		BeeManager.inducers.put(ForestryItem.royalJelly.getItemStack(), 10);

		GameRegistry.registerTileEntity(TileAlvearyPlain.class, "forestry.Alveary");
		GameRegistry.registerTileEntity(TileSwarm.class, "forestry.Swarm");
		GameRegistry.registerTileEntity(TileAlvearySwarmer.class, "forestry.AlvearySwarmer");
		GameRegistry.registerTileEntity(TileAlvearyHeater.class, "forestry.AlvearyHeater");
		GameRegistry.registerTileEntity(TileAlvearyFan.class, "forestry.AlvearyFan");
		GameRegistry.registerTileEntity(TileAlvearyHygroregulator.class, "forestry.AlvearyHygro");
		GameRegistry.registerTileEntity(TileAlvearyStabiliser.class, "forestry.AlvearyStabiliser");
		GameRegistry.registerTileEntity(TileAlvearySieve.class, "forestry.AlvearySieve");
		GameRegistry.registerTileEntity(TileCandle.class, "forestry.Candle");


		BeeManager.villageBees[0].add(BeeDefinition.FOREST.getGenome());
		BeeManager.villageBees[0].add(BeeDefinition.MEADOWS.getGenome());
		BeeManager.villageBees[0].add(BeeDefinition.MODEST.getGenome());
		BeeManager.villageBees[0].add(BeeDefinition.MARSHY.getGenome());
		BeeManager.villageBees[0].add(BeeDefinition.WINTRY.getGenome());
		BeeManager.villageBees[0].add(BeeDefinition.TROPICAL.getGenome());

		BeeManager.villageBees[1].add(BeeDefinition.FOREST.getRainResist().getGenome());
		BeeManager.villageBees[1].add(BeeDefinition.COMMON.getGenome());
		BeeManager.villageBees[1].add(BeeDefinition.VALIANT.getGenome());

		if (Config.enableVillager) {
			// Register villager stuff
			VillageHandlerApiculture villageHandler = new VillageHandlerApiculture();
			VillagerRegistry.instance().registerVillageCreationHandler(villageHandler);
			VillagerRegistry.instance().registerVillagerId(Defaults.ID_VILLAGER_BEEKEEPER);
			Proxies.render.registerVillagerSkin(Defaults.ID_VILLAGER_BEEKEEPER, Defaults.TEXTURE_SKIN_BEEKPEEPER);
			VillagerRegistry.instance().registerVillageTradeHandler(Defaults.ID_VILLAGER_BEEKEEPER, villageHandler);
		}

		proxy.initializeRendering();
	}

	@Override
	public void postInit() {
		super.postInit();
		registerDungeonLoot();
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerApiculture();
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerApiculture();
	}

	@Override
	protected void registerItems() {

		// / BEES
		ForestryItem.beeQueenGE.registerItem((new ItemBeeGE(EnumBeeType.QUEEN)), "beeQueenGE");
		ForestryItem.beeDroneGE.registerItem((new ItemBeeGE(EnumBeeType.DRONE)), "beeDroneGE");
		ForestryItem.beePrincessGE.registerItem((new ItemBeeGE(EnumBeeType.PRINCESS)), "beePrincessGE");
		ForestryItem.beeLarvaeGE.registerItem((new ItemBeeGE(EnumBeeType.LARVAE)), "beeLarvaeGE");

		ForestryItem.beealyzer.registerItem((new ItemBeealyzer()), "beealyzer");
		ForestryItem.habitatLocator.registerItem(new ItemHabitatLocator(), "habitatLocator");
		ForestryItem.imprinter.registerItem((new ItemImprinter()), "imprinter");

		// / COMB FRAMES
		ForestryItem.frameUntreated.registerItem(new ItemHiveFrame(80, 0.9f), "frameUntreated");
		ForestryItem.frameImpregnated.registerItem(new ItemHiveFrame(240, 0.4f), "frameImpregnated");
		ForestryItem.frameProven.registerItem(new ItemHiveFrame(720, 0.3f), "frameProven");

		// / BEE RESOURCES
		ForestryItem.honeyDrop.registerItem(new ItemOverlay(Tabs.tabApiculture,
				new OverlayInfo("honey", 0xecb42d, 0xe8c814),
				new OverlayInfo("charged", 0x800505, 0x9c0707).setIsSecret(),
				new OverlayInfo("omega", 0x191919, 0x4a8ca7).setIsSecret()), "honeyDrop");
		OreDictionary.registerOre("dropHoney", ForestryItem.honeyDrop.getItemStack());

		ForestryItem.pollenCluster.registerItem(new ItemOverlay(Tabs.tabApiculture,
						new OverlayInfo("normal", 0xa28a25, 0xa28a25),
						new OverlayInfo("crystalline", 0xffffff, 0xc5feff)),
				"pollen");
		OreDictionary.registerOre("itemPollen", ForestryItem.pollenCluster.getItemStack());

		ForestryItem.propolis.registerItem(new ItemOverlay(Tabs.tabApiculture,
						new OverlayInfo("normal", 0xc5b24e),
						new OverlayInfo("sticky", 0xc68e57),
						new OverlayInfo("pulsating", 0x2ccdb1).setIsSecret(),
						new OverlayInfo("silky", 0xddff00)),
				"propolis");

		ForestryItem.honeydew.registerItem(new ItemForestry().setCreativeTab(Tabs.tabApiculture), "honeydew");
		OreDictionary.registerOre("dropHoneydew", ForestryItem.honeydew.getItemStack());

		ForestryItem.royalJelly.registerItem(new ItemForestry().setCreativeTab(Tabs.tabApiculture), "royalJelly");
		OreDictionary.registerOre("dropRoyalJelly", ForestryItem.royalJelly.getItemStack());

		ForestryItem.waxCast.registerItem(new ItemWaxCast().setCreativeTab(Tabs.tabApiculture), "waxCast");

		// / BEE COMBS
		ForestryItem.beeComb.registerItem(new ItemHoneycomb(), "beeCombs");
		OreDictionary.registerOre("beeComb", ForestryItem.beeComb.getWildcard());

		// / APIARIST'S CLOTHES
		ForestryItem.apiaristHat.registerItem(new ItemArmorApiarist(0), "apiaristHelmet");
		ForestryItem.apiaristChest.registerItem(new ItemArmorApiarist(1), "apiaristChest");
		ForestryItem.apiaristLegs.registerItem(new ItemArmorApiarist(2), "apiaristLegs");
		ForestryItem.apiaristBoots.registerItem(new ItemArmorApiarist(3), "apiaristBoots");

		// TOOLS
		ForestryItem.scoop.registerItem(new ItemScoop(), "scoop");
		ForestryItem.scoop.item().setHarvestLevel("scoop", 3);
	}

	@Override
	protected void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		crateRegistry.registerCrate(ForestryItem.beeswax.getItemStack(), "cratedBeeswax");
		crateRegistry.registerCrate(ForestryItem.pollenCluster.getItemStack(), "cratedPollen");
		crateRegistry.registerCrate(ForestryItem.propolis.getItemStack(), "cratedPropolis");
		crateRegistry.registerCrate(ForestryItem.honeydew.getItemStack(), "cratedHoneydew");
		crateRegistry.registerCrate(ForestryItem.royalJelly.getItemStack(), "cratedRoyalJelly");

		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 0), "cratedHoneycombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 1), "cratedCocoaComb");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 2), "cratedSimmeringCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 3), "cratedStringyCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 4), "cratedFrozenCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 5), "cratedDrippingCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 6), "cratedSilkyCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 7), "cratedParchedCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 8), "cratedMysteriousCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 10), "cratedPowderyCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 14), "cratedWheatenCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 15), "cratedMossyCombs");
		crateRegistry.registerCrate(ForestryItem.beeComb.getItemStack(1, 16), "cratedMellowCombs");

		crateRegistry.registerCrate(ForestryItem.refractoryWax.getItemStack(), "cratedRefractoryWax");
	}

	@Override
	protected void registerRecipes() {

		// / APIARIST'S ARMOR
		Proxies.common.addRecipe(ForestryItem.apiaristHat.getItemStack(),
				"###", "# #",
				'#', ForestryItem.craftingMaterial.getItemStack(1, 3));
		Proxies.common.addRecipe(ForestryItem.apiaristChest.getItemStack(),
				"# #", "###", "###",
				'#', ForestryItem.craftingMaterial.getItemStack(1, 3));
		Proxies.common.addRecipe(ForestryItem.apiaristLegs.getItemStack(),
				"###", "# #", "# #",
				'#', ForestryItem.craftingMaterial.getItemStack(1, 3));
		Proxies.common.addRecipe(ForestryItem.apiaristBoots.getItemStack(),
				"# #", "# #",
				'#', ForestryItem.craftingMaterial.getItemStack(1, 3));

		// / HABITAT LOCATOR
		Proxies.common.addRecipe(ForestryItem.habitatLocator.getItemStack(),
				" X ",
				"X#X",
				" X ",
				'#', Items.redstone, 'X', "ingotBronze");

		// Bees
		Proxies.common.addRecipe(ForestryItem.scoop.getItemStack(1),
				"#X#", "###", " # ",
				'#', "stickWood",
				'X', Blocks.wool);
		Proxies.common.addRecipe(new ItemStack(Items.slime_ball),
				"#X#", "#X#", "#X#",
				'#', ForestryItem.propolis,
				'X', ForestryItem.pollenCluster);
		Proxies.common.addRecipe(new ItemStack(Items.speckled_melon),
				"#X#", "#Y#", "#X#",
				'#', ForestryItem.honeyDrop,
				'X', ForestryItem.honeydew,
				'Y', Items.melon);
		Proxies.common.addRecipe(ForestryItem.frameUntreated.getItemStack(),
				"###", "#S#", "###",
				'#', "stickWood",
				'S', Items.string);
		Proxies.common.addRecipe(ForestryItem.frameImpregnated.getItemStack(),
				"###", "#S#", "###",
				'#', ForestryItem.stickImpregnated,
				'S', Items.string);

		// FOOD STUFF
		if (ForestryItem.honeyedSlice.item() != null) {
			Proxies.common.addRecipe(ForestryItem.honeyedSlice.getItemStack(4),
					"###", "#X#", "###",
					'#', ForestryItem.honeyDrop,
					'X', Items.bread);
		}
		if (ForestryItem.honeyPot.item() != null) {
			Proxies.common.addRecipe(ForestryItem.honeyPot.getItemStack(1),
					"# #", " X ", "# #",
					'#', ForestryItem.honeyDrop,
					'X', ForestryItem.waxCapsule);
		}
		if (ForestryItem.ambrosia.item() != null) {
			Proxies.common.addRecipe(ForestryItem.ambrosia.getItemStack(),
					"#Y#", "XXX", "###",
					'#', ForestryItem.honeydew,
					'X', ForestryItem.royalJelly,
					'Y', ForestryItem.waxCapsule);
		}

		// / CAPSULES
		Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.capsule"), "###", '#', ForestryItem.beeswax);
		Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.refractory"), "###", '#', ForestryItem.refractoryWax);

		// / BITUMINOUS PEAT
		Proxies.common.addRecipe(ForestryItem.bituminousPeat.getItemStack(),
				" # ", "XYX", " # ",
				'#', "dustAsh",
				'X', ForestryItem.peat,
				'Y', ForestryItem.propolis);

		// / TORCHES
		Proxies.common.addRecipe(new ItemStack(Blocks.torch, 3),
				" # ", " # ", " Y ",
				'#', ForestryItem.beeswax,
				'Y', "stickWood");
		Proxies.common.addRecipe(ForestryItem.craftingMaterial.getItemStack(1, 1),
				"# #", " # ", "# #",
				'#', ForestryItem.propolis.getItemStack(1, 2));

		// / WAX CAST
		Proxies.common.addRecipe(ForestryItem.waxCast.getItemStack(),
				"###",
				"# #",
				"###",
				'#', ForestryItem.beeswax);

		// / ALVEARY
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(),
				"###",
				"#X#",
				"###",
				'X', ForestryItem.impregnatedCasing,
				'#', ForestryItem.craftingMaterial.getItemStack(1, 6));
		// SWARMER
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, 2),
				"#G#",
				" X ",
				"#G#",
				'#', ForestryItem.tubes.getItemStack(1, 5),
				'X', ForestryBlock.alveary,
				'G', Items.gold_ingot);
		// FAN
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, 3),
				"I I",
				" X ",
				"I#I",
				'#', ForestryItem.tubes.getItemStack(1, 4),
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot);
		// HEATER
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, 4),
				"#I#",
				" X ",
				"YYY",
				'#', ForestryItem.tubes.getItemStack(1, 4),
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot, 'Y', Blocks.stone);
		// HYGROREGULATOR
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, 5),
				"GIG",
				"GXG",
				"GIG",
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot,
				'G', Blocks.glass);
		// STABILISER
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, 6),
				"G G",
				"GXG",
				"G G",
				'X', ForestryBlock.alveary,
				'G', Items.quartz);
		// SIEVE
		Proxies.common.addRecipe(ForestryBlock.alveary.getItemStack(1, TileAlvearySieve.BLOCK_META),
				"III",
				" X ",
				"WWW",
				'X', ForestryBlock.alveary,
				'I', Items.iron_ingot,
				'W', ForestryItem.craftingMaterial.getItemStack(1, 3));

		if (PluginManager.Module.FACTORY.isEnabled()) {
			// / SQUEEZER
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.honeyDrop.getItemStack()}, Fluids.HONEY.getFluid(Defaults.FLUID_PER_HONEY_DROP),
					ForestryItem.propolis.getItemStack(), 5);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.honeydew.getItemStack()}, Fluids.HONEY.getFluid(Defaults.FLUID_PER_HONEY_DROP));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.phosphor.getItemStack(2), new ItemStack(Blocks.sand)}, Fluids.LAVA.getFluid(2000));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.phosphor.getItemStack(2), new ItemStack(Blocks.dirt)}, Fluids.LAVA.getFluid(1600));

			// / CARPENTER
			RecipeManagers.carpenterManager.addRecipe(100, Fluids.WATER.getFluid(2000), null, ForestryItem.beealyzer.getItemStack(),
					"X#X", "X#X", "RDR",
					'#', Blocks.glass_pane,
					'X', "ingotTin",
					'R', Items.redstone,
					'D', Items.diamond);
			RecipeManagers.carpenterManager.addRecipe(50, Fluids.HONEY.getFluid(500), null, ForestryItem.craftingMaterial.getItemStack(1, 6),
					" J ", "###", "WPW",
					'#', "plankWood",
					'J', ForestryItem.royalJelly,
					'W', ForestryItem.beeswax,
					'P', ForestryItem.pollenCluster);

			RecipeManagers.carpenterManager.addRecipe(30, Fluids.WATER.getFluid(600), null, ForestryBlock.candle.getItemStack(24),
					" X ",
					"###",
					"###",
					'#', ForestryItem.beeswax,
					'X', Items.string);
			RecipeManagers.carpenterManager.addRecipe(10, Fluids.WATER.getFluid(200), null, ForestryBlock.candle.getItemStack(6),
					"#X#",
					'#', ForestryItem.beeswax,
					'X', ForestryItem.craftingMaterial.getItemStack(1, 2));
			Proxies.common.addShapelessRecipe(ForestryBlock.candle.getItemStack(), ForestryBlock.candle.getItemStack());
			Proxies.common.addShapelessRecipe(ForestryBlock.candle.getItemStack(1, 1), ForestryBlock.candle.getItemStack(1, 1));

			// / CENTRIFUGE
			// Honey combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 0), ForestryItem.beeswax.getItemStack(), ForestryItem.honeyDrop.getItemStack(), 90);
			// Cocoa combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 1), ForestryItem.beeswax.getItemStack(), new ItemStack(Items.dye, 1, 3), 50);
			// Simmering combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 2), ForestryItem.refractoryWax.getItemStack(), ForestryItem.phosphor.getItemStack(2), 70);
			// Stringy combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 3), ForestryItem.propolis.getItemStack(), ForestryItem.honeyDrop.getItemStack(), 40);
			// Dripping combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 5), ForestryItem.honeydew.getItemStack(), ForestryItem.honeyDrop.getItemStack(), 40);
			// Frozen combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 4), new ItemStack[]{ForestryItem.beeswax.getItemStack(),
					ForestryItem.honeyDrop.getItemStack(), new ItemStack(Items.snowball), ForestryItem.pollenCluster.getItemStack(1, 1)}, new int[]{80, 70, 40, 20});
			// Silky combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 6), ForestryItem.honeyDrop.getItemStack(), ForestryItem.propolis.getItemStack(1, 3), 80);
			// Parched combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 7), ForestryItem.beeswax.getItemStack(), ForestryItem.honeyDrop.getItemStack(), 90);
			// Mysterious combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 8), new ItemStack[]{ForestryItem.honeyDrop.getItemStack(),
					ForestryItem.propolis.getItemStack(1, 2)}, new int[]{40, 100});
			// Irradiated combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 9), new ItemStack[]{}, new int[]{});
			// Powdery combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 10), new ItemStack[]{ForestryItem.honeyDrop.getItemStack(),
					ForestryItem.beeswax.getItemStack(), new ItemStack(Items.gunpowder)}, new int[]{20, 20, 90});
			// Reddened Combs
			RecipeManagers.centrifugeManager.addRecipe(80, ForestryItem.beeComb.getItemStack(1, 11),
					new ItemStack[]{ForestryItem.honeyDrop.getItemStack(2, 1)}, new int[]{100});
			// Darkened Combs
			RecipeManagers.centrifugeManager.addRecipe(80, ForestryItem.beeComb.getItemStack(1, 12),
					new ItemStack[]{ForestryItem.honeyDrop.getItemStack(1, 1)}, new int[]{100});
			// Omega Combs
			RecipeManagers.centrifugeManager.addRecipe(400, ForestryItem.beeComb.getItemStack(1, 13), new ItemStack[]{ForestryItem.honeyDrop.getItemStack(1, 2)}, new int[]{100});
			// Wheaten Combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 14), new ItemStack[]{ForestryItem.honeyDrop.getItemStack(),
					ForestryItem.beeswax.getItemStack(), new ItemStack(Items.wheat)}, new int[]{20, 20, 80});
			// Mossy Combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 15), ForestryItem.beeswax.getItemStack(), ForestryItem.honeyDrop.getItemStack(), 90);
			// Mellow Combs
			RecipeManagers.centrifugeManager.addRecipe(20, ForestryItem.beeComb.getItemStack(1, 16), new ItemStack[]{ForestryItem.honeydew.getItemStack(),
					ForestryItem.beeswax.getItemStack(), new ItemStack(Items.quartz)}, new int[]{60, 20, 30});

			// Silk
			RecipeManagers.centrifugeManager.addRecipe(5, ForestryItem.propolis.getItemStack(1, 3), new ItemStack[]{
					ForestryItem.craftingMaterial.getItemStack(1, 2), ForestryItem.propolis.getItemStack()}, new int[]{60, 10});

			// / FERMENTER
			RecipeManagers.fermenterManager.addRecipe(ForestryItem.honeydew.getItemStack(), 500, 1.0f, Fluids.SHORT_MEAD.getFluid(1), Fluids.HONEY.getFluid(1));
		}

		// ANALYZER
		definitionAnalyzer.recipes = createAlyzerRecipes(ForestryBlock.core.block(), Defaults.DEFINITION_ANALYZER_META);

		definitionAnalyzer.register();
		definitionApiary.register();
		definitionBeehouse.register();
		definitionChest.register();
	}

	public static IRecipe[] createAlyzerRecipes(Block block, int meta) {
		ArrayList<IRecipe> recipes = new ArrayList<IRecipe>();
		recipes.add(ShapedRecipeCustom.createShapedRecipe(new ItemStack(block, 1, meta), "XTX", " Y ", "X X", 'Y', ForestryItem.sturdyCasing, 'T', ForestryItem.beealyzer, 'X', "ingotBronze"));
		recipes.add(ShapedRecipeCustom.createShapedRecipe(new ItemStack(block, 1, meta), "XTX", " Y ", "X X", 'Y', ForestryItem.sturdyCasing, 'T', ForestryItem.treealyzer, 'X', "ingotBronze"));
		return recipes.toArray(new IRecipe[recipes.size()]);
	}

	private static void registerBeehiveDrops() {
		ItemStack honeyComb = ForestryItem.beeComb.getItemStack(1, 0);
		hiveRegistry.addDrops(HiveRegistry.forest,
				new HiveDrop(80, BeeDefinition.FOREST, honeyComb).setIgnobleShare(0.7f),
				new HiveDrop(8, BeeDefinition.FOREST.getRainResist(), honeyComb),
				new HiveDrop(3, BeeDefinition.VALIANT, honeyComb)
		);

		hiveRegistry.addDrops(HiveRegistry.meadows,
				new HiveDrop(80, BeeDefinition.MEADOWS, honeyComb).setIgnobleShare(0.7f),
				new HiveDrop(3, BeeDefinition.VALIANT, honeyComb)
		);

		ItemStack parchedComb = ForestryItem.beeComb.getItemStack(1, 7);
		hiveRegistry.addDrops(HiveRegistry.desert,
				new HiveDrop(80, BeeDefinition.MODEST, parchedComb).setIgnobleShare(0.7f),
				new HiveDrop(3, BeeDefinition.VALIANT, parchedComb)
		);

		ItemStack silkyComb = ForestryItem.beeComb.getItemStack(1, 6);
		hiveRegistry.addDrops(HiveRegistry.jungle,
				new HiveDrop(80, BeeDefinition.TROPICAL, silkyComb).setIgnobleShare(0.7f),
				new HiveDrop(3, BeeDefinition.VALIANT, silkyComb)
		);

		ItemStack mysteriousComb = ForestryItem.beeComb.getItemStack(1, 8);
		hiveRegistry.addDrops(HiveRegistry.end,
				new HiveDrop(90, BeeDefinition.ENDED, mysteriousComb)
		);

		ItemStack frozenComb = ForestryItem.beeComb.getItemStack(1, 4);
		hiveRegistry.addDrops(HiveRegistry.snow,
				new HiveDrop(80, BeeDefinition.WINTRY, frozenComb).setIgnobleShare(0.5f),
				new HiveDrop(3, BeeDefinition.VALIANT, frozenComb)
		);

		ItemStack mossyComb = ForestryItem.beeComb.getItemStack(1, 15);
		hiveRegistry.addDrops(HiveRegistry.swamp,
				new HiveDrop(80, BeeDefinition.MARSHY, mossyComb).setIgnobleShare(0.4f),
				new HiveDrop(3, BeeDefinition.VALIANT, mossyComb)
		);
	}

	private static void registerDungeonLoot() {
		int rarity;
		if (Config.dungeonLootRare) {
			rarity = 5;
		} else {
			rarity = 10;
		}

		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(BeeDefinition.STEADFAST.getMemberStack(EnumBeeType.DRONE), 1, 1, rarity));

		ItemStack stack = ForestryBlock.candle.getItemStack();
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(BlockCandle.colourTagName, 0xffffff);
		stack.setTagCompound(tag);

		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(stack, 7, 12, 12));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.scoop.getItemStack(), 1, 1, 8));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.propolis.getItemStack(), 2, 4, 6));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.beeComb.getItemStack(), 4, 12, 7));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.beeComb.getItemStack(1, 4), 2, 10, 7));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.beeComb.getItemStack(1, 6), 1, 6, 7));

		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(BeeDefinition.FOREST.getRainResist().getMemberStack(EnumBeeType.PRINCESS), 1, 1, 5));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(BeeDefinition.COMMON.getMemberStack(EnumBeeType.DRONE), 1, 2, 8));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(BeeDefinition.MEADOWS.getMemberStack(EnumBeeType.PRINCESS), 1, 1, 5));
	}

	private static void createHives() {
		hiveRegistry.registerHive(HiveRegistry.forest, HiveDescription.FOREST);
		hiveRegistry.registerHive(HiveRegistry.meadows, HiveDescription.MEADOWS);
		hiveRegistry.registerHive(HiveRegistry.desert, HiveDescription.DESERT);
		hiveRegistry.registerHive(HiveRegistry.jungle, HiveDescription.JUNGLE);
		hiveRegistry.registerHive(HiveRegistry.end, HiveDescription.END);
		hiveRegistry.registerHive(HiveRegistry.snow, HiveDescription.SNOW);
		hiveRegistry.registerHive(HiveRegistry.swamp, HiveDescription.SWAMP);
	}

	private static void createAlleles() {

		IClassification hymnoptera = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "hymnoptera", "Hymnoptera");
		AlleleManager.alleleRegistry.getClassification("class.insecta").addMemberGroup(hymnoptera);

		IClassification apidae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "apidae", "Apidae");
		hymnoptera.addMemberGroup(apidae);

		for (BeeBranchDefinition beeBranch : BeeBranchDefinition.values()) {
			apidae.addMemberGroup(beeBranch.getBranch());
		}

		// / BEES // FLOWER PROVIDERS 1500 - 1599
		Allele.flowersVanilla = new AlleleFlowers("flowersVanilla", new FlowerProvider(FlowerManager.FlowerTypeVanilla, "flowers.vanilla"), true);
		Allele.flowersNether = new AlleleFlowers("flowersNether", new FlowerProvider(FlowerManager.FlowerTypeNether, "flowers.nether"));
		Allele.flowersCacti = new AlleleFlowers("flowersCacti", new FlowerProvider(FlowerManager.FlowerTypeCacti, "flowers.cacti"));
		Allele.flowersMushrooms = new AlleleFlowers("flowersMushrooms", new FlowerProvider(FlowerManager.FlowerTypeMushrooms, "flowers.mushroom"));
		Allele.flowersEnd = new AlleleFlowers("flowersEnd", new FlowerProvider(FlowerManager.FlowerTypeEnd, "flowers.end"));
		Allele.flowersJungle = new AlleleFlowers("flowersJungle", new FlowerProvider(FlowerManager.FlowerTypeJungle, "flowers.jungle"));
		Allele.flowersSnow = new AlleleFlowers("flowersSnow", new FlowerProvider(FlowerManager.FlowerTypeSnow, "flowers.vanilla"), true);
		Allele.flowersWheat = new AlleleFlowers("flowersWheat", new FlowerProvider(FlowerManager.FlowerTypeWheat, "flowers.wheat"), true);
		Allele.flowersGourd = new AlleleFlowers("flowersGourd", new FlowerProvider(FlowerManager.FlowerTypeGourd, "flowers.gourd"), true);

		// / BEES // EFFECTS 1800 - 1899
		Allele.effectNone = new AlleleEffectNone("effectNone");
		Allele.effectAggressive = new AlleleEffectAggressive("effectAggressive");
		Allele.effectHeroic = new AlleleEffectHeroic("effectHeroic");
		Allele.effectBeatific = new AlleleEffectPotion("effectBeatific", "beatific", false, Potion.regeneration, 100, true);
		Allele.effectMiasmic = new AlleleEffectMiasmic("effectMiasmic");
		Allele.effectMisanthrope = new AlleleEffectMisanthrope("effectMisanthrope");
		Allele.effectGlacial = new AlleleEffectGlacial("effectGlacial");
		Allele.effectRadioactive = new AlleleEffectRadioactive("effectRadioactive");
		Allele.effectCreeper = new AlleleEffectCreeper("effectCreeper");
		Allele.effectIgnition = new AlleleEffectIgnition("effectIgnition");
		Allele.effectExploration = new AlleleEffectExploration("effectExploration");
		Allele.effectFestiveEaster = new AlleleEffectNone("effectFestiveEaster");
		Allele.effectSnowing = new AlleleEffectSnowing("effectSnowing");
		Allele.effectDrunkard = new AlleleEffectPotion("effectDrunkard", "drunkard", false, Potion.confusion, 100, false);
		Allele.effectReanimation = new AlleleEffectResurrection("effectReanimation", "reanimation", AlleleEffectResurrection.getReanimationList());
		Allele.effectResurrection = new AlleleEffectResurrection("effectResurrection", "resurrection", AlleleEffectResurrection.getResurrectionList());
		Allele.effectRepulsion = new AlleleEffectRepulsion("effectRepulsion");
		Allele.effectFertile = new AlleleEffectFertile("effectFertile");
		Allele.effectMycophilic = new AlleleEffectFungification("effectMycophilic");
	}

	public static int getSecondPrincessChance() {
		return secondPrincessChance;
	}

	private static void parseAdditionalFlowers(String list, ArrayList<ItemStack> target) {
		List<ItemStack> flowers = StackUtils.parseItemStackStrings(list);
		target.addAll(flowers);
	}

	private static void parseBeeBlacklist(String list) {
		String[] items = list.split("[;]+");

		for (String item : items) {
			if (item.isEmpty()) {
				continue;
			}

			FMLCommonHandler.instance().getFMLLogger().debug("Blacklisting bee species identified by " + item);
			AlleleManager.alleleRegistry.blacklistAllele(item);
		}
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerApiculture();
	}

	@Override
	public void populateChunk(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		if (Config.generateBeehives) {
			HiveDecorator.instance().decorateHives(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
		}
	}

	@Override
	public void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
		if (Config.generateBeehives) {
			HiveDecorator.instance().decorateHives(world, rand, chunkX, chunkZ);
		}
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-candle-lighting-id")) {
			ItemStack value = message.getItemStackValue();
			if (value != null) {
				((BlockCandle) ForestryBlock.candle.block()).addItemToLightingList(value.getItem());
			} else {
				Logger.getLogger("Forestry").log(Level.WARNING,
						"Received an invalid 'add-candle-lighting-id' request from mod %s. Please contact the author and report this issue.",
						message.getSender());
			}
			return true;
		} else if (message.key.equals("add-alveary-slab") && message.isStringMessage()) {
			try {
				Block block = GameData.getBlockRegistry().getRaw(message.getStringValue());

				if (block == null || block == Blocks.air) {
					Logger.getLogger("Forestry").log(Level.WARNING,
							"Received an invalid 'add-alveary-slab' request from mod %s. Please contact the author and report this issue.",
							message.getSender());
				} else {
					StructureLogicAlveary.slabBlocks.add(block);
				}
			} catch (Exception e) {
				Logger.getLogger("Forestry").log(Level.WARNING,
						"Received an invalid 'add-alveary-slab' request from mod %s. Please contact the author and report this issue.",
						message.getSender());
			}
		}
		return super.processIMCMessage(message);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 1) {
			EntitySnowFX.icons = new IIcon[3];
			for (int i = 0; i < EntitySnowFX.icons.length; i++) {
				EntitySnowFX.icons[i] = event.map.registerIcon("forestry:particles/snow." + (i + 1));
			}
		}
	}
}
