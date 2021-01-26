package net.cmauri.chain.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.cmauri.chain.Birchain;

import java.util.Set;

@Data
@AllArgsConstructor
public class ChainData {
    private Birchain chain;
    private String currentNodeUrl;
    private Set<String> networkNodes;
}
