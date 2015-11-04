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

import net.minecraft.block.material.Material;
import net.minecraft.item.crafting.CraftingManager;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.IGuiHandler;

import forestry.Forestry;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.recipes.RecipeManagers;
import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.fluids.Fluids;
import forestry.core.items.ItemBlockForestry;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.tiles.MachineDefinition;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.Log;
import forestry.lepidopterology.ButterflySpawner;
import forestry.lepidopterology.GuiHandlerLepidopterology;
import forestry.lepidopterology.commands.CommandButterfly;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.genetics.AlleleButterflyEffect;
import forestry.lepidopterology.genetics.ButterflyBranchDefinition;
import forestry.lepidopterology.genetics.ButterflyDefinition;
import forestry.lepidopterology.genetics.ButterflyFactory;
import forestry.lepidopterology.genetics.ButterflyHelper;
import forestry.lepidopterology.genetics.MothDefinition;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.items.ItemFlutterlyzer;
import forestry.lepidopterology.proxy.ProxyLepidopterology;
import forestry.lepidopterology.recipes.MatingRecipe;
import forestry.lepidopterology.tiles.TileLepidopteristChest;

@Plugin(pluginID = "Lepidopterology", name = "Lepidopterology", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.lepidopterology.description")
public class PluginLepidopterology extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.lepidopterology.proxy.ProxyLepidopterologyClient", serverSide = "forestry.lepidopterology.proxy.ProxyLepidopterology")
	public static ProxyLepidopterology proxy;
	private static final String CONFIG_CATEGORY = "lepidopterology";
	public static int spawnConstraint = 100;
	public static int entityConstraint = 1000;
	private static boolean allowPollination = true;

	@Override
	protected void setupAPI() {
		super.setupAPI();

		ButterflyManager.butterflyRoot = new ButterflyHelper();
		AlleleManager.alleleRegistry.registerSpeciesRoot(ButterflyManager.butterflyRoot);

		ButterflyManager.butterflyFactory = new ButterflyFactory();
	}

	@Override
	public void preInit() {
		ForestryBlock.lepidopterology.registerBlock(new BlockBase(Material.iron, true), ItemBlockForestry.class, "lepidopterology");
		ForestryBlock.lepidopterology.block().setCreativeTab(Tabs.tabLepidopterology);

		ButterflyBranchDefinition.createAlleles();
		AlleleButterflyEffect.createAlleles();
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
				Log.info("Migrated " + CONFIG_CATEGORY + " settings to the new file '" + newConfig + "' and renamed '" + oldConfig + "' to '" + oldConfigRenamed + "'.");
			}
		}

		loadNewConfig(configFile);

		PluginCore.rootCommand.addChildCommand(new CommandButterfly());

		EntityUtil.registerEntity(EntityButterfly.class, "butterflyGE", 0, 0x000000, 0xffffff, 50, 1, true);
		proxy.initializeRendering();

		MothDefinition.initMoths();
		ButterflyDefinition.initButterflies();

		BlockBase lepidopterology = ((BlockBase) ForestryBlock.lepidopterology.block());
		MachineDefinition definitionChest = lepidopterology.addDefinition(new MachineDefinition(Constants.DEFINITION_LEPICHEST_META, "forestry.LepiChest", TileLepidopteristChest.class, Proxies.render.getRenderChest("lepichest"),
				ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.lepidopterology.getItemStack(1, Constants.DEFINITION_LEPICHEST_META),
						" # ",
						"XYX",
						"XXX",
						'#', "blockGlass",
						'X', ForestryItem.butterflyGE.getItemStack(1, OreDictionary.WILDCARD_VALUE),
						'Y', "chestWood"))
				.setBoundingBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F));
		definitionChest.register();

		TreeManager.treeRoot.registerLeafTickHandler(new ButterflySpawner());

		RecipeSorter.register("forestry:lepidopterologymating", MatingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shapeless");
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

		forestry.core.config.deprecated.Property property = config.get("entities.spawn.limit", CONFIG_CATEGORY, spawnConstraint);
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

		RecipeManagers.carpenterManager.addRecipe(100, Fluids.WATER.getFluid(2000), null, ForestryItem.flutterlyzer.getItemStack(),
				"X#X", "X#X", "RDR", '#', "paneGlass", 'X', "ingotBronze", 'R',
				"dustRedstone", 'D', "gemDiamond");
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerLepidopterology();
	}

	public static boolean isPollinationAllowed() {
		return allowPollination;
	}
}
