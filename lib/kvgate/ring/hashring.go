package ring

import (
	"github.com/buraksezer/consistent"
	"github.com/cespare/xxhash"

	"github.com/arjit95/kvstore/discovery/zookeeper"
)

type hasher struct{}

func (h hasher) Sum64(data []byte) uint64 {
	//TODO: Move to a proper hashing function
	return xxhash.Sum64(data)
}

type Hashring struct {
	Ring *consistent.Consistent
}

func (hr *Hashring) Add(server zookeeper.Server) {
	hr.Ring.Add(server)
}

func (hr *Hashring) Remove(server zookeeper.Server) {
	hr.Ring.Remove(server.String())
}

func CreateHashRing(partitionCount int, replicas int) *Hashring {
	cfg := consistent.Config{
		PartitionCount:    partitionCount,
		ReplicationFactor: replicas,
		Hasher:            hasher{},
	}

	c := consistent.New(nil, cfg)
	return &Hashring{Ring: c}
}
