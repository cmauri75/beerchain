package net.cmauri.chain.rest;

import lombok.extern.log4j.Log4j2;
import net.cmauri.chain.Birchain;
import net.cmauri.chain.support.Block;
import net.cmauri.chain.support.Transaction;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

@Log4j2
@RestController
public class ChainController {

    Birchain chain;

    public ChainController() {
        chain = new Birchain();
    }

    @GetMapping("/blockchain")
    Birchain getChain() {
        return chain;
    }

    @PostMapping(value = "/transaction", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    String createTransaction(@RequestBody Transaction trans) {
        int blockIdx = chain.createNewTransaction(trans);
        return ("Transaction will be added in index " + blockIdx);
    }

    @GetMapping("/mine")
    public String mine() {
        // i prepare reward from mining than i do the work. block will be validated with my reward inside it
        int idx = chain.createNewTransaction(new BigDecimal(12.5), "00", "nodeAddr");

        String previousBlockHash = chain.retreiveLastBlock().getHash();
        Block creatingBlock = new Block(idx, null, chain.getPendingTransactions(), 0, null, previousBlockHash);
        int powNonce = chain.proofOfWork(creatingBlock);
        //String calcHash = chain.hashBlock(creatingBlock, powNonce);
        //log.info("calcHash {} {} {}",calcHash,idx, this.chain.getChainSize());

        Block newBlock = chain.createNewBlock(powNonce);

        return "New block mined successfully" + newBlock;
    }
}
