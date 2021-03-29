package main

import (
	"container/heap"
	"log"
	"math/rand"
	"strings"

	"github.com/arjit95/kvstore/go/pkg/discovery"
)

type Servers []*discovery.Server

func (s Servers) Len() int {
	return len(s)
}

func (s Servers) Swap(i, j int) {
	s[i], s[j] = s[j], s[i]
}

func (s *Servers) Push(x interface{}) {
	item := x.(*discovery.Server)
	*s = append(*s, item)
}

func (s Servers) Index(x *discovery.Server) int {
	for i, a := range s {
		if a.Path() == x.Path() {
			return i
		}
	}

	return -1
}

func (s *Servers) Pop() interface{} {
	old := *s
	n := len(old)
	item := old[n-1]
	old[n-1] = nil // avoid memory leak
	*s = old[0 : n-1]
	return item
}

func (s Servers) Peek() *discovery.Server {
	if len(s) > 0 {
		return s[0]
	}

	return nil
}

func (s Servers) Random() *discovery.Server {
	if len(s) == 0 {
		return nil
	}

	return s[rand.Intn(len(s))]
}

func (s Servers) Less(i, j int) bool {
	si := s[i].Path()
	sj := s[j].Path()

	var si_lower = strings.ToLower(si)
	var sj_lower = strings.ToLower(sj)
	if si_lower == sj_lower {
		return si < sj
	}

	return si_lower < sj_lower
}

type Partition struct {
	name     string
	replicas Servers
}

func (p *Partition) removeReplica(server discovery.Server) bool {
	idx := p.replicas.Index(&server)
	if idx < 0 {
		return false
	}

	heap.Remove(&p.replicas, idx)

	leader := p.replicas.Peek()
	if leader != nil {
		log.Println(leader.Path(), "is leader")
	} else {
		log.Println("All replicas are removed for", p.name)
	}

	return true
}

func (p *Partition) Name() string {
	return p.name
}

func (p *Partition) addReplica(server discovery.Server) bool {
	if p.replicas.Index(&server) > -1 {
		return false
	}

	heap.Push(&p.replicas, &server)
	log.Println("Adding", server.Path(), "to partition", p.name)
	log.Println(p.replicas.Peek().Path(), "is leader")
	return true
}

func (p *Partition) count() int {
	return len(p.replicas)
}

func createPartition(name string) *Partition {
	return &Partition{
		name:     name,
		replicas: make(Servers, 0),
	}
}
