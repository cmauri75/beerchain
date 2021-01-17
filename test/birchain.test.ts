import 'mocha'
import {assert} from 'chai';

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

        assert.equal(block0.index,1);
        assert.lengthOf(block0.transactions,0);
    });
    it('Proof of work algoritm testing ', function() {
        const block0 = bircoin.createNewBlock(0,sha256('0000'),'0000');

        const pow = bircoin.proofOfWork(sha256('a'),block0);
        const verify:string = bircoin.hashBlock(block0.previousBlockHash, block0, pow);
        assert(verify.substring(0,4),'0000');
        //console.log('nonce is:'+pow);
    });
  });
});

/*

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
