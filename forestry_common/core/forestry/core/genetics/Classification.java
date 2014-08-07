/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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
