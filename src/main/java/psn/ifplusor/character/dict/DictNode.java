package psn.ifplusor.character.dict;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DictNode {
	
	/*
	 * 使用图模型保存语料数据, 图中的超级点"^"和"$"分别表示句子的头和尾
	 */
	private static final Map<Character, DictNode> charNode = new HashMap<Character , DictNode>(16 , 0.95f);
	
	static {
		// 初始化图模型
		DictNode nodeHead = new DictNode('^');
		charNode.put('^', nodeHead);
		
		DictNode nodeTail = new DictNode('$');
		charNode.put('$', nodeTail);
		
		DictEdge edgeSuper = new DictEdge(nodeHead, nodeTail);
		nodeHead.outEdgeList.put('$', edgeSuper);
		nodeTail.inEdgeList.put('^', edgeSuper);
		
		DictEdge edgeHead = new DictEdge(nodeHead, nodeHead);
		nodeHead.inEdgeList.put('^', edgeHead);
		
		DictEdge edgeTail = new DictEdge(nodeTail, nodeTail);
		nodeTail.outEdgeList.put('$', edgeTail);
	}
	
	/**
	 * 创建结点，与超级点关联
	 * @param character
	 * @return
	 */
	private static synchronized DictNode createNode(Character character) {
		DictNode node = charNode.get(character);
		if (node == null) {
			node = new DictNode(character);
			charNode.put(character, node);
			
			// 建立单字与超级点的连接
			DictNode nodeHead = charNode.get('^');
			DictEdge edgeHead = new DictEdge(nodeHead, node);
			nodeHead.outEdgeList.put(character, edgeHead);
			node.inEdgeList.put('^', edgeHead);
			
			DictNode nodeTail = charNode.get('$');
			DictEdge edgeTail = new DictEdge(node, nodeTail);
			nodeTail.inEdgeList.put(character, edgeTail);
			node.outEdgeList.put('$', edgeTail);
		}
		return node;
	}
	
	public static DictNode getNode(Character character) {
		return charNode.get(character);
	}
	
	
	private Map<Character, DictEdge> outEdgeList = new HashMap<Character , DictEdge>(16 , 0.95f);
	private Map<Character, DictEdge> inEdgeList = new HashMap<Character , DictEdge>(16 , 0.95f);
	
	private Character character;
	private long count;
	private long inCount;  //绝对入度
	private long outCount; //绝对出度
	
	private DictNode(Character character) {
		this.character = character;
	}
	
	public synchronized void increaseCount() {
		count++;
	}
	
	public synchronized void increaseIn() {
		inCount++;
	}
	
	public synchronized void increaseOut() {
		outCount++;
	}
	
	/**
	 * 增加后继结点
	 * @param character
	 * @return
	 */
	public DictNode appendNode(Character character) {
		DictEdge edge = outEdgeList.get(character);
		if (edge == null) {
			synchronized (this.outEdgeList) {
				edge = outEdgeList.get(character);
				if (edge == null) {
					DictNode node = charNode.get(character);
					if (node == null) {
						node = createNode(character);
					}
					
					if (this.character == '^') { // 超级点，在创建新结点时会建边
						edge = outEdgeList.get(character);
					} else {
						edge = new DictEdge(this, node);
						outEdgeList.put(character, edge);
						node.inEdgeList.put(this.character, edge);
					}
				}
			}
		}
		edge.increase();

		return edge.getOutNode();
	}
	
	public Character getCharacter() {
		return character;
	}
	
	public long getCount() {
		return count;
	}
	
	public long getInCount() {
		return inCount;
	}
	
	public long getOutCount() {
		return outCount;
	}
	
	public Collection<DictEdge> getInEdges() {
		return inEdgeList.values();
	}
	
	public Collection<DictEdge> getOutEdges() {
		return outEdgeList.values();
	}
	
	@Override
	public String toString() {
		return character + ":" + count + ":" + inCount + ":" + outCount + "\n" + inEdgeList.size() + ":" + outEdgeList.size();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DictNode) {
			DictNode obj = (DictNode) o;
			if (this.character == obj.getCharacter())
				return true;
		}
		return false;
	}
}
