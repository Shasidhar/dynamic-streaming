package com.shashidhar.zookeeper

import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor}

import com.netflix.config.{WatchedUpdateResult, WatchedUpdateListener}

/**
 * Created by shashidhar on 28/10/15.
 */
class ZookeeperUpdateListener(watchedSource:ZookeeperConfigSource,
                               sparkDriver:StreamingDriver) extends WatchedUpdateListener
{
  val executorService = new ThreadPoolExecutor(10,100, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable])

  def init():Unit={
    watchedSource.start()
    watchedSource.addUpdateListener(this)
  }

  override def updateConfiguration(result: WatchedUpdateResult): Unit = {
    executorService.submit(new StreamingHandler(sparkDriver, watchedSource.getCurrentData.toString))
  }
}

class StreamingHandler(sDriver:StreamingDriver, currentData:String) extends Runnable
{
  override def run(): Unit = {
    sDriver.stopContext()
    sDriver.startContext(currentData)
  }
}
