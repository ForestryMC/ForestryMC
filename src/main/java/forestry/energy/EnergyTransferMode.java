package forestry.energy;

public enum EnergyTransferMode {
	EXTRACT, RECEIVE, BOTH, NONE;

	public boolean canExtract() {
		return switch (this) {
			case EXTRACT, BOTH -> true;
			default -> false;
		};
	}

	public boolean canReceive() {
		return switch (this) {
			case RECEIVE, BOTH -> true;
			default -> false;
		};
	}
}
