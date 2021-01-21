package net.cmauri.chain.support;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Block {
    private int index;
    private Date timestamp;
    private List<Transaction> transactions;
    private int nonce;
    private String hash;
    private String previousBlockHash;

    public String getPublicRepresentation (){
        return index+"\n"+transactions.stream().map(Transaction::getPublicRepresentation).collect(Collectors.joining("\n"));
    }

}
