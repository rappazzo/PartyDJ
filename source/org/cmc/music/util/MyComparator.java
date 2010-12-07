/*
 * Written By Charles M. Chen
 * charlesmchen@gmail.com
 * Created on Jun 21, 2005
 * 
 * 
 * 
 *
 */

package org.cmc.music.util;

import java.io.File;
import java.util.Comparator;

public abstract class MyComparator implements Comparator
{
	public static final Comparator kToStringLength = new Comparator() {
		public int compare(Object o1, Object o2)
		{
			if ((o1 == null) && (o2 == null))
				return 0;
			if (o1 == null)
				return 1;
			if (o2 == null)
				return -1;

			String s1 = o1.toString();
			String s2 = o2.toString();

			return s1.length() - s2.length();
		}
	};
	public static final Comparator kToStringLengthReverse = new Comparator() {
		public int compare(Object o1, Object o2)
		{
			if ((o1 == null) && (o2 == null))
				return 0;
			if (o1 == null)
				return 1;
			if (o2 == null)
				return -1;

			String s1 = o1.toString();
			String s2 = o2.toString();

			return s2.length() - s1.length();
		}
	};

	public static final Comparator kToStringIgnoreCase = new Comparator() {
		public int compare(Object o1, Object o2)
		{
			if ((o1 == null) && (o2 == null))
				return 0;
			if (o1 == null)
				return 1;
			if (o2 == null)
				return -1;

			String s1 = o1.toString();
			String s2 = o2.toString();

			return s1.compareToIgnoreCase(s2);
		}
	};
	public static final Comparator kToStringHonorCase = new Comparator() {
		public int compare(Object o1, Object o2)
		{
			if ((o1 == null) && (o2 == null))
				return 0;
			if (o1 == null)
				return 1;
			if (o2 == null)
				return -1;

			String s1 = o1.toString();
			String s2 = o2.toString();

			return s1.compareTo(s2);
		}
	};

	public static final Comparator kFileName = new Comparator() {
		public int compare(Object o1, Object o2)
		{
			File f1 = (File) o1;
			File f2 = (File) o2;

			return f1.getName().toLowerCase().compareTo(
					f2.getName().toLowerCase());
		}
	};

	public static final Comparator kFilePath = new Comparator() {
		public int compare(Object o1, Object o2)
		{
			File f1 = (File) o1;
			File f2 = (File) o2;

			return f1.getAbsolutePath().toLowerCase().compareTo(
					f2.getAbsolutePath().toLowerCase());
		}
	};

	public static final Comparator kFileNameReverse = new Comparator() {
		public int compare(Object o1, Object o2)
		{
			File f1 = (File) o1;
			File f2 = (File) o2;

			return f2.getName().toLowerCase().compareTo(
					f1.getName().toLowerCase());
		}
	};

	public static final Comparator kFilePathReverse = new Comparator() {
		public int compare(Object o1, Object o2)
		{
			File f1 = (File) o1;
			File f2 = (File) o2;

			return f2.getAbsolutePath().toLowerCase().compareTo(
					f1.getAbsolutePath().toLowerCase());
		}
	};
}