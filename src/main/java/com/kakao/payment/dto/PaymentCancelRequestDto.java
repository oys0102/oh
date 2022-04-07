package com.kakao.payment.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCancelRequestDto extends CommonRequestDto{
	
	@Size(min=20, max=20, message="�ŷ���ȣ�� 20�ڸ� �Դϴ�.")
	@NotEmpty(message = "�ŷ���ȣ�� �ʼ� ���Դϴ�.")
	private String tranNo;
	
	@Override
	public String toString() {
		return "tranNo : " + tranNo + "/ payAmount : " + tranAmount + "/ taxAmount : "+ taxAmount;
	}
}
