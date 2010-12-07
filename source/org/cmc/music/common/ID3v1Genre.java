package org.cmc.music.common;

import java.util.Map;

import org.cmc.music.util.MyMap;

public class ID3v1Genre
{
	public final int id;
	public final String name;

	public ID3v1Genre(int id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public static final ID3v1Genre GENRE_BLUES = new ID3v1Genre(0, "Blues");
	public static final ID3v1Genre GENRE_CLASSIC_ROCK = new ID3v1Genre(1,
			"Classic Rock");
	public static final ID3v1Genre GENRE_COUNTRY = new ID3v1Genre(2, "Country");
	public static final ID3v1Genre GENRE_DANCE = new ID3v1Genre(3, "Dance");
	public static final ID3v1Genre GENRE_DISCO = new ID3v1Genre(4, "Disco");
	public static final ID3v1Genre GENRE_FUNK = new ID3v1Genre(5, "Funk");
	public static final ID3v1Genre GENRE_GRUNGE = new ID3v1Genre(6, "Grunge");
	public static final ID3v1Genre GENRE_HIP_HOP = new ID3v1Genre(7, "Hip-Hop");
	public static final ID3v1Genre GENRE_JAZZ = new ID3v1Genre(8, "Jazz");
	public static final ID3v1Genre GENRE_METAL = new ID3v1Genre(9, "Metal");
	public static final ID3v1Genre GENRE_NEW_AGE = new ID3v1Genre(10, "New Age");
	public static final ID3v1Genre GENRE_OLDIES = new ID3v1Genre(11, "Oldies");
	public static final ID3v1Genre GENRE_OTHER = new ID3v1Genre(12, "Other");
	public static final ID3v1Genre GENRE_POP = new ID3v1Genre(13, "Pop");
	public static final ID3v1Genre GENRE_RNB = new ID3v1Genre(14, "R&B");
	public static final ID3v1Genre GENRE_RAP = new ID3v1Genre(15, "Rap");
	public static final ID3v1Genre GENRE_REGGAE = new ID3v1Genre(16, "Reggae");
	public static final ID3v1Genre GENRE_ROCK = new ID3v1Genre(17, "Rock");
	public static final ID3v1Genre GENRE_TECHNO = new ID3v1Genre(18, "Techno");
	public static final ID3v1Genre GENRE_INDUSTRIAL = new ID3v1Genre(19,
			"Industrial");
	public static final ID3v1Genre GENRE_ALTERNATIVE = new ID3v1Genre(20,
			"Alternative");
	public static final ID3v1Genre GENRE_SKA = new ID3v1Genre(21, "Ska");
	public static final ID3v1Genre GENRE_DEATH_METAL = new ID3v1Genre(22,
			"Death Metal");
	public static final ID3v1Genre GENRE_PRANKS = new ID3v1Genre(23, "Pranks");
	public static final ID3v1Genre GENRE_SOUNDTRACK = new ID3v1Genre(24,
			"Soundtrack");
	public static final ID3v1Genre GENRE_EURO_TECHNO = new ID3v1Genre(25,
			"Euro-Techno");
	public static final ID3v1Genre GENRE_AMBIENT = new ID3v1Genre(26, "Ambient");
	public static final ID3v1Genre GENRE_TRIP_HOP = new ID3v1Genre(27,
			"Trip-Hop");
	public static final ID3v1Genre GENRE_VOCAL = new ID3v1Genre(28, "Vocal");
	public static final ID3v1Genre GENRE_JAZZ_FUNK = new ID3v1Genre(29,
			"Jazz+Funk");
	public static final ID3v1Genre GENRE_FUSION = new ID3v1Genre(30, "Fusion");
	public static final ID3v1Genre GENRE_TRANCE = new ID3v1Genre(31, "Trance");
	public static final ID3v1Genre GENRE_CLASSICAL = new ID3v1Genre(32,
			"Classical");
	public static final ID3v1Genre GENRE_INSTRUMENTAL = new ID3v1Genre(33,
			"Instrumental");
	public static final ID3v1Genre GENRE_ACID = new ID3v1Genre(34, "Acid");
	public static final ID3v1Genre GENRE_HOUSE = new ID3v1Genre(35, "House");
	public static final ID3v1Genre GENRE_GAME = new ID3v1Genre(36, "Game");
	public static final ID3v1Genre GENRE_SOUND_CLIP = new ID3v1Genre(37,
			"Sound Clip");
	public static final ID3v1Genre GENRE_GOSPEL = new ID3v1Genre(38, "Gospel");
	public static final ID3v1Genre GENRE_NOISE = new ID3v1Genre(39, "Noise");
	public static final ID3v1Genre GENRE_ALTERNROCK = new ID3v1Genre(40,
			"AlternRock");
	public static final ID3v1Genre GENRE_BASS = new ID3v1Genre(41, "Bass");
	public static final ID3v1Genre GENRE_SOUL = new ID3v1Genre(42, "Soul");
	public static final ID3v1Genre GENRE_PUNK = new ID3v1Genre(43, "Punk");
	public static final ID3v1Genre GENRE_SPACE = new ID3v1Genre(44, "Space");
	public static final ID3v1Genre GENRE_MEDITATIVE = new ID3v1Genre(45,
			"Meditative");
	public static final ID3v1Genre GENRE_INSTRUMENTAL_POP = new ID3v1Genre(46,
			"Instrumental Pop");
	public static final ID3v1Genre GENRE_INSTRUMENTAL_ROCK = new ID3v1Genre(47,
			"Instrumental Rock");
	public static final ID3v1Genre GENRE_ETHNIC = new ID3v1Genre(48, "Ethnic");
	public static final ID3v1Genre GENRE_GOTHIC = new ID3v1Genre(49, "Gothic");
	public static final ID3v1Genre GENRE_DARKWAVE = new ID3v1Genre(50,
			"Darkwave");
	public static final ID3v1Genre GENRE_TECHNO_INDUSTRIAL = new ID3v1Genre(51,
			"Techno-Industrial");
	public static final ID3v1Genre GENRE_ELECTRONIC = new ID3v1Genre(52,
			"Electronic");
	public static final ID3v1Genre GENRE_POP_FOLK = new ID3v1Genre(53,
			"Pop-Folk");
	public static final ID3v1Genre GENRE_EURODANCE = new ID3v1Genre(54,
			"Eurodance");
	public static final ID3v1Genre GENRE_DREAM = new ID3v1Genre(55, "Dream");
	public static final ID3v1Genre GENRE_SOUTHERN_ROCK = new ID3v1Genre(56,
			"Southern Rock");
	public static final ID3v1Genre GENRE_COMEDY = new ID3v1Genre(57, "Comedy");
	public static final ID3v1Genre GENRE_CULT = new ID3v1Genre(58, "Cult");
	public static final ID3v1Genre GENRE_GANGSTA = new ID3v1Genre(59, "Gangsta");
	public static final ID3v1Genre GENRE_TOP_40 = new ID3v1Genre(60, "Top 40");
	public static final ID3v1Genre GENRE_CHRISTIAN_RAP = new ID3v1Genre(61,
			"Christian Rap");
	public static final ID3v1Genre GENRE_POP_FUNK = new ID3v1Genre(62,
			"Pop/Funk");
	public static final ID3v1Genre GENRE_JUNGLE = new ID3v1Genre(63, "Jungle");
	public static final ID3v1Genre GENRE_NATIVE_AMERICAN = new ID3v1Genre(64,
			"Native American");
	public static final ID3v1Genre GENRE_CABARET = new ID3v1Genre(65, "Cabaret");
	public static final ID3v1Genre GENRE_NEW_WAVE = new ID3v1Genre(66,
			"New Wave");
	public static final ID3v1Genre GENRE_PSYCHADELIC = new ID3v1Genre(67,
			"Psychadelic");
	public static final ID3v1Genre GENRE_RAVE = new ID3v1Genre(68, "Rave");
	public static final ID3v1Genre GENRE_SHOWTUNES = new ID3v1Genre(69,
			"Showtunes");
	public static final ID3v1Genre GENRE_TRAILER = new ID3v1Genre(70, "Trailer");
	public static final ID3v1Genre GENRE_LO_FI = new ID3v1Genre(71, "Lo-Fi");
	public static final ID3v1Genre GENRE_TRIBAL = new ID3v1Genre(72, "Tribal");
	public static final ID3v1Genre GENRE_ACID_PUNK = new ID3v1Genre(73,
			"Acid Punk");
	public static final ID3v1Genre GENRE_ACID_JAZZ = new ID3v1Genre(74,
			"Acid Jazz");
	public static final ID3v1Genre GENRE_POLKA = new ID3v1Genre(75, "Polka");
	public static final ID3v1Genre GENRE_RETRO = new ID3v1Genre(76, "Retro");
	public static final ID3v1Genre GENRE_MUSICAL = new ID3v1Genre(77, "Musical");
	public static final ID3v1Genre GENRE_ROCK_N_ROLL = new ID3v1Genre(78,
			"Rock & Roll");
	public static final ID3v1Genre GENRE_HARD_ROCK = new ID3v1Genre(79,
			"Hard Rock");
	public static final ID3v1Genre GENRE_FOLK = new ID3v1Genre(80, "Folk");
	public static final ID3v1Genre GENRE_FOLK_ROCK = new ID3v1Genre(81,
			"Folk-Rock");
	public static final ID3v1Genre GENRE_NATIONAL_FOLK = new ID3v1Genre(82,
			"National Folk");
	public static final ID3v1Genre GENRE_SWING = new ID3v1Genre(83, "Swing");
	public static final ID3v1Genre GENRE_FAST_FUSION = new ID3v1Genre(84,
			"Fast Fusion");
	public static final ID3v1Genre GENRE_BEBOB = new ID3v1Genre(85, "Bebob");
	public static final ID3v1Genre GENRE_LATIN = new ID3v1Genre(86, "Latin");
	public static final ID3v1Genre GENRE_REVIVAL = new ID3v1Genre(87, "Revival");
	public static final ID3v1Genre GENRE_CELTIC = new ID3v1Genre(88, "Celtic");
	public static final ID3v1Genre GENRE_BLUEGRASS = new ID3v1Genre(89,
			"Bluegrass");
	public static final ID3v1Genre GENRE_AVANTGARDE = new ID3v1Genre(90,
			"Avantgarde");
	public static final ID3v1Genre GENRE_GOTHIC_ROCK = new ID3v1Genre(91,
			"Gothic Rock");
	public static final ID3v1Genre GENRE_PROGRESSIVE_ROCK = new ID3v1Genre(92,
			"Progressive Rock");
	public static final ID3v1Genre GENRE_PSYCHEDELIC_ROCK = new ID3v1Genre(93,
			"Psychedelic Rock");
	public static final ID3v1Genre GENRE_SYMPHONIC_ROCK = new ID3v1Genre(94,
			"Symphonic Rock");
	public static final ID3v1Genre GENRE_SLOW_ROCK = new ID3v1Genre(95,
			"Slow Rock");
	public static final ID3v1Genre GENRE_BIG_BAND = new ID3v1Genre(96,
			"Big Band");
	public static final ID3v1Genre GENRE_CHORUS = new ID3v1Genre(97, "Chorus");
	public static final ID3v1Genre GENRE_EASY_LISTENING = new ID3v1Genre(98,
			"Easy Listening");
	public static final ID3v1Genre GENRE_ACOUSTIC = new ID3v1Genre(99,
			"Acoustic");
	public static final ID3v1Genre GENRE_HUMOUR = new ID3v1Genre(100, "Humour");
	public static final ID3v1Genre GENRE_SPEECH = new ID3v1Genre(101, "Speech");
	public static final ID3v1Genre GENRE_CHANSON = new ID3v1Genre(102,
			"Chanson");
	public static final ID3v1Genre GENRE_OPERA = new ID3v1Genre(103, "Opera");
	public static final ID3v1Genre GENRE_CHAMBER_MUSIC = new ID3v1Genre(104,
			"Chamber Music");
	public static final ID3v1Genre GENRE_SONATA = new ID3v1Genre(105, "Sonata");
	public static final ID3v1Genre GENRE_SYMPHONY = new ID3v1Genre(106,
			"Symphony");
	public static final ID3v1Genre GENRE_BOOTY_BASS = new ID3v1Genre(107,
			"Booty Bass");
	public static final ID3v1Genre GENRE_PRIMUS = new ID3v1Genre(108, "Primus");
	public static final ID3v1Genre GENRE_PORN_GROOVE = new ID3v1Genre(109,
			"Porn Groove");
	public static final ID3v1Genre GENRE_SATIRE = new ID3v1Genre(110, "Satire");
	public static final ID3v1Genre GENRE_SLOW_JAM = new ID3v1Genre(111,
			"Slow Jam");
	public static final ID3v1Genre GENRE_CLUB = new ID3v1Genre(112, "Club");
	public static final ID3v1Genre GENRE_TANGO = new ID3v1Genre(113, "Tango");
	public static final ID3v1Genre GENRE_SAMBA = new ID3v1Genre(114, "Samba");
	public static final ID3v1Genre GENRE_FOLKLORE = new ID3v1Genre(115,
			"Folklore");
	public static final ID3v1Genre GENRE_BALLAD = new ID3v1Genre(116, "Ballad");
	public static final ID3v1Genre GENRE_POWER_BALLAD = new ID3v1Genre(117,
			"Power Ballad");
	public static final ID3v1Genre GENRE_RHYTHMIC_SOUL = new ID3v1Genre(118,
			"Rhythmic Soul");
	public static final ID3v1Genre GENRE_FREESTYLE = new ID3v1Genre(119,
			"Freestyle");
	public static final ID3v1Genre GENRE_DUET = new ID3v1Genre(120, "Duet");
	public static final ID3v1Genre GENRE_PUNK_ROCK = new ID3v1Genre(121,
			"Punk Rock");
	public static final ID3v1Genre GENRE_DRUM_SOLO = new ID3v1Genre(122,
			"Drum Solo");
	public static final ID3v1Genre GENRE_A_CAPELLA = new ID3v1Genre(123,
			"A capella");
	public static final ID3v1Genre GENRE_EURO_HOUSE = new ID3v1Genre(124,
			"Euro-House");
	public static final ID3v1Genre GENRE_DANCE_HALL = new ID3v1Genre(125,
			"Dance Hall");

	private static final ID3v1Genre ALL[] = { GENRE_BLUES, //
			GENRE_CLASSIC_ROCK, //
			GENRE_COUNTRY, //
			GENRE_DANCE, //
			GENRE_DISCO, //
			GENRE_FUNK, //
			GENRE_GRUNGE, //
			GENRE_HIP_HOP, //
			GENRE_JAZZ, //
			GENRE_METAL, //
			GENRE_NEW_AGE, //
			GENRE_OLDIES, //
			GENRE_OTHER, //
			GENRE_POP, //
			GENRE_RNB, //
			GENRE_RAP, //
			GENRE_REGGAE, //
			GENRE_ROCK, //
			GENRE_TECHNO, //
			GENRE_INDUSTRIAL, //
			GENRE_ALTERNATIVE, //
			GENRE_SKA, //
			GENRE_DEATH_METAL, //
			GENRE_PRANKS, //
			GENRE_SOUNDTRACK, //
			GENRE_EURO_TECHNO, //
			GENRE_AMBIENT, //
			GENRE_TRIP_HOP, //
			GENRE_VOCAL, //
			GENRE_JAZZ_FUNK, //
			GENRE_FUSION, //
			GENRE_TRANCE, //
			GENRE_CLASSICAL, //
			GENRE_INSTRUMENTAL, //
			GENRE_ACID, //
			GENRE_HOUSE, //
			GENRE_GAME, //
			GENRE_SOUND_CLIP, //
			GENRE_GOSPEL, //
			GENRE_NOISE, //
			GENRE_ALTERNROCK, //
			GENRE_BASS, //
			GENRE_SOUL, //
			GENRE_PUNK, //
			GENRE_SPACE, //
			GENRE_MEDITATIVE, //
			GENRE_INSTRUMENTAL_POP, //
			GENRE_INSTRUMENTAL_ROCK, //
			GENRE_ETHNIC, //
			GENRE_GOTHIC, //
			GENRE_DARKWAVE, //
			GENRE_TECHNO_INDUSTRIAL, //
			GENRE_ELECTRONIC, //
			GENRE_POP_FOLK, //
			GENRE_EURODANCE, //
			GENRE_DREAM, //
			GENRE_SOUTHERN_ROCK, //
			GENRE_COMEDY, //
			GENRE_CULT, //
			GENRE_GANGSTA, //
			GENRE_TOP_40, //
			GENRE_CHRISTIAN_RAP, //
			GENRE_POP_FUNK, //
			GENRE_JUNGLE, //
			GENRE_NATIVE_AMERICAN, //
			GENRE_CABARET, //
			GENRE_NEW_WAVE, //
			GENRE_PSYCHADELIC, //
			GENRE_RAVE, //
			GENRE_SHOWTUNES, //
			GENRE_TRAILER, //
			GENRE_LO_FI, //
			GENRE_TRIBAL, //
			GENRE_ACID_PUNK, //
			GENRE_ACID_JAZZ, //
			GENRE_POLKA, //
			GENRE_RETRO, //
			GENRE_MUSICAL, //
			GENRE_ROCK_N_ROLL, //
			GENRE_HARD_ROCK, //
			GENRE_FOLK, //
			GENRE_FOLK_ROCK, //
			GENRE_NATIONAL_FOLK, //
			GENRE_SWING, //
			GENRE_FAST_FUSION, //
			GENRE_BEBOB, //
			GENRE_LATIN, //
			GENRE_REVIVAL, //
			GENRE_CELTIC, //
			GENRE_BLUEGRASS, //
			GENRE_AVANTGARDE, //
			GENRE_GOTHIC_ROCK, //
			GENRE_PROGRESSIVE_ROCK, //
			GENRE_PSYCHEDELIC_ROCK, //
			GENRE_SYMPHONIC_ROCK, //
			GENRE_SLOW_ROCK, //
			GENRE_BIG_BAND, //
			GENRE_CHORUS, //
			GENRE_EASY_LISTENING, //
			GENRE_ACOUSTIC, //
			GENRE_HUMOUR, //
			GENRE_SPEECH, //
			GENRE_CHANSON, //
			GENRE_OPERA, //
			GENRE_CHAMBER_MUSIC, //
			GENRE_SONATA, //
			GENRE_SYMPHONY, //
			GENRE_BOOTY_BASS, //
			GENRE_PRIMUS, //
			GENRE_PORN_GROOVE, //
			GENRE_SATIRE, //
			GENRE_SLOW_JAM, //
			GENRE_CLUB, //
			GENRE_TANGO, //
			GENRE_SAMBA, //
			GENRE_FOLKLORE, //
			GENRE_BALLAD, //
			GENRE_POWER_BALLAD, //
			GENRE_RHYTHMIC_SOUL, //
			GENRE_FREESTYLE, //
			GENRE_DUET, //
			GENRE_PUNK_ROCK, //
			GENRE_DRUM_SOLO, //
			GENRE_A_CAPELLA, //
			GENRE_EURO_HOUSE, //
			GENRE_DANCE_HALL, //
	};

	private static final String simplify(String s)
	{
		StringBuffer result = new StringBuffer();

		char chars[] = s.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			if (Character.isLetter(c))
				result.append(Character.toLowerCase(c));
			else if (Character.isDigit(c))
				result.append(c);
			else
				;
		}

		return result.toString();
	}

	private static final Map ID_TO_NAME_MAP = new MyMap();
	private static final Map NAME_TO_ID_MAP = new MyMap();
	private static final Map SIMPLE_NAME_TO_ID_MAP = new MyMap();

	static
	{
		for (int i = 0; i < ALL.length; i++)
		{
			ID3v1Genre genre = ALL[i];

			String name = genre.name;
			Number id = new Integer(genre.id);

			ID_TO_NAME_MAP.put(id, name);
			NAME_TO_ID_MAP.put(name, id);

			String simple = simplify(name);
			SIMPLE_NAME_TO_ID_MAP.put(simple, id);
		}
	}

	public static final Number getIDForName(String name)
	{
		Number result = (Number) NAME_TO_ID_MAP.get(name);
		if (result != null)
			return result;

		String simple = simplify(name);

		result = (Number) SIMPLE_NAME_TO_ID_MAP.get(simple);
		return result;
	}

	public static final String getNameForID(Number id)
	{
		return (String) ID_TO_NAME_MAP.get(id);
	}
}
