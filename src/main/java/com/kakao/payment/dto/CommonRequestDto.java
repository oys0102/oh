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

	@Min(value = 100, message = "100원 이상, 10억원 이하만 가능합니다.")
	@Max(value = 1000000000, message = "100원 이상, 10억원 이하만 가능합니다.")
	@NotNull(message = "결제금액은 필수 값입니다.")
	public BigDecimal tranAmount;
	
	@Min(value = 0, message = "tax 는 0원 이상 결제금액 이하 여야 합니다.")
	public BigDecimal taxAmount;
}
