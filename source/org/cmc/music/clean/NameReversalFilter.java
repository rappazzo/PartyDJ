/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.cmc.music.clean;

import java.io.File;

public class NameReversalFilter extends RenamingFileFilter
{

	protected String getNewName2(File file, String s)
	{
		if (!file.isFile())
			return null;

		int first_index = s.indexOf('-');
		int last_index = s.lastIndexOf('-');
		if (first_index < 0)
		{
			System.out.println("---------------------------------------------");
			System.out.println("\tNo hyphen, skipping: '" + s + "'");
		} else if (first_index != last_index)
		{
			System.out.println("---------------------------------------------");
			System.out.println("\tmore than one hyphen, skipping: '" + s + "'");
		} else
		{
			String left = s.substring(0, first_index);
			String right = s.substring(first_index + 1);
			String new_s = right + " - " + left;
			System.out.println("'" + s + "' -> '" + new_s + "'");
			return new_s;
		}

		return null;
	}

}
