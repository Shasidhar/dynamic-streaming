package com.shashidhar.zookeeper

import com.netflix.config.{WatchedUpdateResult, WatchedUpdateListener}

/**
 * Created by shashidhar on 28/10/15.
 */
class ZookeeperUpdateListener(watchedSource:ZookeeperConfigSource) extends WatchedUpdateListener
{
  def init():Unit={
    watchedSource.start()
    watchedSource.addUpdateListener(this)
  }

  override def updateConfiguration(result: WatchedUpdateResult): Unit = {
    println("data changed")
    println(watchedSource.getCurrentData)
  }
}
