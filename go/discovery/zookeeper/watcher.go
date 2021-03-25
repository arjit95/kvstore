package zookeeper

import (
	"path"

	"github.com/arjit95/kvstore/go/discovery"
	"github.com/z-division/go-zookeeper/zk"
)

// Listener interface needs to be implemented by all the callers which want
// to use watcher to get any updates
type Listener interface {
	OnAdd(discovery.Server) error
	OnRemove(discovery.Server) error
	OnError(error)
}

// Watcher is a wrapper on zookeeper watcher to make watching paths
// easy
type Watcher struct {
	client *Client

	stop chan bool
	ch   <-chan zk.Event
	data map[string]discovery.Server

	prefix   string
	listener Listener
}

// Watch starts watching using the given prefix
func (zw *Watcher) Watch(listener Listener) error {
	zw.listener = listener
	return zw.scan(zw.prefix, "")
}

func (w *Watcher) watch(ch <-chan zk.Event) {
	select {
	case <-w.stop:
		{
			return
		}
	case event := <-ch:
		{
			if event.Err != nil {
				w.listener.OnError(event.Err)
				return
			}

			if event.Type == zk.EventNodeDeleted {
				err := w.listener.OnRemove(w.data[event.Path])
				if err != nil {
					w.listener.OnError(err)
				}

				w.removeServer(w.data[event.Path])
				return
			}

			err := w.partialScan(event.Path)
			if err != nil {
				w.listener.OnError(event.Err)
				return
			}
		}
	}
}

func (w *Watcher) removeServer(server discovery.Server) {
	delete(w.data, server.Path())
}

func (w *Watcher) addServer(node string) error {
	data, _, err := w.client.Get(node)
	if err != nil {
		return err
	}

	w.data[node] = discovery.CreateServer(node, data)
	return nil
}

func (w *Watcher) scan(prefix, node string) error {
	node = path.Join(prefix, node)

	children, _, ch, err := w.client.ChildrenW(node)
	if err != nil {
		return err
	}

	err = w.addServer(node)
	if err != nil {
		return err
	}

	if node != w.prefix {
		err = w.listener.OnAdd(w.data[node])
		if err != nil {
			return err
		}
	}

	go w.watch(ch)

	for _, child := range children {
		err := w.scan(node, child)
		if err != nil {
			return err
		}
	}

	return nil
}

func (w *Watcher) partialScan(node string) error {
	children, parent_stat, ch, err := w.client.ChildrenW(node)
	if err != nil {
		return err
	}

	err = w.addServer(node)
	if err != nil {
		return err
	}

	go w.watch(ch)

	for _, child := range children {
		_, stat, err := w.client.Get(path.Join(node, child))
		if err != nil {
			return err
		}

		if stat.Czxid == parent_stat.Pzxid {
			err = w.scan(node, child)
			if err != nil {
				return err
			}
		}
	}

	return nil
}

// Close closes the watcher
func (zw *Watcher) Close() {
	close(zw.stop)
}

func createWatcher(zc *Client, prefix string) *Watcher {
	return &Watcher{
		client: zc,
		data:   make(map[string]discovery.Server),
		stop:   make(chan bool),
		prefix: prefix,
	}
}
