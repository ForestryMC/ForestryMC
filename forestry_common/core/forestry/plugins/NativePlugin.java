/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.plugins;

import java.util.Random;

import net.minecraft.command.ICommand;
import net.minecraft.world.World;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.IGuiHandler;

import forestry.api.core.IPlugin;
import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.interfaces.IPickupHandler;
import forestry.core.interfaces.IResupplyHandler;
import forestry.core.interfaces.ISaveEventHandler;

public abstract class NativePlugin implements IPlugin {

	@Override
	public void preInit() {
		registerItems();
	}

	@Override
	public void doInit() {
		registerBackpackItems();
		registerCrates();
	}

	@Override
	public void postInit() {
		registerRecipes();
	}

	public boolean processIMCMessage(IMCMessage message) {
		return false;
	}

	public IGuiHandler getGuiHandler() {
		return null;
	}

	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

	public void generateSurface(World world, Random rand, int chunkX, int chunkZ) {
	}

	public IPacketHandler getPacketHandler() {
		return null;
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

	protected abstract void registerItems();

	protected abstract void registerBackpackItems();

	protected abstract void registerCrates();

	protected abstract void registerRecipes();

}
