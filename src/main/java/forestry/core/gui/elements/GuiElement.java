package forestry.core.gui.elements;

import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;

import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.elements.layouts.ContainerElement;

@OnlyIn(Dist.CLIENT)
public abstract class GuiElement extends AbstractGui {
	public static final int UNKNOWN_HEIGHT = -1;
	public static final int UNKNOWN_WIDTH = -1;
	public static final Dimension UNKNOWN_SIZE = new Dimension(-1, -1);
	/* Attributes - Final */
	//Tooltip of the element
	private final List<ITooltipSupplier> tooltipSuppliers = new ArrayList<>();
	/* Attributes - State*/
	//Element Position
	@Nullable
	protected Point preferredPos;
	/**
	 * The size this widget wants to occupy. If one of the two values is unknown (smaller than zero) the container will
	 * try to dynamically calculate the value based on size of the sibling widgets. (flex)
	 * <p>
	 * This value is only reliable. For the exact values please call {@link #getPreferredSize()}
	 */
	protected Dimension preferredSize = UNKNOWN_SIZE;
	/**
	 * Position and width of this widget in the parent container.
	 * <p>
	 * Null if the layout of the container was not performed yet.
	 */
	@Nullable
	protected Rectangle bounds;
	//The start coordinates of the crop
	protected int cropX;
	protected int cropY;
	protected int cropWidth = -1;
	protected int cropHeight = -1;
	//The element to that the crop coordinates are relative to.
	@Nullable
	protected GuiElement cropElement = null;
	//Element Alignment relative to the parent
	private Alignment align = Alignment.TOP_LEFT;
	protected boolean visible = true;

	//The element container that contains this element
	@Nullable
	protected GuiElement parent;
	protected final ActionConfig actionConfig;

	@Deprecated
	protected GuiElement(int xPos, int yPos) {
		this();
		setPos(xPos, yPos);
	}

	@Deprecated
	protected GuiElement(int xPos, int yPos, int width, int height) {
		this();
		setPreferredBounds(xPos, yPos, width, height);
	}

	protected GuiElement() {
		actionConfig = buildActions(ActionConfig.selfBuilder()).create();
	}

	public final int getX() {
		return bounds != null ? bounds.x : 0;
	}

	public final int getY() {
		return bounds != null ? bounds.y : 0;
	}

	public final int getAbsoluteX() {
		return parent == null ? getX() : getX() + parent.getAbsoluteX();
	}

	public final int getAbsoluteY() {
		return parent == null ? getY() : getY() + parent.getAbsoluteY();
	}

	@SuppressWarnings("deprecation")
	public final void draw(MatrixStack transform, int mouseX, int mouseY) {
		if (!isVisible()) {
			return;
		}
		RenderSystem.pushMatrix();
		RenderSystem.translatef(getX(), getY(), 0.0F);
		if (isCropped()) {
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			Minecraft mc = Minecraft.getInstance();
			//TODO - resolution stuff again, check gameSettings.guiscale too
			MainWindow window = mc.getWindow();
			double scaleWidth = ((double) window.getScreenWidth()) / window.getGuiScaledWidth();
			double scaleHeight = ((double) window.getScreenHeight()) / window.getGuiScaledHeight();
			GuiElement cropRelative = cropElement != null ? cropElement : this;
			int posX = cropRelative.getAbsoluteX();
			int posY = cropRelative.getAbsoluteY();
			GL11.glScissor((int) ((posX + cropX) * scaleWidth), (int) (window.getScreenHeight() - ((posY + cropY + cropHeight) * scaleHeight)), (int) (cropWidth * scaleWidth), (int) (cropHeight * scaleHeight));
		}

		drawElement(transform, mouseX, mouseY);

		if (isCropped()) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}

		RenderSystem.popMatrix();
	}

	protected void drawElement(MatrixStack transform, int mouseX, int mouseY) {
		//Default-Implementation
	}

	/**
	 * Called after all elements of the parent were laid out
	 */
	public void afterLayout() {

	}

	public int getWidth() {
		if (bounds != null) {
			return bounds.width;
		}
		return preferredSize.width;
	}

	public int getHeight() {
		if (bounds != null) {
			return bounds.height;
		}
		return preferredSize.height;
	}

	public Dimension getLayoutSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	@Nullable
	public Point getPreferredPos() {
		return preferredPos;
	}

	@Nullable
	public Rectangle getBounds() {
		return bounds;
	}

	public void setHeight(int height) {
		setSize(preferredSize.width, height);
	}

	public void setWidth(int width) {
		setSize(width, preferredSize.height);
	}

	public GuiElement setSize(int width, int height) {
		this.preferredSize = new Dimension(width, height);
		return this;
	}

	public GuiElement setXPosition(int xPos) {
		setLocation(xPos, preferredPos != null ? preferredPos.y : 0);
		return this;
	}

	public GuiElement setYPosition(int yPos) {
		setLocation(preferredPos != null ? preferredPos.x : 0, yPos);
		return this;
	}

	public GuiElement setLocation(int xPos, int yPos) {
		this.preferredPos = new Point(xPos, yPos);
		requestLayout();
		return this;
	}

	public GuiElement setPreferredBounds(int xPos, int yPos, int width, int height) {
		setLocation(xPos, yPos);
		setSize(width, height);
		return this;
	}

	public void setAssignedBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public GuiElement setAlign(Alignment align) {
		this.align = align;
		return this;
	}

	public Alignment getAlign() {
		return align;
	}

	/* CROPPED */
	public void setCroppedZone(@Nullable GuiElement cropElement, int cropX, int cropY, int cropWidth, int cropHeight) {
		this.cropElement = cropElement;
		this.cropX = cropX;
		this.cropY = cropY;
		this.cropWidth = cropWidth;
		this.cropHeight = cropHeight;
	}

	@Nullable
	public GuiElement getCropElement() {
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

	public Window getWindow() {
		if (this.parent == null) {
			throw new IllegalStateException("Tried to access the window element of an element that doesn't had one.");
		} else {
			return this.parent.getWindow();
		}
	}

	public boolean isMouseOver(double mouseX, double mouseY) {
		if (!isVisible()) {
			return false;
		}
		return mouseX >= 0 && mouseX < getWidth() && mouseY >= 0 && mouseY < getHeight();
	}

	public final boolean isMouseOver() {
		Window window = getWindow();
		int mouseX = window.getRelativeMouseX(this);
		int mouseY = window.getRelativeMouseY(this);
		if (!isCropped()) {
			return isMouseOver(mouseX, mouseY);
		}
		GuiElement cropRelative = cropElement != null ? cropElement : this;
		int posX = cropRelative.getAbsoluteX() - this.getAbsoluteX();
		int posY = cropRelative.getAbsoluteY() - this.getAbsoluteY();
		boolean inCrop = mouseX >= posX && mouseY >= posY && mouseX <= posX + cropWidth && mouseY <= posY + cropHeight;
		return inCrop && isMouseOver(mouseX, mouseY);
	}

	/**
	 * Called if this element get updated on the client side.
	 */
	protected void onUpdateClient() {
		//Default-Implementation
	}

	public void updateClient() {
		if (!this.isVisible()) {
			return;
		}
		this.onUpdateClient();
	}

	public boolean isVisible() {
		return visible && (parent == null || parent.isVisible());
	}

	public void show() {
		this.visible = true;
	}

	public void hide() {
		this.visible = false;
	}

	public boolean isEnabled() {
		return parent == null || parent.isEnabled();
	}

	public boolean canMouseOver() {
		return hasTooltip();
	}

	public boolean canFocus() {
		return false;
	}

	@Nullable
	public GuiElement getParent() {
		return parent;
	}

	public GuiElement setParent(@Nullable GuiElement parent) {
		this.parent = parent;
		return this;
	}

	public ToolTip getTooltip(int mouseX, int mouseY) {
		ToolTip toolTip = new ToolTip();
		tooltipSuppliers.stream().filter(ITooltipSupplier::hasTooltip).forEach(supplier -> supplier.addTooltip(toolTip, this, mouseX, mouseY));
		return toolTip;
	}

	public GuiElement addTooltip(ITextComponent line) {
		addTooltip((toolTip, element, mouseX, mouseY) -> toolTip.add(line));
		return this;
	}

	public GuiElement addTooltip(Collection<ITextComponent> lines) {
		addTooltip((toolTip, element, mouseX, mouseY) -> toolTip.addAll(lines));
		return this;
	}

	public GuiElement addTooltip(ITooltipSupplier supplier) {
		tooltipSuppliers.add(supplier);
		return this;
	}

	public boolean hasTooltip() {
		return !tooltipSuppliers.isEmpty();
	}

	public void clearTooltip() {
		tooltipSuppliers.clear();
	}

	public ToolTip getTooltip() {
		int mouseX = getWindow().getRelativeMouseX(this);
		int mouseY = getWindow().getRelativeMouseY(this);
		ToolTip toolTip = new ToolTip();
		tooltipSuppliers.stream().filter(ITooltipSupplier::hasTooltip).forEach(supplier -> supplier.addTooltip(toolTip, this, mouseX, mouseY));
		return toolTip;
	}

	public ActionConfig getActionConfig() {
		return actionConfig;
	}

	public boolean hasOrigin(ActionType type, ActionOrigin origin) {
		return actionConfig.has(type, origin)
				|| origin == ActionOrigin.SELF_TOP && actionConfig.has(type, ActionOrigin.SELF)
				|| (origin == ActionOrigin.SELF || origin == ActionOrigin.SELF_TOP) && actionConfig.has(type, ActionOrigin.ALL);
	}

	protected ActionConfig.Builder buildActions(ActionConfig.Builder builder) {
		return builder;
	}

	public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
		return false;
	}

	public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton) {
		return false;
	}

	public boolean onMouseScrolled(double mouseX, double mouseY, double dWheel) {
		return false;
	}

	public void onMouseMove(double mouseX, double mouseY) {
	}

	public void onMouseEnter(double mouseX, double mouseY) {
	}


	public void onMouseLeave(double mouseX, double mouseY) {
	}

	public boolean onMouseDrag(double mouseX, double mouseY) {
		return false;
	}

	public void onDragStart(double mouseX, double mouseY) {
	}

	public void onDragEnd(double mouseX, double mouseY) {
	}

	public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	public boolean onCharTyped(char keyCode, int modifiers) {
		return false;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("bounds", bounds)
				.add("a", align)
				.add("v", isVisible())
				.toString();
	}

	public GuiElement setPos(int x, int y) {
		preferredPos = new Point(x, y);
		requestLayout();
		return this;
	}

	/**
	 * Request an layout in the next render cycle by marking the parent dirty if there is any
	 */
	public void requestLayout() {
		if (!(parent instanceof ContainerElement)) {
			return;
		}
		((ContainerElement) parent).markDirty();
	}
}
