package forestry.core.gadgets;

import forestry.core.config.Defaults;
import net.minecraft.util.IStringSerializable;

public enum MachineDefinitionTypes implements IStringSerializable {

	ANALYZER(Defaults.DEFINITION_CORE_ID, Defaults.DEFINITION_ANALYZER_META),
	ESCRITOIRE(Defaults.DEFINITION_CORE_ID, Defaults.DEFINITION_ESCRITOIRE_META),
	BOTTLER(Defaults.DEFINITION_FACTORY_TESR_ID, Defaults.DEFINITION_BOTTLER_META),
	CARPENTER(Defaults.DEFINITION_FACTORY_TESR_ID, Defaults.DEFINITION_CARPENTER_META),
	CENTRIFUGE(Defaults.DEFINITION_FACTORY_TESR_ID, Defaults.DEFINITION_CENTRIFUGE_META),
	FERMENTER(Defaults.DEFINITION_FACTORY_TESR_ID, Defaults.DEFINITION_FERMENTER_META),
	MOISTENER(Defaults.DEFINITION_FACTORY_TESR_ID, Defaults.DEFINITION_MOISTENER_META),
	SQUEEZER(Defaults.DEFINITION_FACTORY_TESR_ID, Defaults.DEFINITION_SQUEEZER_META),
	STILL(Defaults.DEFINITION_FACTORY_TESR_ID, Defaults.DEFINITION_STILL_META),
	RAINMAKER(Defaults.DEFINITION_FACTORY_TESR_ID, Defaults.DEFINITION_RAINMAKER_META),
	FABRICATOR(Defaults.DEFINITION_FACTORY_Plain_ID, Defaults.DEFINITION_FABRICATOR_META),
	RAINTANK(Defaults.DEFINITION_FACTORY_Plain_ID, Defaults.DEFINITION_RAINTANK_META),
	WORKTABLE(Defaults.DEFINITION_FACTORY_Plain_ID, Defaults.DEFINITION_WORKTABLE_META),
	MAILBOX(Defaults.DEFINITION_MAIL_ID, Defaults.DEFINITION_MAILBOX_META),
	TRADESTATION(Defaults.DEFINITION_MAIL_ID, Defaults.DEFINITION_TRADESTATION_META),
	PHILATELIST(Defaults.DEFINITION_MAIL_ID, Defaults.DEFINITION_PHILATELIST_META),
	CHEST_LEPIDOPTEROLOGY(Defaults.DEFINITION_LEPIDOPTEROLOGY_ID, Defaults.DEFINITION_LEPICHEST_META),
	ENGINE_COPPER(Defaults.DEFINITION_ENERGY_ID, Defaults.DEFINITION_ENGINECOPPER_META),
	ENGINE_TIN(Defaults.DEFINITION_ENERGY_ID, Defaults.DEFINITION_ENGINETIN_META),
	ENGINE_BRONZE(Defaults.DEFINITION_ENERGY_ID, Defaults.DEFINITION_ENGINEBRONZE_META),
	ENGINE_CLOCKWORK(Defaults.DEFINITION_ENERGY_ID, Defaults.DEFINITION_ENGINECLOCKWORK_META),
	GENERATOR(Defaults.DEFINITION_ENERGY_ID, Defaults.DEFINITION_GENERATOR_META),
	APAIRY(Defaults.DEFINITION_APICULTURE_ID, Defaults.DEFINITION_APIARY_META),
	CHEST_APICULTURE_LEGACY(Defaults.DEFINITION_APICULTURE_ID, Defaults.DEFINITION_APIARISTCHEST_LEGACY_META),
	BEEHOUSE(Defaults.DEFINITION_APICULTURE_ID, Defaults.DEFINITION_BEEHOUSE_META),
	CHEST_APICULTURE(Defaults.DEFINITION_APICULTURE_CHEST_ID, Defaults.DEFINITION_APIARISTCHEST_META),
	CHEST_ARBORICULTURE(Defaults.DEFINITION_ARBORICULTURE_ID, Defaults.DEFINITION_ARBCHEST_META);
	
	private MachineDefinitionTypes(int definitionID, int meta) {
		this.meta = meta;
		this.definitionID = definitionID;
	}

	private final int meta;
	private final int definitionID;
	
	@Override
	public String getName() {
		return name().toLowerCase();
	}
	
	public int getDefinitionID() {
		return definitionID;
	}

	public int getMeta() {
		return meta;
	}
	
	public static MachineDefinitionTypes getType(int definitionID, int meta)
	{
		for(MachineDefinitionTypes type : values())
		{
			if(type.getDefinitionID() == definitionID && type.getMeta() == meta)
				return type;
		}
		return null;
	}
	
}
