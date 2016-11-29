package com.shashidhar.zookeeper

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable
/**
 * Created by shashidhar on 28/11/16.
 */
class StreamingDriver() extends Runnable
{

  var ssc : StreamingContext = _

  override def run(): Unit = {
//    startContext("initial dummy data")
  }

  def startContext(currentData:String): Unit = {
    val sc = new SparkContext("local[2]", "temp")
    ssc = new StreamingContext(sc, Seconds(10))
    val lines = mutable.Queue[RDD[String]]()
    val textRDD = sc.parallelize(mutable.Seq(currentData))
    lines += textRDD

    val dStream = ssc.queueStream(lines,true)
    dStream.print()

    ssc.start()
  }

  def stopContext(): Unit = {
    ssc.stop(true,true)
  }
}
