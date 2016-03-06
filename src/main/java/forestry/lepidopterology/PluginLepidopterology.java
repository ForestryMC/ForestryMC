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
package forestry.lepidopterology;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;

import forestry.Forestry;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.recipes.RecipeManagers;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.EntityUtil;
import forestry.lepidopterology.blocks.BlockRegistryLepidopterology;
import forestry.lepidopterology.blocks.BlockTypeLepidopterologyTesr;
import forestry.lepidopterology.commands.CommandButterfly;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.lepidopterology.genetics.AlleleButterflyEffect;
import forestry.lepidopterology.genetics.ButterflyBranchDefinition;
import forestry.lepidopterology.genetics.ButterflyDefinition;
import forestry.lepidopterology.genetics.ButterflyFactory;
import forestry.lepidopterology.genetics.ButterflyRoot;
import forestry.lepidopterology.genetics.MothDefinition;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.lepidopterology.items.ItemRegistryLepidopterology;
import forestry.lepidopterology.proxy.ProxyLepidopterology;
import forestry.lepidopterology.recipes.MatingRecipe;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.LEPIDOPTEROLOGY, name = "Lepidopterology", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.lepidopterology.description")
public class PluginLepidopterology extends BlankForestryPlugin {

	@SidedProxy(clientSide = "forestry.lepidopterology.proxy.ProxyLepidopterologyClient", serverSide = "forestry.lepidopterology.proxy.ProxyLepidopterology")
	public static ProxyLepidopterology proxy;
	private static final String CONFIG_CATEGORY = "lepidopterology";
	public static int spawnConstraint = 100;
	public static int entityConstraint = 1000;
	private static boolean allowPollination = true;

	public static ItemRegistryLepidopterology items;
	public static BlockRegistryLepidopterology blocks;

	@Override
	public void setupAPI() {
		ButterflyManager.butterflyRoot = new ButterflyRoot();
		AlleleManager.alleleRegistry.registerSpeciesRoot(ButterflyManager.butterflyRoot);

		ButterflyManager.butterflyFactory = new ButterflyFactory();
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryLepidopterology();
		blocks = new BlockRegistryLepidopterology();
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
		
		ButterflyBranchDefinition.createAlleles();
		AlleleButterflyEffect.createAlleles();

		blocks.lepidopterology.addDefinitions(BlockTypeLepidopterologyTesr.VALUES);
	}

	@Nonnull
	@Override
	public Set<String> getDependencyUids() {
		Set<String> dependencyUids = super.getDependencyUids();
		dependencyUids.add(ForestryPluginUids.ARBORICULTURE);
		return dependencyUids;
	}

	@Override
	public void doInit() {
		File configFile = new File(Forestry.instance.getConfigFolder(), CONFIG_CATEGORY + ".cfg");
		loadConfig(configFile);

		PluginCore.rootCommand.addChildCommand(new CommandButterfly());

		EntityUtil.registerEntity(EntityButterfly.class, "butterflyGE", 0, 0x000000, 0xffffff, 50, 1, true);
		proxy.initializeRendering();

		MothDefinition.initMoths();
		ButterflyDefinition.initButterflies();

		blocks.lepidopterology.init();

		TreeManager.treeRoot.registerLeafTickHandler(new ButterflySpawner());

		RecipeSorter.register("forestry:lepidopterologymating", MatingRecipe.class, RecipeSorter.Category.SHAPELESS, "before:minecraft:shapeless");
	}

	private static void loadConfig(File configFile) {
		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");

		spawnConstraint = config.getIntLocalized("butterfly.entities", "spawn.limit", spawnConstraint, 0, 500);
		entityConstraint = config.getIntLocalized("butterfly.entities", "maximum", entityConstraint, 0, 5000);
		allowPollination = config.getBooleanLocalized("butterfly.entities", "pollination", allowPollination);

		config.save();
	}

	@Override
	public void registerRecipes() {
		CraftingManager.getInstance().getRecipeList().add(new MatingRecipe());

		RecipeManagers.carpenterManager.addRecipe(100, Fluids.WATER.getFluid(2000), null, items.flutterlyzer.getItemStack(),
				"X#X", "X#X", "RDR", '#', "paneGlass", 'X', "ingotBronze", 'R',
				"dustRedstone", 'D', "gemDiamond");

		RecipeUtil.addRecipe(blocks.lepidopterology.get(BlockTypeLepidopterologyTesr.LEPICHEST),
				" # ",
				"XYX",
				"XXX",
				'#', "blockGlass",
				'X', new ItemStack(items.butterflyGE, 1, OreDictionary.WILDCARD_VALUE),
				'Y', "chestWood");
	}

	public static boolean isPollinationAllowed() {
		return allowPollination;
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerSprites(TextureStitchEvent.Pre event) {
		ItemButterflyGE.registerSprites();
	}
}
