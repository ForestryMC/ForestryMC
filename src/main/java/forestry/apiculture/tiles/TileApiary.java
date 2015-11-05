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
package forestry.apiculture.tiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.IHiveFrame;
import forestry.apiculture.inventory.InventoryApiary;
import forestry.apiculture.trigger.ApicultureTriggers;
import forestry.core.network.GuiId;

import buildcraft.api.statements.ITriggerExternal;

public class TileApiary extends TileAbstractBeeHousing {
	private static final IBeeModifier beeModifier = new ApiaryBeeModifier();

	private final IBeeListener beeListener;
	private final InventoryApiary inventory;

	public TileApiary() {
		super(GuiId.ApiaryGUI, "apiary");
		this.beeListener = new ApiaryBeeListener(this);

		InventoryApiary apiaryInventory = new InventoryApiary(this);
		this.inventory = apiaryInventory;
		setInternalInventory(apiaryInventory);
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return inventory;
	}

	@Override
	public Collection<IBeeModifier> getBeeModifiers() {
		List<IBeeModifier> beeModifiers = new ArrayList<>();

		beeModifiers.add(beeModifier);

		for (IHiveFrame frame : inventory.getFrames()) {
			beeModifiers.add(frame.getBeeModifier());
		}

		return beeModifiers;
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.singleton(beeListener);
	}

	private static class ApiaryBeeModifier extends DefaultBeeModifier {
		@Override
		public float getProductionModifier(IBeeGenome genome, float currentModifier) {
			return 0.1f;
		}
	}

	private static class ApiaryBeeListener extends DefaultBeeListener {
		private final TileApiary apiary;

		public ApiaryBeeListener(TileApiary apiary) {
			this.apiary = apiary;
		}

		@Override
		public void wearOutEquipment(int amount) {
			IBeekeepingMode beekeepingMode = BeeManager.beeRoot.getBeekeepingMode(apiary.getWorldObj());
			int wear = Math.round(amount * beekeepingMode.getWearModifier());

			IInventory framesInventory = apiary.inventory.getFrameInventory();
			for (int i = 0; i < framesInventory.getSizeInventory(); i++) {
				ItemStack hiveFrameStack = framesInventory.getStackInSlot(i);
				if (hiveFrameStack == null) {
					continue;
				}

				Item hiveFrameItem = hiveFrameStack.getItem();
				if (!(hiveFrameItem instanceof IHiveFrame)) {
					continue;
				}

				IHiveFrame hiveFrame = (IHiveFrame) hiveFrameItem;

				ItemStack queenStack = apiary.getBeeInventory().getQueen();
				IBee queen = BeeManager.beeRoot.getMember(queenStack);
				ItemStack usedFrame = hiveFrame.frameUsed(apiary, hiveFrameStack, queen, wear);

				framesInventory.setInventorySlotContents(i, usedFrame);
			}
		}
	}

	/* ITRIGGERPROVIDER */
	@Optional.Method(modid = "BuildCraftAPI|statements")
	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		LinkedList<ITriggerExternal> res = new LinkedList<>();
		res.add(ApicultureTriggers.missingQueen);
		res.add(ApicultureTriggers.missingDrone);
		res.add(ApicultureTriggers.noFrames);
		return res;
	}
}
