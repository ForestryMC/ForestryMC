package forestry.greenhouse.gui;

import java.util.ArrayList;
import java.util.List;

import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WidgetClimatePillar extends Widget {

	private final List<ClimateButton> buttons = new ArrayList<>();
	
	public WidgetClimatePillar(WidgetManager manager, int xPos, int yPos) {
		super(manager, xPos, yPos);
		this.width = 23;
		this.height = 90;
		for(int i = 1;i < 6;i++){
			EnumTemperature temp = EnumTemperature.VALUES[i];
			float value;
			switch (temp) {
			case ICY:
				value = 0.0F;
				break;
			case COLD:
				value = 0.2F;
				break;
			case WARM:
				value = 0.9F;
				break;
			case HOT:
				value = 1.2F;
				break;
			default:
				value = 0.5F;
				break;
			}
			buttons.add(new ClimateButton(this, temp, value, xPos + 5, yPos + 5 + (i - 1) * 16));
		}
	}

	@Override
	public void draw(int startX, int startY) {
		Proxies.render.bindTexture(manager.gui.textureFile);
		manager.gui.drawTexturedModalRect(startX + xPos, startY + yPos, 216, 25, width, height);
		for(ClimateButton button : buttons){
			button.draw(startX, startY);
		}
	}
	
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		for(ClimateButton button : buttons){
			if(button.isMouseOver(mouseX, mouseY)){
				return button.getToolTip();
			}
		}
		return null;
	}
	
	@Override
	public boolean handleMouseRelease(int mouseX, int mouseY, int eventType) {
		mouseX-=manager.gui.getGuiLeft();
		mouseY-=manager.gui.getGuiTop();
		if(isMouseOver(mouseX, mouseY)){
			return true;
		}
		return false;
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		mouseX-=manager.gui.getGuiLeft();
		mouseY-=manager.gui.getGuiTop();
		for(ClimateButton button : buttons){
			if(button.isMouseOver(mouseX, mouseY)){
				((GuiGreenhouse)manager.gui).setClimate(button.value);
			}
		}
	}
	
	private static class ClimateButton{
		final WidgetClimatePillar parent;
		final EnumTemperature temperature; 
		final float value;
		final int xPos, yPos;
		
		public ClimateButton(WidgetClimatePillar parent, EnumTemperature temperature, float value, int xPos, int yPos) {
			this.parent = parent;
			this.temperature = temperature;
			this.value = value;
			this.xPos = xPos;
			this.yPos = yPos;
		}
		
		public void draw(int startX, int startY) {
			parent.drawSprite(temperature.getSprite(), startX + xPos, startY + yPos);
		}
		
		public ToolTip getToolTip() {
			return toolTip;
		}
		
		public boolean isMouseOver(int mouseX, int mouseY) {
			return mouseX >= xPos && mouseX <= xPos + 16 && mouseY >= yPos && mouseY <= yPos + 16;
		}
		
		protected final ToolTip toolTip = new ToolTip(250) {
			@Override
			@SideOnly(Side.CLIENT)
			public void refresh() {
				toolTip.clear();
				toolTip.add("T: " + AlleleManager.climateHelper.toDisplay(temperature));
				toolTip.add("V: " + value);
			}
		};
	}
	
	protected void drawSprite(TextureAtlasSprite sprite, int x, int y) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0F);
		Proxies.render.bindTexture(TextureManager.getInstance().getGuiTextureMap());
		manager.gui.drawTexturedModalRect(x, y, sprite, 16, 16);
	}
}
