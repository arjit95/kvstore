package ring

import "github.com/serialx/hashring"

func CreateHashRing() *hashring.HashRing {
	return hashring.New([]string{})
}
