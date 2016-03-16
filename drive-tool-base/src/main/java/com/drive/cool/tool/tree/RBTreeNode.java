package com.drive.cool.tool.tree;

public class RBTreeNode<T extends IRBTreeNode<T>> {
	private RBTreeNode<T> left;
	private RBTreeNode<T> right;
	private RBTreeNode<T> parent;
	private RBTreeColor color;
	private T data;

	public RBTreeNode(RBTreeNode<T> left, RBTreeNode<T> right,
			RBTreeNode<T> parent, RBTreeColor color, T data) {
		super();
		this.left = left;
		this.right = right;
		this.parent = parent;
		this.color = color;
		this.data = data;
	}

	public RBTreeNode() {
	}

	public RBTreeNode(T data) {
		this(null, null, null, null, data);
	}

	public RBTreeNode<T> getLeft() {
		return left;
	}

	public void setLeft(RBTreeNode<T> left) {
		this.left = left;
	}

	public RBTreeNode<T> getRight() {
		return right;
	}

	public void setRight(RBTreeNode<T> right) {
		this.right = right;
	}

	public RBTreeNode<T> getParent() {
		return parent;
	}

	public void setParent(RBTreeNode<T> parent) {
		this.parent = parent;
	}

	public RBTreeColor getColor() {
		return color;
	}

	public void setColor(RBTreeColor color) {
		this.color = color;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setRed(){
		this.color = RBTreeColor.RED;
	}
	
	public void setBlack(){
		this.color = RBTreeColor.BLACK;
	}
	
	public boolean isRed(){
		return this.color == RBTreeColor.RED;
	}
	
	public boolean isBlack(){
		return this.color == RBTreeColor.BLACK;
	}
	
	
	public String toString() {
		if (null != getData()) {
			return getData().toString() + " " + getColor();
		} else {
			return "空节点";
		}

	}

	/**
	 * 是否空节点
	 * 只要有任意一个子节点是null就认为是空节点
	 * @return
	 */
	public boolean isNIL(){
		return null == this.getData();
	}
}