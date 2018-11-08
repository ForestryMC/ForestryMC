package forestry.core.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

import forestry.core.gui.slots.SlotLockable;

public class ContainerAnalyzerProvider<T extends TileEntity> extends ContainerTile<T> implements IContainerAnalyzerProvider {
	/* Attributes - Final*/
	private final ContainerAnalyzerProviderHelper providerHelper;

	/* Constructors */
	public ContainerAnalyzerProvider(T tileForestry, InventoryPlayer playerInventory, int xInv, int yInv) {
		super(tileForestry, playerInventory, xInv, yInv);

		providerHelper = new ContainerAnalyzerProviderHelper(this, playerInventory);
	}

	/* Methods - Implement IContainerAnalyzerProvider */
	@Nullable
	public Slot getAnalyzerSlot() {
		return providerHelper.getAnalyzerSlot();
	}

	/* Methods - Implement ContainerForestry */
	@Override
	protected void addSlot(InventoryPlayer playerInventory, int slot, int x, int y) {
		addSlotToContainer(new SlotLockable(playerInventory, slot, x, y));
	}

	@Override
	protected void addHotbarSlot(InventoryPlayer playerInventory, int slot, int x, int y) {
		addSlotToContainer(new SlotLockable(playerInventory, slot, x, y));
	}

	/* Methods - Implement IGuiSelectable */
	@Override
	public void handleSelectionRequest(EntityPlayerMP player, int primary, int secondary) {
		providerHelper.analyzeSpecimen(secondary);
	}
}
