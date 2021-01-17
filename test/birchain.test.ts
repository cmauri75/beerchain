import 'mocha'
import * as assert from 'assert';

import { sha256 } from 'js-sha256';
import BirChain from "../src/birchain";

describe('Utils', function() {
  describe('#sha()', function() {
    it('sha256 should work as https://emn178.github.io/online-tools/sha256.html ', function() {
        assert.equal( sha256('0000') , '9af15b336e6a9619928537df30b2e6a2376569fcf9d7e773eccede65606529a0');
    });
  });
});


let bircoin = new BirChain();

describe('BirCoin', function() {
  describe('#sha()', function() {
    it('Block creation ', function() {
        const block0 = bircoin.createNewBlock(0,sha256('0000'),'0000');

        assert.equal(block0.previousBlockHash,sha256('0000'));
        assert.equal(block0.index,1);
        assert.lengthOf(block0.transactions,0);
    });
    it('Hash generation testing ', function() {
        const block0 = bircoin.createNewBlock(0,sha256('0000'),'0000');
        const hash0 = bircoin.hashBlock(sha256('0000'), block0, 0);

        assert.equal(hash0, 'e839f8359dc6c84f5408fc79d3cba665c4ec398e540f665dc6b3c1b02bc87567');
    });
  });
});

/*
console.log ('. Testing proofOfWork algoritm');
const pow = bircoin.proofOfWork(sha256('a'),block0);
console.assert('0000' === bircoin.hashBlock(block0.previousBlockHash, block0, pow),'Error on proofOfWork generation');


//block creation test
console.log ('creating first block');
const newBlock = bircoin.createNewBlock(0,'','');
console.log('new block hash: '+newBlock.hash);

console.log ('creating first block correcteness');
console.assert('431bf5c814e384ce9fa40f655f4b94f7f6a8c8504ea4844c30c484ebf03121f1' === newBlock.hash,'Block hash creation error');
console.assert('' === newBlock.previousBlockHash,'Block creation error');

console.log ('creating transactions and mining');
bircoin.createNewTransaction(10,'cmauriADDR','fraADDR');
bircoin.createNewTransaction(10,'cmauriADDR','manuelADDR');
bircoin.createNewBlock(0,'YYYYYYYYY','ZZZZZZZ');

console.log ('creating transactions and mining');
bircoin.createNewTransaction(5,'cmauriADDR','fraADDR');
bircoin.createNewTransaction(5,'cmauriADDR','manuelADDR');
bircoin.createNewTransaction(2,'fraADDR','manuelADDR');
bircoin.createNewBlock(0,'ZZZZZZZ','XXXXX');
*/

console.log("************************* DEBUG");
// console.log(bircoin);
// console.log("-----");
// console.log(bircoin.chain[1]);
// console.log("-----");
// console.log(bircoin.getLastBlock());
