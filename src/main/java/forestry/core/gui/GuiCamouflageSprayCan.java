package forestry.core.gui;

import forestry.api.core.CamouflageManager;
import forestry.core.config.Constants;
import forestry.core.gui.widgets.WidgetCamouflageSlot;
import forestry.core.inventory.ItemInventoryCamouflageSprayCan;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;
import net.minecraft.entity.player.EntityPlayer;

public class GuiCamouflageSprayCan extends GuiForestry<ContainerCamouflageSprayCan> {

	public GuiCamouflageSprayCan(EntityPlayer player, ItemInventoryCamouflageSprayCan inventory) {
		super(Constants.TEXTURE_PATH_GUI + "/camouflage_spray_can.png", new ContainerCamouflageSprayCan(inventory, player.inventory));

		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 80, 39, inventory, CamouflageManager.NONE));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

		String name = Translator.translateToLocal("for.gui.camouflage_spray_can");
		textLayout.line = 6;
		textLayout.drawCenteredLine(name, 0, ColourProperties.INSTANCE.get("gui.title"));
		bindTexture(textureFile);
	}

	@Override
	protected void addLedgers() {

	}

}
