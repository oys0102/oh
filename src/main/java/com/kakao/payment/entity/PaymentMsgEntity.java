package com.kakao.payment.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentMsgEntity {

	//Ʈ�����ID
	private String transactionId;
	//����
	private String msg;
	//����Ͻ�
	private String regYms;

	@Override
	public String toString() {
		return "PaymentMsg >>>> Ʈ�����ID::"+transactionId+"\n"+"����::"+msg+"\n"+"����Ͻ�"+regYms;
	}
}
