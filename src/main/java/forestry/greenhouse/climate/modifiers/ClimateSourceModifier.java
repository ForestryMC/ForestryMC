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
package forestry.greenhouse.climate.modifiers;

import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.ClimateStateType;
import forestry.api.climate.ClimateType;
import forestry.api.climate.IClimateState;
import forestry.core.climate.ClimateStates;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;
import forestry.greenhouse.PluginGreenhouse;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateModifier;
import forestry.greenhouse.api.climate.IClimateSource;

public class ClimateSourceModifier implements IClimateModifier {
	public static final float CLIMATE_CHANGE = 0.01F;

	@Override
	public IClimateState modifyTarget(IClimateContainer container, IClimateState newState, IClimateState previousState, NBTTagCompound data) {
		Collection<IClimateSource> sources = container.getClimateSources();
		if (sources.isEmpty()) {
			data.removeTag("rangeUp");
			data.removeTag("rangeDown");
			data.removeTag("change");
			return newState;
		}
		container.recalculateBoundaries();

		IClimateState boundaryUp = container.getBoundaryUp();
		IClimateState boundaryDown = container.getBoundaryDown();

		//Send the boundaries to the client
		data.setTag("rangeUp", boundaryUp.writeToNBT(new NBTTagCompound()));
		data.setTag("rangeDown", boundaryDown.writeToNBT(new NBTTagCompound()));

		IClimateState targetedState = container.getTargetedState();
		if (!targetedState.isPresent()) {
			return newState;
		}
		IClimateState target = getTargetOrBound(previousState, container.getBoundaryDown(), container.getBoundaryUp(), targetedState);
		IClimateState changeState = ClimateStates.extendedZero();

		for (IClimateSource source : container.getClimateSources()) {
			newState = newState.add(source.getState());
		}

		for (IClimateSource source : container.getClimateSources()) {
			IClimateState state = source.work(previousState, target, newState, container.getSizeModifier(), container.canWork());
			changeState = changeState.add(source.getState());
			newState = newState.add(state);
		}
		data.setTag("change", changeState.writeToNBT(new NBTTagCompound()));
		return newState;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(IClimateContainer container, NBTTagCompound nbtData, ClimateType type, List<String> lines) {
		IClimateState rangeDown = ClimateStates.INSTANCE.create(nbtData.getCompoundTag("rangeDown"), ClimateStateType.DEFAULT);
		IClimateState rangeUp = ClimateStates.INSTANCE.create(nbtData.getCompoundTag("rangeUp"), ClimateStateType.DEFAULT);
		IClimateState change = ClimateStates.INSTANCE.create(nbtData.getCompoundTag("change"), ClimateStateType.EXTENDED);
		if (type == ClimateType.HUMIDITY) {
			lines.add(Translator.translateToLocalFormatted("for.gui.modifier.sources.range.up", StringUtil.floatAsPercent(rangeUp.getHumidity())));
			lines.add(Translator.translateToLocalFormatted("for.gui.modifier.sources.range.down", StringUtil.floatAsPercent(rangeDown.getHumidity())));
			lines.add(Translator.translateToLocalFormatted("for.gui.modifier.sources.change", StringUtil.floatAsPercent(change.getHumidity())));
		} else {
			lines.add(Translator.translateToLocalFormatted("for.gui.modifier.sources.range.up", StringUtil.floatAsPercent(rangeUp.getTemperature())));
			lines.add(Translator.translateToLocalFormatted("for.gui.modifier.sources.range.down", StringUtil.floatAsPercent(rangeDown.getTemperature())));
			lines.add(Translator.translateToLocalFormatted("for.gui.modifier.sources.change", StringUtil.floatAsPercent(change.getTemperature())));
		}
	}

	@Override
	public boolean canModify(ClimateType type) {
		return true;
	}

	@Override
	public String getName() {
		return Translator.translateToLocal("for.gui.modifier.sources.title");
	}

	@Override
	public int getPriority() {
		return -1;
	}

	/**
	 * @return The target if it is within the bounds and the bounds if it is above or below the bounds.
	 */
	private IClimateState getTargetOrBound(IClimateState climateState, IClimateState boundaryDown, IClimateState boundaryUp, IClimateState targetedState) {
		float temperature = climateState.getTemperature();
		float humidity = climateState.getHumidity();
		float targetTemperature = targetedState.getTemperature();
		float targetHumidity = targetedState.getHumidity();
		if (targetTemperature > temperature) {
			temperature = Math.min(targetTemperature, boundaryUp.getTemperature());
		} else if (targetTemperature < temperature) {
			temperature = Math.max(targetTemperature, boundaryDown.getTemperature());
		}
		if (targetHumidity > humidity) {
			humidity = Math.min(targetHumidity, boundaryUp.getHumidity());
		} else if (targetHumidity < humidity) {
			humidity = Math.max(targetHumidity, boundaryDown.getHumidity());
		}
		return ClimateStates.of(temperature, humidity);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return PluginGreenhouse.getBlocks().window.getItem("glass");
	}
}
