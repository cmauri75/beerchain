const sha256 = require('sha256');

console.assert(sha256('a')==='ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb','Error with sha256 algoritm');

const BirChain = require('./birchain');
const bircoin = new BirChain();

const newBlock = bircoin.createNewBlock(0,'','');
console.assert('431bf5c814e384ce9fa40f655f4b94f7f6a8c8504ea4844c30c484ebf03121f1'===newBlock.hash,'Block hash creation error');
console.assert(''===newBlock.previousBlockHash,'Block creation error');

bircoin.createNewTransaction(10,'cmauriADDR','fraADDR');
bircoin.createNewTransaction(10,'cmauriADDR','manuelADDR');
bircoin.createNewBlock(0,'YYYYYYYYY','ZZZZZZZ');

bircoin.createNewTransaction(5,'cmauriADDR','fraADDR');
bircoin.createNewTransaction(5,'cmauriADDR','manuelADDR');
bircoin.createNewTransaction(2,'fraADDR','manuelADDR');
bircoin.createNewBlock(0,'ZZZZZZZ','XXXXX');


console.log(bircoin);
console.log("-----");
console.log(bircoin.chain[1]);
console.log("-----");
console.log(bircoin.getLastBlock());


