/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gadgets;

import net.minecraft.entity.player.EntityPlayer;

import forestry.core.interfaces.IHintSource;
import forestry.core.network.ClassMap;
import forestry.core.network.IndexInPayload;
import forestry.core.network.PacketPayload;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Utils;

public abstract class TileBase extends TileForestry implements IHintSource {

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean isOwnable() {
		return true;
	}

	/* UPDATING */
	@Override
	public void initialize() {
	}

	@Override
	public void updateEntity() {

		super.updateEntity();

		if (!Proxies.common.isSimulating(worldObj))
			updateClientSide();
		else
			updateServerSide();
	}

	protected void updateClientSide() {
	}

	protected void updateServerSide() {
	}

	/* NETWORK */
	@Override
	public PacketPayload getPacketPayload() {
		if (!ClassMap.classMappers.containsKey(this.getClass()))
			ClassMap.classMappers.put(this.getClass(), new ClassMap(this.getClass()));

		ClassMap classmap = ClassMap.classMappers.get(this.getClass());
		PacketPayload payload = new PacketPayload(classmap.intSize, classmap.floatSize, classmap.stringSize);

		try {
			classmap.setData(this, payload.intPayload, payload.floatPayload, payload.stringPayload, new IndexInPayload(0, 0, 0));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return payload;
	}

	@Override
	public void fromPacketPayload(PacketPayload payload) {

		if (payload.isEmpty())
			return;

		if (!ClassMap.classMappers.containsKey(this.getClass()))
			ClassMap.classMappers.put(this.getClass(), new ClassMap(this.getClass()));

		ClassMap classmap = ClassMap.classMappers.get(this.getClass());

		try {
			classmap.fromData(this, payload.intPayload, payload.floatPayload, payload.stringPayload, new IndexInPayload(0, 0, 0));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/* INTERACTION */
	public void openGui(EntityPlayer player, TileBase tile) {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return Utils.isUseableByPlayer(player, this, worldObj, xCoord, yCoord, zCoord);
	}

	public boolean canDrainWithBucket() {
		return false;
	}

	/* IHINTSOURCE */
	private String[] hints;

	public void setHints(String[] hints) {
		this.hints = hints;
	}

	@Override
	public boolean hasHints() {
		return hints != null && hints.length > 0;
	}

	@Override
	public String[] getHints() {
		return hints;
	}
}
