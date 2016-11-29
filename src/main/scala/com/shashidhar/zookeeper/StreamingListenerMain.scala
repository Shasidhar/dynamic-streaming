package com.shashidhar.zookeeper

import java.util.UUID
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor, ExecutorService}

import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.codehaus.jackson.map.ObjectMapper

/**
 * Created by shashidhar on 22/11/16.
 */
object StreamingListenerMain {
  def main(args: Array[String]) {

    val objectMapper = new ObjectMapper

    val executorService = new ThreadPoolExecutor(10,100, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable])

    val zkConnectionString = "localhost:2181"

    val configPath = "/test"

    val client = CuratorFrameworkFactory.newClient(zkConnectionString,
      new ExponentialBackoffRetry(1000, 3))

    client.start()

    executorService.submit(new ListenerHandler(new ZookeeperConfigSource(client,configPath),new StreamingDriver()))

  }

}

class ListenerHandler(source:ZookeeperConfigSource,streamingDriver:StreamingDriver) extends Runnable
{
  override def run(): Unit = {
    val listener = new ZookeeperUpdateListener(source,streamingDriver)
    listener.init()
    streamingDriver.startContext("initial dummy data")
  }
}

