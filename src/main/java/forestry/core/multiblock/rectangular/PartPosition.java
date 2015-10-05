package forestry.core.multiblock.rectangular;

public enum PartPosition {
	Unknown,
	Interior,
	FrameCorner,
	Frame,
	TopFace,
	BottomFace,
	NorthFace,
	SouthFace,
	EastFace,
	WestFace;
	
	public static boolean isFace(PartPosition position) {
		switch (position) {
			case TopFace:
			case BottomFace:
			case NorthFace:
			case SouthFace:
			case EastFace:
			case WestFace:
				return true;
			default:
				return false;
		}
	}
}
