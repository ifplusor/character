package psn.ifplusor.character;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import psn.ifplusor.character.core.MatchedContent;
import psn.ifplusor.character.core.Matcher;
import psn.ifplusor.character.core.TrieMatcher;
import psn.ifplusor.character.dict.Dictionary;

public class App {

	public static void main(String[] args) {
		trieMemory(args);
	}

    public static void graphDict( String[] args ) {

    	Dictionary dictionary = Dictionary.getDictionary();

    	dictionary.inputCorpus("2.txt", "GB2312");
    	//dictionary.printDict();

    	dictionary.storeToFile(true);

    	System.out.println("Succeed!");
    }
    
	public static void trieMemory(String[] args) {
		
		File file = new File("main2012.dic");
		
		int num = 0;
		Matcher<String> matcher = new TrieMatcher<String>();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0)
					continue;
				
				matcher.addKeyword(line, "词", 1);
				
				if ((++num) % 1000 == 0)
					System.out.println(num + " : " + TrieMatcher.NODENUM);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("end:" + TrieMatcher.NODENUM);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void trieMatcher(String []objs) {
		Matcher<String> matcher = new TrieMatcher<String>();
		ArrayList<String> lstKeys=new ArrayList<String>();
		lstKeys.add("操作系统");
		lstKeys.add("内存");
		matcher.addKeyword(lstKeys,"术语",1);
		matcher.addKeyword("?内存", "硬件", 1);
		matcher.addKeyword("启动.*?", "行为", 1);

//		long starTime=System.currentTimeMillis();
//		for(int n=0; n<1000000; n++) {
//			List<MatchedContent<String>> lst = matcher.match("当 x86 PC 启动时，它执行的是一个叫 BIOS 的程序。BIOS 存放在非易失存储器中，BIOS 的作用是在启动时进行硬件的准备工作，接着把控制权交给操作系统。具体来说，BIOS 会把控制权交给从引导扇区（用于引导的磁盘的第一个512字节的数据区）加载的代码。引导扇区中包含引导加载器——负责内核加载到内存中。BIOS 会把引导扇区加载到内存 0x7c00 处，接着（通过设置寄存器 %ip）跳转至该地址。引导加载器开始执行后，处理器处于模拟 Intel 8088 处理器的模式下。而接下来的工作就是把处理器设置为现代的操作模式，并从磁盘中把 xv6 内核载入到内存中，然后将控制权交给内核。xv6 引导加载器包括两个源文件，一个由16位和32位汇编混合编写而成（bootasm.S；（8400）），另一个由 C 写成（bootmain.c；（8500））。", true, true);
//		}
//		long endTime=System.currentTimeMillis();
//		long Time=endTime-starTime;
//		System.out.println(Time + " ms");

		List<MatchedContent<String>> lst = matcher.match("当 x86 PC 启动时，它执行的是一个叫 BIOS 的程序。BIOS 存放在非易失存储器中，BIOS 的作用是在启动时进行硬件的准备工作，接着把控制权交给操作系统。具体来说，BIOS 会把控制权交给从引导扇区（用于引导的磁盘的第一个512字节的数据区）加载的代码。引导扇区中包含引导加载器——负责内核加载到内存中。BIOS 会把引导扇区加载到内存 0x7c00 处，接着（通过设置寄存器 %ip）跳转至该地址。引导加载器开始执行后，处理器处于模拟 Intel 8088 处理器的模式下。而接下来的工作就是把处理器设置为现代的操作模式，并从磁盘中把 xv6 内核载入到内存中，然后将控制权交给内核。xv6 引导加载器包括两个源文件，一个由16位和32位汇编混合编写而成（bootasm.S；（8400）），另一个由 C 写成（bootmain.c；（8500））。", true, true);
		for(MatchedContent<String> wf : lst) {
			System.out.println(wf.getValue()+"-"+wf.getKey()+"-"+wf.getContent()+"-"+wf.getWeight());
			System.out.println(wf.getIndex()+":"+wf.getContext());
		}
	}
}
