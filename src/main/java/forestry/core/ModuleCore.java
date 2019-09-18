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
package forestry.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

import com.mojang.brigadier.Command;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.api.circuits.ChipsetManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.modules.ForestryModule;
import forestry.api.multiblock.MultiblockManager;
import forestry.api.recipes.IHygroregulatorManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.blocks.EnumResourceType;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.GuiSolderingIron;
import forestry.core.circuits.SolderManager;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.fluids.ForestryFluids;
import forestry.core.genetics.alleles.AlleleFactory;
import forestry.core.gui.CoreContainerTypes;
import forestry.core.gui.GuiAlyzer;
import forestry.core.gui.GuiAnalyzer;
import forestry.core.gui.GuiEscritoire;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.items.EnumCraftingMaterial;
import forestry.core.loot.SetSpeciesNBT;
import forestry.core.models.ModelManager;
import forestry.core.multiblock.MultiblockLogicFactory;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketRegistryCore;
import forestry.core.owner.GameProfileDataSerializer;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.HygroregulatorManager;
import forestry.core.recipes.RecipeUtil;
import forestry.core.tiles.TileRegistryCore;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.ForestryModEnvWarningCallable;
import forestry.core.utils.OreDictUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

//import forestry.core.commands.CommandListAlleles;
//import forestry.core.commands.CommandModules;
//import forestry.core.commands.RootCommand;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CORE, name = "Core", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.core.description", coreModule = true)
public class ModuleCore extends BlankForestryModule {
	//	public static final RootCommand rootCommand = new RootCommand();
	@Nullable
	private static TileRegistryCore tiles;
	@Nullable    //TODO - there are lots of these. Make helper class/map or something?
	private static CoreContainerTypes containerTypes;

	public ModuleCore() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	public static TileRegistryCore getTiles() {
		Preconditions.checkNotNull(tiles);
		return tiles;
	}

	public static CoreContainerTypes getContainerTypes() {
		Preconditions.checkNotNull(containerTypes);
		return containerTypes;
	}

	@Override
	public boolean canBeDisabled() {
		return false;
	}

	@Override
	public Set<ResourceLocation> getDependencyUids() {
		return Collections.emptySet();
	}

	@Override
	public void setupAPI() {
		ChipsetManager.solderManager = new SolderManager();

		ChipsetManager.circuitRegistry = new CircuitRegistry();

		AlleleManager.climateHelper = new ClimateUtil();
		AlleleManager.alleleFactory = new AlleleFactory();

		LootFunctionManager.registerFunction(new SetSpeciesNBT.Serializer());

		MultiblockManager.logicFactory = new MultiblockLogicFactory();

		RecipeManagers.hygroregulatorManager = new HygroregulatorManager();
	}

	@Override
	public void registerFeatures() {
		CoreBlocks.RESOURCE_ORE.getClass();
		CoreItems.PEAT.getClass();
	}

	@Override
	public void registerTiles() {
		tiles = new TileRegistryCore();
	}

	@Override
	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		containerTypes = new CoreContainerTypes(registry);
	}

	@Override
	public void registerGuiFactories() {
		CoreContainerTypes containerTypes = getContainerTypes();
		ScreenManager.registerFactory(containerTypes.ALYZER, GuiAlyzer::new);
		ScreenManager.registerFactory(containerTypes.ANALYZER, GuiAnalyzer::new);
		ScreenManager.registerFactory(containerTypes.NATURALIST_INVENTORY, GuiNaturalistInventory::new);
		ScreenManager.registerFactory(containerTypes.ESCRITOIRE, GuiEscritoire::new);
		ScreenManager.registerFactory(containerTypes.SOLDERING_IRON, GuiSolderingIron::new);
	}


	@Override
	public void preInit() {
		GameProfileDataSerializer.register();

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ClimateHandlerServer());

		//		rootCommand.addChildCommand(new CommandModules());
		//		rootCommand.addChildCommand(new CommandListAlleles());
	}

	@Override
	public void doInit() {
		ForestryModEnvWarningCallable.register();

		Proxies.render.initRendering();
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerCore();
	}

	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		// forestry items
		crateRegistry.registerCrate(CoreItems.PEAT);
		crateRegistry.registerCrate(CoreItems.APATITE);
		crateRegistry.registerCrate(CoreItems.FERTILIZER_COMPOUND);
		crateRegistry.registerCrate(CoreItems.MULCH);
		crateRegistry.registerCrate(CoreItems.PHOSPHOR);
		crateRegistry.registerCrate(CoreItems.ASH);
		crateRegistry.registerCrate(OreDictUtil.INGOT_TIN);
		crateRegistry.registerCrate(OreDictUtil.INGOT_COPPER);
		crateRegistry.registerCrate(OreDictUtil.INGOT_BRONZE);

		// forestry blocks
		crateRegistry.registerCrate(CoreBlocks.HUMUS);
		crateRegistry.registerCrate(CoreBlocks.BOG_EARTH);

		// vanilla items
		crateRegistry.registerCrate(OreDictUtil.CROP_WHEAT);
		crateRegistry.registerCrate(Items.COOKIE);
		crateRegistry.registerCrate(OreDictUtil.DUST_REDSTONE);
		crateRegistry.registerCrate(new ItemStack(Items.LAPIS_LAZULI, 1));    //TODO - I think...
		crateRegistry.registerCrate("sugarcane");
		crateRegistry.registerCrate(Items.CLAY_BALL);
		crateRegistry.registerCrate("dustGlowstone");
		crateRegistry.registerCrate(Items.APPLE);
		crateRegistry.registerCrate(new ItemStack(Items.NETHER_WART));
		crateRegistry.registerCrate(new ItemStack(Items.COAL, 1));
		crateRegistry.registerCrate(new ItemStack(Items.CHARCOAL, 1));
		crateRegistry.registerCrate(Items.WHEAT_SEEDS);
		crateRegistry.registerCrate("cropPotato");
		crateRegistry.registerCrate("cropCarrot");

		// vanilla blocks
		crateRegistry.registerCrate(new ItemStack(Blocks.OAK_LOG, 1));    //TODO - use tags?
		crateRegistry.registerCrate(new ItemStack(Blocks.BIRCH_LOG, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.JUNGLE_LOG, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.SPRUCE_LOG, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.ACACIA_LOG, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.DARK_OAK_LOG, 1));
		crateRegistry.registerCrate("cobblestone");
		crateRegistry.registerCrate("dirt");
		crateRegistry.registerCrate(new ItemStack(Blocks.GRASS_BLOCK, 1));
		crateRegistry.registerCrate("stone");
		crateRegistry.registerCrate("stoneGranite");
		crateRegistry.registerCrate("stoneDiorite");
		crateRegistry.registerCrate("stoneAndesite");
		crateRegistry.registerCrate("blockPrismarine");
		crateRegistry.registerCrate("blockPrismarineBrick");
		crateRegistry.registerCrate("blockPrismarineDark");
		crateRegistry.registerCrate(Blocks.BRICKS);
		crateRegistry.registerCrate("blockCactus");
		crateRegistry.registerCrate(new ItemStack(Blocks.SAND, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.RED_SAND, 1));
		crateRegistry.registerCrate("obsidian");
		crateRegistry.registerCrate("netherrack");
		crateRegistry.registerCrate(Blocks.SOUL_SAND);
		crateRegistry.registerCrate(Blocks.SANDSTONE);
		crateRegistry.registerCrate(Blocks.NETHER_BRICKS);
		crateRegistry.registerCrate(Blocks.MYCELIUM);
		crateRegistry.registerCrate("gravel");
		crateRegistry.registerCrate(new ItemStack(Blocks.OAK_SAPLING, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.BIRCH_SAPLING, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.JUNGLE_SAPLING, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.SPRUCE_SAPLING, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.ACACIA_SAPLING, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.DARK_OAK_SAPLING, 1));
	}

	@Override
	public void registerRecipes() {
		/* SMELTING RECIPES */
		RecipeUtil.addSmelting(CoreBlocks.RESOURCE_ORE.stack(EnumResourceType.APATITE, 1), CoreItems.APATITE.stack(), 0.5f);
		RecipeUtil.addSmelting(CoreBlocks.RESOURCE_ORE.stack(EnumResourceType.COPPER, 1), CoreItems.INGOT_COPPER.stack(), 0.5f);
		RecipeUtil.addSmelting(CoreBlocks.RESOURCE_ORE.stack(EnumResourceType.TIN, 1), CoreItems.INGOT_TIN.stack(), 0.5f);
		RecipeUtil.addSmelting(CoreItems.PEAT.stack(), CoreItems.ASH.stack(), 0.0f);
		if (ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
			// / CARPENTER
			// Portable ANALYZER
			RecipeManagers.carpenterManager.addRecipe(100, new FluidStack(Fluids.WATER, 2000), ItemStack.EMPTY, CoreItems.PORTABLE_ALYZER.stack(),
				"X#X", "X#X", "RDR",
				'#', OreDictUtil.PANE_GLASS,
				'X', OreDictUtil.INGOT_TIN,
				'R', OreDictUtil.DUST_REDSTONE,
				'D', OreDictUtil.GEM_DIAMOND);
			// Camouflaged Paneling
			FluidStack biomass = ForestryFluids.BIOMASS.getFluid(150);
			if (!biomass.isEmpty()) {
				RecipeManagers.squeezerManager.addRecipe(8, CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.CAMOUFLAGED_PANELING, 1), biomass);
			}
		}
		// alternate recipes
		if (!ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			RecipeManagers.centrifugeManager.addRecipe(5, new ItemStack(Items.STRING), ImmutableMap.of(
				CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.SILK_WISP, 1), 0.15f
			));
		}

		IHygroregulatorManager hygroManager = RecipeManagers.hygroregulatorManager;
		if (hygroManager != null) {
			hygroManager.addRecipe(new FluidStack(Fluids.WATER, 1), 1, -0.005f, 0.01f);
			hygroManager.addRecipe(new FluidStack(Fluids.LAVA, 1), 10, 0.005f, -0.01f);
			FluidStack ice = ForestryFluids.ICE.getFluid(1);
			if (!ice.isEmpty()) {
				hygroManager.addRecipe(ice, 10, -0.01f, 0.02f);
			}
		}
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryCore();
	}

	@Override
	public boolean processIMCMessage(InterModComms.IMCMessage message) {
		if (message.getMethod().equals("blacklist-ores-dimension")) {
			ResourceLocation[] dims = (ResourceLocation[]) message.getMessageSupplier().get();    //TODO - how does IMC work
			for (ResourceLocation dim : dims) {
				Config.blacklistOreDim(dim);
			}
			return true;
		}
		return false;
	}

	@Override
	public IPickupHandler getPickupHandler() {
		return new PickupHandlerCore();
	}

	@Override
	public Command[] getConsoleCommands() {
		return new Command[0];//{rootCommand};
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// research note items are not useful without actually having completed research
		hiddenItems.add(CoreItems.RESEARCH_NOTE.stack());
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onBakeModels(ModelBakeEvent event) {
		ModelManager.getInstance().onBakeModels(event);
	}
}
