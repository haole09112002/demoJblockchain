package de.neozo.jblockchain.node.service;


import de.neozo.jblockchain.common.domain.Block;
import de.neozo.jblockchain.common.domain.Node;
import de.neozo.jblockchain.common.domain.Transaction;
import de.neozo.jblockchain.common.domain.TransactionInput;
import de.neozo.jblockchain.common.domain.TransactionOutput;
import de.neozo.jblockchain.node.Config;
import de.neozo.jblockchain.node.dto.TxHistoryDTO;
import de.neozo.jblockchain.common.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class BlockService {

    private final static Logger LOG = LoggerFactory.getLogger(BlockService.class);

    private final TransactionService transactionService;
    private List<Block> blockchain = new ArrayList<>();

    @Autowired
    public BlockService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public List<Block> getBlockchain() {
    	List<Block> blockchain = this.blockchain;
    	Collections.sort(blockchain, new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
            	if(o1.getIndex() < o2.getIndex())
            		return 1;
            	else {
					return -1;
				}
            }
        });
        return blockchain;
    }

    /**
     * Determine the last added Block
     * @return Last Block in chain
     */
    public Block getLastBlock() {
        if (blockchain.isEmpty()) {
            return null;
        }
        return blockchain.get(blockchain.size() - 1);
    }

    /**
     * Append a new Block at the end of chain
     * @param block Block to append
     * @return true if verifcation succeeds and Block was appended
     * @throws MalformedURLException 
     */
    public synchronized boolean append(Block block,int typeBlock) throws MalformedURLException {
        if (verify(block,typeBlock)) {
            blockchain.add(block);
            JavaCouchDB.getInstance().addBlock(block);
           if(typeBlock == Config.NEW_BLOCK) {
        	   block.getTransactions().forEach(transactionService::remove);
           }
            return true;
        }
        return false;
    }
    /**
     * Download Blocks from other Node and them to the blockchain
     * @param node Node to query
     * @param restTemplate RestTemplate to use
     */
    
    public void retrieveBlockchain(Node node, RestTemplate restTemplate) {
        Block[] blocks = restTemplate.getForObject(node.getAddress() + "/block", Block[].class);
        Collections.addAll(blockchain, blocks);
        LOG.info("Retrieved " + blocks.length + " blocks from node " + node.getAddress());
    }
    public int  getVersionBlock(Node node,RestTemplate restTemplate) {
    	int bestVersion = 0 ;
    	bestVersion = restTemplate.getForObject(node.getAddress() +"/block/version", Integer.class);
    	return bestVersion;
    	
    }
    public void addMissingBlocks(Node node , RestTemplate restTemplate) throws MalformedURLException {
    	int localVersion = getLastBlock().getIndex();
    	Block[] blocks = restTemplate.getForObject(node.getAddress() +"/block/getblocks?index="+localVersion,Block[].class);
    	for(int i = 0 ; i < blocks.length ; i++) {
    		if(!append(blocks[i],Config.OLD_BLOCK)) {
    			LOG.info("Fail : Old block is invalid");
    		}
    	}
    }
    public void loadLocalBlockDB() {
    	try {
			blockchain = JavaCouchDB.getInstance().getAllBlock();
		} catch (MalformedURLException e) {
			System.out.println("ERROR: Can't load blockchain from CouchDB!");
		}
    }
    public void createGenesisBlock() {
    	try {
			transactionService.createCoinBase();
			long tries = 0;
	        List<Transaction> transactions = transactionService.getTransactionPool()
	                .stream().limit(Config.MAX_TRANSACTIONS_PER_BLOCK).collect(Collectors.toList());
	        if (transactions.isEmpty()) {
	            LOG.info("No transactions available, pausing");
	            try {
	                Thread.sleep(10000);
	            } catch (InterruptedException e) {
	                LOG.error("Thread interrupted", e);
	            }
	           
	        }
	        while(true) {
	        	Block block = new Block(-1,null, transactions, tries);
	            if (block.getLeadingZerosCount() >= Config.DIFFICULTY) {
	                append(block,Config.NEW_BLOCK);
	                return;
	            }
	            tries++;
	        }

		} catch (NoSuchProviderException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
    	
    }
    public List<TransactionOutput>  findAllUTXOs(){
		List<TransactionOutput> allUTXOs = new ArrayList<>();
		List<String> allSpentTXOs = this.getAllSpentTXOs();
		for (Block block : blockchain) {
			for (Transaction tx : block.getTransactions()) {
				for (TransactionOutput txOutput : tx.getTxOutputs()) {
					if(allSpentTXOs.contains(txOutput.getId())) {
						continue;
					}
					allUTXOs.add(txOutput);
				}
			}
		}
		List<Transaction> transactions = transactionService.getTransactionPool()
                .stream().collect(Collectors.toList());
		for (Transaction transaction : transactions) {
			for (TransactionOutput txOutput : transaction.getTxOutputs()) {
				if(allSpentTXOs.contains(txOutput.getId())) {
					continue;
				}
				allUTXOs.add(txOutput);
			}
		}
		return allUTXOs;
	}
    private List<String> getAllSpentTXOs(){
		List<String> spentTXOs = new ArrayList<>();
		for (Block block : blockchain) {
			for (Transaction tx : block.getTransactions()) {
				if(tx.CheckCoinBase()) {
					continue;
				}
				for (TransactionInput txInput : tx.getTxInputs()) {
					spentTXOs.add(txInput.getTransactionOutputId());
				}
			}
		}
		List<Transaction> transactions = transactionService.getTransactionPool()
                .stream().collect(Collectors.toList());
		for (Transaction transaction : transactions) {
			for (TransactionInput txInput : transaction.getTxInputs()) {
				spentTXOs.add(txInput.getTransactionOutputId());
			}
		}
		return spentTXOs;
	}
    public float getBalance(byte[] publickey) {
    	float balance = 0f;
    	List<TransactionOutput> UTXOs = findAllUTXOs();
    	for (TransactionOutput txOutput : UTXOs) {
			if(Arrays.equals(txOutput.getReciepient(),publickey)) {
				balance += txOutput.getValue();
			}
		}
    	return balance;
    }
    public Set<TxHistoryDTO> getTransationsByHash(byte[] publickey){
    	Set<Transaction> transactionHistory = new HashSet<>();
    	for(int i=blockchain.size() -1 ; i >= 0 ;i--) {
    		for (Transaction transaction : blockchain.get(i).getTransactions()) {
				if(Arrays.equals(publickey,transaction.getSenderHash()) || Arrays.equals(publickey,transaction.getReceiverHash())) {
					transactionHistory.add(transaction);
				}
			}
    	}
    	return transactionService.convertTransactions(transactionHistory);
    }
    private boolean verify(Block block,int typeBlock) {
        // references last block in chain
        if (blockchain.size() > 0) {
            byte[] lastBlockInChainHash = getLastBlock().getHash();
            if (!Arrays.equals(lastBlockInChainHash,block.getPreviousBlockHash())) {
                return false;
            }
        } else {
            if (block.getPreviousBlockHash() != null) {
                return false;
            }
        }

        // correct hashes
        if (!Arrays.equals(block.getMerkleRoot(),block.calculateMerkleRoot())) {
        	System.out.println("Fail to add block!Invalid merkleroot");
            return false;
        }
        if (!Arrays.equals(block.getHash(),block.calculateHash())) {
        	System.out.println("Fail to add block!Invalid hash");
            return false;
        }

        // transaction limit
        if (block.getTransactions().size() > Config.MAX_TRANSACTIONS_PER_BLOCK) {
            return false;
        }

        // all transactions in pool
       if(typeBlock == 1) {
    	   if (!transactionService.containsAll(block.getTransactions())) {
               return false;
           }
       }
        // considered difficulty
        return block.getLeadingZerosCount() >= Config.DIFFICULTY;
    }
}
