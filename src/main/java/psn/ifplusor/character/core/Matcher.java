package psn.ifplusor.character.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Matcher {
	private static int MAXLEN = 15;
	private static int CONTENTWIDTH = 5;
	
	private MatchNode root = new MatchNode('\0');
	
	static public void main(String []objs) {
		Matcher matcher = new Matcher();
		ArrayList<String> lstKeys=new ArrayList<String>();
		lstKeys.add("操作系统");
		lstKeys.add("引导扇区");
		lstKeys.add("内存");
		lstKeys.add("磁盘");
		matcher.addKeyword(lstKeys,1,"术语");
		
//		long starTime=System.currentTimeMillis();
//		for(int n=0; n<1000000; n++) {
//			List<WordFind> lst = matcher.match("当 x86 PC 启动时，它执行的是一个叫 BIOS 的程序。BIOS 存放在非易失存储器中，BIOS 的作用是在启动时进行硬件的准备工作，接着把控制权交给操作系统。具体来说，BIOS 会把控制权交给从引导扇区（用于引导的磁盘的第一个512字节的数据区）加载的代码。引导扇区中包含引导加载器——负责内核加载到内存中。BIOS 会把引导扇区加载到内存 0x7c00 处，接着（通过设置寄存器 %ip）跳转至该地址。引导加载器开始执行后，处理器处于模拟 Intel 8088 处理器的模式下。而接下来的工作就是把处理器设置为现代的操作模式，并从磁盘中把 xv6 内核载入到内存中，然后将控制权交给内核。xv6 引导加载器包括两个源文件，一个由16位和32位汇编混合编写而成（bootasm.S；（8400）），另一个由 C 写成（bootmain.c；（8500））。", true, true);
//		}
//		long endTime=System.currentTimeMillis();
//		long Time=endTime-starTime;
//		System.out.println(Time + " ms");
		
		List<WordFind> lst = matcher.match("当 x86 PC 启动时，它执行的是一个叫 BIOS 的程序。BIOS 存放在非易失存储器中，BIOS 的作用是在启动时进行硬件的准备工作，接着把控制权交给操作系统。具体来说，BIOS 会把控制权交给从引导扇区（用于引导的磁盘的第一个512字节的数据区）加载的代码。引导扇区中包含引导加载器——负责内核加载到内存中。BIOS 会把引导扇区加载到内存 0x7c00 处，接着（通过设置寄存器 %ip）跳转至该地址。引导加载器开始执行后，处理器处于模拟 Intel 8088 处理器的模式下。而接下来的工作就是把处理器设置为现代的操作模式，并从磁盘中把 xv6 内核载入到内存中，然后将控制权交给内核。xv6 引导加载器包括两个源文件，一个由16位和32位汇编混合编写而成（bootasm.S；（8400）），另一个由 C 写成（bootmain.c；（8500））。", true, true);
		for(WordFind wf : lst) {
			System.out.println(wf.ClasslyName+"-"+wf.getName()+"-"+wf.get匹配串()+"-"+wf.get权重());
			System.out.println(wf.getCount()+":"+wf.get上下文());
		}
	}

	public void addKeyword(String keys, int LV, String classname) {
		if (keys.length() > MAXLEN)
			return;
		
		for (String key : keys.split(" ")) {
			if (regexTools.isNullOrEmpty(key))
				continue;
			
			String pattern = null;
			if (key.contains(".*?")) {
				pattern = key.replace(".*?", ".");
				String[] o = pattern.split("\\.");
				String subkeyl = o[0];
				String subkeyr = o[1];
				int num = MAXLEN - pattern.length() + 1;
				pattern = subkeyl;
				while (num-- > 0) pattern += '?';
				pattern += subkeyr;
			} else {
				pattern = key;
			}
			
			root.addBranch(pattern, LV, classname, key);
		}
	}
	
	public void addKeyword(List<String> lst) {
		for (String line : lst) addKeyword(line, 1, "");
	}

	public void addKeyword(List<String> lst, int lv, String classname) {
		for (String line : lst) addKeyword(line, lv, classname);
	}
	
	public List<WordFind> match(String content, Boolean b智能权重, boolean b全返回) {
		
		List<MatchWorker> lstMatcher = new ArrayList<MatchWorker>();
		List<WordFind> lstResult = new ArrayList<WordFind>();
		
		for (int index = 0; index < content.length(); index++) {
			char ch = content.charAt(index);
			lstMatcher.add(new MatchWorker(index));  // 每个字都启动一个新的匹配器
			
			ListIterator<MatchWorker> iter = lstMatcher.listIterator();
			while (iter.hasNext()) {
				MatchWorker matcher = iter.next();
				MatchWorker fuzzyMatcher = null;
				if (matcher.cur.fuzzy) {  // 具有模糊匹配机制，复制匹配器
					fuzzyMatcher = new MatchWorker(matcher);
					fuzzyMatcher.match('?');
				}
				
				int r = matcher.match(ch);
				if (r >= 2) {  // 满足匹配项
					int nLV = matcher.cur.LV;
					
					WordFind nc = new WordFind();
					nc.setName(matcher.cur.key);
					nc.setCount(matcher.begin + 1);
					nc.匹配串 = content.substring(matcher.begin, index + 1);
					if (b智能权重)
						nc.权重 = nLV - nc.匹配串.length() - 2;
					else
						nc.权重 = nLV;
					
                    if (nc.权重 < 0)
                        nc.权重 = -nc.权重;
					
					nc.ClasslyName = matcher.cur.classname;

					lstResult.add(nc);
					
					//这里加入当前上N字，下N字的语境
                    int nBegin = matcher.begin - CONTENTWIDTH;
                    if (nBegin < 0) nBegin = 0;
                    int nEnd = index + 1 + CONTENTWIDTH;
                    if (nEnd > content.length()) nEnd = content.length();
                    String 上下文 = content.substring(nBegin, nEnd);

                    nc.上下文 = 上下文;
				}
				if (r % 2 == 0)  // 没有后续分支
					iter.remove();  // 注意remove与add的次序

				if (fuzzyMatcher != null)
					iter.add(fuzzyMatcher);
			}
		}
		
		return lstResult;
	}
	
	// 匹配器，仅能在字典树上沿一条分支向下检查
	private class MatchWorker {
		int begin;
		MatchNode cur = root;  // 字典树游标
		
		public MatchWorker(int begin) {
			this.begin = begin;
		}
		
		public MatchWorker(MatchWorker matcher) {
			this.begin = matcher.begin;
			this.cur = matcher.cur;
		}
		
		// 按字选择进入分支
		public int match(Character character) {
			cur = cur.next(character);
			if (cur == null)
				return 0;
			int r = 0;
			if (cur.matched) // 匹配
				r += 2;
			if (cur.hasnext) // 有后续分支
				r += 1;
			return r;
		}
	}
	
	// 字典树节点
	private class MatchNode {
		Character character = null;
		boolean matched = false;
		boolean hasnext = false;
		boolean fuzzy = false;
		Map<Character, MatchNode> htNext = new HashMap<Character, MatchNode>();

		int LV = 0;
		String classname = null;
		String key = null;
		
		public MatchNode(Character character) {
			this.character = character;
		}
		
		public void addBranch(String pattern, int LV, String classname, String key) {
			
			if (pattern.length() == 0) {  // 叶子节点
				if (matched == false) {
					this.matched = true;
					this.LV = LV;
					this.classname = classname;
					this.key = key;
				}
				return;
			}
			
			Character nextChar = pattern.charAt(0);
			MatchNode nextNode = htNext.get(nextChar);
			if (nextNode == null) {
				nextNode = new MatchNode(nextChar);
				htNext.put(nextChar, nextNode);
				this.hasnext = true;
			}
			nextNode.addBranch(pattern.substring(1), LV, classname, key);
			if (nextChar == '?') {
				this.fuzzy = true;
				this.addBranch(pattern.substring(1), LV, classname, key);
			}
		}
		
		public MatchNode next(Character character) {
			return htNext.get(character);
		}
	}
}
