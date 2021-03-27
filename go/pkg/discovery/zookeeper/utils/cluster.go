package utils

import (
	"strings"
)

// GetNodeID translates path to index. It also removes any extra identifiers
// added by zookeeper Eg: /leader/kv00012 => kv
// Note: This might not always give the correct result if app name contains digits
func GetNodeID(path string) string {
	parts := strings.Split(path, "/")

	keyName := parts[len(parts)-1]

	for i := 0; i < len(parts); i++ {
		if keyName[i] >= 48 && keyName[i] <= 57 {
			keyName = keyName[0:i]
			break
		}
	}

	return string(keyName)
}

// GetChildPath returns the complete path for zookeeper node
func GetChildPath(prefix string, name string) string {
	return strings.Join([]string{prefix, name}, "/")
}
