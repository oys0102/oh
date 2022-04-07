package com.kakao.payment.entity;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentEntity {

	// 거래번호
	private String tranNo;
	// 결제금액
	private BigDecimal paymentAmount;
	// 부가가치세금액
	private BigDecimal taxAmount;
	// 결제잔액
	private BigDecimal paymentBalance;
	// 부가가치세잔액
	private BigDecimal taxBalance;
	// 암호화_카드정보
	private String encCardInfo;
	// 할부개월
	private String installmentMonth;
	// 결제승인시간
	private String payApplyYms;
	// 결제취소시간
	private String payCancelYms;
	// 거래 상태
	private String status;
	// 트랜잭션ID
	private String transactionId;
	// 등록일시
	private String regYms;
	// 변경일시
	private String udtYms;

	@Override
	public String toString() {
		return "PaymentEntity >>>> 거래번호::"+tranNo+"\n"+"결제금액::"+paymentAmount+"\n"+"부가가치세금액::"+taxAmount+"\n"+"결제잔액::"+paymentBalance+"\n"+"부가가치세잔액::"+taxBalance+"\n"+"암호화_카드정보::"+encCardInfo+"\n"+"할부개월::"+installmentMonth+"\n"+"결제승인시간::"+payApplyYms+"\n"+"결제취소시간::"+payCancelYms+"\n"+"거래 상태::"+status+"\n"+"트랜잭션ID::"+transactionId+"\n"+"등록일시::"+regYms+"\n"+"변경일시::"+udtYms;
	}
}
