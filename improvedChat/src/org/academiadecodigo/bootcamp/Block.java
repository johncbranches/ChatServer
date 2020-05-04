package org.academiadecodigo.bootcamp;


// Singleton class, for using in synchronised
public class Block {
    private static Block block;
    private Block(){

    }
    public static Block getBlock(){
        if (block==null){
            block = new Block();
        }
        return block;
    }
}
