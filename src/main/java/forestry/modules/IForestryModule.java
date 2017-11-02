package forestry.modules;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.command.ICommand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;

import net.minecraftforge.fml.common.event.FMLInterModComms;

import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.network.IPacketRegistry;

public interface IForestryModule {
	default boolean isAvailable() {
		return true;
	}

	default boolean canBeDisabled() {
		return true;
	}

	default String getFailMessage() {
		return "";
	}

	/**
	 * The ForestryModule.moduleID()s of any other modules this module depends on.
	 */
	default Set<ResourceLocation> getDependencyUids(){
		return Collections.emptySet();
	}

	default void setupAPI() {
	}

	default void disabledSetupAPI() {
	}

	default void registerItemsAndBlocks() {
	}

	default void preInit() {
	}

	default void registerTriggers() {
	}

	default void registerBackpackItems() {
	}

	default void registerCrates() {
	}

	default void doInit() {
	}

	default void registerRecipes(){
	}

	default void addLootPoolNames(Set<String> lootPoolNames){
	}

	default void postInit(){
	}

	default boolean processIMCMessage(FMLInterModComms.IMCMessage message){
		return false;
	}

	default void populateChunk(IChunkGenerator chunkGenerator, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated){
	}

	default void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ){
	}

	default void decorateBiome(World world, Random rand, BlockPos pos){
	}

	default void getHiddenItems(List<ItemStack> hiddenItems){
	}

	@Nullable
	default ISaveEventHandler getSaveEventHandler(){
		return null;
	}

	@Nullable
	default IPacketRegistry getPacketRegistry(){
		return null;
	}

	@Nullable
	default IPickupHandler getPickupHandler(){
		return null;
	}

	@Nullable
	default IResupplyHandler getResupplyHandler(){
		return null;
	}

	@Nullable
	default ICommand[] getConsoleCommands(){
		return null;
	}
}
