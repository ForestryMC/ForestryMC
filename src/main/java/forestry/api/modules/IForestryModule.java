/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.modules;

import com.mojang.brigadier.Command;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * Defines a Forestry module.
 * Any class implementing this interface and annotated by {@link ForestryModule} to be loaded by
 * the model manager of Forestry.
 */
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
    default Set<ResourceLocation> getDependencyUids() {
        return Collections.emptySet();
    }

    /**
     * Can be used to setup the api.
     * Will only be called if the module is active if not {@link #disabledSetupAPI()} will be called.
     * <p>
     * Must be called by the mod that registers the container.
     */
    default void setupAPI() {
    }

    /**
     * Called to setup the api if this module is disabled in the config or has missing dependencies.
     * <p>
     * Must be called by the mod that registers the container.
     */
    default void disabledSetupAPI() {
    }

    default void registerEntityTypes(IForgeRegistry<EntityType<?>> registry) {

    }

    @OnlyIn(Dist.CLIENT)
    default void registerGuiFactories() {

    }

    /**
     * Must be called by the mod that registers the container.
     */
    default void preInit() {
    }

    /**
     * Can be used to register Buildcraft triggers.
     */
    default void registerTriggers() {
    }

    default void doInit() {
    }

    @OnlyIn(Dist.CLIENT)
    default void clientSetup() {

    }

    /**
     * Can be used to register recipes. Called after {@link #doInit()}.
     */
    default void registerRecipes() {
    }

    default void addLootPoolNames(Set<String> lootPoolNames) {
    }

    default void postInit() {
    }

    @Nullable
    default Command[] getConsoleCommands() {
        return null;
    }
}
