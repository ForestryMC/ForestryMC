package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

import forestry.core.gui.slots.SlotLockable;

public class ContainerAnalyzerProvider<T extends BlockEntity> extends ContainerTile<T> implements IContainerAnalyzerProvider {
	private final ContainerAnalyzerProviderHelper providerHelper;

	//TODO maybe this is the constructor I need?
	public ContainerAnalyzerProvider(int windowId, MenuType<?> type, Inventory playerInventory, T tile, int xInv, int yInv) {
		super(windowId, type, playerInventory, tile, xInv, yInv);
		//TODO maybe analyzer container type can be reused?

		providerHelper = new ContainerAnalyzerProviderHelper(this, playerInventory);
	}

	/* Methods - Implement IContainerAnalyzerProvider */
	@Override
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
}
