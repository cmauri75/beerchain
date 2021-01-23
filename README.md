# Birchain

This project implements a proof of work blockchain

# Running
use: 

```bash
mvn clean package
docker build -t birchain .

export NODE_URL=localhost
docker up run -p8000:8888 birchain

or, preferred multiple node:
docker-compose up
```


# Main content

## Birchain

Thes the manager of blockchain.

Full testable by: <code>mvn test</code>

## Network

The web access to Birchain services.

<!--Activable by: <code>mvn exec:java -Dexec.args="8000"</code>-->
Activable by: <code>java -jar target/birchain*.jar localhost 8000"

### Registering of nodes
After creating nodes choose one and send a register-and-broadcast-node POST call sending url of second node.
Than go on with subsequent one, all other will syncronize

### Creating transactions
Choose a node and send a /transaction/broadcast POST. All other nodes will synchronize automatically