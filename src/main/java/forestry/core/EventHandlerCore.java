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

import java.util.Collection;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraftforge.fml.common.Mod;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.apiculture.ApiaristAI;
import forestry.apiculture.villagers.RegisterVillager;
import forestry.core.config.Constants;
import forestry.modules.ModuleManager;

import genetics.api.GeneticsAPI;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class EventHandlerCore {

	//TODO - register event handler
	@SubscribeEvent
	public static void handleItemPickup(EntityItemPickupEvent event) {
		if (event.isCanceled() || event.getResult() == Event.Result.ALLOW) {
			return;
		}

		for (IPickupHandler handler : ModuleManager.pickupHandlers) {
			if (handler.onItemPickup(event.getPlayer(), event.getItem())) {
				event.setResult(Event.Result.ALLOW);
				return;
			}
		}
	}

	@SubscribeEvent
	public static void handlePlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getPlayer();
		syncBreedingTrackers(player);
	}

	@SubscribeEvent
	public static void handlePlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		Player player = event.getPlayer();
		syncBreedingTrackers(player);
	}

	private static void syncBreedingTrackers(Player player) {
		Collection<IRootDefinition> speciesRoots = GeneticsAPI.apiInstance.getRoots().values();
		for (IRootDefinition definition : speciesRoots) {
			if (!definition.isPresent()) {
				continue;
			}
			IIndividualRoot root = definition.get();
			if (!(root instanceof IForestrySpeciesRoot)) {
				continue;
			}
			IForestrySpeciesRoot speciesRoot = (IForestrySpeciesRoot) root;
			IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.getCommandSenderWorld(), player.getGameProfile());
			breedingTracker.synchToPlayer(player);
		}
	}

	@SubscribeEvent
	public static void handleWorldLoad(WorldEvent.Load event) {
		LevelAccessor world = event.getWorld();

		for (ISaveEventHandler handler : ModuleManager.saveEventHandlers) {
			handler.onWorldLoad(world);
		}
	}

	@SubscribeEvent
	public static void handleWorldSave(WorldEvent.Save event) {
		for (ISaveEventHandler handler : ModuleManager.saveEventHandlers) {
			handler.onWorldSave(event.getWorld());
		}
	}

	@SubscribeEvent
	public static void handleWorldUnload(WorldEvent.Unload event) {
		for (ISaveEventHandler handler : ModuleManager.saveEventHandlers) {
			handler.onWorldUnload(event.getWorld());
		}
	}

	//TODO: Was replaced by the global loot modifiers, should be removes once testing finished
	/*@SubscribeEvent
	public static void lootLoad(LootTableLoadEvent event) {
		if (!event.getName().getNamespace().equals("minecraft")
				&& !event.getName().equals(Constants.VILLAGE_NATURALIST_LOOT_KEY) || !event.getName().getPath().startsWith("chests/")) {
			return;
		}

		Set<String> lootPoolNames = ModuleManager.getLootPoolNames();

		for (String lootTableFile : ModuleManager.getLootTableFiles()) {
			ResourceLocation resourceLocation = new ResourceLocation(Constants.MOD_ID, event.getName().getPath() + "/" + lootTableFile);
			URL url = EventHandlerCore.class.getResource("/data/" + resourceLocation.getNamespace() + "/loot_tables/" + resourceLocation.getPath() + ".json");
			if (url != null) {
				LootTable forestryChestAdditions = event.getLootTableManager().get(resourceLocation);
				if (forestryChestAdditions != LootTable.EMPTY) {
					for (String poolName : lootPoolNames) {
						LootPool pool = forestryChestAdditions.getPool(poolName);
						if (pool != null) {
							event.getTable().addPool(pool);
						}
					}
				}
			}
		}
	}*/

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if ((entity instanceof Villager)) {
			Villager villager = (Villager) entity;
			VillagerProfession prof = ForgeRegistries.PROFESSIONS.getValue(EntityType.getKey(villager.getType()));
			if (prof.getRegistryName().equals(RegisterVillager.BEEKEEPER)) {
				villager.goalSelector.addGoal(6, new ApiaristAI(villager, 0.6));
			}
		}
	}

	//	@SubscribeEvent
	//	public static void renderOverlay(RenderGameOverlayEvent.Post event) {
	//		if(event.getType() != RenderGameOverlayEvent.ElementType.TEXT){
	//			return;
	//		}
	//		Entity entity = Minecraft.getInstance().getCameraEntity();
	//		if(entity == null){
	//			return;
	//		}
	//		RayTraceResult block = entity.pick(20.0D, 0.0F, false);
	//		if (block.getType() == RayTraceResult.Type.BLOCK) {
	//			BlockPos blockpos = ((BlockRayTraceResult)block).getBlockPos();
	//			BlockState blockstate = Minecraft.getInstance().level.getBlockState(blockpos);
	//			ITextComponent component = blockstate.getBlock().getName();
	//			Minecraft.getInstance().font.drawShadow(event.getMatrixStack(), component, event.getWindow().getGuiScaledWidth() / 2.0f, 10, 16777215);
	//		}
	//	}
}
