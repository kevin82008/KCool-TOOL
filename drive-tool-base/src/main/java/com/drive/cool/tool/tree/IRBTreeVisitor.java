package com.drive.cool.tool.tree;

public interface IRBTreeVisitor<T extends IRBTreeNode<T>> {
	
	/**
	 * 访问单个节点
	 * @param node 当前访问的节点
	 */
	public void visit(RBTreeNode<T> node);
	
	
	/**
	 * 是否访问下一个节点
	 * @param node 当前访问的节点
	 * @return true继续访问 false退出访问
	 */
	public boolean visitNext(RBTreeNode<T> node);
	
}
