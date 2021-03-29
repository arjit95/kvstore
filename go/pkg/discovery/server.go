package discovery

import (
	kvstore "github.com/arjit95/kvstore/go/pkg/client"
	"github.com/arjit95/kvstore/go/pkg/discovery/zookeeper/utils"
)

// Server is used to store information inside a zookeeper node
type Server struct {
	path    string
	content []byte
	client  kvstore.Client
}

// GetPath returns the path to the zookeeper node
func (s Server) Path() string {
	return s.path
}

// GetContent returns the content stored inside the zookeeper node
func (s Server) Content() string {
	return string(s.content)
}

// GetName returns the name of the node
func (s Server) Name() string {
	return utils.GetNodeID(s.Path())
}

func (s Server) String() string {
	return string(s.Content())
}

func (s Server) Put(entry kvstore.Entry) error {
	return s.client.Put(entry)
}

func (s Server) Get(key string) ([]byte, error) {
	return s.client.Get(key)
}

func CreateServer(path string, content []byte) Server {
	server := Server{
		path:    path,
		content: content,
	}

	server.client = kvstore.CreateClient(server.Content())
	return server
}
