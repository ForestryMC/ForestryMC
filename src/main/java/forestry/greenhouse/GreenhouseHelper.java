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
package forestry.greenhouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import forestry.api.greenhouse.IGreenhouseLogic;
import forestry.api.greenhouse.IGreenhouseLogicFactory;
import forestry.api.multiblock.IGreenhouseController;
import forestry.greenhouse.api.greenhouse.IGreenhouseHelper;

public class GreenhouseHelper implements IGreenhouseHelper {

	private List<IGreenhouseLogicFactory> factories = new ArrayList<>();
	private Map<String, GreenhouseWindowGlass> windowGlasses = new HashMap<>();

	@Override
	public void registerWindowGlass(String name, ItemStack item, String texture) {
		windowGlasses.put(name, new GreenhouseWindowGlass(name, texture, item));
	}

	@Override
	public ItemStack getGlassItem(String name) {
		GreenhouseWindowGlass glass = windowGlasses.get(name);
		if (glass == null) {
			return ItemStack.EMPTY;
		}
		return glass.item.copy();
	}

	@Override
	public String getGlassTexture(String name) {
		GreenhouseWindowGlass glass = windowGlasses.get(name);
		if (glass == null) {
			return "minecraft:blocks/glass";
		}
		return glass.texture;
	}

	@Override
	public Collection<String> getWindowGlasses() {
		return windowGlasses.keySet();
	}

	@Override
	public void registerLogic(IGreenhouseLogicFactory logicFactory) {
		factories.add(logicFactory);
	}

	@Override
	public Collection<IGreenhouseLogic> createLogics(IGreenhouseController controller) {
		List<IGreenhouseLogic> logics = new ArrayList<>();
		for (IGreenhouseLogicFactory factory : factories) {
			logics.add(factory.createLogic(controller));
		}
		return logics;
	}

	private final class GreenhouseWindowGlass {
		ItemStack item;
		String name;
		String texture;

		public GreenhouseWindowGlass(String name, String texture, ItemStack item) {
			this.name = name;
			this.texture = texture;
			this.item = item;
		}
	}

}
