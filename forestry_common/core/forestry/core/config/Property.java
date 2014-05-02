/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.config;

public class Property {
	public String Key;
	public String Value;
	public String Comment;

	public Property(String key, String value) {
		Key = key;
		Value = value;
	}
}
