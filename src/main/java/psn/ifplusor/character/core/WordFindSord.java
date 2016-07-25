package psn.ifplusor.character.core;

import java.util.Comparator;

public class WordFindSord implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		WordFind s1 = (WordFind) o1;
		WordFind s2 = (WordFind) o2;

		if (s1.get权重() > s2.get权重())
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
}
