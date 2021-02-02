# Birchain

This project implements a dimostrative functional blockchain with transaction management, difficulty scalable mining algoritm (based on proof of work), consensus.

Based on spring boot and java11, tests provides documentation on services.

# Running
use: 

```bash
mvnw clean package
docker build -t birchain .

export NODE_URL=localhost
docker up run -p8000:8888 birchain

or, preferred multiple node:
docker-compose up
```


# Main content

## Birchain

The the manager of blockchain. It's a full working manager.
Full testable by: 
```bash
mvnw test
```

## Network

Rest access to Birchain services. All functions are well documented by ChainControllerTest that executes typical scenarios. 
First activate 3 nodes using provided docker-compose than run te test via <code>mvn test</code>

<!--Activable by: <code>mvn exec:java -Dexec.args="http://localhost 8001"</code>-->
Single noden be activated by: 
```bash
mvnw package
java -jar target/birchain*.jar http://localhost 8000"
```

Main functionalities are
### Registering of nodes
After creating nodes choose one and send a register-and-broadcast-node POST call sending url of second node.
Than go on with subsequent one, all other will syncronize

### Creating transactions
Choose a node and send a /transaction/broadcast POST. All other nodes will synchronize automatically

### Mining
Get to /mine to one node of network

### Consensus
Post to /consensus. The node will query all other for their chain, if a longer than current one is found, current chain will be replaced with remote one.

