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
package forestry;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.Type;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.EngineCopperFuel;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.fuels.RainSubstrate;
import forestry.core.ForestryCore;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.config.Version;
import forestry.core.network.PacketHandler;
import forestry.core.proxy.Proxies;
import forestry.core.utils.FluidMap;
import forestry.core.utils.ItemStackMap;
import forestry.core.utils.StringUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Forestry Minecraft Mod
 *
 * @author SirSengir
 */
@Mod(
		modid = Defaults.MOD,
		name = "Forestry",
		version = Version.VERSION,
		dependencies = "required-after:Forge@[10.13.0.1208,);"
		+ "after:Buildcraft|Core;"
		+ "after:ExtrabiomesXL;"
		+ "after:BiomesOPlenty;"
		+ "after:IC2")
//, certificateFingerprint = Version.FINGERPRINT)
public class Forestry {

	@Mod.Instance(Defaults.MOD)
	public static Forestry instance;

	public Forestry() {
		FuelManager.fermenterFuel = new ItemStackMap<FermenterFuel>();
		FuelManager.moistenerResource = new ItemStackMap<MoistenerFuel>();
		FuelManager.rainSubstrate = new ItemStackMap<RainSubstrate>();
		FuelManager.bronzeEngineFuel = new FluidMap<EngineBronzeFuel>();
		FuelManager.copperEngineFuel = new ItemStackMap<EngineCopperFuel>();
	}
	@SidedProxy(clientSide = "forestry.core.ForestryClient", serverSide = "forestry.core.ForestryCore")
	public static ForestryCore core = new ForestryCore();
	public static PacketHandler packetHandler;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		packetHandler = new PacketHandler();

		core.preInit(event.getSourceFile(), this);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		core.init(this);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		core.postInit();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		core.serverStarting(event.getServer());
	}

	/*@EventHandler
	 public void fingerprintWarning(FMLFingerprintViolationEvent event) {
	 Proxies.log.info("Fingerprint of the mod jar is invalid. The jar file was tampered with.");
	 FMLInterModComms.sendMessage("Railcraft", "securityViolation", "Fingerprint of jar file did not match.");
	 FMLInterModComms.sendMessage("Thaumcraft", "securityViolation", "Fingerprint of jar file did not match.");
	 FMLInterModComms.sendMessage("IC2", "securityViolation", "Fingerprint of jar file did not match.");
	 }*/
	@EventHandler
	public void processIMCMessages(IMCEvent event) {
		core.processIMCMessages(event.getMessages());
	}

	@EventHandler
	public void missingMapping(FMLMissingMappingsEvent event) {
		for (MissingMapping mapping : event.get())
			if (mapping.type == Type.BLOCK) {
				Block block = GameRegistry.findBlock(Defaults.MOD, StringUtil.cleanTags(mapping.name));
				if (block != null) {
					mapping.remap(block);
					Proxies.log.warning("Remapping block " + mapping.name + " to " + StringUtil.cleanBlockName(block));
				}
			} else {
				Block block = GameRegistry.findBlock(Defaults.MOD, StringUtil.cleanTags(mapping.name));
				if (block != null) {
					mapping.remap(Item.getItemFromBlock(block));
					Proxies.log.warning("Remapping item " + mapping.name + " to " + StringUtil.cleanBlockName(block));
					continue;
				}
				if (mapping.name.contains("builderBackpack"))
					mapping.remap(ForestryItem.builderBackpack.item());
				if (mapping.name.contains("builderBackpackT2"))
					mapping.remap(ForestryItem.builderBackpackT2.item());
				if (mapping.name.contains("adventurerBackpack"))
					mapping.remap(ForestryItem.adventurerBackpack.item());
				if (mapping.name.contains("adventurerBackpackT2"))
					mapping.remap(ForestryItem.adventurerBackpackT2.item());
				if (mapping.name.contains("shortMead"))
					mapping.remap(ForestryItem.beverage.item());
			}
	}
}
