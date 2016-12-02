package com.shashidhar.zookeeper

import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.codehaus.jackson.map.ObjectMapper

/**
 * Created by shashidhar on 2/12/16.
 */
object ZookeeperCRUD {
  def main(args: Array[String]) {
    val objectMapper = new ObjectMapper

    val zkConnectionString = "localhost:2181"
    val client = CuratorFrameworkFactory.newClient(zkConnectionString,
      new ExponentialBackoffRetry(1000, 3))

    client.start()

    val configPath = "/meetup/data"
    val zkData = "first node"

    println(client.checkExists().forPath(configPath))

    //Write data
    client.create().creatingParentsIfNeeded().forPath(configPath,objectMapper.writeValueAsBytes(zkData))

    //Read data
    println(objectMapper.readValue(client.getData.forPath(configPath),classOf[String]))

    //Delete node
    client.delete().forPath(configPath)
  }
}
