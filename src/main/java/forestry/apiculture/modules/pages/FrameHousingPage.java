package forestry.apiculture.modules.pages;

import de.nedelosk.modularmachines.api.modules.handlers.ModulePage;
import de.nedelosk.modularmachines.api.modules.handlers.inventory.IModuleInventoryBuilder;
import de.nedelosk.modularmachines.api.modules.state.IModuleState;
import forestry.apiculture.modules.ModuleBeeHouse;
import forestry.apiculture.modules.handlers.ItemFilterFrame;
import forestry.core.config.Constants;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FrameHousingPage extends ModulePage<ModuleBeeHouse> {
	
	public FrameHousingPage(String title, IModuleState<ModuleBeeHouse> moduleState) {
		super("FrameHousingPage", title, moduleState);
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
		invBuilder.addInventorySlot(true, 80, 18, new ItemFilterFrame());
		invBuilder.addInventorySlot(true, 80, 47, new ItemFilterFrame());
		invBuilder.addInventorySlot(true, 80, 76, new ItemFilterFrame());
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
		return new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/frame_housing.png");
	}

}
