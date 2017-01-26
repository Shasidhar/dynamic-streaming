# Dynamic-streaming
Interactive data analysis with Spark streaming session code repo

##PREREQUISITE

* Java
* Scala
* Maven
* Zookeeper

##Building and Running Application

 * Load project into IDE (eclipse/intellij) as a Maven Project
 * Create a zNode in path */meetup/data*
 * Go to -> ZookeeperCRUD.scala to run from IDE as main program
 * Once you start main program, add/delete a node under */meetup/data*
 * When ever data changes you should see streaming context is restarted and printing the latest data from Zookeeper into IDE console
 
 NOTE: Refer [Handso-On](https://github.com/Shasidhar/dynamic-streaming/blob/streaming-listener/src/main/resources/handson.sh) for zookeeper starting/stopping, Creating/Setting data to a node
 
