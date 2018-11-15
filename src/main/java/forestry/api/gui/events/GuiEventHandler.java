package forestry.api.gui.events;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.IGuiElement;

@SideOnly(Side.CLIENT)
public final class GuiEventHandler<E extends GuiElementEvent> implements Consumer<E> {
	private final Consumer<E> handlerAction;
	private final Class<? super E> eventClass;
	private final GuiEventOrigin origin;
	@Nullable
	private final IGuiElement relative;

	public GuiEventHandler(Class<? super E> eventClass, Consumer<E> handlerAction) {
		this.origin = GuiEventOrigin.ANY;
		this.relative = null;
		this.eventClass = eventClass;
		this.handlerAction = handlerAction;
	}

	public GuiEventHandler(Class<? super E> eventClass, GuiEventOrigin origin, IGuiElement relative, Consumer<E> handlerAction) {
		this.origin = origin;
		this.relative = relative;
		this.eventClass = eventClass;
		this.handlerAction = handlerAction;
	}

	private boolean canHandle(GuiElementEvent event) {
		boolean instance = this.eventClass.isInstance(event);
		return instance && this.origin.isOrigin(event.getOrigin(), this.relative);
	}

	@Override
	public final void accept(E e) {
		if (canHandle(e)) {
			handlerAction.accept(e);
		}
	}
}
