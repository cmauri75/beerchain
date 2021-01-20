package net.cmauri.chain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import lombok.extern.log4j.Log4j2;
import net.cmauri.chain.support.Block;
import net.cmauri.chain.support.Transaction;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
public class BirchainTest {

    private static List<Transaction> getTestListTrans() {
        Transaction t1 = new Transaction(new BigDecimal(10), "CMAURI", "FRA");
        Transaction t2 = new Transaction(new BigDecimal(10), "FRA", "CMAURI");
        List<Transaction> trans = new ArrayList<>(2);
        trans.add(t1);
        trans.add(t2);
        return trans;
    }

    @Test
    public void testTransactSerialization() {
        Transaction t = new Transaction(new BigDecimal(10), "CMAURI", "FRA");
        assertEquals("10 from CMAURI to FRA", t.getpublicRepresentation());
    }

    @Test
    public void testBlockSerialization() {

        Block b = new Block(0, new Date(), getTestListTrans(), 0, "", "");
        assertEquals("0\n10 from CMAURI to FRA\n10 from FRA to CMAURI", b.getPublicRepresentation());
    }

    @Test
    public void testHashing() {
        Block b = new Block(0, new Date(), getTestListTrans(), 0, "", "");

        Birchain chain = new Birchain();
        assertEquals("62fafb5b12e4b57701650c8588d252981397cc0492c2a5c9e651368992c33995", chain.hashBlock( b, 0));
    }

    @Test
    public void testCreateNewBlock() {
        Birchain chain = new Birchain();

        Block block1 = chain.createNewBlock(62494);
        assertEquals(2, block1.getIndex());
        assertEquals(0, block1.getTransactions().size());
    }

    @Test
    public void testPowAlg() {

        Birchain chain = new Birchain();

        Block pb = chain.retreiveLastBlock();
        int pow = chain.proofOfWork(pb);
        String calcHash = chain.hashBlock(pb, pow);
        assertTrue(calcHash.startsWith(Birchain.hashCodeStarter));
    }


}
