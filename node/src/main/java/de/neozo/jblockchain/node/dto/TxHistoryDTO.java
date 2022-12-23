package de.neozo.jblockchain.node.dto;

public class TxHistoryDTO {
	private byte[] hashID;
	private  byte[] senderHash;
	private  byte[] receiverHash;
	private  float value;
	private  String timeStamp;
	public byte[] getHashID() {
		return hashID;
	}
	public void setHashID(byte[] hashID) {
		this.hashID = hashID;
	}
	public byte[] getSenderHash() {
		return senderHash;
	}
	public void setSenderHash(byte[] senderHash) {
		this.senderHash = senderHash;
	}
	public byte[] getReceiverHash() {
		return receiverHash;
	}
	public void setReceiverHash(byte[] receiverHash) {
		this.receiverHash = receiverHash;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
}
