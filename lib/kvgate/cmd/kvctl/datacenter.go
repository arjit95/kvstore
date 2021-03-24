package main

import (
	"errors"
	"log"

	"github.com/arjit95/kvstore/discovery/zookeeper"
	"github.com/arjit95/kvstore/discovery/zookeeper/utils"
	"github.com/arjit95/kvstore/ring"
)

type dataCenter struct {
	servers map[string][]zookeeper.Server
	client  *zookeeper.Client
	hr      *ring.Hashring
}

func (dc *dataCenter) paritions() int {
	return len(dc.servers)
}

func (dc *dataCenter) replicas() int {
	if len(dc.servers) == 0 {
		return 0
	}

	min := 999999999

	for _, v := range dc.servers {
		if min > len(v) {
			min = len(v)
		}
	}

	return min
}

func (dc *dataCenter) calculateResources(prefix string) error {
	children, err := dc.client.Children(prefix)
	if err != nil {
		return err
	}

	for _, child := range children {
		server, err := dc.client.GetServer(utils.GetChildPath(prefix, child))
		if err != nil {
			return err
		}

		err = dc.OnAdd(server)
		if err != nil {
			return err
		}
	}

	dc.hr = ring.CreateHashRing(dc.paritions(), dc.replicas())
	log.Print("Creating hashring with ", dc.paritions(), " partitions and ", dc.replicas(), " replicas ")
	return nil
}

func (dc *dataCenter) getServerIdx(server zookeeper.Server) int {
	if val, ok := dc.servers[server.Name()]; ok {
		for i, s := range val {
			if s.Path() == server.Path() {
				return i
			}
		}
	}

	return -1
}

func (dc *dataCenter) OnRemove(server zookeeper.Server) error {
	idx := dc.getServerIdx(server)

	if idx < 0 {
		return errors.New("Server not found in hashring")
	}

	servers := dc.servers[server.Name()]
	dc.servers[server.Name()] = append(servers[:idx], servers[idx+1])
	if len(dc.servers[server.Name()]) == 0 {
		delete(dc.servers, server.Name())
		log.Print("Removed all partitions for server", server.Name())
	}

	return nil
}

func (dc *dataCenter) OnError(err error) {
	log.Println("Error occurred", err)
}

func (dc *dataCenter) OnAdd(server zookeeper.Server) error {
	if dc.getServerIdx(server) > -1 {
		log.Println("Server", server.Path(), "already added to hashring, skipping....")
		return nil
	}

	if _, ok := dc.servers[server.Name()]; !ok {
		log.Println("Adding new partition", server.Path(), server.Content())
		dc.servers[server.Name()] = make([]zookeeper.Server, 0)
	} else {
		log.Println("Adding replica at", server.Content(), "for", server.Name())
	}

	slice := dc.servers[server.Name()]
	dc.servers[server.Name()] = append(slice, server)

	return nil
}

func createDataCenter(client *zookeeper.Client) *dataCenter {
	return &dataCenter{
		client:  client,
		servers: make(map[string][]zookeeper.Server),
	}
}
