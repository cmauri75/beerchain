import express from 'express';
import bodyParser from 'body-parser';
import { v1 as uuid } from 'uuid';

import BirChain from "./birchain";

const PORT = 8000;
const nodeAddr = uuid();

const bircoin = new BirChain();

const app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:false}));

app.get('/blockchain', function(req, res){
    res.send(bircoin);
});

app.post('/transaction', function(req, res){
    const blockIndex = bircoin.createNewTransaction(req.body.amout,req.body.sender,req.body.recipient);
    res.json(`Transaction will be added in block ${blockIndex}`);
});

app.get('/mine', function(req, res){

    const previousBlockHash = bircoin.getLastBlock().hash;
    const currentBlockData = {
        transactions: bircoin.pendingTransactions,
        index: bircoin.getLastBlock().index+1
    };

    // i prepare reward from mining than i do the work. block will be validated with my reward inside it
    bircoin.createNewTransaction(12.5, "00", nodeAddr);

    const nonce = bircoin.proofOfWork(previousBlockHash,currentBlockData);

//	const blockHash = bircoin.hashBlock(previousBlockHash, currentBlockData, nonce);
//	const newBlock = bircoin.createNewBlock(nonce, previousBlockHash, blockHash);
//    console.log(bircoin.hashBlock(previousBlockHash, currentBlockData, nonce));

    const newBlock = bircoin.createNewBlock(nonce);


    res.json({
        note:"New block mined successfully",
        block: newBlock
    });
});

app.listen(PORT, () => {
  console.log(`⚡️[birchain]: Server is running at https://localhost:${PORT}`);
});
