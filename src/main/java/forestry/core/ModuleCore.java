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

import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.circuits.ChipsetManager;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.modules.ForestryModule;
import forestry.api.multiblock.MultiblockManager;
import forestry.api.recipes.IHygroregulatorManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.blocks.BlockRegistryCore;
import forestry.core.blocks.EnumResourceType;
import forestry.core.circuits.CircuitRegistry;
import forestry.core.circuits.SolderManager;
import forestry.core.commands.CommandModules;
import forestry.core.commands.RootCommand;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.genetics.alleles.AlleleFactory;
import forestry.core.genetics.alleles.AlleleRegistry;
import forestry.core.items.ItemRegistryCore;
import forestry.core.loot.SetSpeciesNBT;
import forestry.core.models.ModelManager;
import forestry.core.multiblock.MultiblockLogicFactory;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketRegistryCore;
import forestry.core.owner.GameProfileDataSerializer;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.HygroregulatorManager;
import forestry.core.recipes.RecipeUtil;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.ForestryModEnvWarningCallable;
import forestry.core.utils.OreDictUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CORE, name = "Core", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.core.description", coreModule = true)
public class ModuleCore extends BlankForestryModule {
	public static final RootCommand rootCommand = new RootCommand();
	@Nullable
	public static ItemRegistryCore items;
	@Nullable
	private static BlockRegistryCore blocks;

	public static ItemRegistryCore getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	public static BlockRegistryCore getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
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

		AlleleRegistry alleleRegistry = new AlleleRegistry();
		AlleleManager.alleleRegistry = alleleRegistry;
		AlleleManager.climateHelper = new ClimateUtil();
		AlleleManager.alleleFactory = new AlleleFactory();
		alleleRegistry.initialize();

		LootFunctionManager.registerFunction(new SetSpeciesNBT.Serializer());

		MultiblockManager.logicFactory = new MultiblockLogicFactory();

		RecipeManagers.hygroregulatorManager = new HygroregulatorManager();
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryCore();
		blocks = new BlockRegistryCore();
	}

	@Override
	public void preInit() {

		GameProfileDataSerializer.register();

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ClimateHandlerServer());

		rootCommand.addChildCommand(new CommandModules());
	}

	@Override
	public void doInit() {

		BlockRegistryCore blocks = getBlocks();

		blocks.analyzer.init();
		blocks.escritoire.init();

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
		ItemRegistryCore items = getItems();
		crateRegistry.registerCrate(items.peat);
		crateRegistry.registerCrate(items.apatite);
		crateRegistry.registerCrate(items.fertilizerCompound);
		crateRegistry.registerCrate(items.mulch);
		crateRegistry.registerCrate(items.phosphor);
		crateRegistry.registerCrate(items.ash);
		crateRegistry.registerCrate(OreDictUtil.INGOT_TIN);
		crateRegistry.registerCrate(OreDictUtil.INGOT_COPPER);
		crateRegistry.registerCrate(OreDictUtil.INGOT_BRONZE);

		// forestry blocks
		BlockRegistryCore blocks = getBlocks();
		crateRegistry.registerCrate(blocks.humus);
		crateRegistry.registerCrate(blocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, 1));

		// vanilla items
		crateRegistry.registerCrate(OreDictUtil.CROP_WHEAT);
		crateRegistry.registerCrate(Items.COOKIE);
		crateRegistry.registerCrate(OreDictUtil.DUST_REDSTONE);
		crateRegistry.registerCrate(new ItemStack(Items.DYE, 1, 4));
		crateRegistry.registerCrate("sugarcane");
		crateRegistry.registerCrate(Items.CLAY_BALL);
		crateRegistry.registerCrate("dustGlowstone");
		crateRegistry.registerCrate(Items.APPLE);
		crateRegistry.registerCrate(new ItemStack(Items.NETHER_WART));
		crateRegistry.registerCrate(new ItemStack(Items.COAL, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Items.COAL, 1, 0));
		crateRegistry.registerCrate(Items.WHEAT_SEEDS);
		crateRegistry.registerCrate("cropPotato");
		crateRegistry.registerCrate("cropCarrot");

		// vanilla blocks
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 2));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG, 1, 3));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG2, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.LOG2, 1, 1));
		crateRegistry.registerCrate("cobblestone");
		crateRegistry.registerCrate("dirt");
		crateRegistry.registerCrate(new ItemStack(Blocks.DIRT, 1, 2));
		crateRegistry.registerCrate("stone");
		crateRegistry.registerCrate("stoneGranite");
		crateRegistry.registerCrate("stoneDiorite");
		crateRegistry.registerCrate("stoneAndesite");
		crateRegistry.registerCrate("blockPrismarine");
		crateRegistry.registerCrate("blockPrismarineBrick");
		crateRegistry.registerCrate("blockPrismarineDark");
		crateRegistry.registerCrate(Blocks.BRICK_BLOCK);
		crateRegistry.registerCrate("blockCactus");
		crateRegistry.registerCrate(new ItemStack(Blocks.SAND, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAND, 1, 1));
		crateRegistry.registerCrate("obsidian");
		crateRegistry.registerCrate("netherrack");
		crateRegistry.registerCrate(Blocks.SOUL_SAND);
		crateRegistry.registerCrate(Blocks.SANDSTONE);
		crateRegistry.registerCrate(Blocks.NETHER_BRICK);
		crateRegistry.registerCrate(Blocks.MYCELIUM);
		crateRegistry.registerCrate("gravel");
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 0));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 1));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 2));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 3));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 4));
		crateRegistry.registerCrate(new ItemStack(Blocks.SAPLING, 1, 5));
	}

	@Override
	public void registerRecipes() {
		BlockRegistryCore blocks = getBlocks();
		ItemRegistryCore items = getItems();

		/* SMELTING RECIPES */
		RecipeUtil.addSmelting(blocks.resources.get(EnumResourceType.APATITE, 1), items.apatite, 0.5f);
		RecipeUtil.addSmelting(blocks.resources.get(EnumResourceType.COPPER, 1), items.ingotCopper, 0.5f);
		RecipeUtil.addSmelting(blocks.resources.get(EnumResourceType.TIN, 1), items.ingotTin, 0.5f);
		RecipeUtil.addSmelting(new ItemStack(items.peat), items.ash, 0.0f);
		if (ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
			// / CARPENTER
			// Portable ANALYZER
			RecipeManagers.carpenterManager.addRecipe(100, new FluidStack(FluidRegistry.WATER, 2000), ItemStack.EMPTY, items.portableAlyzer.getItemStack(),
				"X#X", "X#X", "RDR",
				'#', OreDictUtil.PANE_GLASS,
				'X', OreDictUtil.INGOT_TIN,
				'R', OreDictUtil.DUST_REDSTONE,
				'D', OreDictUtil.GEM_DIAMOND);
			// Camouflaged Paneling
			FluidStack biomass = Fluids.BIOMASS.getFluid(150);
			if (biomass != null) {
				RecipeManagers.squeezerManager.addRecipe(8, items.craftingMaterial.getCamouflagedPaneling(1), biomass);
			}
		}
		// alternate recipes
		if (!ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			RecipeManagers.centrifugeManager.addRecipe(5, new ItemStack(Items.STRING), ImmutableMap.of(
				items.craftingMaterial.getSilkWisp(), 0.15f
			));
		}

		IHygroregulatorManager hygroManager = RecipeManagers.hygroregulatorManager;
		if (hygroManager != null) {
			hygroManager.addRecipe(new FluidStack(FluidRegistry.WATER, 1), 1, -0.005f, 0.01f);
			hygroManager.addRecipe(new FluidStack(FluidRegistry.LAVA, 1), 10, 0.005f, -0.01f);
			if (Fluids.ICE.getFluid() != null) {
				hygroManager.addRecipe(Fluids.ICE.getFluid(1), 10, -0.01f, 0.02f);
			}
		}
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryCore();
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("blacklist-ores-dimension")) {
			int[] dims = message.getNBTValue().getIntArray("dimensions");
			for (int dim : dims) {
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
	public ICommand[] getConsoleCommands() {
		return new ICommand[]{rootCommand};
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// research note items are not useful without actually having completed research
		hiddenItems.add(new ItemStack(getItems().researchNote));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onBakeModels(ModelBakeEvent event) {
		ModelManager.getInstance().onBakeModels(event);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerSprites(TextureStitchEvent.Pre event) {
		TextureManagerForestry.getInstance().registerSprites();
	}
}
