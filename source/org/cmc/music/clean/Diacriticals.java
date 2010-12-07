/*
 * Written By Charles M. Chen 
 * 
 * Created on Jan 1, 2006
 *
 */

package org.cmc.music.clean;

import java.util.HashMap;
import java.util.Map;

import org.cmc.music.metadata.MusicMetadataConstants;

public abstract class Diacriticals implements MusicMetadataConstants
{

	private static final Map DIACRITICALS = new HashMap();
	static
	{
		DIACRITICALS.put("À", "a");
		DIACRITICALS.put("Á", "a");
		DIACRITICALS.put("Â", "a");
		DIACRITICALS.put("Ã", "a");
		DIACRITICALS.put("Ä", "a");
		DIACRITICALS.put("Å", "a");
		DIACRITICALS.put("Æ", "ae");
		DIACRITICALS.put("Ç", "c");
		DIACRITICALS.put("È", "e");
		DIACRITICALS.put("É", "e");
		DIACRITICALS.put("Ê", "e");
		DIACRITICALS.put("Ë", "e");
		DIACRITICALS.put("Ì", "i");
		DIACRITICALS.put("Í", "i");
		DIACRITICALS.put("Î", "i");
		DIACRITICALS.put("Ï", "i");
		DIACRITICALS.put("Ñ", "n");
		DIACRITICALS.put("Ò", "o");
		DIACRITICALS.put("Ó", "o");
		DIACRITICALS.put("Ô", "o");
		DIACRITICALS.put("Õ", "o");
		DIACRITICALS.put("Ö", "o");
		DIACRITICALS.put("Ø", "o");
		DIACRITICALS.put("Ù", "u");
		DIACRITICALS.put("Ú", "u");
		DIACRITICALS.put("Û", "u");
		DIACRITICALS.put("Ü", "u");
		DIACRITICALS.put("Ý", "y");
		DIACRITICALS.put("à", "a");
		DIACRITICALS.put("á", "a");
		DIACRITICALS.put("â", "a");
		DIACRITICALS.put("ã", "a");
		DIACRITICALS.put("â", "a");
		DIACRITICALS.put("ä", "a");
		DIACRITICALS.put("å", "a");
		DIACRITICALS.put("æ", "ae");
		DIACRITICALS.put("ç", "c");
		DIACRITICALS.put("è", "e");
		DIACRITICALS.put("é", "e");
		DIACRITICALS.put("ê", "e");
		DIACRITICALS.put("ë", "e");
		DIACRITICALS.put("ì", "i");
		DIACRITICALS.put("í", "i");
		DIACRITICALS.put("î", "i");
		DIACRITICALS.put("ï", "i");
		DIACRITICALS.put("ð", "o");
		DIACRITICALS.put("ñ", "n");
		DIACRITICALS.put("ò", "o");
		DIACRITICALS.put("ó", "o");
		DIACRITICALS.put("ô", "o");
		DIACRITICALS.put("õ", "o");
		DIACRITICALS.put("ö", "o");
		DIACRITICALS.put("ø", "o");
		DIACRITICALS.put("ù", "u");
		DIACRITICALS.put("ú", "u");
		DIACRITICALS.put("û", "u");
		DIACRITICALS.put("ü", "u");
		DIACRITICALS.put("ý", "u");
	}

	public static final String convertDiacriticals(String s)
	{
		StringBuffer result = new StringBuffer();

		char chars[] = s.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			String replacement = (String) DIACRITICALS.get("" + c);
			if (null != replacement)
				result.append(replacement);
			else
				result.append(c);
		}

		return result.toString();
	}

}
