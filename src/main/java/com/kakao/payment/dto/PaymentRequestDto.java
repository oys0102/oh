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
	
	@Size(min=10, max=16, message="카드번호는 최소 10자리, 최대 16자리 입니다.")
	@Pattern(regexp = "^[0-9]+$", message = "카드번호는 숫자만 가능합니다.")
	@NotEmpty(message = "카드번호는 필수 값입니다.")
	private String cardNo;
	
	@Size(min=4, max=4, message="카드유효기간은 4자리입니다.")
	@Pattern(regexp = "^[0-9]+$", message = "카드유효기간은 숫자만 가능합니다.")
	@NotEmpty(message = "카드유효기간은 필수 값입니다.")
	private String cardExp;
	
	@Size(min=3, max=3, message="카드CVC는 3자리입니다.")
	@Pattern(regexp = "^[0-9]+$", message = "카드CVC는 숫자만 가능합니다.")
	@NotEmpty(message = "카드CVC는 필수 값입니다.")
	private String cardCVC;
	
	@Min(value = 0, message = "할부기간은 0 ~ 12 개월만 가능합니다.")
	@Max(value = 12, message = "할부기간은 0 ~ 12 개월만 가능합니다.")
	@NotNull(message = "할부개월은 필수 값입니다.")
	private Integer installmentMonth;
	
	@Override
	public String toString() {
		return "cardNo : " + cardNo + " / cardExp : " + cardExp + " / cardCVC : " + cardCVC + " / tax : "+ taxAmount;
	}
}
