package net.cmauri.chain.rest;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.cmauri.chain.Birchain;
import net.cmauri.chain.support.Block;
import net.cmauri.chain.support.Transaction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Log4j2
@RestController
public class ChainController {
    private String nodeAddress = UUID.randomUUID().toString();

    @Setter
    private String currentNodeUrl;

    private final RestTemplate restTemplate;

    private final Birchain chain;

    private final Set<String> networkNodes;

    public ChainController(Birchain chain, RestTemplate restTemplate) {
        log.info("Created ChainController with url: {}", currentNodeUrl);
        //autowire by constructor, so chain can be final
        //chain = new Birchain();
        this.chain = chain;
        this.restTemplate = restTemplate;

        networkNodes = new HashSet<>();
    }

    @GetMapping("/blockchain")
    Map<String, Object> getChain() {
        Map<String, Object> res = new HashMap<>();
        res.put("chain", chain);
        res.put("currentNodeUrl", currentNodeUrl);
        res.put("networkNodes", networkNodes);
        return res;
    }

    @GetMapping("/clearNodes")
    String clearNodes() {
        networkNodes.clear();
        return "done";
    }

    /**
     * Creates a local transaction.
     */
    @PostMapping(value = "/transaction", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    String createTransaction(@RequestBody Transaction t) {
        chain.createNewTransaction(t);
        return ("Transaction will be added in next block");
    }

    /**
     * Creates a local transaction and broadcast it to all nodes
     */
    @PostMapping(value = "/transaction/broadcast", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    String createTransactionBroadcast(@RequestBody Transaction t) {
        Transaction newT = chain.createNewTransaction(t.getAmount(), t.getSender(), t.getRecipient());

        networkNodes.forEach(networkNodeUrl -> {
            String callUrl = networkNodeUrl + "/transaction";
            log.info("Broadcasting new transaction to: {}", callUrl);

            HttpEntity<Transaction> request = new HttpEntity<>(newT, getBodyHeader());
            ResponseEntity<String> res = restTemplate.postForEntity(callUrl, request, String.class);
            log.info("Res: {}", res.getBody());
        });

        return ("Transaction will be added in next block");
    }

    @GetMapping("/mine")
    public String mine() {
        int idx = chain.retrieveLastBlock().getIndex() + 1;

        String previousBlockHash = chain.retrieveLastBlock().getHash();
        Block creatingBlock = new Block(idx, null, chain.getPendingTransactions(), 0, null, previousBlockHash);
        int powNonce = chain.proofOfWork(creatingBlock);
        //String calcHash = chain.hashBlock(creatingBlock, powNonce);
        //log.info("calcHash {} {} {}",calcHash,idx, this.chain.getChainSize());

        Block newBlock = chain.createNewBlock(powNonce);

        //Broadcast new block to all other nodes
        networkNodes.forEach(networkNodeUrl -> {
            String callUrl = networkNodeUrl + "/receive-new-block";
            HttpEntity<Block> request = new HttpEntity<>(newBlock, getBodyHeader());
            ResponseEntity<String> res = restTemplate.postForEntity(callUrl, request, String.class);
            log.debug("got res to newblock {}",res);
        });


        // reward will be added to chain in next block and broadcast to all other nodes
        Transaction newT = new Transaction(new BigDecimal(12.5), "00", "nodeAddr");
        this.createTransactionBroadcast(newT);

        return "New block mined successfully" + newBlock;
    }

    /**
     * Receive a new block mined by another node
     */
    @PostMapping("/receive-new-block")
    public String receiveNewBlock(@RequestBody Block newBlock) {
        Block lastBlock = chain.retrieveLastBlock();

        boolean correctHash = lastBlock.getHash().equals(newBlock.getPreviousBlockHash());
        boolean correctIndex = lastBlock.getIndex() + 1 == newBlock.getIndex();

        if (correctHash && correctIndex) {
            chain.addBlock(newBlock);
            chain.clearPendingTransactions();
            return "New block received and accepted";
        } else {
            return "New block rejected";
        }
    }

    ;

    /**
     * Register new node in list and broadcast it to all other network nodes
     * This is a MAIN entrypoint
     */
    @PostMapping("/register-and-broadcast-node")
    public String registerAndBroadcastNode(@RequestParam String newNodeUrl) {
        boolean nodeAlreadyPresent = this.networkNodes.contains(newNodeUrl);
        if (!nodeAlreadyPresent) this.networkNodes.add(newNodeUrl);

        networkNodes.forEach(networkNodeUrl -> {
            String callUrl = networkNodeUrl + "/register-node";
            log.info("Broadcasting new node to: {}", callUrl);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("newNodeUrl", newNodeUrl);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, getUrlHeader());

            ResponseEntity<String> res = restTemplate.postForEntity(callUrl, request, String.class);
            log.info("Res: {}", res.getBody());
        });


        String callUrl = newNodeUrl + "/register-nodes-bulk";
        log.info("Bulk registering to: {}", newNodeUrl);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        map.add("allNetworkNodes", String.join(", ", this.networkNodes).concat(",").concat(currentNodeUrl));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, getUrlHeader());

        ResponseEntity<String> res = restTemplate.postForEntity(callUrl, request, String.class);

        return "Done";
    }

    /**
     * register the existence of a new node in current list of active nodes
     * Avoid duplicates and self-registering
     * UTILITY point, receive a broadcast call from register-and-broadcast-node
     */
    @PostMapping("/register-node")
    public String registerNode(@RequestParam String newNodeUrl) {
        boolean nodeAlreadyPresent = this.networkNodes.contains(newNodeUrl);
        boolean isCurrentNode = this.currentNodeUrl.equals(newNodeUrl);
        if (!nodeAlreadyPresent && !isCurrentNode) {
            log.info("Ok to register: {}", newNodeUrl);
            this.networkNodes.add(newNodeUrl);
        }
        log.info("Done");
        return ("New node registered successfully. Current size is: " + this.networkNodes.size());
    }

    /**
     * Align a new node registering all nodes already present in a single call
     * UTILITY point, receive a broadcast call from register-and-broadcast-node
     *
     * @param allNetworkNodes
     * @return
     */
    @PostMapping("/register-nodes-bulk")
    public String bulkRegisterNode(@RequestParam List<String> allNetworkNodes) {
        allNetworkNodes.forEach(networkNodeUrl -> {
            registerNode(networkNodeUrl);
        });
        return "Done";
    }


    /**
     * Creates header for call to RequestParam urls
     */
    private HttpHeaders getUrlHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    /**
     * Creates header for call to RequestBody urls
     */
    private HttpHeaders getBodyHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
