package de.neozo.jblockchain.node.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.neozo.jblockchain.common.SignatureUtils;
import de.neozo.jblockchain.common.domain.Transaction;
import de.neozo.jblockchain.node.dto.TransactionDTO;
import de.neozo.jblockchain.node.service.TransactionService;



@RestController()
@RequestMapping("login")
public class ViewController {
	private final TransactionService transactionService;
	@Autowired
	public ViewController(TransactionService transactionService)
	{
		this.transactionService = transactionService;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public boolean checkLogin(@RequestBody TransactionDTO tx)
	{
//		byte[] signature = SignatureUtils.sign(tx.getMessage().getBytes(), tx.getPrivateKey());
//        Transaction transaction = new Transaction(tx.getMessage(), tx.getSenderHash(), signature);
//         return transactionService.verify(transaction);
        return true;
		
	}
	
	
	
}
