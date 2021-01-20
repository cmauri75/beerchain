//nonce comes from POW, it's a number that confirm we created this new block in a legitimate way
//hash is the hash of all pending transactions (newTransaction)
// transactions are queued but only when a new block is mined (Created) transactions are validated
import {sha256} from 'js-sha256';

class BirChain {
    private hashLenCheck = 4;
    private hashCode = '';

    chain: any[];
    pendingTransactions: any[];

    constructor() {
        this.chain = [];
        this.pendingTransactions = [];

        //create an arbitrary genesis file. The unique does not fullfill pow rule
        const currentBlockData = {
            transactions: this.pendingTransactions,
            index: this.chain.length + 1
        };

        console.log('blockchain should be created with nonce: '+this.proofOfWork('', currentBlockData));
        this.createNewBlock(29894);

    }

    createNewBlock(nonce: Number) {
        //new block is a container of all new pending transactions
        const previousBlockHash = this.getLastBlock() ? this.getLastBlock().hash : '';
        const currentBlockData = {
            transactions: this.pendingTransactions,
            index: this.chain.length + 1
        };

        const newBlock = {
            index: this.chain.length + 1,
            timeStamp: Date.now(),
            transactions: this.pendingTransactions,
            nonce: nonce,
            hash: this.hashBlock(previousBlockHash, currentBlockData, nonce), //orig: hash:string
            previousBlockHash: previousBlockHash //orig: passed by param
        };

        //once a new block is created the queue is cleared
        this.pendingTransactions = [];
        this.chain.push(newBlock);

        return newBlock;
    }

    getLastBlock() {
        return this.chain[this.chain.length - 1];
    }

    //return the number of the block the new transaction will be added to
    createNewTransaction(amount: number, sender: string, recipient: string) {
        const newTransaction = {
            amount: amount,
            sender: sender,
            recipient: recipient
        }
        this.pendingTransactions.push(newTransaction);
        return this.getLastBlock()['index'] + 1;
    }

    hashBlock(previousBlockHash: string, currentBlockData: any, nonce: Number): string {
        const sData = previousBlockHash + nonce.toString() + JSON.stringify(currentBlockData);
        return sha256(sData);
    }

    //this is the core of the blockchain
    //an hash is legitimate only if starts with "0000" (this is our algorith) in order to do it the only way is trial-and-error way
    //using various nonces, this is a good algoritm because it's long to calculate but fast to verify
    //it returns the found nonce
    //it's very important that the algoritm is complicate so if a users would like to change a block it should also change all sequent blocks,
    //re-mining all blocks, it should cost an huge amount of energy
    //enlarging 4 to longer value can result in a much longer computation
    proofOfWork(previousBlockHash: string, currentBlockData: any): number {
        let nonce: number = 0;
        let hash: string = '';
        do {
            nonce++;
            hash = this.hashBlock(previousBlockHash, currentBlockData, nonce);
        }
        while (hash.substring(0, this.hashLenCheck) !== this.hashCode);
        return nonce;
    }

}

export default BirChain;
