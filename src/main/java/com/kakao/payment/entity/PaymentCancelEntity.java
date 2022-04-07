package com.kakao.payment.entity;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentCancelEntity {

	// 취소거래번호
	private String cancelTranNo;
	// 원거래번호
	private String tranNo;
	// 상태
	private String status;
	// 거래금액
	private BigDecimal tranAmount;
	// 부가가치세금액
	private BigDecimal taxAmount;
	// 트랜잭션ID
	private String transactionId;
	// 등록일시
	private String regYms;
	// 변경일시
	private String udtYms;

	@Override
	public String toString() {
		return "CancelEntity >>>> 취소거래번호::"+cancelTranNo+
								"\n"+"원거래번호::"+tranNo+
								"\n"+"상태::"+status+
								"\n"+"거래금액::"+tranAmount+
								"\n"+"부가가치세금액::"+taxAmount+
								"\n"+"트랜잭션ID::"+transactionId+
								"\n"+"등록일시::"+regYms+
								"\n"+"변경일시::"+udtYms;
	}
}
