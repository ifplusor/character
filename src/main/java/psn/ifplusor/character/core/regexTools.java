package psn.ifplusor.character.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regexTools 
{
	
	 public static String getCSV(String line)
	    {
	        return line.replace(",", "，").replace("\r", " ").
	        		replace("\n", " ").replace("\t", " ").
	        		replace("\"", " ").replace(":", " ").
	        		replace("?", " ");
	    }

	
	public static String GetStringByLen(String str, int nLen)
	{
		if (regexTools.isNullOrEmpty(str))
		{
			return "";
		}

		String temp = str;
		if (temp.length() > nLen)
		{
			temp = temp.substring(0, nLen);
		}

		return temp;
	}
	
	public static String GetStringByLenAndSQLRule(String str, int nLen)
	{
		String lenStr= GetStringByLen(str,nLen);
		if (regexTools.isNullOrEmpty(lenStr))
		{
			return "";
		}
		
		return lenStr;
	}
	
	public static String GetStringByIntObject(Object o)
	{
		
		return (Integer)o+"";
	}
	
	
	
	static public String RegexReplace(String content,String rule,String replace)
	{
		String zc1="";
		
		if(isNullOrEmpty(content) || isNullOrEmpty(rule))
		{
			return "";
		}
		
		Pattern p=Pattern.compile(rule);//Pattern.CASE_INSENSITIVE
		Matcher matcher=p.matcher(content);
		
		zc1=matcher.replaceAll(replace);
		
		return zc1;
	}
	

	static public boolean isMatch(String content,String rule)
	{
		String zc1="";
		
		if(isNullOrEmpty(content) || isNullOrEmpty(rule))
		{
			return false;
		}
		
		Pattern p=Pattern.compile(rule,Pattern.DOTALL|Pattern.CASE_INSENSITIVE);//Pattern.CASE_INSENSITIVE
		Matcher matcher=p.matcher(content);
		if(matcher.find())
		{
			return true;
		}
		
		
		return false;
		//return matcher.matches();
	}
	
	
	static public String GetSingleData(String content,String rule)
	{
		String zc1="";
		
		if(isNullOrEmpty(content) || isNullOrEmpty(rule))
		{
			return "";
		}
		
		Pattern p=Pattern.compile(rule,Pattern.DOTALL);//Pattern.CASE_INSENSITIVE
		Matcher matcher=p.matcher(content);
		if(matcher.find())
		{
			zc1= matcher.group(0);
		}
		return zc1;
	}
	
	static public String GetSingleDataByNoTag(String content,String rule)
	{
		
		String zc1="";
		
		if(isNullOrEmpty(content) || isNullOrEmpty(rule))
		{
			return "";
		}
		
		Pattern p=Pattern.compile(rule,Pattern.DOTALL);//Pattern.CASE_INSENSITIVE
		Matcher matcher=p.matcher(content);
		if(matcher.find())
		{
			zc1= matcher.group(0);
		}
		if(!isNullOrEmpty(zc1))
		{
			zc1=GetNotagHtml(zc1,"",true).trim();
			//zc1=�����滻���(zc1,"<.*?>","");
		}
		
		return zc1;
	}
	
	static public ArrayList<String> GetMultiData(String content,String rule)
	{
		
		
		ArrayList<String> list = new ArrayList<String>();  
		
		if(isNullOrEmpty(content) || isNullOrEmpty(rule))
		{
			return list;
		}
		
		String zc1="";
		Pattern p=Pattern.compile(rule,Pattern.DOTALL);
		Matcher matcher=p.matcher(content);
		
		while (matcher.find()) 
		{
			zc1 = matcher.group(0);
			list.add(zc1);
		}
		
		return list;
	}

	
	
	static public HashMap<String, Integer> GetMultiDataAndIndex(String content,String rule)
	{
		HashMap<String, Integer> ht = new HashMap<String, Integer>();  
		
		if(isNullOrEmpty(content) || isNullOrEmpty(rule))
		{
			return ht;
		}
		
		String zc1="";
		Pattern p=Pattern.compile(rule);
		Matcher matcher=p.matcher(content);
		
		
		while (matcher.find()) 
		{
			zc1 = matcher.group();
			int index = content.indexOf(matcher.group());
			ht.put(zc1, index);
		}
		
		return ht;
	}
	

    public static String GetNotagHtml(String FormatText, String m_url, Boolean bImgSave)
    {
        return GetNotagHtml(FormatText, m_url, bImgSave, false, false);
    }

    public static String GetNotagHtml(String buffer, String m_url, Boolean bImgSave, Boolean keepTableTags, Boolean keepAllTags)
    {
    	String Result = "";
        try
        {
        	String FormatText= buffer;// StringEscapeUtils.unescapeHtml4(buffer);
            //StringEscapeUtils.unescapeHtml(htmlDecodeString);
            
            if (!keepAllTags || !keepTableTags)
            {
                Result = RegexReplace(FormatText,"<br.*?>","\r\n");
            }
            else
            {
                Result = FormatText;
            }

            Result = FormatText;

            if (bImgSave==true)
            {

            	ArrayList<String> lStrings=GetMultiData(Result,"<img.*?src=.*?>|<img.*?file=.*?>");
            	for( String s:lStrings )
                //foreach (System.Text.RegularExpressions.Match m in Regex.Matches(Result, "<img.*?src=.*?>|<img.*?file=.*?>", RegexOptions.Singleline | RegexOptions.IgnoreCase))
                {

                    String srcUrl = "";
                    String fileUrl = "";

                    try
                    {
                        srcUrl = GetSingleData(s,"((?<=src\\w*=\\w*('|\")).*?((?=')|(?=\")|\\.jpg|\\.gif|\\.png))");// Regex.Match(s, "((?<=src\\w*=\\w*('|\")).*?((?=')|(?=\")|\\.jpg|\\.gif|\\.png))", RegexOptions.Singleline | RegexOptions.IgnoreCase).Value;

                        srcUrl = srcUrl.replace("\"", "");

                        srcUrl = srcUrl.replace("'", "");

                        fileUrl = GetSingleData(s, "((?<=file\\w*=\\w*('|\")).*?((?=')|(?=\")|\\.jpg|\\.gif|\\.png))");

                        fileUrl = fileUrl.replace("\"", "");

                        fileUrl = fileUrl.replace("'", "");

                        //|((?<=file\\w*=\\w*('|\")).*?((?=')|(?=\")|\\.jpg|\\.gif|\\.png))
                    }
                    catch (Exception e) 
                    {
					}


                    String tempSrc = "";
                    try
                    {
                        if (m_url == null)
                        {
                            tempSrc = srcUrl;
                        }
                        else
                        {
                            //tempSrc = ����URL��ʽ(src�����ַ, m_url, "").ToString();
                            //if(m_)
                        }
                    }
                    catch (Exception e)
                    { }

                    String tempFile = "";
                    if ( fileUrl!=null && fileUrl.length()!=0)
                    {
                        try
                        {
                            if (m_url == null)
                            {
                                tempFile = fileUrl;
                            }
                            else
                            {


                                //tempFile = ����URL��ʽ(file�����ַ, m_url, "").ToString();
                                //if(m_)
                            }
                        }

					catch (Exception e) {
						// TODO: handle exception
					}
                    }               
                    


                    try
                    {
                        String text = s.replace(srcUrl, tempSrc);

                        if (!isNullOrEmpty(fileUrl))
                        {
                            text = text.replace(fileUrl, tempFile);
                        }

                        text = RegexReplace(text,"onload=\".*?\"","");//Regex.Replace(text, "onload=\".*?\"", "", RegexOptions.Singleline);

                        text = RegexReplace(text,"onclick=\".*?\"","");// Regex.Replace(text, "onclick=\".*?\"", "", RegexOptions.Singleline);

                        String imgurl =RegexReplace(text,"<img","[img");// Regex.Replace(text, "<img", "[img", RegexOptions.IgnoreCase);// text.Replace("<img", "[img");
                        imgurl = imgurl.replace(">", "]");



                        Result = Result.replace(s, imgurl);//"[img src=" + getUrl(m_url, m.Groups[1].Value).ToString() + "]"
                        //Result= Result.Replace('"', ' ');
                    }
                    catch (Exception e)
                    { }
                }

            }
            else
            {
                Result = RegexReplace(Result,"\\[img.*?\\]","");  
            }

            if (keepTableTags)
            {
            	Result = RegexReplace(Result,"td>","td]");
            	Result = RegexReplace(Result,"<td","[td");
            	Result = RegexReplace(Result,"tr>","tr]");
            	Result = RegexReplace(Result,"<tr","[tr");

            }

            if (!keepAllTags)
            {

            	Result = RegexReplace(Result,"(<head>).*(</head>)"," ");
            	Result = RegexReplace(Result,"<script\\s*.*?</script>"," ");
            	Result = RegexReplace(Result,"<style*.*?</style>"," ");
            	Result = RegexReplace(Result,"(<script>).*?(</script>)"," ");
            	Result = RegexReplace(Result,"<( )*style([^>])*>"," ");
            	Result = RegexReplace(Result,"(<style).*(</style>)"," ");
            	Result = RegexReplace(Result,"<( )*td([^>])*>"," ");
            	Result = RegexReplace(Result,"<( )*tr([^>])*>","\r");
            	Result = RegexReplace(Result,"<( )*br( )*>","\r");
            	Result = RegexReplace(Result,"<( )*li( )*>","\r");
            	
            	Result = RegexReplace(Result,"<( )*p([^>])*>","\r");
            	Result = RegexReplace(Result,"<[^>]*>"," ");
            	
            	
                Result = RegexReplace(Result, "<( )*p([^>])*>", "\r\r");
               Result = RegexReplace(Result, "<[^>]*>", "");

                Result = RegexReplace(Result, "&amp;", "&");
               Result = RegexReplace(Result, "&nbsp;", " ");
                Result = RegexReplace(Result, "&lt;", "<");
                Result = RegexReplace(Result, "&gt;", ">");
                Result = RegexReplace(Result, "&(.{2,6});","");
               Result = RegexReplace(Result, " ( )+", " ");

            	Result = RegexReplace(Result,"(\r)( )+(\r)","\r");
            	Result = RegexReplace(Result,"(\r\r)+","");
            	Result = RegexReplace(Result,"(\r\n) +","\r");
            	Result = RegexReplace(Result,"(\r\n)+","\r");
            	Result = RegexReplace(Result,"(\r)+","\r");
            	Result = RegexReplace(Result,"(\n)+","\r");
//            	
//                Result = Regex.Replace(Result, "(\r)( )+(\r)", "\r\r");
//                Result = Regex.Replace(Result, @"(\r\r)+", "\r\n");
//                Result = Regex.Replace(Result, @"(\r\n) +", "\r\n", RegexOptions.IgnoreCase | RegexOptions.Singleline);
//                Result = Regex.Replace(Result, @"(\r\n)+", "\r\n", RegexOptions.IgnoreCase | RegexOptions.Singleline);
//                Result = Regex.Replace(Result, @"(\r)+", "\r", RegexOptions.IgnoreCase | RegexOptions.Singleline);
//                Result = Regex.Replace(Result, @"(\n)+", "\n", RegexOptions.IgnoreCase | RegexOptions.Singleline);
            }
            else
            {
                //Result = Regex.Replace(Result, @"(<a).*?(a>)", " ", RegexOptions.IgnoreCase);

            	
            	Result = RegexReplace(Result,"(<a).*?(>)"," ");
            	Result = RegexReplace(Result,"(</a>)"," ");
            	Result = RegexReplace(Result,"(<head>).*(</head>)"," ");
            	Result = RegexReplace(Result,"<script\\s*.*?</script>"," ");
            	Result = RegexReplace(Result,"(<!DOCTYPE HTML PUBLIC).*(>)"," ");

            	
//                Result = Regex.Replace(Result, @"(<a).*?(>)", " ", RegexOptions.IgnoreCase | RegexOptions.IgnoreCase);
//                Result = Regex.Replace(Result, @"(</a>)", " ", RegexOptions.IgnoreCase | RegexOptions.IgnoreCase);
//
//                Result = Regex.Replace(Result, "(<head>).*(</head>)", String.Empty, RegexOptions.IgnoreCase);
//                Result = Regex.Replace(Result, @"<script\s*.*?</script>", "", RegexOptions.Singleline | RegexOptions.IgnoreCase);
//                ////Result = Regex.Replace(Result, @"<style*.*?</style>", "", RegexOptions.Singleline | RegexOptions.IgnoreCase);
//                ////Result = Regex.Replace(Result, @"(<script>).*?(</script>)", String.Empty, RegexOptions.IgnoreCase);
//                ////Result = Regex.Replace(Result, @"<( )*style([^>])*>", "<style>", RegexOptions.IgnoreCase);
//                ////Result = Regex.Replace(Result, "(<style).*(</style>)", String.Empty, RegexOptions.IgnoreCase);
//                Result = Regex.Replace(Result, "(<!DOCTYPE HTML PUBLIC).*(>)", String.Empty, RegexOptions.IgnoreCase);

            }


            if (keepTableTags)
            {
            	
            	Result = RegexReplace(Result,"td]","td>");
            	Result = RegexReplace(Result,"[td","<td");
            	Result = RegexReplace(Result,"tr]","tr>");
            	Result = RegexReplace(Result,"[tr","<tr");
            	
//                Result = Regex.Replace(Result, "td]", "td>", RegexOptions.IgnoreCase);
//                Result = Regex.Replace(Result, "[td", "<td", RegexOptions.IgnoreCase);
//
//                Result = Regex.Replace(Result, "tr]", "tr>", RegexOptions.IgnoreCase);
//                Result = Regex.Replace(Result, "[tr", "<tr", RegexOptions.IgnoreCase);
            }
        }
        catch (Exception e) {
			
		}
        
        return Result;

    }
    
    
    public static String GetHtmlStyleByContent(String content)
    {
    	String Result = RegexReplace(content,"(\r)( )+(\r)","<br />");

    	Result=Result.replaceAll("<font color='#ff0000'>", "FONT BEGIN");
    	Result=Result.replaceAll("</font>", "FONT END");

    	
    	Result = RegexReplace(Result,"<.*?(?=[\u4e00-\u9fa5]|$)"," ");
    	Result = RegexReplace(Result,"(?<=[Z-a]).*?>"," ");

    	Result = RegexReplace(Result,"(\\\\r\\\\n)+","<br />");
    	Result = RegexReplace(Result,"(\\\\n)+","<br />");
    	Result = RegexReplace(Result,"(\\\\r)+","<br />");
    	Result = RegexReplace(Result,"(\\\\t)+","<br />");
    	
    	Result = RegexReplace(Result,"(\r\r)+","<br />");
    	Result = RegexReplace(Result,"(\r\n) +","<br />");
    	Result = RegexReplace(Result,"(\r\n)+","<br />");
    	Result = RegexReplace(Result,"(\r)+","<br />");
    	Result = RegexReplace(Result,"(\n)+","<br />");
    	
    	
    	
    	Result = RegexReplace(Result,"(\\r)( )+(\\r)","<br />");
    	Result = RegexReplace(Result,"(\\r\\r)+","<br />");
    	Result = RegexReplace(Result,"(\\r\\n) +","<br />");
    	Result = RegexReplace(Result,"(\\r\\n)+","<br />");
    	Result = RegexReplace(Result,"(\\r)+","<br />");
    	Result = RegexReplace(Result,"(\\n)+","<br />");
    	Result = RegexReplace(Result,"(\\t)+","<br />");
    	
    	Result=Result.replaceAll("�� ", "��<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    	Result=Result.replaceAll("����", "<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    	//����
    	
    	Result=Result.replaceAll("FONT BEGIN", "<font color='#ff0000'>");
    	Result=Result.replaceAll("FONT END", "</font>");
    	Result=Result.replaceAll("\\\\x\\S\\S", "");
    	
    	
    	//Result = RegexReplace(Result,"(?<=[\u4e00-\u9fa5]) (?=[\u4e00-\u9fa5]) ","<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    	
    	ArrayList<String> lstArrayList= GetMultiData(Result, "<br />");
    	int nBR=lstArrayList.size();
    	
    	if(nBR<Result.length()/200)
    	{
    		Result=Result.replaceAll("��", "��<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    		Result=Result.replaceAll("��", "��<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    	}
    	
    	lstArrayList= GetMultiData(Result, "<br />");
    	nBR=lstArrayList.size();
    	if(nBR<Result.length()/200)
    	{
    		Result=Result.replaceAll(" (?=[\u4e00-\u9fa5])", "<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    	}

    	
    	return Result;
    }
    
    public static String GetHtmlStyleByTitle(String Title)
    {
    	String Result = RegexReplace(Title,"(\r)( )+(\r)"," ");
    	
    	Result=Result.replaceAll("<font color='#ff0000'>", "FONT BEGIN");
    	Result=Result.replaceAll("</font>", "FONT END");
    	
    	Result = RegexReplace(Result,"<.*?(?=[\u4e00-\u9fa5]|$)"," ");
    	Result = RegexReplace(Result,"(?<=[Z-a]).*?>"," ");

    	Result = RegexReplace(Result,"(\\\\r\\\\n)+"," ");
    	Result = RegexReplace(Result,"(\\\\n)+"," ");
    	Result = RegexReplace(Result,"(\\\\r)+"," ");
    	Result = RegexReplace(Result,"(\\\\t)+"," ");
    	
    	Result = RegexReplace(Result,"(\r\r)+"," ");
    	Result = RegexReplace(Result,"(\r\n) +"," ");
    	Result = RegexReplace(Result,"(\r\n)+"," ");
    	Result = RegexReplace(Result,"(\r)+"," ");
    	Result = RegexReplace(Result,"(\n)+"," ");
    	Result = RegexReplace(Result,"(\t)+"," ");
    	
    	Result = RegexReplace(Result,"(\\r)( )+(\\r)"," ");
    	Result = RegexReplace(Result,"(\\r\\r)+"," ");
    	Result = RegexReplace(Result,"(\\r\\n) +"," ");
    	Result = RegexReplace(Result,"(\\r\\n)+"," ");
    	Result = RegexReplace(Result,"(\\r)+"," ");
    	Result = RegexReplace(Result,"(\\n)+"," ");
    	
    	//Result=Result.replace(" ", "&nbsp;");
    	Result=Result.replace("\\xc2\\xa0", "&nbsp;");
    	
    	
    	Result=Result.replace("\\xc2", "");
    	Result=Result.replace("\\xb7", "");
    	

    	Result=Result.replaceAll("FONT BEGIN", "<font color='#ff0000'>");
    	Result=Result.replaceAll("FONT END", "</font>");
    	Result=Result.replaceAll("\\\\x\\S\\S", "");
    	
    			
    	
    	//Result = Result.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "");  
    	
    	return Result;
    }
    
    public static boolean isNullOrEmpty(String input) 
    {
    	if(input==null)
    	{
    		return true;
    	}
    	
    	if(input.trim().length()==0)
    	{
    		return true;
    	}
    	
    	
    	return false;
    	
        //return input == null || input.length() ==0;
    }

    public static String GetNotagHtml(String FormatText)
    {
        return GetNotagHtml(FormatText, null, true);
    }

}
