package com.shashidhar.zookeeper

import java.util.concurrent.{Executors, TimeUnit, LinkedBlockingQueue, ThreadPoolExecutor}

import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.recipes.cache.{NodeCache, NodeCacheListener}
import org.apache.curator.retry.ExponentialBackoffRetry

/**
 * Created by shashidhar on 2/12/16.
 */
object NodeCacheDemo {
  def main(args: Array[String]) = {
    val zkConnectionString = "localhost:2181"
    val client = CuratorFrameworkFactory.newClient(zkConnectionString,
      new ExponentialBackoffRetry(1000, 3))
    client.start()

    val znodePath = "/test"
    println("original data:"+new String(client.getData.forPath(znodePath)))

    /* Zookeeper NodeCache service to get properties from ZNode */
    val nodeCache = new NodeCache(client, znodePath)
    nodeCache.getListenable.addListener(new NodeCacheListener {
      @Override
      def nodeChanged() = {
        try {
          val currentData = nodeCache.getCurrentData
          println("new data:"+new String(currentData.getData))
        } catch {
          case ex: Exception => println("Exception while fetching properties from zookeeper ZNode, reason " + ex.getCause)
        }
      }
      nodeCache.start()
    })
  }

  val executorService = Executors.newFixedThreadPool(10)
  executorService.submit(Thread.currentThread())
}
