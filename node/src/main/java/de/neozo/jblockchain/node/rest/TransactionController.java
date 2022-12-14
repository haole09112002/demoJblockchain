package de.neozo.jblockchain.node.rest;


import de.neozo.jblockchain.common.SignatureUtils;
import de.neozo.jblockchain.common.domain.Transaction;
import de.neozo.jblockchain.node.dto.TransactionDTO;
import de.neozo.jblockchain.node.service.MiningService;
import de.neozo.jblockchain.node.service.NodeService;
import de.neozo.jblockchain.node.service.TransactionService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.nio.file.Files;
import java.util.Set;


@RestController()
@RequestMapping("transaction")
public class TransactionController {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;
    private final NodeService nodeService;
    private final MiningService miningService;
    @Autowired
    public TransactionController(TransactionService transactionService, NodeService nodeService, MiningService miningService) {
        this.transactionService = transactionService;
        this.nodeService = nodeService;
        this.miningService = miningService;
    }

    /**
     * Retrieve all Transactions, which aren't in a block yet
     * @return JSON list of Transactions
     */
    @RequestMapping
    Set<Transaction> getTransactionPool() {
        return transactionService.getTransactionPool();
    }


    /**
     * Add a new Transaction to the pool.
     * It is expected that the transaction has a valid signature and the correct hash.
     *
     * @param transaction the Transaction to add
     * @param publish if true, this Node is going to inform all other Nodes about the new Transaction
     * @param response Status Code 202 if Transaction accepted, 406 if verification fails
     */
    @RequestMapping(method = RequestMethod.PUT)
    void addTransaction(@RequestBody TransactionDTO transactionDTO, @RequestParam(required = false) Boolean publish, HttpServletResponse response) {
    	byte[] signature = SignatureUtils.sign(transactionDTO.getMessage().getBytes(), transactionDTO.getPrivateKey());
        Transaction transaction = new Transaction(transactionDTO.getMessage(), transactionDTO.getSenderHash(), signature);
    	LOG.info("Add transaction " +Base64.encodeBase64String(transaction.getHash()));
        boolean success = transactionService.add(transaction);
        if (success) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            if(transactionService.getTransactionPool().size() >= 2) {
            	miningService.startMiner();
            }
            if (publish != null && publish) {
                nodeService.broadcastPut("transaction/add", transaction);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }
    ///
    @RequestMapping(value = "add", method = RequestMethod.PUT)
    void addTransaction(@RequestBody Transaction transaction, @RequestParam(required = false) Boolean publish, HttpServletResponse response) {
   
    	LOG.info("Add transaction " +Base64.encodeBase64String(transaction.getHash()));
        boolean success = transactionService.add(transaction);
        if (success) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            if(transactionService.getTransactionPool().size() >= 2) {
            	miningService.startMiner();
            }
            if (publish != null && publish) {
                nodeService.broadcastPut("transaction/add", transaction);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

}
