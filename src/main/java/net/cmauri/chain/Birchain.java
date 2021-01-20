package net.cmauri.chain;

import lombok.ToString;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import net.cmauri.chain.support.Block;
import net.cmauri.chain.support.Transaction;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ToString
@Log4j2
public class Birchain {

    //the longer is, the longer is computation
    public static String hashCodeStarter = "0000";

    private String hashCode;

    private List<Block> chain;
    private List<Transaction> pendingTransactions;

    /**
     * Creates the chain witch genesys block
     *
     * @param hashCode: da capire, è l'hash dell'ultimo blocco ma non so se serve davvero
     */
    public Birchain(String hashCode) {
        log.debug("Creating chain");

        this.hashCode = "";
        this.chain = new ArrayList<>();
        this.pendingTransactions = new ArrayList<>();


        //create an arbitrary genesis file. The unique does not fullfill pow rule
        Block genesys = new Block(0, new Date(), pendingTransactions, 0, this.hashCode, "");

        log.debug("blockchain should be created with nonce {}",this.proofOfWork("", genesys));
        this.createNewBlock(62494);


    }

    /**
     * Creates a transaction and add it to next block
     *
     * @param amount
     * @param sender
     * @param recipient
     * @return number of the block the new transaction will be added to
     */
    public int createNewTransaction(int amount, String sender, String recipient) {
        Transaction newTransaction = new Transaction(new BigDecimal(amount), sender, recipient);
        this.pendingTransactions.add(newTransaction);
        return this.getLastBlock().getIndex() + 1;
    }


    public Block createNewBlock(int nonce) {
        //new block is a container of all new pending transactions
        String previousBlockHash = this.getLastBlock() != null ? this.getLastBlock().getHash() : "";


        Block newBlock = new Block(this.chain.size() + 1, new Date(), this.pendingTransactions, nonce, null, previousBlockHash);
        String hash = hashBlock(previousBlockHash, newBlock, nonce);
        newBlock.setHash(hash);

        //once a new block is created the queue is cleared
        this.pendingTransactions = new ArrayList<>();
        this.chain.add(newBlock);

        return newBlock;
    }

    public Block getLastBlock() {
        return this.chain.size() > 0 ? this.chain.get(this.chain.size() - 1) : null;
    }


    /**
     * Hashes a block using it's important data, previous block data (for linking) and nonce for POW
     *
     * @param previousBlockHash
     * @param currentBlock
     * @param nonce
     * @return
     */
    public String hashBlock(String previousBlockHash, Block currentBlock, int nonce) {
        String sData = previousBlockHash + nonce + currentBlock.getPublicRepresentation();

        return DigestUtils.sha256Hex(sData);
    }


    /**
     * this is the core of the blockchain
     * an hash is legitimate only if starts with "0000" (this is our algorith) in order to do it the only way is trial-and-error way
     * using various nonces, this is a good algoritm because it's long to calculate but fast to verify
     *
     * it's very important that the algoritm is complicate so if a users would like to change a block it should also change all sequent blocks,
     * re-mining all blocks, it should cost an huge amount of energy
     * enlarging 4 to longer value can result in a much longer computation
     *
     * @param previousBlockHash
     * @param currentBlock
     * @return the found nonce
     */
    public int proofOfWork(String previousBlockHash, Block currentBlock) {
        int nonce = 0;
        String hash;
        do {
            nonce++;
            hash = this.hashBlock(previousBlockHash, currentBlock, nonce);
        }
        while (!hash.startsWith(this.hashCodeStarter));
        return nonce;
    }


}
