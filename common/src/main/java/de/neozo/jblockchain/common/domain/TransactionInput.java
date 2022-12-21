package de.neozo.jblockchain.common.domain;

public class TransactionInput {
	public String transactionOutputId;
	public TransactionOutput UTXO; 
	
	public TransactionInput() {
		
	}
	public TransactionInput(TransactionOutput transactionOutput) {
		this.transactionOutputId = transactionOutput.getId();
		this.UTXO = transactionOutput;
	}

	public String getTransactionOutputId() {
		return transactionOutputId;
	}

	public void setTransactionOutputId(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

//	public TransactionOutput getUTXO() {
//		return UTXO;
//	}

	public void setUTXO(TransactionOutput uTXO) {
		UTXO = uTXO;
	}
	
}
