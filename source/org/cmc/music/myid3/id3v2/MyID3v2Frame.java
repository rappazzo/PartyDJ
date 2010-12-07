/*
 * Written By Charles M. Chen 
 * 
 * Created on Sep 2, 2005
 *
 */

package org.cmc.music.myid3.id3v2;

import java.util.Comparator;

public class MyID3v2Frame
{
	public final String frameID;
	public final byte dataBytes[];

	public MyID3v2Frame(String frame_id, byte data_bytes[])
	{
		this.frameID = frame_id;
		this.dataBytes = data_bytes;
	}

	public String toString()
	{
		return "{" + frameID + "}";
	}

	public static final Comparator COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2)
		{
			MyID3v2Frame ph1 = (MyID3v2Frame) o1;
			MyID3v2Frame ph2 = (MyID3v2Frame) o2;
			return ph1.frameID.compareTo(ph2.frameID);
		}
	};

}
