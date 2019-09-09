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
package forestry.arboriculture;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.Forestry;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.core.IArmorNaturalist;
import forestry.api.modules.ForestryModule;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
import forestry.arboriculture.capabilities.ArmorNaturalist;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeFactory;
import forestry.arboriculture.genetics.TreeMutationFactory;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.items.ItemRegistryArboriculture;
import forestry.arboriculture.models.SaplingModelLoader;
import forestry.arboriculture.models.TextureLeaves;
import forestry.arboriculture.models.WoodTextureManager;
import forestry.arboriculture.network.PacketRegistryArboriculture;
import forestry.arboriculture.proxy.ProxyArboriculture;
import forestry.arboriculture.proxy.ProxyArboricultureClient;
import forestry.arboriculture.tiles.TileRegistryArboriculture;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.network.IPacketRegistry;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.ARBORICULTURE, name = "Arboriculture", author = "Binnie & SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.arboriculture.description", lootTable = "arboriculture")
public class ModuleArboriculture extends BlankForestryModule {

	private static final String CONFIG_CATEGORY = "arboriculture";

	@SuppressWarnings("NullableProblems")
	//@SidedProxy(clientSide = "forestry.arboriculture.proxy.ProxyArboricultureClient", serverSide = "forestry.arboriculture.proxy.ProxyArboriculture")
	public static ProxyArboriculture proxy;
	public static String treekeepingMode = "NORMAL";

	public static final List<Block> validFences = new ArrayList<>();

	@Nullable
	private static ItemRegistryArboriculture items;
	@Nullable
	private static BlockRegistryArboriculture blocks;
	@Nullable
	private static TileRegistryArboriculture tiles;
	@Nullable
	public static VillagerProfession villagerArborist;

	public ModuleArboriculture() {
		proxy = DistExecutor.runForDist(() -> () -> new ProxyArboricultureClient(), () -> () -> new ProxyArboriculture());
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	public static ItemRegistryArboriculture getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	public static BlockRegistryArboriculture getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static TileRegistryArboriculture getTiles() {
		Preconditions.checkNotNull(tiles);
		return tiles;
	}

	@Override
	public void setupAPI() {
		TreeManager.treeFactory = new TreeFactory();
		TreeManager.treeMutationFactory = new TreeMutationFactory();

		TreeManager.woodAccess = WoodAccess.getInstance();
	}

	@Override
	public void disabledSetupAPI() {
		TreeManager.woodAccess = WoodAccess.getInstance();
	}

	@Override
	public void registerBlocks() {
		blocks = new BlockRegistryArboriculture();

		WoodAccess woodAccess = WoodAccess.getInstance();

		woodAccess.registerLogs(blocks.logs.values());
		woodAccess.registerPlanks(blocks.planks.values());
		woodAccess.registerSlabs(blocks.slabs.values());
		woodAccess.registerFences(blocks.fences.values());
		woodAccess.registerFenceGates(blocks.fenceGates.values());
		woodAccess.registerStairs(blocks.stairs.values());
		woodAccess.registerDoors(blocks.doors.values());

		woodAccess.registerLogs(blocks.logsFireproof.values());
		woodAccess.registerPlanks(blocks.planksFireproof.values());
		woodAccess.registerSlabs(blocks.slabsFireproof.values());
		woodAccess.registerFences(blocks.fencesFireproof.values());
		woodAccess.registerFenceGates(blocks.fenceGatesFireproof.values());
		woodAccess.registerStairs(blocks.stairsFireproof.values());

		woodAccess.registerLogs(blocks.logsVanillaFireproof.values());
		woodAccess.registerPlanks(blocks.planksVanillaFireproof.values());
		woodAccess.registerSlabs(blocks.slabsVanillaFireproof.values());
		woodAccess.registerFences(blocks.fencesVanillaFireproof.values());
		woodAccess.registerFenceGates(blocks.fenceGatesVanillaFireproof.values());
		woodAccess.registerStairs(blocks.stairsVanillaFireproof.values());
	}

	@Override
	public void registerItems() {
		items = new ItemRegistryArboriculture();
	}

	@Override
	public void registerTiles() {
		tiles = new TileRegistryArboriculture();
	}

	@Override
	public void preInit() {
		// Capabilities
		CapabilityManager.INSTANCE.register(IArmorNaturalist.class, new NullStorage<>(), () -> ArmorNaturalist.INSTANCE);

		MinecraftForge.EVENT_BUS.register(this);

		//TODO: World Gen
		if (TreeConfig.getSpawnRarity(null) > 0.0F) {
			//MinecraftForge.TERRAIN_GEN_BUS.register(new TreeDecorator());
		}

		// Init rendering
		proxy.initializeModels();

		//TODO: Commands
		// Commands
		//ModuleCore.rootCommand.addChildCommand(new CommandTree());

		if (ModuleHelper.isEnabled(ForestryModuleUids.SORTING)) {
			ArboricultureFilterRuleType.init();
		}
	}

	@Override
	public void addLootPoolNames(Set<String> lootPoolNames) {
		lootPoolNames.add("forestry_arboriculture_items");
	}

	@Override
	public void doInit() {
		TreeDefinition.initTrees();

		ItemRegistryArboriculture items = getItems();
		BlockRegistryArboriculture blocks = getBlocks();

		blocks.treeChest.init();

		if (Config.enableVillagers) {
			//TODO: villagers
			//			villagerArborist = new VillagerProfession(Constants.ID_VILLAGER_ARBORIST, Constants.TEXTURE_SKIN_LUMBERJACK, Constants.TEXTURE_SKIN_ZOMBIE_LUMBERJACK);
			//			ForgeRegistries.VILLAGER_PROFESSIONS.register(villagerArborist);
			//
			//			VillagerRegistry.VillagerCareer arboristCareer = new VillagerRegistry.VillagerCareer(villagerArborist, "arborist");
			//			arboristCareer.addTrade(1,
			//				new VillagerArboristTrades.GivePlanksForEmeralds(new VillagerEntity.PriceInfo(1, 1), new VillagerEntity.PriceInfo(10, 32)),
			//				new VillagerArboristTrades.GivePollenForEmeralds(new VillagerEntity.PriceInfo(1, 1), new VillagerEntity.PriceInfo(1, 3), EnumGermlingType.SAPLING, 4)
			//			);
			//			arboristCareer.addTrade(2,
			//				new VillagerArboristTrades.GivePlanksForEmeralds(new VillagerEntity.PriceInfo(1, 1), new VillagerEntity.PriceInfo(10, 32)),
			//				new VillagerTradeLists.GiveItemForEmeralds(new VillagerEntity.PriceInfo(1, 4), items.grafterProven.getItemStack(), new VillagerEntity.PriceInfo(1, 1)),
			//				new VillagerArboristTrades.GivePollenForEmeralds(new VillagerEntity.PriceInfo(2, 3), new VillagerEntity.PriceInfo(1, 1), EnumGermlingType.POLLEN, 6)
			//			);
			//			arboristCareer.addTrade(3,
			//				new VillagerArboristTrades.GiveLogsForEmeralds(new VillagerEntity.PriceInfo(2, 5), new VillagerEntity.PriceInfo(6, 18)),
			//				new VillagerArboristTrades.GiveLogsForEmeralds(new VillagerEntity.PriceInfo(2, 5), new VillagerEntity.PriceInfo(6, 18))
			//			);
			//			arboristCareer.addTrade(4,
			//				new VillagerArboristTrades.GivePollenForEmeralds(new VillagerEntity.PriceInfo(5, 20), new VillagerEntity.PriceInfo(1, 1), EnumGermlingType.POLLEN, 10),
			//				new VillagerArboristTrades.GivePollenForEmeralds(new VillagerEntity.PriceInfo(5, 20), new VillagerEntity.PriceInfo(1, 1), EnumGermlingType.SAPLING, 10)
			//			);
		}

		File configFile = new File(Forestry.instance.getConfigFolder(), CONFIG_CATEGORY + ".cfg");

		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");
		if (!Objects.equals(config.getLoadedConfigVersion(), config.getDefinedConfigVersion())) {
			boolean deleted = configFile.delete();
			if (deleted) {
				config = new LocalizedConfiguration(configFile, "1.0.0");
			}
		}
		TreeConfig.parse(config);
		config.save();
	}

	@Override
	public void registerCrates() {
		//TODO: Tags
		//		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		//		crateRegistry.registerCrate(EnumFruit.CHERRY.getStack());
		//		crateRegistry.registerCrate(EnumFruit.WALNUT.getStack());
		//		crateRegistry.registerCrate(EnumFruit.CHESTNUT.getStack());
		//		crateRegistry.registerCrate(EnumFruit.LEMON.getStack());
		//		crateRegistry.registerCrate(EnumFruit.PLUM.getStack());
		//		crateRegistry.registerCrate(EnumFruit.PAPAYA.getStack());
		//		crateRegistry.registerCrate(EnumFruit.DATES.getStack());
	}

	@Override
	public void registerRecipes() {
		//TODO: Recipes
		//		ItemRegistryCore coreItems = ModuleCore.getItems();
		//		BlockRegistryArboriculture blocks = getBlocks();
		//		ItemRegistryArboriculture items = getItems();
		//
		//		for (BlockForestryLog log : blocks.logs.values()) {
		//			ItemStack logInput = new ItemStack(log, 1, OreDictionary.WILDCARD_VALUE);
		//			ItemStack coalOutput = new ItemStack(Items.COAL, 1, 1);
		//			RecipeUtil.addSmelting(logInput, coalOutput, 0.15F);
		//		}
		//
		//		List<IWoodType> allWoodTypes = new ArrayList<>();
		//		Collections.addAll(allWoodTypes, EnumForestryWoodType.VALUES);
		//		Collections.addAll(allWoodTypes, EnumVanillaWoodType.VALUES);
		//
		//		for (IWoodType woodType : allWoodTypes) {
		//			ItemStack planks = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.PLANKS, false);
		//			ItemStack logs = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.LOG, false);
		//
		//			ItemStack fireproofPlanks = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.PLANKS, true);
		//			ItemStack fireproofLogs = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.LOG, true);
		//
		//			// Fabricator recipes
		//			if (ModuleHelper.allEnabled(ForestryModuleUids.FACTORY, ForestryModuleUids.APICULTURE)) {
		//				logs.setCount(1);
		//				fireproofLogs.setCount(1);
		//				FluidStack liquidGlass = Fluids.GLASS.getFluid(500);
		//				if (liquidGlass != null) {
		//					RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, fireproofLogs.copy(), new Object[]{
		//						" # ",
		//						"#X#",
		//						" # ",
		//						'#', coreItems.refractoryWax,
		//						'X', logs.copy()});
		//
		//					planks.setCount(1);
		//					fireproofPlanks.setCount(5);
		//					RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, fireproofPlanks.copy(), new Object[]{
		//						"X#X",
		//						"#X#",
		//						"X#X",
		//						'#', coreItems.refractoryWax,
		//						'X', planks.copy()});
		//				}
		//			}
		//		}
		//
		//		if (ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
		//
		//			// SQUEEZER RECIPES
		//			int seedOilMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		//			int juiceMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		//			int mulchMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
		//			ItemStack mulch = new ItemStack(coreItems.mulch);
		//			Fluid seedOil = Fluids.SEED_OIL.getFluid();
		//			if (seedOil != null) {
		//				RecipeManagers.squeezerManager.addRecipe(20, EnumFruit.CHERRY.getStack(), new FluidStack(seedOil, 5 * seedOilMultiplier), mulch, 5);
		//				RecipeManagers.squeezerManager.addRecipe(60, EnumFruit.WALNUT.getStack(), new FluidStack(seedOil, 18 * seedOilMultiplier), mulch, 5);
		//				RecipeManagers.squeezerManager.addRecipe(70, EnumFruit.CHESTNUT.getStack(), new FluidStack(seedOil, 22 * seedOilMultiplier), mulch, 2);
		//			}
		//			Fluid juice = Fluids.JUICE.getFluid();
		//			if (juice != null) {
		//				RecipeManagers.squeezerManager.addRecipe(10, EnumFruit.LEMON.getStack(), new FluidStack(juice, juiceMultiplier * 2), mulch, (int) Math.floor(mulchMultiplier * 0.5f));
		//				RecipeManagers.squeezerManager.addRecipe(10, EnumFruit.PLUM.getStack(), new FluidStack(juice, (int) Math.floor(juiceMultiplier * 0.5f)), mulch, mulchMultiplier * 3);
		//				RecipeManagers.squeezerManager.addRecipe(10, EnumFruit.PAPAYA.getStack(), new FluidStack(juice, juiceMultiplier * 3), mulch, (int) Math.floor(mulchMultiplier * 0.5f));
		//				RecipeManagers.squeezerManager.addRecipe(10, EnumFruit.DATES.getStack(), new FluidStack(juice, (int) Math.floor(juiceMultiplier * 0.25)), mulch, mulchMultiplier);
		//			}
		//			RecipeUtil.addFermenterRecipes(new ItemStack(items.sapling, 1, OreDictionary.WILDCARD_VALUE), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		//		}
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryArboriculture();
	}

	@Override
	public boolean processIMCMessage(InterModComms.IMCMessage message) {
		//TODO: IMC
		//		if (message.getMethod().equals("add-fence-block")) {
		//			Supplier<String> blockName = message.getMessageSupplier();
		//			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(message.getMessageSupplier().get()));
		//
		//			if (block != null) {
		//				validFences.add(block);
		//			} else {
		//				IMCUtil.logInvalidIMCMessage(message);
		//			}
		//			return true;
		//		} else if (message.getMethod().equals("blacklist-trees-dimension")) {
		//			String treeUID = message.getNBTValue().getString("treeUID");
		//			int[] dims = message.getNBTValue().getIntArray("dimensions");
		//			for (int dim : dims) {
		//				TreeConfig.blacklistTreeDim(treeUID, dim);
		//			}
		//			return true;
		//		}
		//		return false;
		return false;
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// sapling itemBlock is different from the normal item
		hiddenItems.add(new ItemStack(getBlocks().saplingGE));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void registerSprites(TextureStitchEvent.Pre event) {
		if (event.getMap() != Minecraft.getInstance().getTextureMap()) {
			return;
		}
		TextureLeaves.registerAllSprites(event);
		WoodTextureManager.parseFile();
		for (IAlleleFruit alleleFruit : AlleleFruits.getFruitAlleles()) {
			alleleFruit.getProvider().registerSprites(event);
		}
		List<ResourceLocation> textures = new ArrayList<>();
		for (IWoodType type : TreeManager.woodAccess.getRegisteredWoodTypes()) {
			textures.add(new ResourceLocation(type.getHeartTexture()));
			textures.add(new ResourceLocation(type.getBarkTexture()));
			textures.add(new ResourceLocation(type.getDoorLowerTexture()));
			textures.add(new ResourceLocation(type.getDoorUpperTexture()));
			textures.add(new ResourceLocation(type.getPlankTexture()));
			for (WoodBlockKind kind : WoodBlockKind.values()) {
				for (Entry<String, String> loc : WoodTextureManager.getTextures(type, kind).entrySet()) {
					textures.add(new ResourceLocation(loc.getValue()));
				}
			}
		}
		for (ResourceLocation loc : textures) {
			event.addSprite(loc);
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onModelBake(ModelBakeEvent event) {
		((ProxyArboricultureClient) proxy).onModelBake(event);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onModelRegister(ModelRegistryEvent event) {
		((ProxyArboricultureClient) proxy).onModelRegister();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onClientSetup(FMLClientSetupEvent event) {
		ModelLoaderRegistry.registerLoader(SaplingModelLoader.INSTANCE);
		blocks.treeChest.clientInit();
	}

	@Override
	public void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
		if (TreeConfig.getSpawnRarity(null) > 0.0F) {
			//TreeDecorator.decorateTrees(world, rand, chunkX, chunkZ);
		}
	}

	@SubscribeEvent
	public void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
		//		BlockState state = event.getState();
		//		Block block = state.getBlock();
		//		if (block instanceof LeavesBlock && !(block instanceof BlockForestryLeaves)) {
		//			PlayerEntity player = event.getHarvester();
		//			if (player != null) {
		//				ItemStack harvestingTool = player.getHeldItemMainhand();
		//				if (harvestingTool.getItem() instanceof IToolGrafter) {
		//					if (event.getDrops().isEmpty()) {
		//						World world = event.getWorld();
		//						Item itemDropped = block.getItemDropped(state, world.rand, 3);
		//						if (itemDropped != Items.AIR) {
		//							event.getDrops().add(new ItemStack(itemDropped, 1, block.damageDropped(state)));
		//						}
		//					}
		//
		//					harvestingTool.damageItem(1, player, (entity) -> {
		//						entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
		//					});
		//					if (harvestingTool.isEmpty()) {
		//						net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, harvestingTool, Hand.MAIN_HAND);
		//					}
		//				}
		//			}
		//		}
	}
}
