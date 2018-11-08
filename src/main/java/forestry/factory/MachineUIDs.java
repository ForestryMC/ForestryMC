package forestry.factory;

import com.google.common.collect.Sets;

import java.util.Set;

public class MachineUIDs {

	private MachineUIDs() {
	}

	public static final String BOTTLER = "bottler";
	public static final String CARPENTER = "carpenter";
	public static final String CENTRIFUGE = "centrifuge";
	public static final String FABRICATOR = "fabricator";
	public static final String FERMENTER = "fermenter";
	public static final String MOISTENER = "moistener";
	public static final String RAINMAKER = "rainmaker";
	public static final String RAINTANK = "raintank";
	public static final String SQUEEZER = "squeezer";
	public static final String STILL = "still";

	public static final Set<String> ALL = Sets.newHashSet(BOTTLER, CARPENTER, CENTRIFUGE, FABRICATOR,
		FERMENTER, MOISTENER, RAINMAKER, RAINTANK, SQUEEZER, STILL);
}
