//nonce comes from POW, it's a number that confirm we created this new block in a legitimate way
//hash is the hash of all pending transactions (newTransaction)
// transactions are queued but only when a new block is mined (Created) transactions are validated

const sha256 = require('sha256');

class BirChain {
    constructor() {
        this.chain = [];
        this.pendingTransactions = [];
    };

    createNewBlock(nonce, previousBlockHash, hash) {
        //new block is a container of all new pending transactions
        const newBlock = {
            index: this.chain.length+1,
            timeStamp: Date.now(),
            transactions: this.pendingTransactions,
            nonce: nonce,
            hash: this.hashBlock(previousBlockHash,this.pendingTransactions,nonce),
            previousBlockHash: previousBlockHash
        };

        //once a new block is created the queue is cleared
        this.pendingTransactions = [];
        this.chain.push(newBlock);

        return newBlock;
    }

    getLastBlock() {
        return this.chain[this.chain.length-1];
    }

    //return the number of the block the new transaction will be added to
    createNewTransaction(amount, sender, recipient) {
        const newTransaction = {
            amount: amount,
            sender: sender,
            recipient: recipient
        }
        this.pendingTransactions.push(newTransaction);
        return this.getLastBlock()['index'] + 1;
    }

    hashBlock(previousBlockHash, currentBlockData, nonce){
        const sData = previousBlockHash + nonce.toString() + JSON.stringify(currentBlockData);
        return sha256(sData);
    }
}


module.exports = BirChain;