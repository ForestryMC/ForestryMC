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

import java.io.File;
import java.util.EnumSet;
import java.util.Locale;

import net.minecraft.block.material.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.IRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.RecipeSorter;

import forestry.Forestry;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.api.recipes.RecipeManagers;
import forestry.arboriculture.render.ModelFruitPod;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.fluids.Fluids;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.genetics.Branch;
import forestry.core.genetics.alleles.Allele;
import forestry.core.items.ItemForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.Utils;
import forestry.lepidopterology.ButterflySpawner;
import forestry.lepidopterology.GuiHandlerLepidopterology;
import forestry.lepidopterology.MatingRecipe;
import forestry.lepidopterology.commands.CommandButterfly;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.gadgets.TileLepidopteristChest;
import forestry.lepidopterology.genetics.AlleleButterflySpecies;
import forestry.lepidopterology.genetics.AlleleEffectNone;
import forestry.lepidopterology.genetics.ButterflyHelper;
import forestry.lepidopterology.genetics.ButterflyTemplates;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.items.ItemFlutterlyzer;
import forestry.lepidopterology.proxy.ProxyLepidopterology;
import forestry.lepidopterology.render.ItemModelButterfly;

@Plugin(pluginID = "Lepidopterology", name = "Lepidopterology", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.lepidopterology.description")
public class PluginLepidopterology extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.lepidopterology.proxy.ClientProxyLepidopterology", serverSide = "forestry.lepidopterology.proxy.ProxyLepidopterology")
	public static ProxyLepidopterology proxy;
	private static final String CONFIG_CATEGORY = "lepidopterology";
	public static int spawnConstraint = 100;
	public static int entityConstraint = 1000;
	private static boolean allowPollination = true;
	/**
	 * See {@link IButterflyRoot} for details.
	 */
	public static IButterflyRoot butterflyInterface;

	@Override
	protected void setupAPI() {
		super.setupAPI();

		AlleleManager.alleleRegistry
				.registerSpeciesRoot(PluginLepidopterology.butterflyInterface = new ButterflyHelper());
	}

	@Override
	public void preInit() {

		MinecraftForge.EVENT_BUS.register(this);

		ForestryBlock.lepidopterology.registerBlock(
				new BlockBase(Material.iron, Defaults.DEFINITION_LEPIDOPTEROLOGY_ID), ItemForestryBlock.class,
				"lepidopterology");
		ForestryBlock.lepidopterology.block().setCreativeTab(Tabs.tabLepidopterology);
		((BlockBase) ForestryBlock.lepidopterology.block()).registerStateMapper();

		createAlleles();
	}

	@Override
	public EnumSet<PluginManager.Module> getDependancies() {
		EnumSet<PluginManager.Module> deps = super.getDependancies();
		deps.add(PluginManager.Module.ARBORICULTURE);
		return deps;
	}

	@Override
	public void doInit() {
		final String oldConfig = CONFIG_CATEGORY + ".conf";
		final String newConfig = CONFIG_CATEGORY + ".cfg";

		File configFile = new File(Forestry.instance.getConfigFolder(), newConfig);
		File oldConfigFile = new File(Forestry.instance.getConfigFolder(), oldConfig);
		if (oldConfigFile.exists()) {
			loadOldConfig();

			final String oldConfigRenamed = CONFIG_CATEGORY + ".conf.old";
			File oldConfigFileRenamed = new File(Forestry.instance.getConfigFolder(), oldConfigRenamed);
			if (oldConfigFile.renameTo(oldConfigFileRenamed)) {
				Proxies.log.info("Migrated " + CONFIG_CATEGORY + " settings to the new file '" + newConfig
						+ "' and renamed '" + oldConfig + "' to '" + oldConfigRenamed + "'.");
			}
		}

		loadNewConfig(configFile);

		PluginCore.rootCommand.addChildCommand(new CommandButterfly());

		Utils.registerEntity(EntityButterfly.class, "butterflyGE", 0, 0x000000, 0xffffff, 50, 1, true);
		proxy.initializeRendering();

		registerTemplates();

		BlockBase lepidopterology = ((BlockBase) ForestryBlock.lepidopterology.block());
		MachineDefinition definitionChest = lepidopterology
				.addDefinition(
						(new MachineDefinition(Defaults.DEFINITION_LEPICHEST_META, "forestry.LepiChest",
								TileLepidopteristChest.class,
								ShapedRecipeCustom.createShapedRecipe(
										ForestryBlock.lepidopterology.getItemStack(1,
												Defaults.DEFINITION_LEPICHEST_META),
										" # ", "XYX", "XXX", '#', "blockGlass", 'X',
										ForestryItem.butterflyGE.getItemStack(1, Defaults.WILDCARD), 'Y', "chestWood"))
												.setFaces(0, 1, 2, 3, 4, 4, 0, 7)));
		definitionChest.register();

		((ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees"))
				.registerLeafTickHandler(new ButterflySpawner());

		RecipeSorter.register("forestry:lepidopterologymating", MatingRecipe.class, RecipeSorter.Category.SHAPELESS,
				"before:minecraft:shapeless");
	}

	private static void loadNewConfig(File configFile) {
		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");

		spawnConstraint = config.getIntLocalized("butterfly.entities", "spawn.limit", spawnConstraint, 0, 500);
		entityConstraint = config.getIntLocalized("butterfly.entities", "maximum", entityConstraint, 0, 5000);
		allowPollination = config.getBooleanLocalized("butterfly.entities", "pollination", allowPollination);

		config.save();
	}

	private static void loadOldConfig() {
		forestry.core.config.deprecated.Configuration config = new forestry.core.config.deprecated.Configuration();

		forestry.core.config.deprecated.Property property = config.get("entities.spawn.limit", CONFIG_CATEGORY,
				spawnConstraint);
		property.comment = "determines the global butterfly entity count above which natural spawning of butterflies ceases.";
		spawnConstraint = Integer.parseInt(property.value);

		property = config.get("entities.maximum.allowed", CONFIG_CATEGORY, entityConstraint);
		property.comment = "determines the global butterfly entity count above which butterflies will stay in item form and will not take flight anymore.";
		entityConstraint = Integer.parseInt(property.value);

		property = config.get("entities.pollination.allowed", CONFIG_CATEGORY, allowPollination);
		property.comment = "determines whether butterflies can pollinate leaves.";
		allowPollination = Boolean.parseBoolean(property.value);
	}

	@Override
	protected void registerItems() {
		ForestryItem.flutterlyzer.registerItem(new ItemFlutterlyzer(), "flutterlyzer");
		ForestryItem.butterflyGE.registerItem(new ItemButterflyGE(EnumFlutterType.BUTTERFLY), "butterflyGE");
		ForestryItem.serumGE.registerItem(new ItemButterflyGE(EnumFlutterType.SERUM), "serumGE");
		ForestryItem.caterpillarGE.registerItem(new ItemButterflyGE(EnumFlutterType.CATERPILLAR), "caterpillarGE");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void registerRecipes() {
		CraftingManager.getInstance().getRecipeList().add(new MatingRecipe());

		RecipeManagers.carpenterManager.addRecipe(100, Fluids.WATER.getFluid(2000), null,
				ForestryItem.flutterlyzer.getItemStack(), "X#X", "X#X", "RDR", '#', "paneGlass", 'X', "ingotBronze",
				'R', "dustRedstone", 'D', "gemDiamond");
	}

	private static void createAlleles() {

		IClassification lepidoptera = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER,
				"lepidoptera", "Lepidoptera");
		AlleleManager.alleleRegistry.getClassification("class.insecta").addMemberGroup(lepidoptera);

		IClassification geometridae = AlleleManager.alleleRegistry
				.createAndRegisterClassification(EnumClassLevel.FAMILY, "geometridae", "Geometridae");
		lepidoptera.addMemberGroup(geometridae);
		IClassification saturniidae = AlleleManager.alleleRegistry
				.createAndRegisterClassification(EnumClassLevel.FAMILY, "saturniidae", "Saturniidae");
		lepidoptera.addMemberGroup(saturniidae);

		IClassification pieridae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY,
				"pieridae", "Pieridae");
		lepidoptera.addMemberGroup(pieridae);
		IClassification nymphalidae = AlleleManager.alleleRegistry
				.createAndRegisterClassification(EnumClassLevel.FAMILY, "nymphalidae", "Nymphalidae");
		lepidoptera.addMemberGroup(nymphalidae);
		IClassification lycaenidae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY,
				"lycaenidae", "Lycaenidae");
		lepidoptera.addMemberGroup(lycaenidae);
		IClassification papilionidae = AlleleManager.alleleRegistry
				.createAndRegisterClassification(EnumClassLevel.FAMILY, "papilionidae", "Papilionidae");
		lepidoptera.addMemberGroup(papilionidae);
		IClassification notchidae = AlleleManager.alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY,
				"notchidae", "Notchidae");
		lepidoptera.addMemberGroup(notchidae);

		/* GEOMETRIDAE */
		IClassification opisthograptis = createButterflyBranch(geometridae, "Opisthograptis");
		IClassification chiasmia = createButterflyBranch(geometridae, "Chiasmia");

		Allele.mothBrimstone = new AlleleButterflySpecies("mothBrimstone", true, "brimstone", opisthograptis,
				"luteolata", 0xffea40).setNocturnal().setRarity(1.0f);
		Allele.mothLatticedHeath = new AlleleButterflySpecies("mothLatticedHeath", true, "latticedHeath", chiasmia,
				"clathrata", 0xf2f0be).setNocturnal().setRarity(0.5f);

		/* SATURNIIDAE */
		IClassification attacus = createButterflyBranch(saturniidae, "Attacus");

		Allele.mothAtlas = new AlleleButterflySpecies("mothAtlas", false, "atlas", attacus, "atlas", 0xd96e3d)
				.setNocturnal();

		/* PIERIDAE */
		IClassification pieris = createButterflyBranch(pieridae, "Pieris");
		IClassification gonepteryx = createButterflyBranch(pieridae, "Gonepteryx");
		IClassification anthocharis = createButterflyBranch(pieridae, "Anthocharis");
		IClassification colias = createButterflyBranch(pieridae, "Colias");
		IClassification pontia = createButterflyBranch(pieridae, "Pontia");
		IClassification celastrina = createButterflyBranch(pieridae, "Celastrina");

		Allele.lepiCabbageWhite = new AlleleButterflySpecies("lepiCabbageWhite", true, "cabbageWhite", pieris, "rapae",
				0xccffee).setRarity(1.0f);
		Allele.lepiBrimstone = new AlleleButterflySpecies("lepiBrimstone", true, "brimstone", gonepteryx, "rhamni",
				0xf0ee38).setRarity(1.0f);
		Allele.lepiAurora = new AlleleButterflySpecies("lepiAurora", true, "orangeTip", anthocharis, "cardamines",
				0xe34f05).setRarity(0.5f);
		Allele.lepiPostillion = new AlleleButterflySpecies("lepiPostillion", true, "postillion", colias, "croceus",
				0xd77e04).setRarity(0.5f);
		Allele.lepiPalaenoSulphur = new AlleleButterflySpecies("lepiPalaenoSulphur", true, "palaenoSulphur", colias,
				"palaeno", 0xf8fba3).setRarity(0.4f);
		Allele.lepiReseda = new AlleleButterflySpecies("lepiReseda", true, "reseda", pontia, "edusa", 0x747d48)
				.setRarity(0.3f);

		Allele.lepiSpringAzure = new AlleleButterflySpecies("lepiSpringAzure", true, "springAzure", celastrina,
				"argiolus", 0xb8cae2).setRarity(0.3f);
		Allele.lepiGozoraAzure = new AlleleButterflySpecies("lepiGozoraAzure", true, "gozoraAzure", celastrina,
				"gozora", 0x6870e7).setRarity(0.2f);

		/* PAPILIONIDAE */
		IClassification papilio = createButterflyBranch(papilionidae, "Papilio");
		IClassification protographium = createButterflyBranch(papilionidae, "Protographium");

		Allele.lepiCitrusSwallow = new AlleleButterflySpecies("lepiCitrusSwallow", false, "swallowtailC", papilio,
				"demodocus", 0xeae389).setRarity(1.0f).setTemperatureDeprecated(EnumTemperature.WARM)
						.setHumidityDeprecated(EnumHumidity.DAMP);
		Allele.lepiEmeraldPeacock = new AlleleButterflySpecies("lepiEmeraldPeacock", true, "emeraldPeacock", papilio,
				"palinurus", 0x7cfe80).setTemperatureDeprecated(EnumTemperature.WARM)
						.setHumidityDeprecated(EnumHumidity.DAMP);
		Allele.lepiThoasSwallow = new AlleleButterflySpecies("lepiThoasSwallow", false, "swallowtailT", papilio,
				"thoas", 0xeac783).setRarity(0.2f).setTemperatureDeprecated(EnumTemperature.WARM)
						.setHumidityDeprecated(EnumHumidity.DAMP);
		Allele.lepiSpicebush = new AlleleButterflySpecies("lepiSpicebush", true, "swallowtailS", papilio, "troilus",
				0xeefeff).setRarity(0.5f);
		Allele.lepiBlackSwallow = new AlleleButterflySpecies("lepiBlackSwallow", true, "swallowtailB", papilio,
				"polyxenes", 0xeac783).setRarity(1.0f);

		Allele.lepiZebraSwallow = new AlleleButterflySpecies("lepiZebraSwallow", true, "swallowtailZ", protographium,
				"marcellus", 0xeafeff).setRarity(0.5f);

		/* NYMPHALIDAE */
		IClassification pararge = createButterflyBranch(nymphalidae, "Pararge");
		IClassification polygonia = createButterflyBranch(nymphalidae, "Polygonia");
		IClassification morpho = createButterflyBranch(nymphalidae, "Morpho");
		IClassification greta = createButterflyBranch(nymphalidae, "Greta");
		IClassification batesia = createButterflyBranch(nymphalidae, "Batesia");
		IClassification myscelia = createButterflyBranch(nymphalidae, "Myscelia");
		IClassification danaus = createButterflyBranch(nymphalidae, "Danaus");
		IClassification bassarona = createButterflyBranch(nymphalidae, "Bassarona");
		IClassification parantica = createButterflyBranch(nymphalidae, "Parantica");
		IClassification heliconius = createButterflyBranch(nymphalidae, "Heliconius");
		IClassification siproeta = createButterflyBranch(nymphalidae, "Siproeta");
		IClassification cethosia = createButterflyBranch(nymphalidae, "Cethosia");
		IClassification speyeria = createButterflyBranch(nymphalidae, "Speyeria");

		Allele.lepiGlasswing = new AlleleButterflySpecies("lepiGlasswing", true, "glasswing", greta, "oto", 0x583732)
				.setTemperatureDeprecated(EnumTemperature.WARM);

		Allele.lepiSpeckledWood = new AlleleButterflySpecies("lepiSpeckledWood", true, "speckledWood", pararge,
				"aegeria", 0x947245).setRarity(1.0f);
		Allele.lepiMadeiranSpeckledWood = new AlleleButterflySpecies("lepiMSpeckledWood", true, "speckledWoodM",
				pararge, "xiphia", 0x402919).setRarity(0.5f);
		Allele.lepiCanarySpeckledWood = new AlleleButterflySpecies("lepiCSpeckledWood", true, "speckledWoodC", pararge,
				"xiphioides", 0x51372a).setRarity(0.5f);

		Allele.lepiMenelausBlueMorpho = new AlleleButterflySpecies("lepiMBlueMorpho", true, "blueMorphoM", morpho,
				"menelaus", 0x72e1fd).setRarity(0.5f).setTemperatureDeprecated(EnumTemperature.WARM)
						.setHumidityDeprecated(EnumHumidity.DAMP);
		Allele.lepiPeleidesBlueMorpho = new AlleleButterflySpecies("lepiPBlueMorpho", true, "blueMorphoP", morpho,
				"peleides", 0x6ecce8).setRarity(0.25f).setTemperatureDeprecated(EnumTemperature.WARM)
						.setHumidityDeprecated(EnumHumidity.DAMP);
		Allele.lepiRhetenorBlueMorpho = new AlleleButterflySpecies("lepiRBlueMorpho", true, "blueMorphoR", morpho,
				"rhetenor", 0x00bef8).setTemperatureDeprecated(EnumTemperature.WARM)
						.setHumidityDeprecated(EnumHumidity.DAMP);

		Allele.lepiComma = new AlleleButterflySpecies("lepiComma", true, "comma", polygonia, "c-album", 0xf89505)
				.setRarity(0.3f);
		Allele.lepiBatesia = new AlleleButterflySpecies("lepiBatesia", true, "paintedBeauty", batesia, "hypochlora",
				0xfe7763).setRarity(0.3f).setTemperatureDeprecated(EnumTemperature.WARM)
						.setHumidityDeprecated(EnumHumidity.DAMP);
		Allele.lepiBlueWing = new AlleleButterflySpecies("lepiBlueWing", true, "blueWing", myscelia, "ethusa", 0x3a93cc)
				.setRarity(0.3f);

		Allele.lepiMonarch = new AlleleButterflySpecies("lepiMonarch", true, "monarch", danaus, "plexippus", 0xffa722)
				.setRarity(0.2f);
		Allele.lepiBlueDuke = new AlleleButterflySpecies("lepiBlueDuke", true, "blueDuke", bassarona, "durga", 0x304240)
				.setRarity(0.5f).setTemperatureDeprecated(EnumTemperature.COLD);
		Allele.lepiGlassyTiger = new AlleleButterflySpecies("lepiGlassyTiger", true, "glassyTiger", parantica, "aglea",
				0x5b3935).setRarity(0.3f);
		Allele.lepiPostman = new AlleleButterflySpecies("lepiPostman", true, "postman", heliconius, "melpomene",
				0xf7302d).setRarity(0.3f);
		Allele.lepiMalachite = new AlleleButterflySpecies("lepiMalachite", true, "malachite", siproeta, "stelenes",
				0xbdff53).setRarity(0.5f).setTemperatureDeprecated(EnumTemperature.WARM)
						.setHumidityDeprecated(EnumHumidity.DAMP);
		Allele.lepiLLacewing = new AlleleButterflySpecies("lepiLLacewing", true, "leopardLacewing", cethosia, "cyane",
				0xfb8a06).setRarity(0.7f);

		Allele.lepiDianaFrit = new AlleleButterflySpecies("lepiDianaFrit", true, "dianaFritillary", speyeria, "diana",
				0xffac05).setRarity(0.6f);

		Allele.butterflyNone = new AlleleEffectNone();
	}

	private static IClassification createButterflyBranch(IClassification family, String scientific) {
		IClassification branch = new Branch("moth." + scientific.toLowerCase(Locale.ENGLISH), scientific);
		branch.setParent(family);
		return branch;
	}

	private static void registerTemplates() {
		butterflyInterface.registerTemplate(ButterflyTemplates.getBrimstoneMothTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getLatticedHeathTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getAtlasMothTemplate());

		butterflyInterface.registerTemplate(ButterflyTemplates.getCabbageWhiteTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getGlasswingTemplate());

		butterflyInterface.registerTemplate(ButterflyTemplates.getEmeraldPeacockTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getThoasSwallowTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getCitrusSwallowTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getBlackSwallowTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getZebraSwallowTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getDianaFritTemplate());

		butterflyInterface.registerTemplate(ButterflyTemplates.getSpeckledWoodTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getMadeiranSpeckledWoodTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getCanarySpeckledWoodTemplate());

		butterflyInterface.registerTemplate(ButterflyTemplates.getMenelausBlueMorphoTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getRhetenorBlueMorphoTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getPeleidesBlueMorphoTemplate());

		butterflyInterface.registerTemplate(ButterflyTemplates.getBrimstoneTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getAuroraTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getPostillionTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getPalaenoSulphurTemplate());

		butterflyInterface.registerTemplate(ButterflyTemplates.getResedaTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getSpringAzureTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getGozoraAzureTemplate());

		butterflyInterface.registerTemplate(ButterflyTemplates.getCommaTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getBatesiaTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getBlueWingTemplate());

		butterflyInterface.registerTemplate(ButterflyTemplates.getBlueDukeTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getGlassyTigerTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getMonarchTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getPostmanTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getSpicebushTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getMalachiteTemplate());
		butterflyInterface.registerTemplate(ButterflyTemplates.getLeopardLacewingTemplate());

	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerLepidopterology();
	}

	public static boolean isPollinationAllowed() {
		return allowPollination;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onBakeModel(ModelBakeEvent event) {
		registerButterflyModels(event.modelRegistry);
	}

	@SideOnly(Side.CLIENT)
	public void registerButterflyModels(IRegistry modelRegistry) {
		modelRegistry.putObject(new ModelResourceLocation("forestry:butterflyGE", "inventory"),
				new ItemModelButterfly(new ResourceLocation("forestry:item/butterflyGE.b3d")));
	}
}
