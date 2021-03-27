package client

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"
)

type Client struct {
	hostname string
}

func (c *Client) Put(key string, value []byte) error {
	addr := c.hostname + "/api/cache/put"

	jsonStr := []byte(fmt.Sprintf("{key: %s, value: %b}", key, value))
	resp, err := http.Post(addr, "application/json", bytes.NewBuffer(jsonStr))

	if err != nil {
		return err
	}

	defer resp.Body.Close()
	return nil
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
	if strings.Index(hostname, "http") != 0 {
		hostname = "http://" + hostname
	}

	return Client{hostname}
}
