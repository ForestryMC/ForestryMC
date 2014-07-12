/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.network;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import forestry.core.gadgets.TileForestry;

public class ClassMap {

	@SuppressWarnings("rawtypes")
	public static HashMap<Class, ClassMap> classMappers = new HashMap<Class, ClassMap>();

	private final LinkedList<Field> intMember = new LinkedList<Field>();
	private final LinkedList<Field> floatMember = new LinkedList<Field>();
	private final LinkedList<Field> booleanMember = new LinkedList<Field>();
	private final LinkedList<Field> stringMember = new LinkedList<Field>();
	private final LinkedList<Field> enumMember = new LinkedList<Field>();
	private final LinkedList<Field> gameProfileMember = new LinkedList<Field>();
	private final LinkedList<ClassMap> objectMember = new LinkedList<ClassMap>();

	public int intSize;
	public int floatSize;
	public int stringSize;

	private Field field;

	/**
	 * Writes the actual data to the packet payload
	 * 
	 * @param obj
	 *            {@link TileForestry} to write to packet
	 * @param intPayload
	 * @param floatPayload
	 * @param stringPayload
	 * @param index
	 */
	@SuppressWarnings("rawtypes")
	public void setData(Object obj, int[] intPayload, float[] floatPayload, String[] stringPayload, IndexInPayload index) throws IllegalAccessException {
		for (Field member : intMember) {
			intPayload[index.intIndex] = member.getInt(obj);
			index.intIndex++;
		}
		for (Field member : floatMember) {
			floatPayload[index.floatIndex] = member.getFloat(obj);
			index.floatIndex++;
		}
		for (Field member : booleanMember) {
			if (member.getBoolean(obj))
				floatPayload[index.intIndex] = 1;
			else
				floatPayload[index.intIndex] = 0;
			index.intIndex++;
		}
		for (Field member : stringMember) {
			stringPayload[index.stringIndex] = (String) member.get(obj);
			index.stringIndex++;
		}
		for (Field member : enumMember) {
			intPayload[index.intIndex] = ((Enum) member.get(obj)).ordinal();
			index.intIndex++;
		}
		for (Field member : gameProfileMember) {
			GameProfile profile = (GameProfile) member.get(obj);
			intPayload[index.intIndex] = (int) (profile.getId().getMostSignificantBits() >>> 32);
			intPayload[index.intIndex + 1] = (int) profile.getId().getMostSignificantBits();
			intPayload[index.intIndex + 2] = (int) (profile.getId().getLeastSignificantBits() >>> 32);
			intPayload[index.intIndex + 3] = (int) profile.getId().getLeastSignificantBits();
			index.intIndex += 4;
			stringPayload[index.stringIndex] = profile.getName();
			index.stringIndex++;
		}

		// Handle subobjects
		for (ClassMap submap : objectMember) {
			Object source = submap.field.get(obj);

			if (source == null) {
				intPayload[index.intIndex] = 0;
				index.intIndex++;

				index.intIndex += submap.intSize;
				index.floatIndex += submap.floatSize;
				index.stringIndex += submap.stringSize;
			} else {
				intPayload[index.intIndex] = 1;
				index.intIndex++;
				submap.setData(source, intPayload, floatPayload, stringPayload, index);
			}
		}
	}

	/**
	 * Updates the given object with the data from the passed payload arrays
	 * 
	 * @param obj
	 * @param intPayload
	 * @param floatPayload
	 * @param stringPayload
	 * @param index
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("rawtypes")
	public void fromData(Object obj, int[] intPayload, float[] floatPayload, String[] stringPayload, IndexInPayload index) throws IllegalAccessException {

		for (Field member : intMember) {
			member.setInt(obj, intPayload[index.intIndex]);
			index.intIndex++;
		}

		for (Field member : booleanMember) {
			member.setBoolean(obj, intPayload[index.intIndex] == 1);
			index.intIndex++;
		}

		for (Field member : enumMember) {
			member.set(obj, ((Class) member.getGenericType()).getEnumConstants()[intPayload[index.intIndex]]);
			index.intIndex++;
		}

		for (Field member : floatMember) {
			member.setFloat(obj, floatPayload[index.floatIndex]);
			index.floatIndex++;
		}
		for (Field member : stringMember) {
			member.set(obj, stringPayload[index.stringIndex]);
			index.stringIndex++;
		}
		for (Field member : gameProfileMember) {
			GameProfile profile = new GameProfile(new UUID((long) intPayload[index.intIndex] << 32 | intPayload[index.intIndex + 1],
					(long) intPayload[index.intIndex + 2] << 32 | intPayload[index.intIndex + 3]),
					stringPayload[index.stringIndex]);
			index.intIndex += 4;
			index.stringIndex++;

			member.set(obj, profile);
		}

		for (ClassMap map : objectMember) {
			boolean isNull = intPayload[index.intIndex] == 0;
			index.intIndex++;

			if (isNull) {
				index.intIndex += map.intSize;
				index.floatIndex += map.floatSize;
				index.stringIndex += map.stringSize;
			} else {
				map.field.get(obj);
				map.fromData(map.field.get(obj), intPayload, floatPayload, stringPayload, index);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public ClassMap(final Class targetClass) {
		Field[] fields = targetClass.getFields();

		try {
			for (Field field : fields) {
				// We only handle fields marked with EntityNetData
				if (!isNetworkedMember(field))
					continue;

				Type type = field.getGenericType();

				if (type instanceof Class && !((Class) type).isArray()) {
					Class memberClass = (Class) type;
					if (memberClass.equals(int.class)) {
						intSize++;
						intMember.add(field);
					} else if (memberClass.equals(float.class)) {
						floatSize++;
						floatMember.add(field);
					} else if (memberClass.equals(boolean.class)) {
						intSize++;
						booleanMember.add(field);
					} else if (memberClass.equals(String.class)) {
						stringSize++;
						stringMember.add(field);
					} else if (Enum.class.isAssignableFrom(memberClass)) {
						intSize++;
						enumMember.add(field);
					} else if (GameProfile.class.isAssignableFrom(memberClass)) {
						intSize += 4;
						stringSize++;
						gameProfileMember.add(field);
					} else {
						// If we are none of the above we assume to be another
						// mapable object
						// Might not be true and fail!
						ClassMap mapper = new ClassMap(memberClass);
						mapper.field = field;

						objectMember.add(mapper);
						intSize++;

						intSize += mapper.intSize;
						floatSize += mapper.floatSize;
						stringSize += mapper.stringSize;
					}
				} else if (type instanceof Class && ((Class) type).isArray())
					// We don't handle arrays currently. Throw exception
					throw new RuntimeException("Tried to map class " + targetClass.toString() + " but it requested mapping of an array. Not handled!");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private boolean isNetworkedMember(Field member) {
		return member.getAnnotation(EntityNetData.class) != null;
	}

}
