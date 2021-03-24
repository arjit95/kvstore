package zookeeper

import (
	"github.com/arjit95/kvstore/discovery/zookeeper/utils"
)

// Server is used to store information inside a zookeeper node
type Server struct {
	path    string
	content []byte
}

// GetPath returns the path to the zookeeper node
func (data Server) Path() string {
	return data.path
}

// GetContent returns the content stored inside the zookeeper node
func (data Server) Content() string {
	return string(data.content)
}

// GetName returns the name of the node
func (data Server) Name() string {
	return utils.GetNodeID(data.Path())
}

func (data Server) String() string {
	return string(data.Content())
}
