/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import java.util.ArrayList;
import java.util.List;

public class GreenhouseManager {
	
	public static IGreenhouseAccess greenhouseAccess;
	
	public static IGreenhouseHelper greenhouseHelper;
	
	public static List<Class<? extends IGreenhouseLogic>> greenhouseLogics = new ArrayList();

}
