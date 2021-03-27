package main

import (
	"flag"
	"log"
	"math/rand"
	"strconv"
	"strings"
	"time"

	"github.com/arjit95/kvstore/go/pkg/discovery/zookeeper"
)

var dc *dataCenter

func main() {
	flag.Parse()
	rand.Seed(time.Now().Unix())

	zkString := strings.Split(flag.Arg(0), ",")
	zc := zookeeper.CreateConnection(zkString)
	dc = createDataCenter(zc)
	err := dc.calculateResources("/leader")
	if err != nil {
		log.Fatal("Cannot calculate resources:", err)
	}

	watcher := zc.CreateWatcher("/leader")
	watcher.Watch(dc)

	port := 3000
	if flag.NArg() > 1 {
		portNum := flag.Arg(1)
		i, err := strconv.Atoi(portNum)
		if err != nil {
			panic(err)
		}

		port = i
	}

	startServer(port)
}
