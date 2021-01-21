package net.cmauri.chain;

import lombok.extern.log4j.Log4j2;
import net.cmauri.chain.support.Block;
import net.cmauri.chain.support.Transaction;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@Log4j2
public class BirchainTest {

    private static List<Transaction> getTestListTrans() {
        Transaction t1 = new Transaction("ID1", new BigDecimal(10), "CMAURI", "FRA");
        Transaction t2 = new Transaction("ID2", new BigDecimal(10), "FRA", "CMAURI");
        List<Transaction> trans = new ArrayList<>(2);
        trans.add(t1);
        trans.add(t2);
        return trans;
    }

    @Test
    public void testCreationg() {
        Birchain chain = new Birchain();

        assertNotNull(chain.getBlockList());
        assertEquals(1, chain.getBlockList().size());

        log.info("Chain: {}",chain.toString());
    }

    @Test
    public void testTransactSerialization() {
        Transaction t = new Transaction(new BigDecimal(10), "CMAURI", "FRA");
        assertEquals("10 from CMAURI to FRA", t.getPublicRepresentation());
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

        assertEquals(2, chain.getChainSize());
    }

    @Test
    public void testCreateNewTransaction() {
        Birchain chain = new Birchain();

        Transaction t1 = getTestListTrans().get(0);
        Transaction newT = chain.createNewTransaction(t1.getAmount(),t1.getSender(),t1.getRecipient());
        assertEquals("10 from CMAURI to FRA", newT.getPublicRepresentation());
        assertEquals(1, chain.getPendingTransactions().size());
        assertNotNull(newT.getId());
        assertNotEquals("ID1",newT.getId());

        Transaction t2 = getTestListTrans().get(1);
        Transaction newT2 = chain.createNewTransaction(t2);
        assertEquals("10 from FRA to CMAURI", newT2.getPublicRepresentation());
        assertEquals(2, chain.getPendingTransactions().size());
        assertEquals("ID2",newT2.getId());
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
