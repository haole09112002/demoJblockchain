package de.neozo.jblockchain.node.dto;

public class TransactionDTO {

    private float value;
    private byte[] senderHash;
    private byte[] receiverHash;
    private byte[] privateKey;

    public TransactionDTO() {
		// TODO Auto-generated constructor stub
	}

	public TransactionDTO(float value, byte[] senderHash,byte[] receiverHash, byte[] privateKey) {
		this.receiverHash = receiverHash;
		this.senderHash = senderHash;
		this.privateKey = privateKey;

	}



	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public byte[] getReceiverHash() {
		return receiverHash;
	}

	public void setReceiverHash(byte[] receiverHash) {
		this.receiverHash = receiverHash;
	}

	public byte[] getSenderHash() {
		return senderHash;
	}

	public void setSenderHash(byte[] senderHash) {
		this.senderHash = senderHash;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}

	
    
}
