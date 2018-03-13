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
package forestry.core.errors;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.network.PacketBuffer;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorState;

public class ErrorLogic implements IErrorLogic {
	private final Set<IErrorState> errorStates = new HashSet<>();

	@Override
	public final boolean setCondition(boolean condition, IErrorState errorState) {
		if (errorState == null) {
			return false;
		}
		if (condition) {
			errorStates.add(errorState);
		} else {
			errorStates.remove(errorState);
		}
		return condition;
	}

	@Override
	public final boolean contains(IErrorState state) {
		return errorStates.contains(state);
	}

	@Override
	public final boolean hasErrors() {
		return !errorStates.isEmpty();
	}

	@Override
	public final ImmutableSet<IErrorState> getErrorStates() {
		return ImmutableSet.copyOf(errorStates);
	}

	@Override
	public void clearErrors() {
		errorStates.clear();
	}

	@Override
	public void writeData(PacketBuffer data) {
		data.writeShort(errorStates.size());
		for (IErrorState errorState : errorStates) {
			data.writeShort(errorState.getID());
		}
	}

	@Override
	public void readData(PacketBuffer data) {
		clearErrors();

		short errorStateCount = data.readShort();
		for (int i = 0; i < errorStateCount; i++) {
			short errorStateId = data.readShort();
			IErrorState errorState = ForestryAPI.errorStateRegistry.getErrorState(errorStateId);
			if (errorState != null) {
				errorStates.add(errorState);
			}
		}
	}
}
