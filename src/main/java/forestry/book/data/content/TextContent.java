package forestry.book.data.content;

import javax.annotation.Nullable;

import forestry.api.book.BookContent;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.book.data.TextData;
import forestry.book.gui.elements.TextDataElement;

public class TextContent extends BookContent<TextData> {
	@Override
	public Class<? extends TextData> getDataClass() {
		return TextData.class;
	}

	@Override
	public boolean addElements(IElementGroup group, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement) {
		if(data == null){
			return false;
		}
		TextDataElement element;
		if(previousElement instanceof TextDataElement) {
			element = (TextDataElement) previousElement;
		}else{
			element = new TextDataElement(0, 0, group.getWidth(), 0);
			group.add(element);
		}
		element.addData(data);
		return true;
	}
}
