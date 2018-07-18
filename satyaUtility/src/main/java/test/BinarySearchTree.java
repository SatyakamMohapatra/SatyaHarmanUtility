package test;

public class BinarySearchTree {
	//Create a node to hold key and left node and right node
	class Node{
		int key;
		Node left,right;

		Node(int key){
			this.key = key;
			left = right = null;
		}
	}
	Node base;
	BinarySearchTree(){
		base = null;
	}
	void insert(int key){
		base = insertRec(base,key);
	}
	
	private Node insertRec(Node base,int key){
		//if it is a empty BST then insert the base node
		if(base == null){
			base = new Node(key);
			return base;
		}
		//if it is not empty insert the next element accordingly
		if(key > base.key){
			base.right = insertRec(base.right, key);
		}else if (key < base.key) {
			base.left = insertRec(base.right, key);
		}
		return base;
	}
	
	void search(){
		searchInRec(base);
	}
	private void searchInRec(Node base){
		if(base!=null){
		searchInRec(base.left);
		System.out.println(base.key);
		searchInRec(base.right);
		}
	}
	boolean contains(int key){
		pos = 0;
		if(contains(key,base)>0){
			return true;
		}
		return false;
	}
	int pos = 0;
	private int contains(int key,Node base){
		if(base == null){
			return pos;
		}
		if(key == base.key){
			return pos ++;
		}
		if(key > base.key){
			contains(key,base.right);
		}else if(key < base.key){
			contains(key,base.left);	
		}
		return pos;
	}
}
