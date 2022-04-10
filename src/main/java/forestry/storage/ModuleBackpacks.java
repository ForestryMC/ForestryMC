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

package forestry.storage;

import java.awt.Color;

import forestry.api.modules.ForestryModule;
import forestry.api.storage.IBackpackInterface;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.config.Constants;
import forestry.core.data.ForestryTags;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.storage.features.BackpackContainers;
import forestry.storage.gui.GuiBackpack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.client.gui.ScreenManager;

@ForestryModule(moduleID = ForestryModuleUids.BACKPACKS, containerID = Constants.MOD_ID, name = "Backpack", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.backpacks.description", lootTable = "storage")
public class ModuleBackpacks extends BlankForestryModule {

	public static final IBackpackInterface BACKPACK_INTERFACE = new BackpackInterface();

	public static final BackpackDefinition APIARIST = new BackpackDefinition(new Color(0xc4923d), Color.WHITE, BACKPACK_INTERFACE.createNaturalistBackpackFilter("rootBees"));
	public static final BackpackDefinition LEPIDOPTERIST = new BackpackDefinition(new Color(0x995b31), Color.WHITE, BACKPACK_INTERFACE.createNaturalistBackpackFilter("rootButterflies"));
	public static final BackpackDefinition MINER = new BackpackDefinition(new Color(0x36187d), Color.WHITE, new BackpackFilter(ForestryTags.Items.MINER_ALLOW, ForestryTags.Items.MINER_REJECT));
	public static final BackpackDefinition DIGGER = new BackpackDefinition(new Color(0x363cc5), Color.WHITE, new BackpackFilter(ForestryTags.Items.DIGGER_ALLOW, ForestryTags.Items.DIGGER_REJECT));
	public static final BackpackDefinition FORESTER = new BackpackDefinition(new Color(0x347427), Color.WHITE, new BackpackFilter(ForestryTags.Items.FORESTER_ALLOW, ForestryTags.Items.FORESTER_REJECT));
	public static final BackpackDefinition HUNTER = new BackpackDefinition(new Color(0x412215), Color.WHITE, new BackpackFilter(ForestryTags.Items.HUNTER_ALLOW, ForestryTags.Items.HUNTER_REJECT));
	public static final BackpackDefinition ADVENTURER = new BackpackDefinition(new Color(0x7fb8c2), Color.WHITE, new BackpackFilter(ForestryTags.Items.ADVENTURER_ALLOW, ForestryTags.Items.ADVENTURER_REJECT));
	public static final BackpackDefinition BUILDER = new BackpackDefinition(new Color(0xdd3a3a), Color.WHITE, new BackpackFilter(ForestryTags.Items.BUILDER_ALLOW, ForestryTags.Items.BUILDER_REJECT));

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerGuiFactories() {
		ScreenManager.register(BackpackContainers.BACKPACK.containerType(), GuiBackpack::new);
	}

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public IPickupHandler getPickupHandler() {
		return new PickupHandlerStorage();
	}

	@Override
	public IResupplyHandler getResupplyHandler() {
		return new ResupplyHandler();
	}
}
