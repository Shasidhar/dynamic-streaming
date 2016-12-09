#!/usr/bin/env bash

cd $ZK_HOME
bin/zkServer.sh start
bin/zkcli.sh


create /meetup/test "sample"
set /meetup/test "change"