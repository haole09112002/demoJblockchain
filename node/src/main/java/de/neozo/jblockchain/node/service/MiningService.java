package de.neozo.jblockchain.node.service;


import de.neozo.jblockchain.common.domain.Block;
import de.neozo.jblockchain.common.domain.Transaction;
import de.neozo.jblockchain.node.Config;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class MiningService implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(MiningService.class);

    private final TransactionService transactionService;
    private final NodeService nodeService;
    private final BlockService blockService;
    
    private AtomicBoolean runMiner = new AtomicBoolean(false);


    @Autowired
    public MiningService(TransactionService transactionService,NodeService nodeService,  BlockService blockService) {
        this.transactionService = transactionService;
        this.nodeService = nodeService;
        this.blockService = blockService;
    }
    /**
     * Start the miner
     */
    public void startMiner() {
        if (runMiner.compareAndSet(false, true)) {
            LOG.info("Starting miner");
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Stop the miner after next iteration
     */
    public void stopMiner() {
        LOG.info("Stopping miner");
        runMiner.set(false);
    }

    /**
     * Loop for new blocks until someone signals to stop
     */
    @Override
    public void run() {
        while (runMiner.get()) {
            Block block = mineBlock();
            if (block != null) {
                // Found block! Append and publish
                LOG.info("Mined block with " + block.getTransactions().size() + " transactions and nonce " + block.getTries());
                try {
                	boolean success = blockService.append(block,Config.NEW_BLOCK);
                	if(success) {
                		LOG.info("Added block "+Base64.encodeBase64String(block.getHash()) );
                		nodeService.broadcastPut("block", block);
                		break;
                	}
                	else {
                		LOG.info("Can't add block "+Base64.encodeBase64String(block.getHash()) );
					}
				} catch (MalformedURLException e) {
					LOG.info("ERORR: Block can't add to Couchdb");
				}
            }
        }
    }

    private Block mineBlock() {
        long tries = 0;

        // get previous hash and transactions
        byte[] previousBlockHash = blockService.getLastBlock() != null ? blockService.getLastBlock().getHash() : null;
        int previousBlockIndex = blockService.getLastBlock() != null ? blockService.getLastBlock().getIndex() : -1;
        List<Transaction> transactions = transactionService.getTransactionPool()
                .stream().limit(Config.MAX_TRANSACTIONS_PER_BLOCK).collect(Collectors.toList());

        // sleep if no more transactions left
        if (transactions.isEmpty()) {
            LOG.info("No transactions available, pausing");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOG.error("Thread interrupted", e);
            }
            return null;
        }

        // try new block until difficulty is sufficient
        while (runMiner.get()) {
            Block block = new Block(previousBlockIndex,previousBlockHash, transactions, tries);
            if (block.getLeadingZerosCount() >= Config.DIFFICULTY) {
            	stopMiner();
                return block;
            }
            tries++;
        }
        LOG.info("Stop miner in mineBlock()");
        return null;
    }

}