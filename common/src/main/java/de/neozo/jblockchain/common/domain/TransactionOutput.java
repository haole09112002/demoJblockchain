package de.neozo.jblockchain.common.domain;

import org.apache.commons.codec.binary.Hex;

public class TransactionOutput {
	public String id;
	public byte[] reciepient; 
	public float value; 
	public byte[] parentTransactionId;
	
	public TransactionOutput() {
		
	}
	public TransactionOutput(byte[] reciepient, float value, byte[] parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = CommonUtils.Sha256(Hex.encodeHexString(reciepient)+Double.toString(value)+parentTransactionId);
	}
	

	public boolean isMine(byte[] publicKey) {
		return (publicKey == reciepient);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getReciepient() {
		return reciepient;
	}

	public void setReciepient(byte[] reciepient) {
		this.reciepient = reciepient;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public byte[] getParentTransactionId() {
		return parentTransactionId;
	}

	public void setParentTransactionId(byte[] parentTransactionId) {
		this.parentTransactionId = parentTransactionId;
	}
	
}
