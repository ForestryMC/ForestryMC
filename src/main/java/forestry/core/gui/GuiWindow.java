package forestry.core.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.core.gui.elements.Window;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.events.GuiEvent;
import forestry.core.gui.elements.lib.events.GuiEventDestination;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

/**
 * GuiScreen implementation of a gui that contains {@link IGuiElement}s.
 */
@OnlyIn(Dist.CLIENT)
public class GuiWindow extends Screen implements IGuiSizable {
    protected final Window window;
    protected final int xSize;
    protected final int ySize;
    protected int guiLeft;
    protected int guiTop;

    public GuiWindow(int xSize, int ySize, ITextComponent title) {
        super(title);
        this.xSize = xSize;
        this.ySize = ySize;
        this.window = new Window<>(xSize, ySize, this);
        addElements();
    }

    protected void addElements() {
    }

    //TODO right method?
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        window.updateClient();
    }

    @Override
    public void render(MatrixStack transform, int mouseX, int mouseY, float partialTicks) {
        window.setMousePosition(mouseX, mouseY);
        super.render(transform, mouseX, mouseY, partialTicks);
        window.draw(transform, mouseY, mouseX);
    }

    protected void drawTooltips(MatrixStack transform, int mouseY, int mouseX) {
        PlayerInventory playerInv = minecraft.player.inventory;

        if (playerInv.getItemStack().isEmpty()) {
            GuiUtil.drawToolTips(transform, this, children, mouseX, mouseY);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(guiLeft, guiTop, 0.0F);
            window.drawTooltip(transform, mouseY, mouseX);
            RenderSystem.popMatrix();
        }
    }

    //TODO check right method
    @Override
    public void init() {
        super.init();
        this.guiLeft = (this.width - xSize) / 2;
        this.guiTop = (this.height - ySize) / 2;
        window.init(guiLeft, guiTop);
    }

    @Override
    public void init(Minecraft mc, int width, int height) {
        window.setSize(width, height);
        super.init(mc, width, height);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {    //TODO - keybinds?
            this.minecraft.displayGuiScreen(null);

            if (this.minecraft.currentScreen == null) {
                this.minecraft.setGameFocused(true);
            }
        }
        IGuiElement origin = (window.getFocusedElement() == null) ? this.window : this.window.getFocusedElement();
        window.postEvent(new GuiEvent.KeyEvent(origin, key, scanCode, modifiers), GuiEventDestination.ALL);
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        IGuiElement origin = (window.getFocusedElement() == null) ? this.window : this.window.getFocusedElement();
        window.postEvent(new GuiEvent.CharEvent(origin, codePoint, modifiers), GuiEventDestination.ALL);
        return true;
    }

    //TODO onMouseClicked
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        IGuiElement origin = (window.getMousedOverElement() == null) ? this.window : this.window.getMousedOverElement();
        window.postEvent(new GuiEvent.DownEvent(origin, mouseX, mouseY, mouseButton), GuiEventDestination.ALL);
        return true; //TODO return type
    }

    //TODO onMouseRelease
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        IGuiElement origin = (window.getMousedOverElement() == null) ? this.window : this.window.getMousedOverElement();
        window.postEvent(new GuiEvent.UpEvent(origin, mouseX, mouseY, state), GuiEventDestination.ALL);
        //TODO return type
        return true;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double w) {
        super.mouseScrolled(x, y, w);
        if (w != 0) {
            window.postEvent(new GuiEvent.WheelEvent(window, x, y, w), GuiEventDestination.ALL);

        }
        return true;
    }

    //TODO above is how to do dwheel? maybe?
    //	@Override
    //	public void handleMouseInput() {
    //		super.handleMouseInput();
    //		int dWheel = Mouse.getDWheel();
    //		if (dWheel != 0) {
    //			window.postEvent(new GuiEvent.WheelEvent(window, dWheel), GuiEventDestination.ALL);
    //		}
    //	}

    @Override
    public int getGuiLeft() {
        return guiLeft;
    }

    @Override
    public int getGuiTop() {
        return guiTop;
    }

    @Override
    public int getSizeX() {
        return xSize;
    }

    @Override
    public int getSizeY() {
        return ySize;
    }

    @Override
    public Minecraft getMC() {
        return minecraft;
    }

}
