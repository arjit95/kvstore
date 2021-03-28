<p align="center">
  <h3 align="center">kvstore</h3>
  <p align="center">
    A distributed key-value server 
    <br />
    <a href="docs/"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/arjit95/kvstore/issues">Report Bug</a>
    ·
    <a href="https://github.com/arjit95/kvstore/issues">Request Feature</a>
  </p>
</p>

<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the project](#about-the-project)
* [Built with](#built-with)
* [Building](#building)
    * [kvserver](#kvserver)
    * [kvgate](#kvgate)
    * [kvctl](#kvctl)
* [Sharding](#sharding)
* [Running](#running)
* [Endpoints](#endpoints)
* [kvctl](#kvctl)
* [Todo](#todo)
* [Contributing](#contributing)
* [License](#license)

## About the project
A distributed key-value cache server with data replication/sharding support. (Note: This project is just for educational purpose and should not be used in production environment)

It contains three major parts:
- kvserver: The main cache server built on spring boot. All data is written to this server, and is responsible for data replication.
- kvgate: A server written in golang which acts as a proxy between the client and server. This server is also responsible for sharding.
- kvctl: Cli client written in golang to interact with kvgate.

The client should always interact with kvgate and must not access kvserver directly.

## Built with
* [Spring](https://spring.io): kvserver
* [Zookeeper](https://zookeeper.apache.org/): Leader selection, replicas listing.
* [Curator](https://curator.apache.org/): Zookeeper interaction with kvserver
* [hashring](https://github.com/serialx/hashring): Hashring structure to achieve consistent hashing.
* [cobi](https://github.com/arjit95/cobi): kvctl

kvserver is written in java, whereas kvctl/kvgate are written in golang.

## Building
To get a local copy up and running, follow the steps below.

```bash
# clone the repository
git clone https://github.com/arjit95/kvstore.git
```

### kvserver

```bash
# build jar
cd kvstore/java/kvserver
./gradlew build
```

### kvgate
Make sure go is installed before building kvgate and kvctl.

```bash
cd kvstore/go/

# build kvgate
go build ./cmd/kvgate
```

### kvctl
You can skip if you use your own rest client
```bash
# build kvctl
go build ./cmd/kvctl
```

Copy all the generated binaries/jars into a common directory.

## Sharding
To shard data just modify `kvstore.cache.partition.name` in kvserver before running. The name could be anything but should not contain numbers.

Suppose you start 2 different servers with names kv-as, kv-sz. These should behave as 2 different shards (Note: It does not bind these servers to specific key-ranges). All servers started with the same name will behave as replicas for these shards.

Edit `application.properties` to change other properties or pass them through command line.

## Running
Before running your app make sure your zookeeper server is running.

```bash

# Shard 1
java -jar kv.jar --server.port=8081 --kvstore.cache.partition.name=kva

# Shard 1 replica
java -jar kv.jar --server.port=8082 --kvstore.cache.partition.name=kva

# Shard 2
java -jar kv.jar --server.port=8083 --kvstore.cache.partition.name=kvb

# Shard 2 replica
java -jar kv.jar --server.port=8084 --kvstore.cache.partition.name=kvb

# Start kvgate
./kvgate <zookeeper connection url> <optional port>
```

Once kvgate is running you can start to send requests to kvgate using a rest client.

### Endpoints
[Visit](docs/endpoints.md)

## kvctl
You can also use kvctl as the client

```bash
./kvctl --help
Usage:
  kvctl [flags]
  kvctl [command]

Available Commands:
  get         Retrieves the key from server
  put         Adds a new key to server

Flags:
      --host stringArray   List of kvgate hosts
  -i, --interactive        Run shell in interactive mode

Use "kvctl [command] --help" for more information about a command.

```

## Todo
- Replicate using logs and write them to a common storage.
- Sharding is performed using consistent hashing. But currently when a server is added no data is moved across servers. So there could be a few cache miss initially before data is present in the new server.
- When a replication request fails, the server is still attached to the list of replicas. The leader should remove that replica and that server should try to heal itself and get the updated information.

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## License
Distributed under the MIT License. See LICENSE for more information.