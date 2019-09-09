///*******************************************************************************
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// ******************************************************************************/
//package forestry.apiculture.commands;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import net.minecraft.world.World;
//
//import forestry.api.apiculture.BeeManager;
//import forestry.api.apiculture.IBeekeepingMode;
//import forestry.core.commands.ICommandModeHelper;
//
//public class BeeModeHelper implements ICommandModeHelper {
////TODO commands
//	@Override
//	public String[] getModeNames() {
//		List<IBeekeepingMode> beekeepingModes = BeeManager.beeRoot.getBeekeepingModes();
//		int modeStringCount = beekeepingModes.size();
//		List<String> modeStrings = new ArrayList<>(modeStringCount);
//		for (IBeekeepingMode mode : beekeepingModes) {
//			modeStrings.add(mode.getName());
//		}
//
//		return modeStrings.toArray(new String[modeStringCount]);
//	}
//
//	@Override
//	public String getModeName(World world) {
//		return BeeManager.beeRoot.getBeekeepingMode(world).getName();
//	}
//
//	@Override
//	public boolean setMode(World world, String modeName) {
//		IBeekeepingMode mode = BeeManager.beeRoot.getBeekeepingMode(modeName);
//		if (mode != null) {
//			BeeManager.beeRoot.setBeekeepingMode(world, mode);
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public Iterable<String> getDescription(String modeName) {
//		IBeekeepingMode mode = BeeManager.beeRoot.getBeekeepingMode(modeName);
//		if (mode == null) {
//			return Collections.emptyList();
//		}
//		return mode.getDescription();
//	}
//
//}
