package forestry.core.gui.elements.layouts;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.Drawable;
import forestry.core.gui.elements.DrawableElement;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.ItemElement;
import forestry.core.gui.elements.lib.GuiConstants;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import forestry.core.gui.elements.text.LabelElement;

@OnlyIn(Dist.CLIENT)
public class ElementGroup extends GuiElement {
	protected final List<GuiElement> elements = new ArrayList<>();

	public ElementGroup(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
	}

	public <E extends GuiElement> E add(E element) {
		elements.add(element);
		element.setParent(this);
		element.onCreation();
		return element;
	}

	public <E extends GuiElement> E remove(E element) {
		elements.remove(element);
		element.onDeletion();
		return element;
	}

	public ElementGroup add(GuiElement... elements) {
		for (GuiElement element : elements) {
			add(element);
		}
		return this;
	}

	public ElementGroup remove(GuiElement... elements) {
		for (GuiElement element : elements) {
			remove(element);
		}
		return this;
	}

	public ElementGroup add(Collection<GuiElement> elements) {
		elements.forEach(this::add);
		return this;
	}

	public ElementGroup remove(Collection<GuiElement> elements) {
		elements.forEach(this::remove);
		return this;
	}

	public void clear() {
		for (GuiElement element : new ArrayList<>(elements)) {
			remove(element);
		}
	}

	public List<GuiElement> getElements() {
		return elements;
	}

	@Override
	public void drawElement(MatrixStack transform, int mouseX, int mouseY) {
		int mX = mouseY - getX();
		int mY = mouseX - getY();
		elements.forEach(element -> element.draw(transform, mY, mX));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void updateClient() {
		if (!isVisible()) {
			return;
		}
		onUpdateClient();
		for (GuiElement widget : getElements()) {
			widget.updateClient();
		}
	}

	@Nullable
	public GuiElement getLastElement() {
		return elements.isEmpty() ? null : elements.get(elements.size() - 1);
	}

	public DrawableElement drawable(Drawable drawable) {
		return add(new DrawableElement(drawable));
	}

	public DrawableElement drawable(int x, int y, Drawable drawable) {
		return add(new DrawableElement(x, y, drawable));
	}

	public ItemElement item(int xPos, int yPos, ItemStack itemStack) {
		ItemElement element = new ItemElement(xPos, yPos, itemStack);
		add(element);
		return element;
	}

	public Style defaultStyle() {
		return GuiConstants.DEFAULT_STYLE;
	}

	public LabelElement label(ITextComponent component) {
		return new LabelElement.Builder(this::add, component).create();
	}

	public LabelElement label(IReorderingProcessor component) {
		return new LabelElement.Builder(this::add, component).create();
	}

	public LabelElement translated(String key, Object... args) {
		return label(new TranslationTextComponent(key, args));
	}

	public LabelElement.Builder labelLine(IFormattableTextComponent component) {
		return new LabelElement.Builder(this::add, component);
	}

	public LabelElement.Builder translatedLine(String key, Object... args) {
		return labelLine(new TranslationTextComponent(key, args));
	}

	public LabelElement.Builder labelLine(String text) {
		return labelLine(new StringTextComponent(text));
	}

	public LabelElement label(String text) {
		return label(text, defaultStyle());
	}

	public LabelElement label(String text, Style style) {
		return label(text, GuiElementAlignment.TOP_LEFT, style);
	}

	public LabelElement label(String text, GuiElementAlignment align) {
		return label(text, align, defaultStyle());
	}

	public LabelElement label(String text, GuiElementAlignment align, Style textStyle) {
		return label(text, 0, 0, -1, 12, align, textStyle);
	}

	public LabelElement label(String text, int x, int y, int width, int height, GuiElementAlignment align, Style textStyle) {
		return new LabelElement.Builder(this::add, text, (element) -> element.setBounds(x, y, width, height)).fitText().setStyle(textStyle).create();
	}

	public ElementLayout vertical(int xPos, int yPos, int width) {
		return add(new VerticalLayout(xPos, yPos, width));
	}

	public ElementLayout vertical(int width) {
		return add(new VerticalLayout(0, 0, width));
	}

	public ElementLayout horizontal(int height) {
		return horizontal(0, 0, height);
	}

	public ElementGroup pane(int width, int height) {
		return pane(0, 0, width, height);
	}

	public ElementLayout horizontal(int xPos, int yPos, int height) {
		return add(new HorizontalLayout(xPos, yPos, height));
	}

	public ElementGroup pane(int xPos, int yPos, int width, int height) {
		return add(new PaneLayout(xPos, yPos, width, height));
	}

	public ElementLayoutHelper layoutHelper(ElementLayoutHelper.LayoutFactory layoutFactory, int width, int height) {
		return new ElementLayoutHelper(layoutFactory, width, height, this);
	}
}
