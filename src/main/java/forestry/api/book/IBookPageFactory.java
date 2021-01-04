/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.book;

import forestry.core.gui.elements.lib.IGuiElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;

/**
 * A factory that creates book pages for a book entry.
 */
@OnlyIn(Dist.CLIENT)
public interface IBookPageFactory {
    /**
     * Usually called at the moment the player opens the given entry.
     * Creates a collection of gui elements that represent a page of the book.
     *
     * @param entry           The opened book entry.
     * @param leftPageHeight  The height of a page on the left side of the book.
     * @param rightPageHeight The height of a page on the right side of the book.
     * @param pageWidth       The width of a book page.
     * @return A collection where every member represent a page of the book.
     */
    Collection<IGuiElement> load(IBookEntry entry, int leftPageHeight, int rightPageHeight, int pageWidth);
}
