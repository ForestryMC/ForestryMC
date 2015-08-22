/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.client.renderer.ItemMeshDefinition;

public interface IModelObject {

	public enum ModelType
	{
		DEFAULT,
		META,
		MESHDEFINITION
	}
	
	ModelType getModelType();
	
}
