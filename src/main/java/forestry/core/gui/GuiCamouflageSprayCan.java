package forestry.core.gui;

import forestry.core.config.Constants;
import forestry.core.gui.widgets.WidgetCamouflageSlot;
import forestry.core.inventory.ItemInventoryCamouflageSprayCan;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;
import net.minecraft.entity.player.EntityPlayer;

public class GuiCamouflageSprayCan extends GuiForestry<ContainerCamouflageSprayCan, ItemInventoryCamouflageSprayCan> {

	public GuiCamouflageSprayCan(EntityPlayer player, ItemInventoryCamouflageSprayCan inventory) {
		super(Constants.TEXTURE_PATH_GUI + "/camouflage_spray_can.png", new ContainerCamouflageSprayCan(inventory, player.inventory), inventory);

		widgetManager.add(new WidgetCamouflageSlot(widgetManager, 80, 39, inventory, null));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

		String name = Translator.translateToLocal("for.gui.camouflage.spray.can");
		textLayout.line = 6;
		textLayout.drawCenteredLine(name, 0, ColourProperties.INSTANCE.get("gui.title"));
		bindTexture(textureFile);
	}

}
