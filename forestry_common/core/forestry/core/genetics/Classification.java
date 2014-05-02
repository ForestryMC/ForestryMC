/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import java.util.ArrayList;
import java.util.Locale;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.core.utils.StringUtil;

public class Classification implements IClassification {

	private EnumClassLevel level;
	private String uid;
	private String scientific;

	private IClassification parent;

	private ArrayList<IAlleleSpecies> members = new ArrayList<IAlleleSpecies>();
	private ArrayList<IClassification> groups = new ArrayList<IClassification>();

	public Classification(EnumClassLevel level, String uid, String scientific) {
		this.level = level;
		this.uid = level.name().toLowerCase(Locale.ENGLISH) + "." + uid;
		this.scientific = scientific;
		AlleleManager.alleleRegistry.registerClassification(this);
	}

	@Override
	public EnumClassLevel getLevel() {
		return level;
	}

	@Override
	public String getUID() {
		return uid;
	}

	@Override
	public IClassification getParent() {
		return parent;
	}

	@Override
	public void setParent(IClassification parent) {
		this.parent = parent;
	}

	@Override
	public String getScientific() {
		return this.scientific;
	}

	@Override
	public String getName() {
		return StringUtil.localize(uid);
	}

	@Override
	public String getDescription() {
		return StringUtil.localize(uid + ".description");
	}

	@Override
	public IClassification[] getMemberGroups() {
		return groups.toArray(new IClassification[0]);
	}

	@Override
	public void addMemberGroup(IClassification group) {
		groups.add(group);
		group.setParent(this);
	}

	@Override
	public IAlleleSpecies[] getMemberSpecies() {
		return members.toArray(new IAlleleSpecies[0]);
	}

	@Override
	public void addMemberSpecies(IAlleleSpecies species) {
		members.add(species);
	}

}
