package kvstore

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"net/http"
)

type Client struct {
	hostname string
}

func (c *Client) Put(key string, value []byte) error {
	addr := c.hostname + "/api/cache/put"

	jsonStr := []byte(fmt.Sprintf("{key: %s, value: %b}", key, value))
	resp, err := http.NewRequest("POST", addr, bytes.NewBuffer(jsonStr))
	if err != nil {
		defer resp.Body.Close()
	}

	return err
}

func (c *Client) Get(key string) ([]byte, error) {
	addr := fmt.Sprintf("%s/api/cache/get?key=%s", c.hostname, key)

	resp, err := http.Get(addr)
	if err != nil {
		return nil, err
	}

	defer resp.Body.Close()
	value, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	return value, nil
}

func CreateClient(hostname string) Client {
	return Client{hostname}
}
