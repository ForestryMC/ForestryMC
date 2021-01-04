package forestry.core.gui.elements.lib.events;


import forestry.core.gui.elements.lib.IElementGroup;
import forestry.core.gui.elements.lib.IGuiElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum GuiEventDestination {
    //Only the current element
    SINGLE {
        @Override
        public void sendEvent(IGuiElement element, GuiElementEvent event) {
            element.receiveEvent(event);
        }
    },
    //Only the origin element
    ORIGIN {
        @Override
        public void sendEvent(IGuiElement element, GuiElementEvent event) {
            event.getOrigin().receiveEvent(event);
        }
    },
    //All children elements of the element
    CHILDREN {
        @Override
        public void sendEvent(IGuiElement element, GuiElementEvent event) {
            if (!(element instanceof IElementGroup)) {
                return;
            }
            for (IGuiElement child : ((IElementGroup) element).getElements()) {
                child.receiveEvent(event);
            }
        }
    },
    //The parent element of the element
    PARENT {
        @Override
        public void sendEvent(IGuiElement element, GuiElementEvent event) {
            IGuiElement parent = element.getParent();
            if (parent == null) {
                return;
            }
            parent.receiveEvent(event);
        }
    },
    //The other children of the parent of the element
    SIBLINGS {
        @Override
        public void sendEvent(IGuiElement element, GuiElementEvent event) {
            IGuiElement parent = element.getParent();
            if (parent == null) {
                return;
            }
            parent.postEvent(event, CHILDREN);
        }
    },
    ALL {
        @Override
        public void sendEvent(IGuiElement element, GuiElementEvent event) {
            element.receiveEvent(event);
            if (!(element instanceof IElementGroup)) {
                return;
            }
            for (IGuiElement child : ((IElementGroup) element).getElements()) {
                child.postEvent(event, ALL);
            }
        }
    };

    public abstract void sendEvent(IGuiElement element, GuiElementEvent event);
}
