package com.kakao.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentResponseDto {
	
	// �ŷ���ȣ
	private String tranNo;
	
	// ī������޵�����
	private String cardMsg;

	@Override
	public String toString() {
		return "PaymentEntity >>>> �ŷ���ȣ::"+tranNo+"ī������޵�����::"+cardMsg;
	}
}
