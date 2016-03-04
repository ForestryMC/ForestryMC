package forestry.plugins;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.command.ICommand;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.network.IPacketRegistry;

public interface IForestryPlugin {
	boolean isAvailable();

	boolean canBeDisabled();

	String getFailMessage();

	/**
	 * See ForestryPlugin.pluginID()
	 */
	@Nonnull
	Set<String> getDependencyUids();

	void setupAPI();

	void disabledSetupAPI();

	void registerItemsAndBlocks();

	void preInit();

	void registerTriggers();

	void registerBackpackItems();

	void registerCrates();

	void doInit();

	void registerRecipes();

	void postInit();

	boolean processIMCMessage(FMLInterModComms.IMCMessage message);

	void populateChunk(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGeneratedZ);

	void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ);

	void getHiddenItems(List<ItemStack> hiddenItems);

	@Nullable
	ISaveEventHandler getSaveEventHandler();

	@Nullable
	IPacketRegistry getPacketRegistry();

	@Nullable
	IPickupHandler getPickupHandler();

	@Nullable
	IResupplyHandler getResupplyHandler();

	@Nullable
	ICommand[] getConsoleCommands();

	@Nullable
	IFuelHandler getFuelHandler();
}
