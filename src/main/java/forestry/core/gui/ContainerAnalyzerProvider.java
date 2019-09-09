package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.TileEntity;

import forestry.core.gui.slots.SlotLockable;

public class ContainerAnalyzerProvider<T extends TileEntity> extends ContainerTile<T> implements IContainerAnalyzerProvider {
	private final ContainerAnalyzerProviderHelper providerHelper;

	//TODO maybe this is the constructor I need?
	public ContainerAnalyzerProvider(int windowId, ContainerType<?> type, PlayerInventory playerInventory, T tile, int xInv, int yInv) {
		super(windowId, type, playerInventory, tile, xInv, yInv);
		//TODO maybe analyzer container type can be reused?

		providerHelper = new ContainerAnalyzerProviderHelper(this, playerInventory);
	}

	/* Methods - Implement IContainerAnalyzerProvider */
	@Nullable
	public Slot getAnalyzerSlot() {
		return providerHelper.getAnalyzerSlot();
	}

	/* Methods - Implement ContainerForestry */
	@Override
	protected void addSlot(PlayerInventory playerInventory, int slot, int x, int y) {
		addSlot(new SlotLockable(playerInventory, slot, x, y));
	}

	@Override
	protected void addHotbarSlot(PlayerInventory playerInventory, int slot, int x, int y) {
		addSlot(new SlotLockable(playerInventory, slot, x, y));
	}

	/* Methods - Implement IGuiSelectable */
	@Override
	public void handleSelectionRequest(ServerPlayerEntity player, int primary, int secondary) {
		providerHelper.analyzeSpecimen(secondary);
	}
}
