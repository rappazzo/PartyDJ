/*
 * Written By Charles M. Chen
 * 
 * Created on Jan 1, 2006
 *
 */

package org.cmc.music.clean;

import java.util.*;
import org.cmc.music.metadata.*;
import org.cmc.music.util.*;
import com.sun.org.apache.regexp.internal.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.*;

public class MetadataCleanup implements MusicMetadataConstants
{

	private static final String DEFAULTS[] = { "album", //
			"artist", //
			"title", //
			"no title", //
			"no artist", //
			"undefined", //
			"va", //
			"mp3", //
			"cd", //
			"genre", //
			"unknown", //
			"name", //
			"n/a", //
			"Untitled", //
	};
	static
	{
		Arrays.sort(DEFAULTS, MyComparator.kToStringLengthReverse);
	}
	private static final String DEFAULTS_VA[] = { "Compilation", //
			"V.A", //
			"V.A.", //
			"V. A.", //
			"V. A", //
			"V/A", //
			"Va", //
			"V A", //
			"Various Artists", //
			"Various", //
			"Varioius", //
			"Varied Artists", //
			"Varias", //
			"Varios Interpretes", //
			"Varios", //
			"Various Artist", //
			"Various Artistses", //
			"Various Artits", //
			"Various Artisis", //
			"Various Aritsts", //
			"Varius Artists", //
			"Various Composers", //
			"Various djs", //
	};
	static
	{
		Arrays.sort(DEFAULTS_VA, MyComparator.kToStringLengthReverse);
	}
	private static final String DEFAULTS_SOUNDTRACK[] = {
			"The Motion Picture".toLowerCase(), //
			"Motion Picture".toLowerCase(), //
			"Original Motion Picture".toLowerCase(), //
			"Original Motion Picture Soundtrack".toLowerCase(), //
			"The Soundtrack".toLowerCase(), //
			"Music From The Motion Picture".toLowerCase(), //
			"Original Soundtrack Recording".toLowerCase(), //
			"Trilha Sonora Original".toLowerCase(), //
			"ost", //
			"original soundtrack", //
			"soundtrack", //
			"Music From The Motion Picture Soundtrack", //
	};
	static
	{
		Arrays.sort(DEFAULTS_SOUNDTRACK, MyComparator.kToStringLengthReverse);
	}
	private static final String DEFAULT_ACAPELLA = "Ac+ap+el+as?".toLowerCase();

	public String rectifyGeneric(String s)
	{
		return rectifyGeneric(s, null);
	}

	public String rectifyGeneric(String s, IMusicMetadata flags)
	{
		String old = s;
		while (true)
		{
			s = rectifyGeneric_1(s, flags);
			s = removeQuotes(s);
			if (s == null)
				return null;
			if (s.equals(old))
				return s;
			old = s;
		}
	}

	private String removeQuotes(String s)
	{
		if (s == null)
			return null;

		if (java.util.regex.Pattern.compile("^\".+\"$").matcher(s).matches() || java.util.regex.Pattern.compile("^'.+'$").matcher(s).matches()
				|| java.util.regex.Pattern.compile("^\\{.+\\}$").matcher(s).matches()
				|| java.util.regex.Pattern.compile("^\\(.+\\)$").matcher(s).matches() || java.util.regex.Pattern.compile("^<.+>$").matcher(s).matches()
				|| java.util.regex.Pattern.compile("^\\[.+\\]$").matcher(s).matches())
		{
			s = s.substring(1, s.length() - 1);
		}

		return s;
	}

	private static final String ROMAN_NUMERALS = "ivx";

	private boolean isRomanNumeral(String s)
	{
		char chars[] = s.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			if (ROMAN_NUMERALS.indexOf(c) < 0
					&& ROMAN_NUMERALS.toUpperCase().indexOf(c) < 0)
				return false;
		}
		return true;
	}

	private static final Map NATURAL_NUMBERS = new MyMap();
	static
	{
		NATURAL_NUMBERS.put("zero", new Integer(0));
		NATURAL_NUMBERS.put("one", new Integer(1));
		NATURAL_NUMBERS.put("two", new Integer(2));
		NATURAL_NUMBERS.put("three", new Integer(3));
		NATURAL_NUMBERS.put("four", new Integer(4));
		NATURAL_NUMBERS.put("five", new Integer(5));
		NATURAL_NUMBERS.put("six", new Integer(6));
		NATURAL_NUMBERS.put("seven", new Integer(7));
		NATURAL_NUMBERS.put("eight", new Integer(8));
		NATURAL_NUMBERS.put("nine", new Integer(9));
		NATURAL_NUMBERS.put("ten", new Integer(10));
		NATURAL_NUMBERS.put("eleven", new Integer(11));
		NATURAL_NUMBERS.put("twelve", new Integer(12));
		NATURAL_NUMBERS.put("thirteen", new Integer(13));
		NATURAL_NUMBERS.put("fourteen", new Integer(14));
		NATURAL_NUMBERS.put("fifteen", new Integer(15));
		NATURAL_NUMBERS.put("sixteen", new Integer(16));
		NATURAL_NUMBERS.put("seventeen", new Integer(17));
		NATURAL_NUMBERS.put("eighteen", new Integer(18));
		NATURAL_NUMBERS.put("nineteen", new Integer(19));
		NATURAL_NUMBERS.put("twenty", new Integer(20));
	}

	private Number parseNumber(String s)
	{
		if (s == null)
			return null;
		s = s.trim();
		if (s.length() < 1)
			return null;

		try
		{
			return Integer.valueOf(s.trim());
		} catch (Throwable e)
		{
			// Debug.debug(s, e.getMessage());
		}
		Number value = (Number) NATURAL_NUMBERS.get(s.toLowerCase());
		return value;
	}

	private String clean(String s, IMusicMetadata flags)
	{
		// String old_s = s;
		s = s.trim();

		s = Diacriticals.convertDiacriticals(s);

		while (s.startsWith("-"))
			s = s.substring(1);

		s = removeSafePrefixSuffix(s, DEFAULTS);
		if (s == null)
			return null;

		{
			// Debug.debug("!x! considering disc: '" + s + "'");

			final String DISC_REGEXS[] = {
					"[-\\(\\[] ?dis[ck] ?([a-zA-Z\\d]+)[\\)\\]]?$", //
					"[-\\(\\[] ?cd ?([a-zA-Z\\d]+)[\\)\\]]?$", //
					"^[\\(\\[]?dis[ck] ?([a-zA-Z\\d]+) ?[-\\)\\]]", //
					"^[\\(\\[]?cd ?([a-zA-Z\\d]+) ?[-\\)\\]]", //
					"^dis[ck] ?([a-zA-Z\\d]+)$", //
					"^cd[\\. \\-]*([a-zA-Z\\d]+)$", //
			};

			for (int i = 0; i < DISC_REGEXS.length; i++)
			{
				String kDISC_REGEX = DISC_REGEXS[i];

				RE re = new RE(kDISC_REGEX);
				if (re.match(s.toLowerCase()))
				{

					if (re.getParenCount() < 2)
					{
						Debug.debug("Disc missing number", s);
						Debug.dumpStack(3);
						continue;
					}
					int start = re.getParenStart(0);
					int end = re.getParenEnd(0);
					String value = re.getParen(1);
					// Debug.debug("value", value);
					Number number = parseNumber(value);
					if (number == null)
					{
						Debug.debug("Disc missing value", value);
						Debug.dumpStack(3);
						continue;
					}
					// Debug.debug("number", number);

					String fixed = s.substring(0, start) + s.substring(end);
					// Debug.debug("fixed", fixed);
					// Debug.debug("start", start);
					// Debug.debug("end", end);
					if (flags != null)
						flags.setDiscNumber(number);
					s = fixed.trim();
					// return null;
				}
			}

		}

		String suffixes[] = { " ", //
				"-", //
				".Mp3", //
				" Mp3", //
		};
		s = removeSuffixes(s, suffixes);
		String prefixes[] = { " ", //
				"-", //
		};
		s = removePrefixes(s, prefixes);

		// {
		// final String suffixes[] = {
		// " ", ".", "-", "Gabba Cc", "GABBA_CC", "G A B B A C C",
		// "mp3-link", "Mp3 - Link", ".mp3",
		// };
		//
		// s = this.removeSuffixes(s, suffixes);
		// }

		// Debug.debug("clean 2", s);

		s = s.replace('_', ' ');
		s = TextUtils.replace(s, "-", " - ");
		s = TextUtils.replace(s, "`", "'");
		s = TextUtils.replace(s, "’", "'");
		s = TextUtils.replace(s, "´", "'");
		s = TextUtils.replace(s, "[", "(");
		s = TextUtils.replace(s, "]", ")");
		s = TextUtils.replace(s, "(", " (");
		s = TextUtils.replace(s, "~", "-");
		s = TextUtils.replace(s, "  ", " ");
		s = TextUtils.replace(s, "  ", " ");
		s = TextUtils.replace(s, "..", ".");
		s = TextUtils.replace(s, "--", "-");
		s = TextUtils.replace(s, "- -", "-");

		s = TextUtils.replace(s, "#", "No. ");

		{
			String old = s;

			// Debug.debug("s1", s);
			s = removeSafePrefixSuffix(s, DEFAULTS_SOUNDTRACK, true);
			// Debug.debug("s2", s);

			if (s == null || !s.equals(old))
			{
				if (flags != null)
					flags.setIsSoundtrack(Boolean.TRUE);
			}
			if (s == null)
				return null;
		}

		{
			String temp = removeSafePrefixSuffix(s, DEFAULT_ACAPELLA, true);
			// String temp = removeSafePrefixSuffix(s, kDEFAULTS_acapella);
			// Debug.debug("s2", s);

			if (temp == null || !s.equals(temp))
			{
				if (flags != null)
					flags.setIsAcapella(Boolean.TRUE);
			}
		}

		// Debug.debug("clean 3", s);

		{
			String old = s;

			s = removeSafePrefixSuffix(s, DEFAULTS_VA, true);

			if (s == null || !s.equals(old))
			{
				if (flags != null)
					flags.setIsCompilation(Boolean.TRUE);
			}
			if (s == null)
				return null;
		}

		{
			String splits[] = TextUtils.split(s, " ");
			for (int i = 0; i < splits.length; i++)
			{
				if (isRomanNumeral(splits[i]))
					splits[i] = splits[i].toUpperCase();
			}
			s = TextUtils.join(splits, " ");
		}

		s = s.trim();

		// Debug.debug("clean 4", s);

		// if (s == null)
		// return null;

		// s = insertSpacesBeforeCaps(s);
		s = toTitleCase(s);

		return s;
	}

	private String toTitleCase(String s)
	{
		StringBuffer result = new StringBuffer();
		char prev = 0;

		// Debug.debug("toTitleCase before", s);
		char chars[] = s.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];

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

		// Debug.debug("toTitleCase after", s);

		return result.toString();
	}

	private String insertSpacesBeforeCaps(String s)
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

	private String rectifyGeneric_1(String s, IMusicMetadata flags)
	{
		// Debug.debug("rectifyGeneric_1 a", s);
		// Debug.debug("rectifyGeneric_1 1", s);

		if (s == null)
			return null;

		s = s.trim();
		if (s.length() < 1)

			return null;
		// s = new MusicOrganizerFilter().getNewName2(s);
		s = clean(s, flags);

		// Debug.debug("rectifyGeneric_1 2", s);

		if (s == null)
			return null;

		s = TextUtils.replace(s, ".", ". ");
		s = TextUtils.replace(s, " .", " ");
		s = TextUtils.replace(s, "  ", " ");

		// Debug.debug("rectifyGeneric_1 b", s);

		if (new RE("^\\?+$").match(s))
		{
			// Debug.debug("discarding question...", s);
			return null;
		}

		// Debug.debug("rectifyGeneric_1 c", s);

		// Debug.debug("rectifyGeneric_1 6", s);

		while (s.startsWith("."))
			s = s.substring(1);

		s = TextUtils.replace(s, "Live @ ", "Live At ");
		s = TextUtils.replace(s, "Live@", "Live At ");

		// s = s.re

		if (s == null)
			return null;

		if (s.endsWith(", The"))
			s = "The " + s.substring(0, s.length() - 5);

		return s;
	}

	public String rectifySongTitle(String s)
	{
		return rectifySongTitle(
		// null, null,
				s, null);
	}

	// public String rectifySongTitle(Album album, String s)
	// {
	// return rectifySongTitle(null, album, s);
	// }
	//
	// public String rectifySongTitle(Artist artist, String s)
	// {
	// return rectifySongTitle(artist, null, s);
	// }

	public String rectifySongTitle(
	// Artist artist, Album album,
			String s, IMusicMetadata flags)
	{
		String old = s;
		// while (true)
		for (int i = 0; true; i++)
		{
			// Debug.debug("s(" + i + ")", s);

			s = rectifySongTitle_1(s, flags);
			if (s == null)
				return null;

			// if (artist != null)
			// {
			// if (s.startsWith(artist.name + " - "))
			// s = s.substring(artist.name.length() + 3);
			// if (s.endsWith(" - " + artist.name))
			// s = s.substring(0, s.length() - (artist.name.length() + 3));
			// }
			// if (album != null)
			// {
			// if (s.startsWith(album.name + " - "))
			// s = s.substring(album.name.length() + 3);
			// if (s.endsWith(" - " + album.name))
			// s = s.substring(0, s.length() - (album.name.length() + 3));
			// }

			if (s.equals(old))
				return s;
			old = s;
		}
	}

	private void parseTrackNumber(String s, IMusicMetadata flags)
	{
		if (flags == null)
			return;

		try
		{
			s = s.trim();
			Number number = new Integer(s);
			if (flags != null)
				flags.setTrackNumberNumeric(number);
			// Debug.debug(KEY_TRACK_NUMBER, number);
		} catch (NumberFormatException e)
		{
			Debug.debug("bad track number", s);
		} catch (Throwable e)
		{
			Debug.debug("s", s);
			Debug.debug(e);
		}
	}

	private String removeTrackNumbers(String s, IMusicMetadata flags)
	{
		if (s == null)
			return null;

		if (new RE("^(audio)? ?track ?[- ]?[0-9][0-9]?$")
				.match(s.toLowerCase()))
		{
			if (s.toLowerCase().startsWith("audio"))
				s = s.substring(5).trim();
			parseTrackNumber(s.substring(5), flags);
			// Debug.debug("discarding track...", s);
			return null;
		}
		if (new RE("^piste ?[- ]?[0-9][0-9]?$").match(s.toLowerCase()))
		{
			parseTrackNumber(s.substring(5), flags);
			// Debug.debug("discarding track...", s);
			return null;
		}

		// Debug.debug("removeTrackNumbers 1", s);

		if (new RE("^[0-9][0-9] - ").match(s)
				|| new RE("^[0-9][0-9][0-9] - ").match(s)
				|| new RE("^[aAbBcCdD][0-9] - ").match(s))
		{
			// Debug.debug("attempting to strip track number...", s);
			int index = s.indexOf('-');
			if (index >= 0)
			{
				String after = s.substring(index + 1).trim();
				// Debug.debug("\t" + "after", after);
				if (after.indexOf('-') < 0) // if mutiple -'s then ignore...
				{
					parseTrackNumber(s.substring(0, index), flags);
					s = after;
				}
			}
		}

		if (new RE("^\\([0-9][0-9]\\) ").match(s)
				|| new RE("^\\([abcdABCD][0-9]\\) ").match(s))
		{
			// Debug.debug("attempting to strip track number...", s);
			int index = s.indexOf(')');
			if (index >= 0)
			{
				parseTrackNumber(s.substring(1, index), flags);
				s = s.substring(index + 1).trim();
			}
		}
		// Debug.debug("removeTrackNumbers 2", s);

		// if (new RE("^\\([0-9][0-9]\\) ").match(s))
		// {
		// // Debug.debug("attempting to strip track number...", s);
		// int index = s.indexOf(')');
		// if (index >= 0)
		// {
		// parseTrackNumber(s.substring(1, index), flags);
		// s = s.substring(index + 1).trim();
		// }
		// }

		// Debug.debug("removeTrackNumbers 3", s);

		return s;
	}

	private String rectifySongTitle_1(String s, IMusicMetadata flags)
	{
		s = rectifyGeneric_1(s, flags);
		if (s == null)
			return null;

		s = removeTrackNumbers(s, flags);

		if (s == null)
			return null;

		s = removeQuotes(s);

		return s;
	}

	private String removeSuffixes(String s, String suffixes[])
	{
		return removeSuffixes(s, new Vector(Arrays.asList(suffixes)));
	}

	private String removeSuffixes(String s, Vector suffixes)
	{
		// return removeSuffixes(s, suffixes, "");
		// }
		//
		// private String removeSuffixes(String s, String suffixes[],
		// String suffix_prefix)
		// {
		if (s == null)
			return null;

		for (int i = 0; i < suffixes.size(); i++)
		{
			String suffix = (String) suffixes.get(i);
			// suffix = suffix_prefix + suffix;

			if (s.toLowerCase().endsWith(suffix.toLowerCase()))
				s = s.substring(0, s.length() - suffix.length());
		}
		return s;
	}

	private String removePrefixes(String s, String prefixes[])
	{
		return removePrefixes(s, new Vector(Arrays.asList(prefixes)));
		// // return removePrefixes(s, prefixes, "");
		// // }
		// //
		// // private String removePrefixes(String s, String prefixes[],
		// // String prefix_suffix)
		// // {
		// if (s == null)
		// return null;
		//
		// for (int i = 0; i < prefixes.length; i++)
		// {
		// String prefix = prefixes[i];
		// // prefix = prefix + prefix_suffix;
		//
		// if (s.toLowerCase().startsWith(prefix.toLowerCase()))
		// s = s.substring(prefix.length());
		// }
		// return s;
	}

	private String removePrefixes(String s, Vector prefixes)
	{
		if (s == null)
			return null;

		for (int i = 0; i < prefixes.size(); i++)
		{
			String prefix = (String) prefixes.get(i);
			// prefix = prefix + prefix_suffix;

			if (s.toLowerCase().startsWith(prefix.toLowerCase()))
				s = s.substring(prefix.length());
		}
		return s;
	}

	private String discardMatches(String s, String patterns[])
	{
		if (s == null)
			return null;

		for (int i = 0; i < patterns.length; i++)
		{
			String prefix = patterns[i];
			if (s.equalsIgnoreCase(prefix))
				return null;
		}
		return s;
	}

	public String rectifyAlbum(String s)
	{
		return rectifyAlbum(s, null);
	}

	public String rectifyAlbum(String s, IMusicMetadata flags)
	{
		String old = s;
		while (true)
		{
			s = rectifyAlbum_1(s, flags);
			if (s == null)
				return null;

			// if (artist != null)
			// {
			// if (s.startsWith(artist.name + " - "))
			// s = s.substring(artist.name.length() + 3);
			// if (s.endsWith(" - " + artist.name))
			// s = s.substring(0, s.length() - (artist.name.length() + 3));
			// }

			if (s.equals(old))
				return s;
			old = s;
		}
	}

	private String removeYearPrefixSuffix(String s)
	{
		// Debug.debug("removeYearPrefixSuffix before", s);

		if (s == null)
			return null;

		if (new RE("^\\(199[0-9]\\)").match(s)
				|| new RE("^\\(200[0-9]\\)").match(s))
			s = s.substring(7);

		if (new RE("^\\( 199[0-9] \\)").match(s)
				|| new RE("^\\( 200[0-9] \\)").match(s))
			s = s.substring(9);

		if (new RE("\\(199[0-9]\\)$").match(s)
				|| new RE("\\(200[0-9]\\)$").match(s))
			s = s.substring(0, s.length() - 7);

		if (new RE("\\( 199[0-9] \\)$").match(s)
				|| new RE("\\( 200[0-9] \\)$").match(s))
			s = s.substring(0, s.length() - 9);

		if (new RE("199[0-9] - ").match(s) || new RE("200[0-9] - ").match(s))
		{
			int index = s.indexOf('-');
			if (index >= 0)
			{
				String temp = s.substring(index + 1);
				if (temp.indexOf('-') < 0)
					s = temp;
			}
		}

		if (new RE("- 199[0-9]").match(s) || new RE(" - 200[0-9]").match(s))
		{
			int index = s.lastIndexOf('-');
			if (index >= 0)
			{
				String temp = s.substring(0, index);
				if (temp.indexOf('-') < 0)
					s = temp;
			}
		}

		// Debug.debug("removeYearPrefixSuffix after", s);

		return s;
	}

	private static final String PATTERNS_ALBUM[] = { "dvd", //
			"10\"", //
			"12 - Inch", //
			"12 Inch", //
			"12 Inch Single", //
			"12\"", //
			"12\" Ep", //
			"12\" Vinyl", //
			"7 Inch", //
			"7\"", //
			"Advance", //
			"Advance Copy", //
			"Bonus Disc", //
			"Box", //
			"Cd", //
			"Cd Single", //
			"Cdm", //
			"Cdr", //
			"Cds", //
			"maxi", //
			"maxi single", //
			"Promo Cd", //
			"Ep", //
			"Full Vls", //
			// "Vls", //
			"Import", //
			"Lp", //
			// "Ost", //
			"Promo", //
			"Promo Cds", //
			"Retail", //
			"Single", //
			"Vinyl", //
			"Vinyl Single", //
			"Vls", //
			"cd", //
			"cds", //
			"ep", //
			"unknown album", //
			"Remastered", //
	};
	static
	{
		Arrays.sort(PATTERNS_ALBUM, MyComparator.kToStringLengthReverse);
	}

	private static final String PATTERNS_ARTIST[] = { "skit", //
			"live", //
	};
	static
	{
		Arrays.sort(PATTERNS_ARTIST, MyComparator.kToStringLengthReverse);
	}

	public String rectifyAlbum_1(String s, IMusicMetadata flags)
	{
		s = rectifyGeneric_1(s, flags);
		if (s == null)
			return null;

		s = removeSafePrefixSuffix(s, PATTERNS_ALBUM);
		if (s == null)
			return null;

		if (s.endsWith(" Box Set"))
		{
			if (flags != null)
				flags.setIsCompilation(Boolean.TRUE);
		}

		s = removeYearPrefixSuffix(s);

		s = removeURLs(s);

		s = removeQuotes(s);

		{
			String old = s;

			s = removeSafePrefixSuffix(s, DEFAULT_ACAPELLA, true);
			// s = removeSafePrefixSuffix(s, kDEFAULTS_acapella);

			if (s == null || !s.equals(old))
			{
				if (flags != null)
					flags.setIsAcapella(Boolean.TRUE);
			}
			if (s == null)
				return null;
		}

		if (s.endsWith(" !"))
			s = s.substring(0, s.length() - 2);
		else if (s.endsWith(" (!)"))
			s = s.substring(0, s.length() - 4);

		return s;
	}

	private String removeURLs(String s)
	{
		if (s == null)
			return null;

		{
			if (new RE("^http://").match(s.toLowerCase()))
				return null;
			// if (new RE("^[hH][tT][tT][pP]://").match(s))
			// return null;
		}

		{
			String temp = s;
			temp = TextUtils.replace(temp, ". ", ".");

			// Debug.debug("s1", s);
			RE re = new RE("^[\\w \\-]*\\.[\\w \\.\\-]*\\.(com|net|org|edu)$");
			// re.setMatchFlags(RE.MATCH_CASEINDEPENDENT);
			if (re.match(temp.toLowerCase()))
				return null;

			// Debug.debug("s2", s);

			// if (new RE(
			// "^[\\w \\-]*\\.[\\w \\.\\-]*\\.([cC][oO][mM]|[oO][rR][gG]|[nN][eE][tT])$")
			// .match(temp))
			// {
			// // Debug.debug("discarding album url...", s);
			// return null;
			// }
		}

		return s;
	}

	public String rectifyArtist(String s)
	{
		return rectifyArtist(s, null);
	}

	public String rectifyArtist(String s, IMusicMetadata flags)
	{
		String old = s;
		while (true)
		{
			s = rectifyArtist_1(s, flags);
			if (s == null)
				return null;

			// s = removeTrackNumbers(s);

			if (s.equals(old))
				return s;
			old = s;
		}
	}

	private String rectifyArtist_1(String s, IMusicMetadata flags)
	{
		// Debug.debug("rectifyArtist_1 1", s);

		s = rectifyGeneric_1(s, flags);
		if (s == null)
			return null;

		// Debug.debug("rectifyArtist_1 2", s);

		if (s.equalsIgnoreCase("unknown artist"))
			return null;

		// Debug.debug("rectifyArtist_1 3", s);

		s = removeTrackNumbers(s, flags);
		s = removeYearPrefixSuffix(s);

		s = removeSafePrefixSuffix(s, PATTERNS_ARTIST);
		if (s == null)
			return null;

		{
			String old = s;

			// s = removeSafePrefixSuffix(s, kDEFAULTS_acapella);
			s = removeSafePrefixSuffix(s, DEFAULT_ACAPELLA, true);

			if (s == null || !s.equals(old))
			{
				if (flags != null)
					flags.setIsAcapella(Boolean.TRUE);
			}
			if (s == null)
				return null;
		}

		s = removeQuotes(s);
		s = removeURLs(s);

		return s;
	}

	public String rectifyGenre(String s)
	{
		String old = s;
		while (true)
		{
			s = rectifyGenre_1(s);
			if (s == null)
				return null;
			if (s.equals(old))
				return s;
			old = s;
		}
	}

	private String rectifyGenre_1(String s)
	{
		s = rectifyGeneric_1(s, null);
		if (s == null)
			return null;

		if (s.equalsIgnoreCase("music"))
			return null;

		s = removeQuotes(s);

		s = TextUtils.replace(s, " - ", "-");

		s = removeSafePrefixSuffix(s, "©", true);
		s = removeSafePrefixSuffix(s, DEFAULT_ACAPELLA, true);

		return s;
	}

	public String rectifyPublisher(String s)
	{
		String old = s;
		while (true)
		{
			s = rectifyPublisher_1(s);
			if (s == null)
				return null;
			if (s.equals(old))
				return s;
			old = s;
		}
	}

	private String rectifyPublisher_1(String s)
	{
		s = rectifyGeneric_1(s, null);
		if (s == null)
			return null;

		s = removeURLs(s);

		s = removeQuotes(s);

		s = TextUtils.replace(s, " - ", "-");

		return s;
	}

	// public Vector splitName(String s, DatabaseNamedItem.Type type,
	// IMusicMetadata flags)
	// {
	// if (s == null)
	// return new Vector();
	//
	// // if (s.indexOf('/') < 0)
	// // return new Vector(Arrays.asList(new String[]{
	// // s,
	// // }));
	//
	// String splits[] = TextUtils.split(s, '/');
	// Vector v = new Vector(Arrays.asList(splits));
	//
	// // Debug.debug("v(" + id + ")", v.size());
	//
	// // v = removeDuplicates(v, type);
	// // return v;
	//
	// Vector result = new Vector();
	//
	// for (int i = 0; i < v.size(); i++)
	// {
	// String child = (String) v.get(i);
	// child = type.rectifyName(child, flags);
	// if (child == null)
	// continue;
	//
	// result.remove(child);
	// result.add(child);
	// }
	//
	// Collections.sort(result);
	//
	// return result;
	// }

	// private static class SecondaryArtistTag
	// {
	// public final String tag;
	// public final Number artist_type_id;
	//
	// public SecondaryArtistTag(String tag, Number artist_type_id)
	// {
	// this.artist_type_id = artist_type_id;
	// this.tag = tag;
	// }
	// }

	// private static final SecondaryArtistTag kSECONDARY_ARTIST_TAGS[] = {
	// new SecondaryArtistTag("f\\.", kARTIST_TYPE_FEATURING), //
	// new SecondaryArtistTag("ft\\.", kARTIST_TYPE_FEATURING), //
	// new SecondaryArtistTag("feat\\.", kARTIST_TYPE_FEATURING), //
	// new SecondaryArtistTag("featuring ", kARTIST_TYPE_FEATURING), //
	// new SecondaryArtistTag("produced by ", kARTIST_TYPE_PRODUCER), //
	// // new SecondaryArtistTag(" remix", kARTIST_TYPE_MIX_ARTIST), //
	// // new SecondaryArtistTag(" mix", kARTIST_TYPE_MIX_ARTIST), //
	// };

	private static final String FEATURING[] = { "f\\.", //
			"ft\\.", //
			"feat\\.", //
			"featuring ", //
	};

	private Vector listToNames(RE re)
	{
		Vector result = new Vector();

		int count = re.getParenCount();
		for (int i = 0; i < count / 2; i++)
		{
			String child = re.getParen(i * 2 + 1);
			// Debug.debug("child(" + i + ")", child);

			child = rectifyArtist(child);

			// Debug.debug("child.1(" + i + ")", child);
			result.remove(child);
			result.add(child);
		}
		return result;
	}

	private Vector debugRE(RE re)
	{
		Vector result = new Vector();

		int count = re.getParenCount();
		for (int i = 0; i < count; i++)
		{
			String child = re.getParen(i);
			Debug.debug("child(" + i + ")", child);
			result.add(child);
		}
		return result;
	}

	public String processFeaturing(String s, Vector primary_artists,
			Vector featured_artists, String pattern)
	{
		if (s == null)
			return null;

		RE re = new RE(pattern);
		if (re.match(s.toLowerCase()))
		{
			// Debug.debug("\t" + "featuring match(" + pattern + ")", s);
			// String wholeExpr = re.getParen(0);
			// Debug.debug("\t" + "wholeExpr", wholeExpr);
			// String insideParens = re.getParen(1);
			// Debug.debug("\t" + "insideParens", insideParens);

			int startInside = re.getParenStart(1);
			// Debug.debug("\t" + "startInside", startInside);
			int endInside = re.getParenEnd(1);
			// Debug.debug("\t" + "endInside", endInside);

			String left = s.substring(0, startInside);
			// Debug.debug("\t" + "left", left);
			String right = s.substring(startInside, endInside);
			// Debug.debug("\t" + "right", right);
			// int pattern_length = TextUtils.replace(pattern. "\\", "\");

			int index = Integer.MAX_VALUE;
			right = right.trim();

			{
				int i = right.indexOf('.');
				if (i >= 0)
					index = Math.min(index, i);
			}
			{
				int i = right.indexOf(' ');
				if (i >= 0)
					index = Math.min(index, i);
			}
			if (index < 0)
			{
				Debug.debug("\t" + "couldn't use match(" + pattern + ")", s);
				return s;
			}
			right = right.substring(index + 1);
			if (right.endsWith(")"))
				right = right.substring(0, right.length() - 1);
			if (left.endsWith("("))
				left = left.substring(0, left.length() - 1);
			right = right.trim();

			RE re2 = new RE("(.*)(,.*)*\\&(.*)");
			if (re2.match(right))
			{
				Vector v = listToNames(re2);
				featured_artists.removeAll(v);
				featured_artists.addAll(v);
			} else
			{
				re2 = new RE("(.*)(,.*)*\\ And (.*)");
				if (re2.match(right))
				{
					Vector v = listToNames(re2);
					featured_artists.removeAll(v);
					featured_artists.addAll(v);
				} else
				{
					// Debug.debug("simple featuring", right);
					right = rectifyArtist(right);
					featured_artists.remove(right);
					featured_artists.add(right);
				}
			}

			s = rectifyArtist(left);
			// Debug.debug("\t" + "s", s);
		}

		return s;
	}

	public void processFeaturing(String s, Vector primary_artists,
			Vector featured_artists)
	{
		// Debug.debug("processFeaturing", s);

		for (int i = 0; i < FEATURING.length; i++)
		{
			String regex = "\\((" + FEATURING[i] + ".*)\\)$";
			s = processFeaturing(s, primary_artists, featured_artists, regex);
		}
		for (int i = 0; i < FEATURING.length; i++)
		{
			String regex = "( " + FEATURING[i] + ".*$)";
			s = processFeaturing(s, primary_artists, featured_artists, regex);
		}

		if (primary_artists == null)
			return;

		s = rectifyArtist(s);
		primary_artists.remove(s);
		primary_artists.add(s);
	}

	private static final String ESCAPED = "^$.[|*+?\\(<)>#=/-{}";

	public String toRegexLiteral(String s)
	{
		StringBuffer result = new StringBuffer();

		char chars[] = s.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			if (ESCAPED.indexOf(c) >= 0)
				result.append('\\');

			result.append(c);
		}
		return result.toString();
	}

	// private static final String kREGEX_OPEN_TOKENS = "\\(\\[\\{\\\"\\'";
	// private static final String kREGEX_CLOSE_TOKENS = "\\)\\]\\}\\\"\\'";

	public String getPrefixPattern(String s, boolean permissive)
	{
		return "^('" + s + "'|\\\"" + s + "\\\"|\\[" + s + "\\]|\\(" + s
				+ "\\)|\\{" + s + "\\}|" + s + "\\-"
				+ (permissive ? "|" + s + " " : "") + ")";
		// return ("^[" + kREGEX_OPEN_TOKENS + "]?" + s + " ?["
		// + (permissive ? " " : "") + "\\-" + kREGEX_CLOSE_TOKENS + "]");
	}

	public String getSuffixPattern(String s, boolean permissive)
	{
		return "('" + s + "'|\\\"" + s + "\\\"|\\[" + s + "\\]|\\(" + s
				+ "\\)|\\{" + s + "\\}|\\-" + s + ""
				+ (permissive ? "| " + s : "") + ")$";
		// return ("[" + (permissive ? " " : "") + "\\-" + kREGEX_OPEN_TOKENS
		// + "] ?" + s + "[" + kREGEX_CLOSE_TOKENS + "]?$");
	}

	public String getPrefixPattern2(String s)
	{
		return "^('.*'|\\\".*\\\"|\\[.*\\]|\\(.*\\)|\\{.*\\}|.*\\-) ?" + s
				+ "$";
		// return ("^[" + kREGEX_OPEN_TOKENS + "]?" + "(.*)" + " ?["
		// // + (permissive ? " " : "")
		// + "\\-" + kREGEX_CLOSE_TOKENS + "] ?" + s + "$");
	}

	public String getSuffixPattern2(String s)
	{
		return "^" + s
				+ " ?('.*'|\\\".*\\\"|\\[.*\\]|\\(.*\\)|\\{.*\\}|\\-.*)$";
		// return ("^" + s + " ?["
		// // + (permissive ? " " : "")
		// + "\\-" + kREGEX_OPEN_TOKENS + "] ?" + "(.*)" + "["
		// + kREGEX_CLOSE_TOKENS + "]?$");
	}

	private String stripRegexMatch(String s, String pattern)
	{
		if (s == null)
			return null;

		try
		{
			// Debug.debug("\t"+"s", s);
			// Debug.debug("\t"+"pattern", pattern);

			RE re = REGEX_CACHE.getRegEx(pattern);
			// RE re = new RE(pattern.toLowerCase());
			// Debug.debug("prefix", prefix_pattern);
			if (!re.match(s.toLowerCase()))
				return s;
			// String match = re.getParen(0);
			// Debug.debug("match(" + pattern + ")", s);
			s = s.substring(0, re.getParenStart(0))
					+ s.substring(re.getParenEnd(0));
			// Debug.debug("updating...new_name ", new_name);
			return s;
		} catch (Exception e)
		{
			Debug.debug("s", s);
			Debug.debug("pattern", pattern);
			return s;
		}
	}

	private static class RegExCache
	{
		private final Map map = new Hashtable();
		// private final LinkedList order = new LinkedList();
		private static final int kMAX = 25000;

		public final RE getRegEx(String pattern)
		{
			if (pattern == null)
				return null;
			pattern = pattern.toLowerCase();
			RE result = (RE) map.get(pattern);
			if (result == null)
			{
				result = new RE(pattern);
				map.put(pattern, result);
			}
			// else
			// order.remove(pattern);
			// order.addFirst( pattern);
			//
			// if(order.size()>kMAX)
			// {
			// Object key = order.getLast();
			// order.removeLast();
			// map.remove(key);
			// }

			if (map.keySet().size() > kMAX)
			{
				Debug.debug("emptying regex cache.");
				map.clear();
			}
			return result;
		}
	}

	private static final RegExCache REGEX_CACHE = new RegExCache();

	private String extractRegexPattern(String s, String pattern, int paren)
	{
		if (s == null)
			return null;

		try
		{
			RE re = REGEX_CACHE.getRegEx(pattern);
			// RE re = new RE(pattern.toLowerCase());
			if (!re.match(s.toLowerCase()))
				return s;

			if (paren < re.getParenCount())
			{
				s = re.getParen(paren);
			}

			return s;
		} catch (Exception e)
		{
			Debug.debug("s", s);
			Debug.debug("pattern", pattern);
			return s;
		}
	}

	private String removeSafePrefixSuffix(String s, String patterns[])
	{
		return removeSafePrefixSuffix(s, patterns, false);
	}

	private String removeSafePrefixSuffix(String s, String patterns[],
			boolean permissive)
	{
		if (s == null)
			return null;

		for (int i = 0; s != null && i < patterns.length; i++)
		{
			String pattern = patterns[i];

			s = removeSafePrefixSuffixLiteral(s, pattern, permissive);
		}

		return s;
	}

	private String removeSafePrefixSuffixLiteral(String s, String pattern,
			boolean permissive)
	{
		return removeSafePrefixSuffix(s, toRegexLiteral(pattern), permissive);
	}

	private String removeSafePrefixSuffix(String s, String pattern)
	{
		return removeSafePrefixSuffix(s, pattern, false);
	}

	private String removeSafePrefixSuffix(String s, String pattern,
			boolean permissive)
	{
		if (s == null)
			return null;

		if (s.equalsIgnoreCase(pattern))
			return null;

		s = stripRegexMatch(s, getPrefixPattern((pattern), permissive));
		s = stripRegexMatch(s, getSuffixPattern((pattern), permissive));

		s = extractRegexPattern(s, getPrefixPattern2((pattern)), 1);
		s = extractRegexPattern(s, getSuffixPattern2((pattern)), 1);

		return s;
	}

	// public String cleanItemWithItem(String haystack, String needle,
	// DatabaseNamedItem.Type type, IMusicMetadata flags)
	// {
	// if (haystack == null)
	// return null;
	// if (needle == null)
	// return haystack;
	//
	// String s = removeSafePrefixSuffix(haystack, toRegexLiteral(needle),
	// false);
	// if (s == null || !s.equals(haystack))
	// s = type.rectifyName(s, flags);
	// return s;
	// }
	//
	// public String cleanItemWithItem(String haystack, Vector needles,
	// DatabaseNamedItem.Type type, IMusicMetadata flags)
	// {
	// if (haystack == null)
	// return null;
	//
	// for (int i = 0; needles != null && haystack != null
	// && i < needles.size(); i++)
	// {
	// String needle = (String) needles.get(i);
	// haystack = cleanItemWithItem(haystack, needle, type, flags);
	// }
	//
	// return haystack;
	// }
	//
	// public Vector cleanItemWithItem(Vector haystacks, Vector needles,
	// DatabaseNamedItem.Type type, IMusicMetadata flags)
	// {
	// if (haystacks == null)
	// return null;
	//
	// Vector result = new Vector();
	// for (int i = 0; haystacks != null && i < haystacks.size(); i++)
	// {
	// String haystack = (String) haystacks.get(i);
	// haystack = cleanItemWithItem(haystack, needles, type, flags);
	// if (haystack != null)
	// result.add(haystack);
	// }
	//
	// return result;
	// }
	//
	// public String cleanItemWithItem(DatabaseNamedItem haystack,
	// DatabaseNamedItem needle)
	// {
	// if (haystack == null)
	// return null;
	// if (needle == null)
	// return haystack.name;
	//
	// String haystack_name = haystack.name;
	// String needle_name = needle.name;
	//
	// String s = removeSafePrefixSuffix(haystack_name,
	// toRegexLiteral(needle_name), false);
	// if (s == null || !s.equals(haystack_name))
	// s = haystack.getType().rectifyName(s);
	// return s;
	// }

}
