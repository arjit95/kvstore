package client

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"
)

type Client struct {
	hostname string
}

type Entry struct {
	Key   string `json:"key"`
	Value []byte `json:"value"`
}

func (c *Client) Put(entry Entry) error {
	addr := c.hostname + "/api/cache/put"

	jsonValue, _ := json.Marshal(entry)
	resp, err := http.Post(addr, "application/json", bytes.NewBuffer(jsonValue))

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
