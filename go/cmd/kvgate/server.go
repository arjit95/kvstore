package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"time"

	"github.com/arjit95/kvstore/go/pkg/client"
)

func putEntry(w http.ResponseWriter, r *http.Request) {
	var cacheEntry client.Entry

	err := json.NewDecoder(r.Body).Decode(&cacheEntry)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	start := time.Now()
	err = dc.addEntry(cacheEntry)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	end := time.Since(start)
	fmt.Println("Took", end.Milliseconds(), "ms for /put to complete")
}

func getEntry(w http.ResponseWriter, r *http.Request) {
	keys, ok := r.URL.Query()["key"]
	if !ok {
		http.Error(w, "Missing url param key", http.StatusBadRequest)
		return
	}

	key := keys[0]
	start := time.Now()
	val, err := dc.getEntry(key)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	end := time.Since(start)
	fmt.Println("Took", end.Milliseconds(), "ms for /get to complete")
	fmt.Fprintf(w, "%b", val)
}

func registerRoutes() *http.ServeMux {
	mux := http.NewServeMux()
	mux.HandleFunc("/api/cache/put", putEntry)
	mux.HandleFunc("/api/cache/get", getEntry)

	return mux
}

func startServer(port int) error {
	log.Println("Starting server on", port)
	return http.ListenAndServe(fmt.Sprintf(":%d", port), registerRoutes())
}
