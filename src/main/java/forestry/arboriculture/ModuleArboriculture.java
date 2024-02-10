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

import java.util.function.Consumer;

import forestry.apiculture.features.ApicultureFeatures;
import net.minecraft.world.level.levelgen.feature.Feature;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IArmorNaturalist;
import forestry.api.modules.ForestryModule;
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
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.ForgeUtils;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.ARBORICULTURE, name = "Arboriculture", author = "Binnie & SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.arboriculture.description", lootTable = "arboriculture")
public class ModuleArboriculture extends BlankForestryModule {

	@SuppressWarnings("NullableProblems")
	//@SidedProxy(clientSide = "forestry.arboriculture.proxy.ProxyArboricultureClient", serverSide = "forestry.arboriculture.proxy.ProxyArboriculture")
	public static ProxyArboriculture proxy;
	public static String treekeepingMode = "NORMAL";

	public ModuleArboriculture() {
		proxy = DistExecutor.safeRunForDist(() -> ProxyArboricultureClient::new, () -> ProxyArboriculture::new);
		ForgeUtils.registerSubscriber(this);
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		RegisterVillager.POINTS_OF_INTEREST.register(modEventBus);
		RegisterVillager.PROFESSIONS.register(modEventBus);
		MinecraftForge.EVENT_BUS.addListener(RegisterVillager::villagerTrades);

		ApicultureFeatures.FEATURES.register(modEventBus);
		ApicultureFeatures.CONFIGURED_FEATURES.register(modEventBus);
		ApicultureFeatures.PLACED_FEATURES.register(modEventBus);
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
		MinecraftForge.EVENT_BUS.register(this);

		//TODO: World Gen
		if (TreeConfig.getSpawnRarity() > 0.0F) {
			//MinecraftForge.TERRAIN_GEN_BUS.register(new TreeDecorator());
		}

		// Init rendering
		proxy.initializeModels();

		// Commands
		ModuleCore.rootCommand.then(CommandTree.register());

		ArboricultureFilterRuleType.init();
	}

	@Override
	public void registerCapabilities(Consumer<Class<?>> consumer) {
		consumer.accept(IArmorNaturalist.class);
	}

	@Override
	public void doInit() {
		TreeDefinition.initTrees();
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
