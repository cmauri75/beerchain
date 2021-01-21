package net.cmauri.chain.rest;

import lombok.Getter;
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

    private Set<String> networkNodes;

    public ChainController(Birchain chain, RestTemplate restTemplate) {
        log.info("Created ChainController with url: {}", currentNodeUrl);
        //autowire by constructor, so chain can be final
        //chain = new Birchain();
        this.chain = chain;
        this.restTemplate = restTemplate;

        networkNodes = new HashSet<>();
    }

    @GetMapping("/blockchain")
    Map getChain() {
        Map<String, Object> res = new HashMap();
        res.put("chain", chain);
        res.put("currentNodeUrl", currentNodeUrl);
        res.put("networkNodes", networkNodes);
        return res;
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

    /**
     * Register new node in list and broadcast it to all other network nodes
     */
    @PostMapping("/register-and-broadcast-node")
    public String registerAndBroadcastNode(@RequestParam String newNodeUrl) {
        boolean nodeAlreadyPresent = this.networkNodes.contains(newNodeUrl);
        if (!nodeAlreadyPresent) this.networkNodes.add(newNodeUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        networkNodes.forEach(networkNodeUrl -> {
            String callUrl = networkNodeUrl + "/register-node";
            log.info("Broadcasting new node to: {}", callUrl);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("newNodeUrl", newNodeUrl);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            ResponseEntity<String> res = restTemplate.postForEntity(callUrl, request, String.class);
            log.info("Res: {}", res.getBody());
        });


        String callUrl = newNodeUrl + "/register-nodes-bulk";
        log.info("Bulk registering to: {}", newNodeUrl);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

        map.add("allNetworkNodes", String.join(", ", this.networkNodes).concat(",").concat(currentNodeUrl));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> res = restTemplate.postForEntity(callUrl, request, String.class);

        return "Done";
    }

    /**
     * register the existence of a new node in current list of active nodes
     * Avoid duplicates and self-registering
     *
     * @param newNodeUrl
     * @return
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

    @PostMapping("/register-nodes-bulk")
    public String bulkRegisterNode(@RequestParam List<String> allNetworkNodes) {
        allNetworkNodes.forEach(networkNodeUrl -> {
            registerNode(networkNodeUrl);
        });
        return "Done";
    }


}
