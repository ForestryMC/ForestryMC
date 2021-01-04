package forestry.energy;

public enum EnergyTransferMode {
	EXTRACT, RECEIVE, BOTH, NONE;

	public boolean canExtract() {
		switch (this) {
			case EXTRACT:
			case BOTH:
				return true;
			default:
				return false;
		}
	}

	public boolean canReceive() {
		switch (this) {
			case RECEIVE:
			case BOTH:
				return true;
			default:
				return false;
		}
	}
}
