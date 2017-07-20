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

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.command.ICommand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;

import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.network.IPacketRegistry;

public abstract class BlankForestryPlugin implements IForestryPlugin {

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public boolean canBeDisabled() {
		return true;
	}

	@Override
	public String getFailMessage() {
		return "";
	}

	@Override
	public Set<String> getDependencyUids() {
		Set<String> dependencyUids = new HashSet<>();
		dependencyUids.add(ForestryPluginUids.CORE);
		return dependencyUids;
	}

	@Override
	public void setupAPI() {
	}

	@Override
	public void disabledSetupAPI() {
	}

	@Override
	public void registerItemsAndBlocks() {
	}

	@Override
	public void preInit() {
	}

	@Override
	public void registerTriggers() {
	}

	@Override
	public void registerBackpackItems() {
	}

	@Override
	public void registerCrates() {
	}

	@Override
	public void doInit() {
	}

	@Override
	public void registerRecipes() {
	}

	@Override
	public void addLootPoolNames(Set<String> lootPoolNames) {

	}

	@Override
	public void postInit() {
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		return false;
	}

	@Override
	public void populateChunk(IChunkGenerator chunkGenerator, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
	}

	@Override
	public void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
	}

	@Override
	public void decorateBiome(World world, Random rand, BlockPos pos) {
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
	}

	@Nullable
	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Nullable
	@Override
	public IPacketRegistry getPacketRegistry() {
		return null;
	}

	@Nullable
	@Override
	public IPickupHandler getPickupHandler() {
		return null;
	}

	@Nullable
	@Override
	public IResupplyHandler getResupplyHandler() {
		return null;
	}

	@Nullable
	@Override
	public ICommand[] getConsoleCommands() {
		return null;
	}

	@Override
	public String toString() {
		ForestryPlugin forestryPlugin = getClass().getAnnotation(ForestryPlugin.class);
		if (forestryPlugin == null) {
			return getClass().getSimpleName();
		}
		return forestryPlugin.name() + " Plugin";
	}

}
