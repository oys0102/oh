package com.kakao.payment.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentMsgEntity {

	//트랜잭션ID
	private String transactionId;
	//전문
	private String msg;
	//등록일시
	private String regYms;

	@Override
	public String toString() {
		return "PaymentMsg >>>> 트랜잭션ID::"+transactionId+"\n"+"전문::"+msg+"\n"+"등록일시"+regYms;
	}
}
