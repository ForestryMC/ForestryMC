package forestry.database.tiles;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.tiles.TileBase;
import forestry.database.gui.ContainerDatabase;
import forestry.database.gui.GuiDatabase;
import forestry.database.inventory.InventoryDatabase;

public class TileDatabase extends TileBase {

	public TileDatabase() {
		setInternalInventory(new InventoryDatabase(this));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiDatabase(this, player);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerDatabase(this, player.inventory);
	}
}
