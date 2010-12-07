/*
 * Created on Apr 7, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.cmc.music.util;

/**
 * @author charles
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.StringCharacterIterator;
import java.util.Vector;

public class TextUtils implements BasicConstants
{
	public static boolean isNumericDecimal(String s)
	{
		return isNumeric(s, false);
	}

	public static boolean isNumericInteger(String s)
	{
		return isNumeric(s, true);
	}

	private static boolean isNumeric(String s, boolean integer)
	{
		if (s == null)
			return false;

		char chars[] = s.toCharArray();
		if (chars.length < 1)
			return false;

		boolean has_period = integer;
		boolean has_digit = false;

		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			if (i == 0 && c == '-')
				;
			else if (Character.isDigit(c))
				has_digit = true;
			else if (c == '.' && !has_period)
			{
				has_period = true;
			} else
				return false;
		}
		return has_digit;
	}

	public static String pad_left(String s, int length, String padding)
	{
		for (int i = 0; i < 10000 && s.length() < length; i++)
			s = padding + s;
		return s;
	}

	public static String toTitleCase(String s)
	{
		StringBuffer result = new StringBuffer();
		char prev = 0;
		if (s.startsWith("The Jackson 5 - I'Ll Be There"))
			System.out.println(s);

		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			// char next = 0;
			// if ((i + 1) < s.length())
			// next = s.charAt(i + 1);

			if (Character.isLetter(c))
			{
				if (i == 0)
					result.append(Character.toUpperCase(c));
				else if ((prev == '\''))
					// else if ((prev == '\'') && Character.isLetter(next))
					result.append(Character.toLowerCase(c));
				else if (!Character.isLetter(prev))
					result.append(Character.toUpperCase(c));
				else
					result.append(Character.toLowerCase(c));
			} else
				result.append(c);
			prev = c;
		}
		return result.toString();
	}

	public static String insertSpacesBeforeCaps(String s)
	{
		StringBuffer result = new StringBuffer();

		char prev = 0;
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);

			if (Character.isLetter(c) && (i > 0) && Character.isLetter(prev)
					&& Character.isLowerCase(prev) && Character.isUpperCase(c))
				result.append(' ');

			result.append(c);

			prev = c;
		}

		return result.toString();
	}

	public static Vector tokenizeString(String s, String token)
	{
		Vector result = new Vector();
		int index;
		while ((s.length() > 0) && ((index = s.indexOf(token)) >= 0))
		{
			String left = s.substring(0, index);
			result.add(left);
			s = s.substring(index + token.length());
		}
		if (s.length() > 0)
			result.add(s);
		return result;
	}

	public static int findFirstWhiteSpace(String s)
	{
		if (s == null)
			return -1;
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (Character.isWhitespace(c))
				return i;
		}
		return -1;
	}

	public static Vector tokenizeByWhiteSpace(String s, boolean trim)
	{
		Vector result = new Vector();
		int index;
		while ((s.length() > 0) && ((index = findFirstWhiteSpace(s)) >= 0))
		{
			String left = s.substring(0, index);
			result.add(left);
			s = s.substring(index + 1);
			if (trim)
				s = s.trim();
		}
		if (s.length() > 0)
			result.add(s);
		return result;
	}

	public static final String ALPHABET_NUMERALS = "0123456789";
	public static final String ALPHABET_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	public static final String ALPHABET_UPPERCASE = ALPHABET_LOWERCASE
			.toUpperCase();
	public static final String ALPHABET = ALPHABET_LOWERCASE
			+ ALPHABET_UPPERCASE;
	public static final String FILENAME_SAFE = ALPHABET + ALPHABET_NUMERALS
			+ " ._-()&,[]'%!";

	public static String toSafeFilename(String s)
	{
		StringBuffer result = new StringBuffer();
		// char prev = 0;

		s = s.trim();

		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);

			if (FILENAME_SAFE.indexOf(c) < 0)
				;
			else
				result.append(c);
		}

		String filtered = result.toString();

		while (filtered.startsWith("."))
			filtered = filtered.substring(1);
		while (filtered.endsWith("."))
			filtered = filtered.substring(0, filtered.length() - 1);

		return filtered;
	}

	public static String filter(String s, String filter)
	{
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);

			if (filter.indexOf(c) >= 0)
				result.append(c);
		}

		return result.toString();
	}

	public static String head(String s, int count)
	{
		if (s == null || s.length() < 1)
			return s;
		String lines[] = TextUtils.split(s, newline);
		if (lines.length < count)
			count = lines.length;
		String lines2[] = new String[count];
		System.arraycopy(lines, 0, lines2, 0, count);
		return TextUtils.join(lines2, newline);
	}

	public static String tail(String s, int count)
	{
		if (s == null || s.length() < 1)
			return s;
		String lines[] = TextUtils.split(s, newline);
		if (lines.length < count)
			count = lines.length;
		String lines2[] = new String[count];
		System.arraycopy(lines, lines.length - count, lines2, 0, count);
		return TextUtils.join(lines2, newline);
	}

	public static int getLineCount(String s)
	{
		if (s == null || s.length() < 1)
			return 0;
		String lines[] = TextUtils.split(s, newline);
		return lines.length;
	}

	/**
	 * Synonym for <tt>URLEncoder.encode(String, "UTF-8")</tt>.
	 * 
	 * <P>
	 * Used to ensure that HTTP query strings are in proper form, by escaping
	 * special characters such as spaces.
	 * 
	 * <P>
	 * An example use case for this method is a login scheme in which, after
	 * successful login, the user is redirected to the "original" target
	 * destination. Such a target might be passed around as a request parameter.
	 * Such a request parameter will have a URL as its value, as in
	 * "LoginTarget=Blah.jsp?this=that&blah=boo", and would need to be
	 * URL-encoded in order to escape its special characters.
	 * 
	 * <P>
	 * It is important to note that if a query string appears in an
	 * <tt>HREF</tt> attribute, then there are two issues - ensuring the query
	 * string is valid HTTP (it is URL-encoded), and ensuring it is valid HTML
	 * (ensuring the ampersand is escaped).
	 */
	public String urlEncode(String aURLFragment)
	{
		String result = null;
		try
		{
			result = URLEncoder.encode(aURLFragment, "UTF-8");
		} catch (UnsupportedEncodingException ex)
		{
			throw new RuntimeException("UTF-8 not supported", ex);
		}
		return result;
	}

	/**
	 * Replace characters having special meaning <em>inside</em> HTML tags with
	 * their escaped equivalents, using character entities such as <tt>'&amp;'</tt>.
	 * 
	 * <P>
	 * The escaped characters are :
	 * <ul>
	 * <li>< <li>>
	 * <li>"
	 * <li>'
	 * <li>\
	 * <li>&
	 * </ul>
	 * 
	 * <P>
	 * This method ensures that arbitrary text appearing inside a tag does not
	 * "confuse" the tag. For example, <tt>HREF='Blah.do?Page=1&Sort=ASC'</tt> does not comply with strict
	 * HTML because of the ampersand, and should be changed to <tt>HREF='Blah.do?Page=1&amp;Sort=ASC'</tt>. This
	 * is commonly seen in building query strings. (In JSTL, the c:url tag
	 * performs this task automatically.)
	 */
	public String escapeHTMLSpecialCharacters(String s)
	{
		final StringBuffer result = new StringBuffer();

		final StringCharacterIterator iterator = new StringCharacterIterator(s);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE)
		{
			if (character == '<')
			{
				result.append("&lt;");
			} else if (character == '>')
			{
				result.append("&gt;");
			} else if (character == '\"')
			{
				result.append("&quot;");
			} else if (character == '\'')
			{
				result.append("&#039;");
			} else if (character == '\\')
			{
				result.append("&#092;");
			} else if (character == '&')
			{
				result.append("&amp;");
			} else
			{
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	/**
	 * Return <tt>aText</tt> with all start-of-tag and end-of-tag characters
	 * replaced by their escaped equivalents.
	 * 
	 * <P>
	 * If user input may contain tags which must be disabled, then call this
	 * method, not {@link #forHTMLTag}. This method is used for text appearing
	 * <em>outside</em> of a tag, while {@link #forHTMLTag} is used for text
	 * appearing <em>inside</em> an HTML tag.
	 * 
	 * <P>
	 * It is not uncommon to see text on a web page presented erroneously,
	 * because <em>all</em> special characters are escaped (as in
	 * {@link #forHTMLTag}). In particular, the ampersand character is often
	 * escaped not once but <em>twice</em> : once when the original input
	 * occurs, and then a second time when the same item is retrieved from the
	 * database. This occurs because the ampersand is the only escaped character
	 * which appears in a character entity.
	 */
	public String toDisableHTMLTags(String aText)
	{
		final StringBuffer result = new StringBuffer();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				aText);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE)
		{
			if (character == '<')
			{
				result.append("&lt;");
			} else if (character == '>')
			{
				result.append("&gt;");
			} else
			{
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	/**
	 * Replace characters having special meaning in regular expressions with
	 * their escaped equivalents.
	 * 
	 * <P>
	 * The escaped characters include :
	 *<ul>
	 *<li>.
	 *<li>\
	 *<li>?, * , and +
	 *<li>&
	 *<li>:
	 *<li>{ and }
	 *<li>[ and ]
	 *<li>( and )
	 *<li>^ and $
	 *</ul>
	 * 
	 */
	public String forRegex(String aRegexFragment)
	{
		final StringBuffer result = new StringBuffer();

		final StringCharacterIterator iterator = new StringCharacterIterator(
				aRegexFragment);
		char character = iterator.current();
		while (character != StringCharacterIterator.DONE)
		{
			/*
			 * All literals need to have backslashes doubled.
			 */
			if (character == '.')
			{
				result.append("\\.");
			} else if (character == '\\')
			{
				result.append("\\\\");
			} else if (character == '?')
			{
				result.append("\\?");
			} else if (character == '*')
			{
				result.append("\\*");
			} else if (character == '+')
			{
				result.append("\\+");
			} else if (character == '&')
			{
				result.append("\\&");
			} else if (character == ':')
			{
				result.append("\\:");
			} else if (character == '{')
			{
				result.append("\\{");
			} else if (character == '}')
			{
				result.append("\\}");
			} else if (character == '[')
			{
				result.append("\\[");
			} else if (character == ']')
			{
				result.append("\\]");
			} else if (character == '(')
			{
				result.append("\\(");
			} else if (character == ')')
			{
				result.append("\\)");
			} else if (character == '^')
			{
				result.append("\\^");
			} else if (character == '$')
			{
				result.append("\\$");
			} else
			{
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	public static final String[] split(String s, char token)
	{
		return split(s, "" + token);
	}

	public static final String[] split(String s, String token)
	{
		// if (s == null)
		// return s;
		//
		Vector result = new Vector();

		int index;
		while ((index = s.indexOf(token)) >= 0)
		{
			result.add(s.substring(0, index));
			s = s.substring(index + token.length());
		}
		result.add(s);

		String splits[] = new String[result.size()];
		for (int i = 0; i < result.size(); i++)
			splits[i] = (String) result.get(i);
		return splits;
	}

	public static final String replace(String s, String find, String replace)
	{
		if (s == null)
			return s;

		StringBuffer result = new StringBuffer();

		int index;
		while ((index = s.indexOf(find)) >= 0)
		{
			result.append(s.substring(0, index));
			result.append(replace);
			s = s.substring(index + find.length());
		}
		result.append(s);

		return result.toString();
	}

	public static final String join(String splits[], String token)
	{
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < splits.length; i++)
		{
			if (i > 0)
				result.append(token);

			result.append(splits[i]);
		}

		return result.toString();
	}
}