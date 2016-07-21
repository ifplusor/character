package psn.ifplusor.character;

import psn.ifplusor.character.dict.Dictionary;

public class App {
	
    public static void main( String[] args ) {
    	
    	Dictionary dictionary = Dictionary.getDictionary();
    	
    	dictionary.inputCorpus("2.txt", "GB2312");
    	//dictionary.printDict();
    	
    	dictionary.storeToFile(true);
    	
    	System.out.println("Succeed!");
    }
}
