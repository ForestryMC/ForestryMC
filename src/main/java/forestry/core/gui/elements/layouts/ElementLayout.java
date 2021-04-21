package forestry.core.gui.elements.layouts;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ElementLayout extends ElementGroup {
	/* The distance between the different elements of this group. */
	public int distance;

	public ElementLayout(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
	}

	public ElementLayout setDistance(int distance) {
		this.distance = distance;
		return this;
	}

	public int getDistance() {
		return distance;
	}

	public int getSize() {
		return elements.size();
	}
}
