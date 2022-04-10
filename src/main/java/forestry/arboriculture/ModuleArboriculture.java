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

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.world.gen.feature.Feature;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.Forestry;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IArmorNaturalist;
import forestry.api.modules.ForestryModule;
import forestry.arboriculture.capabilities.ArmorNaturalist;
import forestry.arboriculture.commands.CommandTree;
import forestry.arboriculture.features.ArboricultureFeatures;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeFactory;
import forestry.arboriculture.genetics.TreeMutationFactory;
import forestry.arboriculture.network.PacketRegistryArboriculture;
import forestry.arboriculture.proxy.ProxyArboriculture;
import forestry.arboriculture.proxy.ProxyArboricultureClient;
import forestry.arboriculture.villagers.RegisterVillager;
import forestry.core.ModuleCore;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.ForgeUtils;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;
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
	public static VillagerProfession villagerArborist;

	public ModuleArboriculture() {
		proxy = DistExecutor.safeRunForDist(() -> ProxyArboricultureClient::new, () -> ProxyArboriculture::new);
		ForgeUtils.registerSubscriber(this);

		if (Config.enableVillagers) {
			RegisterVillager.Registers.POINTS_OF_INTEREST.register(FMLJavaModLoadingContext.get().getModEventBus());
			RegisterVillager.Registers.PROFESSIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
			MinecraftForge.EVENT_BUS.register(new RegisterVillager.Events());
		}

		if (TreeConfig.getSpawnRarity(null) > 0.0F) {
			IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
			modEventBus.addGenericListener(Feature.class, ArboricultureFeatures::registerFeatures);
			MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ArboricultureFeatures::onBiomeLoad);
		}
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

		// Commands
		ModuleCore.rootCommand.then(CommandTree.register());

		if (ModuleHelper.isEnabled(ForestryModuleUids.SORTING)) {
			ArboricultureFilterRuleType.init();
		}
	}

	@Override
	public void doInit() {
		TreeDefinition.initTrees();

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

	//@SubscribeEvent
	//public void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
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
	//}

	@Override
	public ISidedModuleHandler getModuleHandler() {
		return proxy;
	}
}
