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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorState;
import forestry.api.core.IErrorStateRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ErrorStateRegistry implements IErrorStateRegistry {

	private static final BiMap<Short, IErrorState> states = HashBiMap.create();
	private static final Map<String, IErrorState> stateNames = new HashMap<>();
	private static final Set<IErrorState> stateView = Collections.unmodifiableSet(states.inverse().keySet());

	@Override
	public void registerErrorState(IErrorState state) {
		if (states.containsKey(state.getID())) {
			throw new RuntimeException("Forestry Error State does not possess a unique id.");
		}

		states.put(state.getID(), state);
		addStateName(state, state.getUniqueName());
	}

	@Override
	public void addAlias(IErrorState state, String name) {
		if (!states.values().contains(state)) {
			throw new RuntimeException("Forestry Error State did not exist while trying to register alias.");
		}

		addStateName(state, name);
	}

	private static void addStateName(IErrorState state, String name) {
		if (!name.contains(":")) {
			throw new RuntimeException("Forestry Error State name must be in the format <modid>:<name>.");
		}

		if (stateNames.containsKey(name)) {
			throw new RuntimeException("Forestry Error State does not possess a unique name.");
		}

		stateNames.put(name, state);
	}

	@Override
	public IErrorState getErrorState(short id) {
		return states.get(id);
	}

	@Override
	public IErrorState getErrorState(String name) {
		return stateNames.get(name);
	}

	@Override
	public Set<IErrorState> getErrorStates() {
		return stateView;
	}

	@Override
	public IErrorLogic createErrorLogic() {
		return new ErrorLogic();
	}

	@SideOnly(Side.CLIENT)
	public static void initSprites() {
		for (IErrorState code : states.values()) {
			code.registerSprite();
		}
	}
}
