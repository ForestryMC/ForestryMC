/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class IDAllocator extends WorldSavedData {

	public static String SAVE_NAME = "IDAlloc";

	public static IDAllocator cachedIDAllocator;

	private final int maxId = 2048;

	public static IDAllocator getIDAllocator(World world, String type) {
		if (cachedIDAllocator != null)
			return cachedIDAllocator;

		IDAllocator allocator = (IDAllocator) world.loadItemData(IDAllocator.class, SAVE_NAME + "." + type);
		if (allocator == null) {
			allocator = new IDAllocator(SAVE_NAME + "." + type);
			world.setItemData(SAVE_NAME + type, allocator);
		}

		cachedIDAllocator = allocator;
		return allocator;
	}

	public IDAllocator(String par1Str) {
		super(par1Str);
	}

	public HashMap<String, Integer> idMap = new HashMap<String, Integer>();

	public int getId(String uid) {
		if (idMap.containsKey(uid))
			return idMap.get(uid);

		for (int i = 0; i < maxId; i++)
			if (!idMap.containsValue(i)) {
				idMap.put(uid, i);
				this.markDirty();
				return i;
			}

		throw new RuntimeException("No ids left for the type: " + uid);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		NBTTagList nbttaglist = nbttagcompound.getTagList("IdMap", 10);
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound subcompound = nbttaglist.getCompoundTagAt(i);
			idMap.put(subcompound.getString("UID"), subcompound.getInteger("Id"));
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		NBTTagList nbttaglist = new NBTTagList();
		Iterator<Entry<String, Integer>> it = idMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			NBTTagCompound subcompound = new NBTTagCompound();
			subcompound.setString("UID", entry.getKey());
			subcompound.setInteger("Id", entry.getValue());
			nbttaglist.appendTag(subcompound);
		}
		nbttagcompound.setTag("IdMap", nbttaglist);

	}

}
