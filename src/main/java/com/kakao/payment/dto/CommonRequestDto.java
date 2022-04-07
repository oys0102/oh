package com.kakao.payment.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommonRequestDto {

	@Min(value = 100, message = "100�� �̻�, 10��� ���ϸ� �����մϴ�.")
	@Max(value = 1000000000, message = "100�� �̻�, 10��� ���ϸ� �����մϴ�.")
	@NotNull(message = "�����ݾ��� �ʼ� ���Դϴ�.")
	public BigDecimal tranAmount;
	
	@Min(value = 0, message = "tax �� 0�� �̻� �����ݾ� ���� ���� �մϴ�.")
	public BigDecimal taxAmount;
}
