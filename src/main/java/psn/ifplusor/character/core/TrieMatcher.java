package psn.ifplusor.character.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class TrieMatcher<E> implements Matcher<E> {
	
	public static int NODENUM = 0;
	
	private static int MAXLEN = 15;
	private static int CONTENTWIDTH = 5;
	
	private TrieNode root = new TrieNode();
	
	public void addKeyword(String key, E value, int level) {
		
		if (key == null)
			return;
		
		key = key.trim();
		
		if (key.length()==0 || key.length()>MAXLEN)
			return;
		
		String pattern = null;
		if (key.contains(".*?")) {  // 不定长模糊匹配，扩展为定长模式匹配插入字典树
			pattern = key.replace(".*?", ".");
			String[] o = pattern.split("\\.");
			
			String subkeyl = null;
			String subkeyr = null;
			if (o.length == 1) {
				if (pattern.charAt(0) == '.') {
					subkeyl = "";
					subkeyr = o[0];
				} else {
					subkeyl = o[0];
					subkeyr = "";
				}
			} else {
				subkeyl = o[0];
				subkeyr = o[1];
			}
			int num = MAXLEN - pattern.length() + 1;
			pattern = subkeyl;
			while (num-- > 0) pattern += '?';
			pattern += subkeyr;
		} else {
			pattern = key;
		}
		
		root.addBranch(pattern, key, value, level);
	}

	public void addKeyword(List<String> lstKeys, E value, int level) {
		for (String key : lstKeys) addKeyword(key, value, level);
	}
	
	public List<MatchedContent<E>> match(String content, Boolean intellWeight, boolean returnAll) {
		
		List<MatcherWorker> lstMatcher = new LinkedList<MatcherWorker>();  // 需要频繁插入删除
		List<MatchedContent<E>> lstMatchedTmp = new ArrayList<MatchedContent<E>>();
		
		for (int index = 0; index < content.length(); index++) {
			char ch = content.charAt(index);
			lstMatcher.add(new MatcherWorker(index));  // 每个字都启动一个新的匹配器
			
			ListIterator<MatcherWorker> iter = lstMatcher.listIterator();
			while (iter.hasNext()) {
				MatcherWorker matcher = iter.next();
				MatcherWorker fuzzyMatcher = null;
				if (matcher.cur.fuzzy) {  // 具有模糊匹配机制，复制匹配器
					fuzzyMatcher = new MatcherWorker(matcher);
					int r = fuzzyMatcher.match('?');
					if (r >= 2) {  // 满足匹配项
						int nLevel = matcher.cur.level;
						
						MatchedContent<E> mc = new MatchedContent<E>();
						mc.index = matcher.begin + 1;
						mc.content = content.substring(matcher.begin, index + 1);
						mc.len = mc.content.length();
						if (intellWeight)
							mc.weight = nLevel - mc.key.length() - 2;
						else
							mc.weight = nLevel;
						
						if (mc.weight < 0)
							mc.weight = -mc.weight;
						
						//这里加入当前上N字，下N字的语境
						int nBegin = matcher.begin - CONTENTWIDTH;
						if (nBegin < 0) nBegin = 0;
						int nEnd = index + 1 + CONTENTWIDTH;
						if (nEnd > content.length()) nEnd = content.length();
						mc.context = content.substring(nBegin, nEnd);

						if (matcher.cur.keyValue != null) {
							mc.key = matcher.cur.keyValue.key;
							mc.value = matcher.cur.keyValue.value;
							lstMatchedTmp.add(mc);
						} else {
							for (Pair<String, E> kv : matcher.cur.lstKeyValue) {
								MatchedContent<E> mc2 = new MatchedContent<E>(mc);
								mc2.key = kv.key;
								mc2.value = kv.value;
								lstMatchedTmp.add(mc2);
							}
						}
					}
					if (r % 2 == 0)  // 没有后续分支
						fuzzyMatcher = null;
				}
				
				int r = matcher.match(ch);
				if (r >= 2) {  // 满足匹配项
					int nLevel = matcher.cur.level;
					
					MatchedContent<E> mc = new MatchedContent<E>();
					mc.index = matcher.begin + 1;
					mc.content = content.substring(matcher.begin, index + 1);
					mc.len = mc.content.length();
					if (intellWeight)
						mc.weight = nLevel - mc.key.length() - 2;
					else
						mc.weight = nLevel;
					
					if (mc.weight < 0)
						mc.weight = -mc.weight;
					
					//这里加入当前上N字，下N字的语境
					int nBegin = matcher.begin - CONTENTWIDTH;
					if (nBegin < 0) nBegin = 0;
					int nEnd = index + 1 + CONTENTWIDTH;
					if (nEnd > content.length()) nEnd = content.length();
					mc.context = content.substring(nBegin, nEnd);

					if (matcher.cur.keyValue != null) {
						mc.key = matcher.cur.keyValue.key;
						mc.value = matcher.cur.keyValue.value;
						lstMatchedTmp.add(mc);
					} else {
						for (Pair<String, E> kv : matcher.cur.lstKeyValue) {
							MatchedContent<E> mc2 = new MatchedContent<E>(mc);
							mc2.key = kv.key;
							mc2.value = kv.value;
							lstMatchedTmp.add(mc2);
						}
					}
				}
				if (r % 2 == 0)  // 没有后续分支
					iter.remove();  // 注意remove与add的次序

				if (fuzzyMatcher != null)
					iter.add(fuzzyMatcher);
			}
		}
		
		if (lstMatchedTmp.size() <= 1)
			return lstMatchedTmp;
		
		// 通配符做贪婪匹配
		Collections.sort(lstMatchedTmp, new Comparator<MatchedContent<E>>() {

			public int compare(MatchedContent<E> arg0, MatchedContent<E> arg1) {
				// 以 key 为主序
				if (arg0.index > arg1.index) {
					return 1;
				} else if (arg0.index == arg1.index) {
					if (arg0.key.equals(arg1.key)) {
						if (arg0.value.equals(arg1.value)) {
							if (arg0.len < arg1.len)
								return 1;
							else if (arg0.len == arg1.len)
								return 0;
						}
					} else {
						return arg0.key.compareTo(arg1.key);
					}
				}
				return -1;
			}
			
		});
		
		List<MatchedContent<E>> lstResult = new ArrayList<MatchedContent<E>>();
		MatchedContent<E> cur = lstMatchedTmp.get(0);
		lstResult.add(cur);
		
		for (MatchedContent<E> mc : lstMatchedTmp) {
			if (cur.equals(mc))
				continue;
			
			lstResult.add(mc);
			cur = mc;
		}
		
		return lstResult;
	}
	
	// 匹配器，仅能在字典树上沿一条分支向下检查
	private class MatcherWorker {
		int begin;
		TrieNode cur = root;  // 字典树游标
		
		public MatcherWorker(int begin) {
			this.begin = begin;
		}
		
		public MatcherWorker(MatcherWorker matcher) {
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
	private class TrieNode {
		boolean matched = false;  // 匹配
		boolean hasnext = false;  // 有后续
		boolean fuzzy = false;    // 模糊匹配
		Map<Character, TrieNode> htNext = null;

		int level = 0;
		
		Pair<String, E> keyValue = null;
		List<Pair<String, E>> lstKeyValue = null;  // 一个关键词对应多个value
		
		public TrieNode() {
			++NODENUM;
		}
		
		public synchronized void addBranch(String pattern, String key, E value, int level) {
			
			if (pattern.length() == 0) {  // 叶子节点
				Pair<String, E> kv = new Pair<String, E>();
				kv.key = key;
				kv.value = value;

				if (matched == false) {
					this.matched = true;
					this.level = level;
					this.keyValue = kv;
				}
			
				if (lstKeyValue == null) {
					if (!keyValue.equals(kv)) {
						List<Pair<String, E>> tmpList = new ArrayList<Pair<String, E>>();
						tmpList.add(keyValue);
						tmpList.add(kv);
						lstKeyValue = tmpList;
						this.keyValue = null;
					}
				} else {
					for (Pair<String, E> v : lstKeyValue) {
						if (kv.equals(v))
							return;
					}
					lstKeyValue.add(kv);
				}
				return;
			}
			
			Character nextChar = pattern.charAt(0);
			TrieNode nextNode = null;
			if (htNext == null) {
				htNext = new HashMap<Character, TrieNode>();
			} else {
				nextNode = htNext.get(nextChar);
			}
			if (nextNode == null) {
				nextNode = new TrieNode();
				htNext.put(nextChar, nextNode);
				this.hasnext = true;
			}
			
			nextNode.addBranch(pattern.substring(1), key, value, level);
			if (nextChar == '?') {
				this.fuzzy = true;
				//this.merge(nextNode);
				this.addBranch(pattern.substring(1), key, value, level);
			}
		}
		
		// 实现 merge 需要写时复制机制
		private void merge(TrieNode node) {
			
			Iterator<Character> iter = node.htNext.keySet().iterator();
			while (iter.hasNext()) {
				Character key = iter.next();
				TrieNode nextNode = node.htNext.get(key);
				
				if (key == '?') {
					continue;
				} else {
					TrieNode thisNext = htNext.get(key);
					if (thisNext == null) {
						htNext.put(key, nextNode);
					} else if (thisNext != nextNode) {
						thisNext.merge(nextNode);
					}
				}
			}
			
		}
		
		public TrieNode next(Character character) {
			return htNext.get(character);
		}
	}
}
