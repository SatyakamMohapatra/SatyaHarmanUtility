package util;


public class MessageInfo {
	private String messageCode;
	private String messageTitle;
	private String messageDetail;
	
	/**
	 * @return the messageCode
	 */
	public String getMessageCode() {
		return messageCode;
	}
	/**
	 * @param messageCode the messageCode to set
	 */
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	/**
	 * @return the messageTitle
	 */
	public String getMessageTitle() {
		return messageTitle;
	}
	/**
	 * @param messageTitle the messageTitle to set
	 */
	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}
	/**
	 * @return the messageDetail
	 */
	public String getMessageDetail() {
		return messageDetail;
	}
	/**
	 * @param messageDetail the messageDetail to set
	 */
	public void setMessageDetail(String messageDetail) {
		this.messageDetail = messageDetail;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object info) {
		if (info == null) {
			return false;
		}
		if (info instanceof MessageInfo) {
			if (this.messageTitle.equals(((MessageInfo)info).getMessageTitle()) && this.messageDetail.equals(((MessageInfo)info).getMessageDetail())) {
				return true;
			}
		}
		return false;
	}
}
