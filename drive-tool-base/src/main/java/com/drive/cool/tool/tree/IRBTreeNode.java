package com.drive.cool.tool.tree;

import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public interface IRBTreeNode<T> extends Comparable<T> {
	/**
	 * 获取唯一键，插入到树节点时该值不能有重复
	 * @return
	 */
	public String getKey();
	
	/**
	 * 获取读锁
	 * @return
	 */
	public ReadLock getReadLock();
	
	/**
	 * 获取写锁
	 * @return
	 */
	public WriteLock getWriteLock();
}
