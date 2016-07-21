package psn.ifplusor.character.dict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import psn.ifplusor.character.core.CharacterUtil;

public class Dictionary {
	
	static Dictionary singleton = null;
	
	public static Dictionary getDictionary() {
		if(singleton == null) {
			synchronized(Dictionary.class) {
				if(singleton == null) {
					singleton = new Dictionary();
					return singleton;
				}
			}
		}
		return singleton;
	}
	
	private Dictionary() {
		
	}
	
	/**
	 * 读取语料
	 * @param path
	 */
	public void inputCorpus(String path, String charset) {
		
		//读取语料
        InputStream is;
		try {
			is = new FileInputStream(path);
		} catch (FileNotFoundException e2) {
			throw new RuntimeException(path + ": Corpus not found!!!");
		}
        
		long count = 0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , charset), 512);
			String line = null;
			
			DictNode preNode = DictNode.getNode('^');
			while ((line = br.readLine()) != null) {
				for (char ch : line.toCharArray()) {
					if (CharacterUtil.identifyCharType(ch) == CharacterUtil.CHAR_CHINESE) {
						if (preNode.getCharacter() == '^') {
							preNode.increaseCount();
						}
						preNode = preNode.appendNode(ch);
						count++;
					} else {
						if (preNode.getCharacter() != '^') {
							preNode.appendNode('$');
							preNode = DictNode.getNode('^');
						}
					}
				}
			}
			if (preNode.getCharacter() != '^') {
				preNode.appendNode('$');
			}
		} catch (IOException ioe) {
			System.err.println(path + ": Corpus loading exception.");
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null) {
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Count: " + count);
	}
	
	public void printDict() {
		Collection<DictEdge> edges = DictNode.getNode('^').getOutEdges();
		
		int num = 1;
		long count = 0;
		for (DictEdge edge : edges) {
			DictNode node = edge.getOutNode();
			count += node.getCount();
			
			System.out.println(num++ + " - " + node);
		}
		
		System.out.println("Count: " + count);
		System.out.println("Number: " + edges.size());
	}
	
	/**
	 * 将结点数据存入临时文件
	 * @param node
	 * @param useBase
	 * @throws IOException
	 */
	public void storeNodeToFile(DictNode node, boolean useBase) throws IOException {
		String base = "." + File.separator;
		if (useBase) {
			base += "store" + File.separator;
			File parentFile = new File(base);
			if (!parentFile.exists()) {
				if (!parentFile.mkdir()) {
					throw new IOException(parentFile.getPath() + ": 创建目录失败!");
				}
			}
		}
		
		File file = new File(base + node.getCharacter() + ".tmp");
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(node + "\n");
			for (DictEdge edge : node.getOutEdges()) {
				fw.write(edge + "\n");
			}
			for (DictEdge edge : node.getInEdges()) {
				fw.write(edge + "\n");
			}
		} catch (FileNotFoundException fe) {
			throw new FileNotFoundException(file.getPath() + ": 文件不存在!");
		} catch (IOException e) {
			throw e;
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fw = null;
			}
		}
		
		// 统一修改临时文件名
		File file2 = new File(base + node.getCharacter());
		try {
			Files.move(file.toPath(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void storeToFile(boolean storeSuper) {
		Collection<DictEdge> edges = DictNode.getNode('^').getOutEdges();
		
		try {
			for (DictEdge edge : edges) {
				if (edge.getOutNode().getCharacter() == '$')
					continue;
				
				DictNode node = edge.getOutNode();
				storeNodeToFile(node, true);
			}
			
			if (storeSuper) {
				storeNodeToFile(DictNode.getNode('^'), false);
				storeNodeToFile(DictNode.getNode('$'), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void restoreFromFile() throws Exception {
		String base = "." + File.separator;
		File parentFile = new File(base);
		
		if (!parentFile.isDirectory()) {
			throw new Exception("Path: '" + base + "' is not directiory!");
		}
		
		for (File file : parentFile.listFiles()) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				
				// row 1 - char:count:inCount:outCount
		        String line = reader.readLine();
		        if (line == null) {
		        	throw new Exception("error: File format is not correct!");
		        }
		        String nodeData[] = line.split(":");
		        Character character = new Character(nodeData[0].charAt(0));
		        long count = Long.parseLong(nodeData[1]);
		        long inCount = Long.parseLong(nodeData[2]);
		        long outCount = Long.parseLong(nodeData[3]);
		        
		        // row 2 - inSize:outSize
		        line = reader.readLine();
		        if (line == null) {
		        	throw new Exception("error: File format is not correct!");
		        }
		        String listSize[] = line.split(":");
		        
		        
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				reader = null;
			}
		}
	}
}
