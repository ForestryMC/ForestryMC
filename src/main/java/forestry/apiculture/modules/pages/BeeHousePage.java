package forestry.apiculture.modules.pages;

import java.io.IOException;
import java.util.List;

import de.nedelosk.modularmachines.api.modules.handlers.ModulePage;
import de.nedelosk.modularmachines.api.modules.handlers.inventory.IModuleInventoryBuilder;
import de.nedelosk.modularmachines.api.modules.state.IModuleState;
import forestry.apiculture.modules.ModuleBeeHouse;
import forestry.apiculture.modules.handlers.BeeHouseHandler;
import forestry.apiculture.modules.handlers.ItemFilterBee;
import forestry.core.config.Constants;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IStreamableGui;
import forestry.core.network.packets.PacketGuiUpdateModule;
import forestry.core.proxy.Proxies;
import forestry.core.render.EnumTankLevel;
import forestry.core.utils.Log;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BeeHousePage extends ModulePage<ModuleBeeHouse> implements IStreamableGui {

	private int previousBreedingProgressPercent = 0;
	
	public BeeHousePage(String title, IModuleState<ModuleBeeHouse> moduleState) {
		super("MainPage", title, moduleState);
	}
	
	@Override
	public int getYSize() {
		return 190;
	}
	
	@Override
	public int getPlayerInvPosition() {
		return 107;
	}
	
	@Override
	protected void createInventory(IModuleInventoryBuilder invBuilder) {	
		// Queen/Princess
		invBuilder.addInventorySlot(true, 29, 39, new ItemFilterBee(false));

		// Drone
		invBuilder.addInventorySlot(true, 29, 65, new ItemFilterBee(true));

		// Product Inventory
		invBuilder.addInventorySlot(false, 116, 52);
		invBuilder.addInventorySlot(false, 137, 39);
		invBuilder.addInventorySlot(false, 137, 65);
		invBuilder.addInventorySlot(false, 116, 78);
		invBuilder.addInventorySlot(false, 95, 65);
		invBuilder.addInventorySlot(false, 95, 39);
		invBuilder.addInventorySlot(false, 116, 26);
	}
	
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		BeeHouseHandler housing = moduleState.getContentHandler(BeeHouseHandler.class);
		data.writeInt(housing.getBeekeepingLogic().getBeeProgressPercent());
	}
	
	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		BeeHouseHandler housing = moduleState.getContentHandler(BeeHouseHandler.class);
		housing.setBreedingProgressPercent(data.readInt());
	}
	
	@Override
	public void detectAndSendChanges() {
		BeeHouseHandler housing = moduleState.getContentHandler(BeeHouseHandler.class);
		int breedingProgressPercen = housing.getBeekeepingLogic().getBeeProgressPercent();
		if(previousBreedingProgressPercent != breedingProgressPercen){
			previousBreedingProgressPercent = breedingProgressPercen;
			PacketGuiUpdateModule packet = new PacketGuiUpdateModule(this, moduleState);
			sendPacketToListeners(packet);
		}
	}
	
	protected final void sendPacketToListeners(IForestryPacketClient packet) {
		for (IContainerListener listener : (List<IContainerListener>)container.getListeners()) {
			if (listener instanceof EntityPlayer) {
				Proxies.net.sendToPlayer(packet, (EntityPlayer) listener);
			} else {
				Log.error("Unknown listener type: {}", listener);
			}
		}
	}
	
	@Override
	public void drawBackground(int mouseX, int mouseY) {
		super.drawBackground(mouseX, mouseY);
		BeeHouseHandler housing = moduleState.getContentHandler(BeeHouseHandler.class);
		
		drawHealthMeter(gui.getGuiLeft() + 20, gui.getGuiTop() + 37, housing.getHealthScaled(46), EnumTankLevel.rateTankLevel(housing.getHealthScaled(100)));
	}

	private void drawHealthMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176 + rated.getLevelScaled(16);
		int k = 0;

		gui.getGui().drawTexturedModalRect(x, y + 46 - height, i, k + 46 - height, 4, height);
	}
	
	@Override
	protected void drawSlot(Slot slot) {
	}
	
	@Override
	protected ResourceLocation getInventoryTexture() {
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getGuiTexture() {
		return new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/alveary.png");
	}

}
