package com.kakao.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentResponseDto {
	
	// 거래번호
	private String tranNo;
	
	// 카드사전달데이터
	private String cardMsg;

	@Override
	public String toString() {
		return "PaymentEntity >>>> 거래번호::"+tranNo+"카드사전달데이터::"+cardMsg;
	}
}
