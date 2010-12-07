package org.cmc.music.util;

/*
 * Written By Charles M. Chen
 * charlesmchen@gmail.com
 * Created on Jul 13, 2005
 * 
 * 
 * 
 *
 */

public interface BasicConstants
{
	public static final long kTIME_MILLISECONDS = 1;
	public static final long kTIME_SECONDS = kTIME_MILLISECONDS * 1000;
	public static final long kTIME_MINUTES = kTIME_SECONDS * 60;
	public static final long kTIME_HOURS = kTIME_MINUTES * 60;
	public static final long kTIME_DAYS = kTIME_HOURS * 24;
	public static final long kTIME_WEEKS = kTIME_DAYS * 7;

	public static final String newline = System.getProperty("line.separator");

}
