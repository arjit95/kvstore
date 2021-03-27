package main

import (
	"errors"
	"log"
	"sync"

	"github.com/arjit95/kvstore/go/pkg/discovery"
	"github.com/arjit95/kvstore/go/pkg/discovery/zookeeper"
	"github.com/arjit95/kvstore/go/pkg/discovery/zookeeper/utils"
	"github.com/arjit95/kvstore/go/pkg/ring"
	"github.com/serialx/hashring"
)

type dataCenter struct {
	partitions map[string]*Partition
	client     *zookeeper.Client
	hr         *hashring.HashRing
	mutex      sync.Mutex
}

func (dc *dataCenter) calculateResources(prefix string) error {
	children, err := dc.client.Children(prefix)
	if err != nil {
		return err
	}

	dc.hr = ring.CreateHashRing()

	for _, child := range children {
		server, err := dc.client.GetServer(utils.GetChildPath(prefix, child))
		if err != nil {
			return err
		}

		dc.OnAdd(server)
	}

	log.Println("Created hashring with", len(dc.partitions), "partitions")
	return nil
}

func (dc *dataCenter) OnRemove(server discovery.Server) error {
	if val, ok := dc.partitions[server.Name()]; ok {
		removed := val.removeReplica(server)

		if val.count() == 0 {
			if removed {
				log.Println("Removing parition", server.Name(), "from hashring")
				dc.mutex.Lock()
				dc.hr = dc.hr.RemoveNode(val.Name())
				dc.mutex.Unlock()
			}

			delete(dc.partitions, server.Name())
		}
	} else {
		return errors.New("server not found in hashring")
	}

	return nil
}

func (dc *dataCenter) OnError(err error) {
	log.Println("Error occurred", err)
}

func (dc *dataCenter) OnAdd(server discovery.Server) error {
	if _, ok := dc.partitions[server.Name()]; !ok {
		partition := createPartition(server.Name())
		dc.partitions[server.Name()] = partition

		dc.mutex.Lock()
		dc.hr = dc.hr.AddNode(partition.Name())
		dc.mutex.Unlock()
	}

	dc.partitions[server.Name()].addReplica(server)
	return nil
}

func (dc *dataCenter) addEntry(key string, value []byte) error {
	partition, ok := dc.hr.GetNode(key)
	err := errors.New("cannot find partition for the key")
	if !ok {
		return err
	}

	server := dc.partitions[partition].replicas.Peek()
	if server == nil {
		return err
	}

	return server.Put(key, value)
}

func (dc *dataCenter) getEntry(key string) ([]byte, error) {
	partition, ok := dc.hr.GetNode(key)
	if !ok {
		return nil, errors.New("cannot find partition for the key")
	}

	// TODO: Add strategy support round robin, random, least loaded etc.
	server := dc.partitions[partition].replicas.Random()
	if server == nil {
		return nil, errors.New("cannot find partition for data")
	}

	return server.Get(key)
}

func createDataCenter(client *zookeeper.Client) *dataCenter {
	return &dataCenter{
		client:     client,
		partitions: make(map[string]*Partition),
	}
}
