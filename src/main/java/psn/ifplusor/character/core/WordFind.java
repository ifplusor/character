package psn.ifplusor.character.core;

public class WordFind
{
	String ClasslyName = "";
	String name = "";
	int count = 0;
	int Size = 0;
	int lenStr = 0;
	String 匹配串 = "";
	int 权重 = 0;
	String 上下文="";

	public String get上下文()
	{
		return 上下文;
	}

	public void set上下文(String 上下文)
	{
		this.上下文 = 上下文;
	}

	public String getClasslyName()
	{
		return ClasslyName;
	}

	public void setClasslyName(String classlyName)
	{
		ClasslyName = classlyName;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public int getSize()
	{
		return Size;
	}

	public void setSize(int size)
	{
		Size = size;
	}

	public int getLenStr()
	{
		return lenStr;
	}

	public void setLenStr(int lenStr)
	{
		this.lenStr = lenStr;
	}

	public String get匹配串()
	{
		return 匹配串;
	}

	public void set匹配串(String 匹配串)
	{
		this.匹配串 = 匹配串;
	}

	public int get权重()
	{
		return 权重;
	}

	public void set权重(int 权重)
	{
		this.权重 = 权重;
	}

}
