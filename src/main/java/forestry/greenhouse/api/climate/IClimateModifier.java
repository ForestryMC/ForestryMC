/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.greenhouse.api.climate;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;

/**
 * This modifier is used by {@link IClimateContainer}s.
 */
public interface IClimateModifier {

	/**
	 * Is called every 20 ticks by the {@link IClimateContainer} to change its climate state.
	 *
	 * @param container the climate container
	 * @param newState  the new climate state
	 * @param oldState  the climate static of the previous modification.
	 * @param data      A {@link NBTTagCompound} that can be used to save custom data or to send it to the client to add it to the {@link IClimateData}.
	 * @return a modify newState or the newState.
	 */
	default IClimateState modifyTarget(IClimateContainer container, IClimateState newState, IClimateState oldState, NBTTagCompound data) {
		return newState;
	}

	@SideOnly(Side.CLIENT)
	default void addInformation(IClimateContainer container, NBTTagCompound nbtData, ClimateType type, List<String> lines) {
	}

	default boolean canModify(ClimateType type) {
		return false;
	}

	/**
	 * @return The priority of this modifier. The modifier with the highest priority is called first and the modifier with the lowest at last.
	 */
	default int getPriority() {
		return 0;
	}

	String getName();

	@Nullable
	@SideOnly(Side.CLIENT)
	default TextureAtlasSprite getIcon() {
		return null;
	}

	@SideOnly(Side.CLIENT)
	default ResourceLocation getTextureMap() {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

	/**
	 * @return the itemStack that represents this climate modifier. Used as an icon for the climate modifier if {@link #getIcon()} is null.
	 */
	@SideOnly(Side.CLIENT)
	default ItemStack getIconItemStack() {
		return ItemStack.EMPTY;
	}

}
