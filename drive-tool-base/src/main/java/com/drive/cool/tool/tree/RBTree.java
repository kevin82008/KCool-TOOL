package com.drive.cool.tool.tree;

import java.util.HashMap;

public class RBTree<T extends IRBTreeNode<T>> {
	private final RBTreeNode<T> NIL = new RBTreeNode<T>(null, null, null,
			RBTreeColor.BLACK, null);
	private RBTreeNode<T> root;

	/**
	 * 因为操作tree都需要加锁，这里未考虑线程安全问题
	 */
	private HashMap<String, RBTreeNode<T>> nodeMap = new HashMap<String, RBTreeNode<T>>();

	public RBTreeNode<T> getRoot() {
		return root;
	}
	
	public RBTree() {
		root = NIL;
	}

	public void batchInsertNode(T[] array) {
		for (int i = 0; i < array.length; i++) {
			RBTreeNode<T> node = createNewNode(array[i]);
			insertNode(node);
		}
	}

	/**
	 * 清除所有数据
	 */
	public void clean(){
		this.nodeMap = new HashMap<String, RBTreeNode<T>>();
		root = NIL;
	}
	
	/**
	 * 插入单个节点
	 * 
	 * @param data
	 */
	public void insertNode(T data) {
		RBTreeNode<T> node = createNewNode(data);
		insertNode(node);
	}

	private RBTreeNode<T> createNewNode(T data) {
		RBTreeNode<T> node = new RBTreeNode<T>();
		node.setParent(getNIL());
		node.setLeft(getNIL());
		node.setRight(getNIL());
		node.setRed();
		node.setData(data);
		return node;
	}

	// 插入节点
	private void insertNode(RBTreeNode<T> node) {
		nodeMap.put(node.getData().getKey(), node);

		RBTreeNode<T> previous = NIL;
		RBTreeNode<T> temp = root;

		while (temp != NIL) {
			previous = temp;
			temp = temp.getData().compareTo(node.getData()) < 0 ? temp
					.getRight() : temp.getLeft();
		}
		node.setParent(previous);

		if (previous == NIL) {
			root = node;
			root.setParent(NIL);
		} else if (previous.getData().compareTo(node.getData()) > 0) {
			previous.setLeft(node);
		} else {
			previous.setRight(node);
		}

		node.setLeft(NIL);
		node.setRight(NIL);
		node.setRed();
		insertFixup(node);
	}

	// 插入节点后的调整
	private void insertFixup(RBTreeNode<T> node) {

		while (node.getParent().isRed()) {

			if (node.getParent() == node.getParent().getParent().getLeft()) {

				RBTreeNode<T> rightNuncle = node.getParent().getParent()
						.getRight();

				if (rightNuncle.isRed()) { // Case 1
					rightNuncle.setBlack();
					node.getParent().setBlack();
					node.getParent().getParent().setRed();
					node = node.getParent().getParent();

				} else if (node == node.getParent().getRight()) { // case 2

					node = node.getParent();
					leftRotate(node);

				} else { // case 3

					node.getParent().setBlack();
					node.getParent().getParent().setRed();

					rightRotate(node.getParent().getParent());

				}

			} else {

				RBTreeNode<T> leftNuncle = node.getParent().getParent()
						.getLeft();

				if (leftNuncle.isRed()) { // case 4

					leftNuncle.setBlack();
					node.getParent().setBlack();
					node.getParent().getParent().setRed();
					node = node.getParent().getParent();

				} else if (node == node.getParent().getLeft()) { // case 5

					node = node.getParent();
					rightRotate(node);

				} else { // case 6

					node.getParent().setBlack();
					node.getParent().getParent().setRed();
					leftRotate(node.getParent().getParent());

				}

			}

		}

		root.setBlack();
	}

	/**
	 * 根据key删除节点
	 * 
	 * @param key
	 * @return
	 */
	public boolean deleteNode(String key) {
		RBTreeNode<T> node = getNode(key);
		return deleteNode(node);
	}

	/**
	 * 根据数据删除节点
	 * 
	 * @param data
	 * @return
	 */
	public boolean deleteNode(T data) {
		RBTreeNode<T> node = getNode(data);
		return deleteNode(node);
	}

	// 删除节点
	public boolean deleteNode(RBTreeNode<T> node) {
		RBTreeNode<T> temp = NIL;
		RBTreeNode<T> child = NIL;
		if (node == null) {
			return false;
		} else {
			nodeMap.remove(node.getData().getKey());
			if (node.getLeft() == NIL || node.getRight() == NIL) {
				temp = node;
			} else {
				temp = successor(node);
			}

			if (temp.getLeft() != NIL) {
				child = temp.getLeft();
			} else {
				child = temp.getRight();
			}

			child.setParent(temp.getParent());

			if (temp.getParent() == NIL) {
				root = child;
			} else if (temp == temp.getParent().getLeft()) {
				temp.getParent().setLeft(child);
			} else {
				temp.getParent().setRight(child);
			}

			if (temp != node) {
				nodeMap.put(temp.getData().getKey(), node);
				node.setData(temp.getData());
			}

			if (temp.isBlack()) {
				deleteFixup(child);
			}
			return true;
		}

	}

	// 删除节点后的调整
	private void deleteFixup(RBTreeNode<T> node) {

		while (node != root && node.isBlack()) {
			if (node == node.getParent().getLeft()) {
				RBTreeNode<T> rightBrother = node.getParent().getRight();
				if(NIL == rightBrother){
					node = node.getParent();
					continue;
				}
				if (rightBrother.isRed()) { // case 1
					// node节点为左孩子，node节点的兄弟为RED
					rightBrother.setBlack();
					node.getParent().setRed();
					leftRotate(node.getParent());
					rightBrother = node.getParent().getRight();
				}
				if(NIL == rightBrother){
					node = node.getParent();
					continue;
				}
				if (rightBrother.getLeft().isBlack()
						&& rightBrother.getRight().isBlack()) {
					rightBrother.setRed();
					node = node.getParent();
				} else if (rightBrother.getRight().isBlack()) {
					rightBrother.getLeft().setBlack();
					rightBrother.setRed();
					rightRotate(rightBrother);
					rightBrother = node.getParent().getRight();
				} else {
					rightBrother.setColor(node.getParent().getColor());
					node.getParent().setBlack();
					rightBrother.getRight().setBlack();
					leftRotate(node.getParent());
					node = root;
				}

			} else {

				RBTreeNode<T> leftBrother = node.getParent().getLeft();
				if(NIL == leftBrother){
					node = node.getParent();
					continue;
				}
				if (leftBrother.isRed()) {
					leftBrother.setBlack();
					node.getParent().setRed();
					rightRotate(node.getParent());
					leftBrother = node.getParent().getLeft();
				}
				if(NIL == leftBrother){
					node = node.getParent();
					continue;
				}
				if (leftBrother.getLeft().isBlack()
						&& leftBrother.getRight().isBlack()) {
					leftBrother.setRed();
					node = node.getParent();

				} else if (leftBrother.getLeft().isBlack()) {

					leftBrother.setRed();
					leftBrother.getRight().setBlack();
					leftRotate(leftBrother);
					leftBrother = node.getParent().getLeft();

				} else {

					leftBrother.setColor(node.getParent().getColor());
					node.getParent().setBlack();
					leftBrother.getLeft().setBlack();
					rightRotate(node.getParent());
					node = root;

				}

			}

		}

		node.setBlack();
	}

	// 查找节点node的后继节点

	public RBTreeNode<T> successor(RBTreeNode<T> node) {

		RBTreeNode<T> rightChild = node.getRight();
		if (rightChild != NIL) {
			RBTreeNode<T> previous = null;
			while (rightChild != NIL) {
				previous = rightChild;
				rightChild = rightChild.getLeft();
			}
			return previous;
		} else {

			RBTreeNode<T> parent = node.getParent();
			while (parent != NIL && node != parent.getLeft()) {
				node = parent;
				parent = parent.getParent();
			}

			return parent;

		}
	}

	// 查找节点
	public RBTreeNode<T> getNode(T data) {
		String key = data.getKey();
		return getNode(key);
	}

	public RBTreeNode<T> getNode(String key) {
		return nodeMap.get(key);
	}

	/**
	 * 根据key查找数据
	 * 
	 * @param key
	 * @return
	 */
	public T getDataByKey(String key) {
		RBTreeNode<T> node = getNode(key);
		return null == node ? null : node.getData();
	}

	// 左转函数
	private void leftRotate(RBTreeNode<T> node) {
		RBTreeNode<T> parent = node.getParent();
		RBTreeNode<T> rightNode = node.getRight();

		node.setRight(rightNode.getLeft());
		if (rightNode.getLeft() != NIL) {
			rightNode.getLeft().setParent(node);
		}
		rightNode.setParent(parent);

		if (parent == NIL) {
			root = rightNode;
		} else if (node == parent.getLeft()) {
			parent.setLeft(rightNode);
		} else {
			parent.setRight(rightNode);
		}

		rightNode.setLeft(node);
		node.setParent(rightNode);

	}

	// 右转函数
	private void rightRotate(RBTreeNode<T> node) {

		RBTreeNode<T> leftNode = node.getLeft();
		node.setLeft(leftNode.getRight());

		if (leftNode.getRight() != null) {
			leftNode.getRight().setParent(node);
		}

		leftNode.setParent(node.getParent());

		if (node.getParent() == NIL) {
			root = leftNode;
		} else if (node == node.getParent().getLeft()) {
			node.getParent().setLeft(leftNode);
		} else {
			node.getParent().setRight(leftNode);
		}

		leftNode.setRight(node);
		node.setParent(leftNode);

	}

	
	/**
	 * 获取下一个节点的数据，如果没有下个节点了 返回null 
	 * @param T
	 * @return T
	 */
	public T getNext(T curr){
		RBTreeNode<T> node = this.nodeMap.get(curr.getKey());
		if(null == node) return null;
		if(root == node && node.getRight().isNIL()) return null;
		RBTreeNode<T> tmpNode = node.getRight();
		//如果没有右节点
		//1. 当前节点是父节点的左边孩子，返回父节点
		//2. 当前节点是父节点的右边孩子，父节点的父节点的左节点等于父节点，返回父节点的父节点
		//3. 其他返回null
		if(tmpNode.isNIL()){
			if(node == node.getParent().getLeft()){
				return node.getParent().getData();
			}else{
				tmpNode = node.getParent();
				while(!tmpNode.isNIL()){
					if(!tmpNode.getParent().isNIL() && tmpNode == tmpNode.getParent().getLeft()){
						return tmpNode.getParent().getData();
					}else{
						tmpNode = tmpNode.getParent();
					}
				}
				return null;
			} 
		}
		
		while(!tmpNode.getLeft().isNIL()){
			tmpNode = tmpNode.getLeft();
		}
		return tmpNode.getData();
	}
	
	/**
	 * 获取上个节点的数据，如果没有上个节点了，返回null
	 * @param T
	 * @return T
	 */
	public T getPre(T curr){
		RBTreeNode<T> node = this.nodeMap.get(curr.getKey());
		if(null == node) return null;
		if(root == node && node.getLeft().isNIL()) return null;
		RBTreeNode<T> tmpNode = node.getLeft();
		//如果没有左节点
		//1. 当前节点是父节点的右边孩子，返回左节点
		//2. 当前节点是父节点的左边孩子，如果父节点是其父节点的右节点，返回父节点的父节点
		//3. 否则返回null
		if(tmpNode.isNIL()){
			if(node == node.getParent().getRight()){
				return node.getParent().getData();
			}else{
				tmpNode = node.getParent();
				while(!tmpNode.isNIL()){
					if(!tmpNode.getParent().isNIL() && tmpNode == tmpNode.getParent().getRight()){
						return tmpNode.getParent().getData();
					}else{
						tmpNode = tmpNode.getParent();
					}
				}
				return null;
			}
		}
		
		while(!tmpNode.getRight().isNIL()){
			tmpNode = tmpNode.getRight();
		}
		return tmpNode.getData();
	}
	
	//访问树
	public void visitTree(IRBTreeVisitor<T> vistor) {
		inOrderTraverse(root,vistor);
	}


	/**
	 * 中序访问树节点
	 * @param node 当前访问的节点
	 * @param vistor 访问者 访问前先加读锁
	 * @return
	 */
	private boolean inOrderTraverse(RBTreeNode<T> node,IRBTreeVisitor<T> vistor) {
		if (node == NIL) {
			return true;
		}
		
		if(!inOrderTraverse(node.getLeft(),vistor)){
			return false;
		}
		T data = node.getData();
		data.getReadLock().lock();
		try{
			vistor.visit(node);
		}finally{
			data.getReadLock().unlock();
		}
		return vistor.visitNext(node)?inOrderTraverse(node.getRight(),vistor):false;
	}

	public RBTreeNode<T> getNIL() {
		return NIL;
	}

	public HashMap<String, RBTreeNode<T>> getAllNode(){
		return this.nodeMap;
	}
	
	public int getSize() {
		return this.nodeMap.size();
	}

}