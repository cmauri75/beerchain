package net.cmauri.chain.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
        private BigDecimal amount;
        private String sender;
        private String recipient;

        public String getpublicRepresentation (){
                return amount+" from "+sender+" to "+recipient;
        }
}
