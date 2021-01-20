package net.cmauri.chain;

import lombok.Getter;
import lombok.ToString;
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

    @Getter
    private List<Block> chain;
    @Getter
    private List<Transaction> pendingTransactions;

    public int getChainSize() {
        return this.chain.size();
    }

    /**
     * Creates the chain witch genesys block
     */
    public Birchain() {
        log.info("Creating chain");

        this.chain = new ArrayList<>();
        this.pendingTransactions = new ArrayList<>();

        //create an arbitrary genesis file. The unique does not fullfill pow rule
        Block genesys = new Block(1, new Date(), pendingTransactions, 0, null, "");

        log.debug("Blockchain should be created with nonce {}", this.proofOfWork( genesys));
        this.createNewBlock(69165);
    }

    /**
     * Creates a transaction and add it to next block
     *
     * @param amount
     * @param sender
     * @param recipient
     * @return number of the block the new transaction will be added to
     */
    public int createNewTransaction(BigDecimal amount, String sender, String recipient) {
        return createNewTransaction(new Transaction(amount, sender, recipient));
    }

    public int createNewTransaction(Transaction newTransaction) {
        this.pendingTransactions.add(newTransaction);
        return this.retreiveLastBlock().getIndex() + 1;
    }

    /**
     * Creates a new block with all pending transactions and hashs it using passed nonce
     * @param nonce
     * @return
     */
    public Block createNewBlock(int nonce) {
        //new block is a container of all new pending transactions
        String previousBlockHash = this.retreiveLastBlock() != null ? this.retreiveLastBlock().getHash() : "";

        Block newBlock = new Block(this.chain.size() + 1, new Date(), this.pendingTransactions, nonce, null, previousBlockHash);
        log.info("Creating block {}",newBlock);
        String hash = hashBlock(newBlock, nonce);
        log.debug("Hash is {}",hash);
        newBlock.setHash(hash);

        //once a new block is created the queue is cleared
        this.pendingTransactions = new ArrayList<>();
        this.chain.add(newBlock);

        return newBlock;
    }

    public Block retreiveLastBlock() {
        return this.chain.size() > 0 ? this.chain.get(this.chain.size() - 1) : null;
    }

    /**
     * Hashes a block using it's important data, previous block data (for linking) and nonce for POW
     *
     * @param block
     * @param nonce
     * @return
     */
    public String hashBlock(Block block, int nonce) {
        String sData = block.getPreviousBlockHash() + nonce + block.getPublicRepresentation();
        return DigestUtils.sha256Hex(sData);
    }


    /**
     * this is the core of the blockchain
     * an hash is legitimate only if starts with "0000" (this is our algorith) in order to do it the only way is trial-and-error way
     * using various nonces, this is a good algoritm because it's long to calculate but fast to verify
     * <p>
     * it's very important that the algoritm is complicate so if a users would like to change a block it should also change all sequent blocks,
     * re-mining all blocks, it should cost an huge amount of energy
     * enlarging 4 to longer value can result in a much longer computation
     *
     * @param currentBlock
     * @return the found nonce
     */
    public int proofOfWork(Block currentBlock) {
        String previousBlockHash = currentBlock.getPreviousBlockHash();
        int nonce = 0;
        String hash;
        do {
            nonce++;
            hash = this.hashBlock(currentBlock, nonce);
        }
        while (!hash.startsWith(this.hashCodeStarter));
        return nonce;
    }


}
