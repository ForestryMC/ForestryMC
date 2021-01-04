package forestry.core.gui.elements;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.ITooltipSupplier;
import forestry.core.gui.elements.lib.IWindowElement;
import forestry.core.gui.elements.lib.events.ElementEvent;
import forestry.core.gui.elements.lib.events.GuiElementEvent;
import forestry.core.gui.elements.lib.events.GuiEventDestination;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class GuiElement extends AbstractGui implements IGuiElement, ICroppedGuiElement {
    /* Attributes - Final */
    //Tooltip of the element
    private final List<ITooltipSupplier> tooltipSuppliers = new ArrayList<>();
    //Event handler of this element
    private final Collection<Consumer<? extends GuiElementEvent>> eventHandlers = new ArrayList<>();
    /* Attributes - State*/
    //Element Position
    protected int xPos;
    protected int yPos;
    protected int xOffset;
    protected int yOffset;
    //Size of this element
    protected int width;
    protected int height;
    //The start coordinates of the crop
    protected int cropX;
    protected int cropY;
    protected int cropWidth = -1;
    protected int cropHeight = -1;
    //The element to that the crop coordinates are relative to.
    @Nullable
    protected IGuiElement cropElement = null;
    //Element Alignment relative to the parent
    private GuiElementAlignment align = GuiElementAlignment.TOP_LEFT;

    protected boolean visible = true;

    //The element container that contains this element
    @Nullable
    protected IGuiElement parent;

    public GuiElement(int width, int height) {
        this(0, 0, width, height);
    }

    public GuiElement(int xPos, int yPos, int width, int height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    @Override
    public void onCreation() {
        //Default-Implementation
    }

    @Override
    public void onDeletion() {
        IWindowElement window = getWindow();
        window.postEvent(new ElementEvent.Deletion(this), GuiEventDestination.ALL);
    }

    @Override
    public final int getX() {
        int x = 0;
        int parentWidth = parent != null ? parent.getWidth() : -1;
        int w = getWidth();
        if (parentWidth >= 0 && parentWidth > w) {
            x = (int) ((parentWidth - w) * align.getXOffset());
        }
        return xPos + x + xOffset;
    }

    @Override
    public final int getY() {
        int y = 0;
        int parentHeight = parent != null ? parent.getHeight() : -1;
        int h = getHeight();
        if (parentHeight >= 0 && parentHeight > h) {
            y = (int) ((parentHeight - h) * align.getYOffset());
        }
        return yPos + y + yOffset;
    }

    public final int getAbsoluteX() {
        return parent == null ? getX() : getX() + parent.getAbsoluteX();
    }

    public final int getAbsoluteY() {
        return parent == null ? getY() : getY() + parent.getAbsoluteY();
    }

    @Override
    public final void draw(MatrixStack transform, int mouseY, int mouseX) {
        if (!isVisible()) {
            return;
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(getX(), getY(), 0.0F);
        if (isCropped()) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            Minecraft mc = Minecraft.getInstance();
            //TODO - resolution stuff again, check gameSettings.guiscale too
            MainWindow window = mc.getMainWindow();
            double scaleWidth = ((double) window.getWidth()) / window.getScaledWidth();
            double scaleHeight = ((double) window.getHeight()) / window.getScaledHeight();
            IGuiElement cropRelative = cropElement != null ? cropElement : this;
            int posX = cropRelative.getAbsoluteX();
            int posY = cropRelative.getAbsoluteY();
            GL11.glScissor((int) ((posX + cropX) * scaleWidth), (int) (window.getHeight() - ((posY + cropY + cropHeight) * scaleHeight)), (int) (cropWidth * scaleWidth), (int) (cropHeight * scaleHeight));
        }

        drawElement(transform, mouseY, mouseX);

        if (isCropped()) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        RenderSystem.popMatrix();
    }

    public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
        //Default-Implementation
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        setSize(width, height);
    }

    @Override
    public void setWidth(int width) {
        setSize(width, height);
    }

    @Override
    public IGuiElement setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public IGuiElement setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        return this;
    }

    @Override
    public IGuiElement setXPosition(int xPos) {
        setLocation(xPos, yPos);
        return this;
    }

    @Override
    public IGuiElement setYPosition(int yPos) {
        setLocation(xPos, yPos);
        return this;
    }

    @Override
    public IGuiElement setLocation(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        return this;
    }

    @Override
    public IGuiElement setBounds(int xPos, int yPos, int width, int height) {
        setLocation(xPos, yPos);
        setSize(width, height);
        return this;
    }

    @Override
    public IGuiElement setAlign(GuiElementAlignment align) {
        this.align = align;
        return this;
    }

    @Override
    public GuiElementAlignment getAlign() {
        return align;
    }

    /* CROPPED */
    public void setCroppedZone(@Nullable IGuiElement cropElement, int cropX, int cropY, int cropWidth, int cropHeight) {
        this.cropElement = cropElement;
        this.cropX = cropX;
        this.cropY = cropY;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
    }

    @Nullable
    public IGuiElement getCropElement() {
        return cropElement;
    }

    public int getCropX() {
        return cropX;
    }

    public int getCropY() {
        return cropY;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public boolean isCropped() {
        return cropElement != null && cropWidth >= 0 && cropHeight >= 0;
    }

    @Override
    public IWindowElement getWindow() {
        if (this.parent == null) {
            throw new IllegalStateException("Tried to access the window element of an element that doesn't had one.");
        } else {
            return this.parent.getWindow();
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!isVisible()) {
            return false;
        }
        return mouseX >= 0 && mouseX < getWidth() && mouseY >= 0 && mouseY < getHeight();
    }

    @Override
    public final boolean isMouseOver() {
        IWindowElement window = getWindow();
        int mouseX = window.getRelativeMouseX(this);
        int mouseY = window.getRelativeMouseY(this);
        if (!isCropped()) {
            return isMouseOver(mouseX, mouseY);
        }
        IGuiElement cropRelative = cropElement != null ? cropElement : this;
        int posX = cropRelative.getAbsoluteX() - this.getAbsoluteX();
        int posY = cropRelative.getAbsoluteY() - this.getAbsoluteY();
        boolean inCrop = mouseX >= posX && mouseY >= posY && mouseX <= posX + cropWidth && mouseY <= posY + cropHeight;
        return inCrop && isMouseOver(mouseX, mouseY);
    }

    /**
     * Called if this element get updated on the client side.
     */
    @OnlyIn(Dist.CLIENT)
    protected void onUpdateClient() {
        //Default-Implementation
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateClient() {
        if (!this.isVisible()) {
            return;
        }
        this.onUpdateClient();
    }

    @Override
    public boolean isVisible() {
        return visible && (parent == null || parent.isVisible());
    }

    @Override
    public void show() {
        this.visible = true;
    }

    @Override
    public void hide() {
        this.visible = false;
    }

    @Override
    public boolean isEnabled() {
        return parent == null || parent.isEnabled();
    }

    @Nullable
    @Override
    public IGuiElement getParent() {
        return parent;
    }

    @Override
    public IGuiElement setParent(@Nullable IGuiElement parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public ToolTip getTooltip(int mouseX, int mouseY) {
        ToolTip toolTip = new ToolTip();
        tooltipSuppliers.stream().filter(ITooltipSupplier::hasTooltip).forEach(supplier -> supplier.addTooltip(toolTip, this, mouseX, mouseY));
        return toolTip;
    }

    @Override
    public IGuiElement addTooltip(ITextComponent line) {
        //TODO textcomponent
        addTooltip((toolTip, element, mouseX, mouseY) -> toolTip.add(line));
        return this;
    }

    @Override
    public IGuiElement addTooltip(Collection<ITextComponent> lines) {
        //TODO textcomponent
        addTooltip((toolTip, element, mouseX, mouseY) -> toolTip.addAll(lines));
        return this;
    }

    @Override
    public IGuiElement addTooltip(ITooltipSupplier supplier) {
        tooltipSuppliers.add(supplier);
        return this;
    }

    @Override
    public boolean hasTooltip() {
        return !tooltipSuppliers.isEmpty();
    }

    @Override
    public void clearTooltip() {
        tooltipSuppliers.clear();
    }

    @Override
    public ToolTip getTooltip() {
        int mouseX = getWindow().getRelativeMouseX(this);
        int mouseY = getWindow().getRelativeMouseY(this);
        ToolTip toolTip = new ToolTip();
        tooltipSuppliers.stream().filter(ITooltipSupplier::hasTooltip).forEach(supplier -> supplier.addTooltip(toolTip, this, mouseX, mouseY));
        return toolTip;
    }

    /* Events */
    @Override
    public <E extends GuiElementEvent> void addEventHandler(Consumer<E> eventHandler) {
        eventHandlers.add(eventHandler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void receiveEvent(GuiElementEvent event) {
        for (Consumer<? extends GuiElementEvent> eventHandler : eventHandlers) {
            ((Consumer<GuiElementEvent>) eventHandler).accept(event);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("x", getX())
                .add("y", getY())
                .add("w", width)
                .add("h", height)
                .add("a", align)
                .add("v", isVisible())
                .add("xO", xOffset)
                .add("yO", yOffset)
                .toString();
    }
}
