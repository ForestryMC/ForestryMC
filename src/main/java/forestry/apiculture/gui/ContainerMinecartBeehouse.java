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
package forestry.apiculture.gui;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.FriendlyByteBuf;

import forestry.apiculture.entities.MinecartEntityBeeHousingBase;
import forestry.apiculture.features.ApicultureContainers;
import forestry.core.gui.ContainerAnalyzerProviderHelper;
import forestry.core.gui.ContainerEntity;
import forestry.core.gui.slots.SlotLockable;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.packets.PacketGuiUpdateEntity;

public class ContainerMinecartBeehouse extends ContainerEntity<MinecartEntityBeeHousingBase> implements IContainerBeeHousing {
	/* Attributes - Final*/
	private final ContainerAnalyzerProviderHelper providerHelper;
	private final IGuiBeeHousingDelegate delegate;
	private final GuiBeeHousing.Icon icon;


	//TODO writing things to packets here
	public static ContainerMinecartBeehouse fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
		PacketBufferForestry buf = new PacketBufferForestry(extraData);
		MinecartEntityBeeHousingBase e = (MinecartEntityBeeHousingBase) buf.readEntityById(playerInv.player.level);    //TODO cast
		Player player = playerInv.player;
		boolean hasFrames = buf.readBoolean();
		GuiBeeHousing.Icon icon = buf.readEnum(GuiBeeHousing.Icon.values());
		return new ContainerMinecartBeehouse(windowId, player.inventory, e, hasFrames, icon);
	}

	public ContainerMinecartBeehouse(int windowId, Inventory player, MinecartEntityBeeHousingBase entity, boolean hasFrames, GuiBeeHousing.Icon icon) {
		super(windowId, ApicultureContainers.BEEHOUSE_MINECART.containerType(), entity, player, 8, 108);
		providerHelper = new ContainerAnalyzerProviderHelper(this, player);

		ContainerBeeHelper.addSlots(this, entity, hasFrames);

		entity.getBeekeepingLogic().clearCachedValues();
		delegate = entity;
		this.icon = icon;
	}

	private int beeProgress = -1;

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		int beeProgress = entity.getBeekeepingLogic().getBeeProgressPercent();
		if (this.beeProgress != beeProgress) {
			this.beeProgress = beeProgress;
			IForestryPacketClient packet = new PacketGuiUpdateEntity(entity, entity);
			sendPacketToListeners(packet);
		}
	}

	/* Methods - Implement IContainerAnalyzerProvider */
	@Nullable
	public Slot getAnalyzerSlot() {
		return providerHelper.getAnalyzerSlot();
	}

	/* Methods - Implement ContainerForestry */
	@Override
	protected void addSlot(Inventory playerInventory, int slot, int x, int y) {
		addSlot(new SlotLockable(playerInventory, slot, x, y));
	}

	@Override
	protected void addHotbarSlot(Inventory playerInventory, int slot, int x, int y) {
		addSlot(new SlotLockable(playerInventory, slot, x, y));
	}

	/* Methods - Implement IGuiSelectable */
	@Override
	public void handleSelectionRequest(ServerPlayer player, int primary, int secondary) {
		providerHelper.analyzeSpecimen(secondary);
	}

	@Override
	public IGuiBeeHousingDelegate getDelegate() {
		return delegate;
	}

	@Override
	public GuiBeeHousing.Icon getIcon() {
		return icon;
	}
}
