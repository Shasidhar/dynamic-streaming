package com.shashidhar.zookeeper

import java.nio.charset.Charset
import java.util
import java.util.concurrent.CopyOnWriteArrayList

import com.netflix.config.{WatchedUpdateResult, WatchedUpdateListener, WatchedConfigurationSource}
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache._
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type
import scala.collection.JavaConversions._

/**
 * Created by shashidhar on 28/10/15.
 */
class ZookeeperConfigSource(val client:CuratorFramework,
                            val configRootPath:String) extends WatchedConfigurationSource
{
  val pathChildrenCache = new PathChildrenCache(client,configRootPath,true)

  val charset = Charset.forName("UTF-8")

  val listeners = new CopyOnWriteArrayList[WatchedUpdateListener]()

  def removeRootPath(nodepath:String): String ={
    nodepath.replace(configRootPath+"/","")
  }

  def start(): Unit ={
    println("inside ZookeeperConfigSource start")
    pathChildrenCache.getListenable.addListener(new PathChildrenCacheListener {
      override def childEvent(client: CuratorFramework, event: PathChildrenCacheEvent): Unit ={

        println("inside event")

        val eventType = event.getType
        val data = event.getData

        if(data!=null){
         val path= data.getPath

          // remove configRootPath out of the key name
          val key = removeRootPath(path)

          val value = data.getData
          val stringValue = new String(value, charset)

          val added = scala.collection.mutable.Map[String, Object]()
          val changed = scala.collection.mutable.Map[String, Object]()
          val deleted = scala.collection.mutable.Map[String, Object]()

          if (eventType == Type.CHILD_ADDED) {
            added += (key -> stringValue)
          } else if (eventType == Type.CHILD_UPDATED) {
            changed += key -> stringValue
          } else if (eventType == Type.CHILD_REMOVED) {
            deleted += key -> stringValue
          }

          val result = WatchedUpdateResult.createIncremental(added,changed,deleted)

          fireEvent(result)

        }
      }
    })
    pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE)
  }


 def fireEvent(result:WatchedUpdateResult):Unit= {
   println("updated listeners")
    for(l<-listeners){
      l.updateConfiguration(result)
    }
  }

  override def removeUpdateListener(l: WatchedUpdateListener): Unit = {
    if(l!=null){
      listeners.remove(l)
    }
  }

  override def getCurrentData: util.Map[String, Object] = {
    val children = pathChildrenCache.getCurrentData
    val all = scala.collection.mutable.Map[String, Object]()
    for (child <- children) {
      val path = child.getPath
      val key = removeRootPath(path)
      val value = child.getData
      all.put(key, new String(value, charset))
    }
    all
  }

  override def addUpdateListener(l: WatchedUpdateListener): Unit = {
    if(l!=null){
      listeners.add(l)
    }
  }
}
