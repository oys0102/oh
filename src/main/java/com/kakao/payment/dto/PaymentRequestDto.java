package com.kakao.payment.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto extends CommonRequestDto{
	
	@Size(min=10, max=16, message="ī���ȣ�� �ּ� 10�ڸ�, �ִ� 16�ڸ� �Դϴ�.")
	@Pattern(regexp = "^[0-9]+$", message = "ī���ȣ�� ���ڸ� �����մϴ�.")
	@NotEmpty(message = "ī���ȣ�� �ʼ� ���Դϴ�.")
	private String cardNo;
	
	@Size(min=4, max=4, message="ī����ȿ�Ⱓ�� 4�ڸ��Դϴ�.")
	@Pattern(regexp = "^[0-9]+$", message = "ī����ȿ�Ⱓ�� ���ڸ� �����մϴ�.")
	@NotEmpty(message = "ī����ȿ�Ⱓ�� �ʼ� ���Դϴ�.")
	private String cardExp;
	
	@Size(min=3, max=3, message="ī��CVC�� 3�ڸ��Դϴ�.")
	@Pattern(regexp = "^[0-9]+$", message = "ī��CVC�� ���ڸ� �����մϴ�.")
	@NotEmpty(message = "ī��CVC�� �ʼ� ���Դϴ�.")
	private String cardCVC;
	
	@Min(value = 0, message = "�ҺαⰣ�� 0 ~ 12 ������ �����մϴ�.")
	@Max(value = 12, message = "�ҺαⰣ�� 0 ~ 12 ������ �����մϴ�.")
	@NotNull(message = "�Һΰ����� �ʼ� ���Դϴ�.")
	private Integer installmentMonth;
	
	@Override
	public String toString() {
		return "cardNo : " + cardNo + " / cardExp : " + cardExp + " / cardCVC : " + cardCVC + " / tax : "+ taxAmount;
	}
}
