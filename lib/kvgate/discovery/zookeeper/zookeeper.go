package zookeeper

import (
	"context"
	"time"

	"github.com/z-division/go-zookeeper/zk"
	"golang.org/x/sync/semaphore"
)

// Client provides a wrapper around original zookeeper connection object
type Client struct {
	conn *zk.Conn

	attempts    int
	concurrency int

	sem *semaphore.Weighted
	ctx context.Context
}

func (zc *Client) connect(hosts []string) {
	c, _, err := zk.Connect(hosts, time.Second)
	if err != nil {
		panic(err)
	}

	zc.conn = c
}

// Get data stored for a given path
func (zc *Client) Get(path string) (data []byte, stat *zk.Stat, err error) {
	err = zc.withRetry(func(conn *zk.Conn) error {
		data, stat, err = conn.Get(path)
		return err
	})

	return
}

func (zc *Client) GetServer(path string) (Server, error) {
	var server Server
	err := zc.withRetry(func(conn *zk.Conn) error {
		data, _, err := conn.Get(path)
		if err == nil {
			server = Server{path: path, content: data}
		}

		return err
	})

	return server, err
}

// ChildrenW returns a channel which could be used to watch any changes
func (zc *Client) ChildrenW(prefix string) (children []string, stat *zk.Stat, ch <-chan zk.Event, err error) {
	err = zc.withRetry(func(conn *zk.Conn) error {
		children, stat, ch, err = conn.ChildrenW(prefix)
		return err
	})

	return
}

// Children returns all children starting with the prefix
func (zc *Client) Children(prefix string) (children []string, err error) {
	err = zc.withRetry(func(conn *zk.Conn) error {
		children, _, err = conn.Children(prefix)
		return err
	})

	return
}

// CreateWatcher starts watching the prefix for any changes
func (zc *Client) CreateWatcher(prefix string) *Watcher {
	return createWatcher(zc, prefix)
}

func (zc *Client) withRetry(fn func(*zk.Conn) error) error {
	zc.sem.Acquire(zc.ctx, 1)

	totalAttempts := zc.attempts
	var err error

	for i := 0; i < totalAttempts; i++ {
		err = fn(zc.conn)
		if err == nil {
			return err
		}
	}

	zc.sem.Release(1)
	return err

}

// SetRetries sets the number of retries which will be performed while querying zookeeper
func (zc *Client) SetRetries(attempts int) {
	zc.attempts = attempts
}

// SetConcurrency sets the number of concurrent requests that can be performed
func (zc *Client) SetConcurrency(concurrency int) {
	zc.concurrency = concurrency
}

// CreateClient creates a new zookeeper connection
func CreateConnection(hosts []string) *Client {
	conn := &Client{
		concurrency: 64,
		sem:         semaphore.NewWeighted(64),
		attempts:    2,
		ctx:         context.TODO(),
	}

	conn.connect(hosts)
	return conn
}
