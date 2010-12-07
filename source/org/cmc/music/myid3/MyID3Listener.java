/**
 * 
 */
package org.cmc.music.myid3;

import org.cmc.music.util.Debug;

public abstract class MyID3Listener
{

	public void log(String s, Object o)
	{
		log(Debug.getDebug(s, o));
	}

	public void log(String s, int value)
	{
		log(Debug.getDebug(s, value));
	}

	public void log(String s, byte value)
	{
		log(Debug.getDebug(s, value));
	}

	public void log(String s, boolean value)
	{
		log(Debug.getDebug(s, value));
	}

	public void log(String s, long value)
	{
		log(Debug.getDebug(s, value));
	}

	public void log(String s, String value)
	{
		log(Debug.getDebug(s, value));
	}

	public void logWithLength(String s, String value)
	{
		if (value == null)
			log(Debug.getDebug(s, value));
		else
			log(Debug.getDebug(s, value + " (" + value.length() + " chars)"));
	}

	public abstract void log(String s);

	public abstract void log();

}