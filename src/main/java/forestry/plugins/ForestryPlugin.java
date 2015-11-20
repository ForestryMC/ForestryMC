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

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.command.ICommand;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketRegistryDummy;
import forestry.core.utils.Log;

public abstract class ForestryPlugin {

	public boolean isAvailable() {
		return true;
	}

	public String getFailMessage() {
		return "";
	}

	public EnumSet<PluginManager.Module> getDependancies() {
		return EnumSet.of(PluginManager.Module.CORE);
	}

	protected void setupAPI() {
	}

	protected void disabledSetupAPI() {
	}

	protected void registerItemsAndBlocks() {
	}

	protected void preInit() {
	}

	protected void registerTriggers() {
	}

	protected void registerBackpackItems() {
	}

	protected void registerCrates() {
	}

	protected void doInit() {
	}

	protected void registerRecipes() {
	}

	protected void postInit() {
	}

	public boolean processIMCMessage(IMCMessage message) {
		return false;
	}

	protected static String getInvalidIMCMessageText(IMCMessage message) {
		final Object messageValue;
		if (message.isItemStackMessage()) {
			messageValue = message.getItemStackValue().toString();
		} else if (message.isNBTMessage()) {
			messageValue = message.getNBTValue();
		} else if (message.isStringMessage()) {
			messageValue = message.getStringValue();
		} else {
			messageValue = "";
		}

		return String.format("Received an invalid '%s' request '%s' from mod '%s'. Please contact the author and report this issue.", message.key, messageValue, message.getSender());
	}

	protected static void logInvalidIMCMessage(IMCMessage message) {
		String invalidIMCMessageText = getInvalidIMCMessageText(message);
		Log.warning(invalidIMCMessageText);
	}

	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	public void populateChunk(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGeneratedZ) {
	}

	public void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
	}

	public IPacketRegistry getPacketRegistry() {
		return PacketRegistryDummy.instance;
	}

	public IPickupHandler getPickupHandler() {
		return null;
	}

	public IResupplyHandler getResupplyHandler() {
		return null;
	}

	public ICommand[] getConsoleCommands() {
		return null;
	}
	
	public IFuelHandler getFuelHandler() {
		return null;
	}

	@Override
	public String toString() {
		Plugin info = getClass().getAnnotation(Plugin.class);
		if (info == null) {
			return getClass().getSimpleName();
		}
		return info.name() + " Plugin";
	}

}
