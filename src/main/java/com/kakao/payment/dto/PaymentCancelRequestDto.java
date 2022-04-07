package com.kakao.payment.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCancelRequestDto extends CommonRequestDto{
	
	@Size(min=20, max=20, message="거래번호는 20자리 입니다.")
	@NotEmpty(message = "거래번호는 필수 값입니다.")
	private String tranNo;
	
	@Override
	public String toString() {
		return "tranNo : " + tranNo + "/ payAmount : " + tranAmount + "/ taxAmount : "+ taxAmount;
	}
}
