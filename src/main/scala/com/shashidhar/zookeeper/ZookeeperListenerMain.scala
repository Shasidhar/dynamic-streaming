package com.shashidhar.zookeeper

import java.util.UUID
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor, ExecutorService}

import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.spark.SparkContext
import org.codehaus.jackson.map.ObjectMapper

/**
 * Created by shashidhar on 22/11/16.
 */
object ZookeeperListenerMain {
  def main(args: Array[String]) {

    val objectMapper = new ObjectMapper

    val executorService = new ThreadPoolExecutor(10,100, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable])
    executorService.allowCoreThreadTimeOut(true)

    executorService.submit(Thread.currentThread())

    val zkConnectionString = "localhost:2181"

    val configPath = "/meetup/pathcahce"

    val client = CuratorFrameworkFactory.newClient(zkConnectionString,
      new ExponentialBackoffRetry(1000, 3))

    client.start()

    val source = new ZookeeperConfigSource(client,configPath)

    val listener = new ZookeeperUpdateListener(source)

    listener.init()

  }


}

