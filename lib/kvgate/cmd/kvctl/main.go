package main

import (
	"log"
	"sync"

	"github.com/arjit95/kvstore/discovery/zookeeper"
)

func main() {
	client := zookeeper.CreateConnection([]string{"localhost:2181"})
	dc := createDataCenter(client)
	err := dc.calculateResources("/leader")
	if err != nil {
		log.Fatal("Cannot calculate resources:", err)
	}

	watcher := client.CreateWatcher("/leader")
	watcher.Watch(dc)

	wg := &sync.WaitGroup{}
	wg.Add(1)
	wg.Wait()
}
