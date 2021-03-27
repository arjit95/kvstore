package factory

import (
	"github.com/arjit95/kvstore/go/pkg/client"
)

type Factory struct {
	Clients []client.Client
}

func (factory *Factory) AddClient(host string) {
	factory.Clients = append(factory.Clients, client.CreateClient(host))
}

func CreateFactory() *Factory {
	return &Factory{Clients: make([]client.Client, 0)}
}
