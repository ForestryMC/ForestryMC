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

import cpw.mods.fml.common.IFuelHandler;
import java.util.Random;

import net.minecraft.command.ICommand;
import net.minecraft.world.World;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.IGuiHandler;

import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.interfaces.IPickupHandler;
import forestry.core.interfaces.IResupplyHandler;
import forestry.core.interfaces.ISaveEventHandler;
import java.util.EnumSet;

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

	protected void preInit() {
	}

	protected void doInit() {
	}

	protected void postInit() {
	}

	protected void disabledInit() {
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
	
	public IFuelHandler getFuelHandler() {
		return null;
	}

	protected void registerItems() {
	}

	protected void registerBackpackItems() {
	}

	protected void registerCrates() {
	}

	protected void registerRecipes() {
	}

	@Override
	public String toString() {
		Plugin info = getClass().getAnnotation(Plugin.class);
		if (info == null)
			return getClass().getSimpleName();
		return info.name() + " Plugin";
	}

}
