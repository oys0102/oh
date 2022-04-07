package com.kakao.payment.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentInquiryResponseDto {
	
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
	// 카드번호
	private String cardNo;
	// 카드유효기간
	private String cardExp;
	// 카드CVC
	private String cardCVC;
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
	// 거래일자
	private String tranYmd;
	
	// 취소결제금액
	private BigDecimal cancelAmount;
	// 취소부가가치세금액
	private BigDecimal cancelTax;
	// 취소원거래번호
	private String orgTranNo;
	
	// 카드사전달데이터
	private String cardMsg;

	@Override
	public String toString() {
		return "PaymentEntity >>>> 거래번호::"+tranNo+"\n"+"결제금액::"+paymentAmount+"\n"+"부가가치세금액::"+taxAmount+"\n"+"결제잔액::"+paymentBalance+"\n"+"부가가치세잔액::"+taxBalance+"\n"+"암호화_카드정보::"+encCardInfo+"\n"+"할부개월::"+installmentMonth+"\n"+"결제승인시간::"+payApplyYms+"\n"+"결제취소시간::"+payCancelYms+"\n"+"거래 상태::"+status+"\n"+"트랜잭션ID::"+transactionId+"\n"+"등록일시::"+regYms+"\n"+"변경일시::"+udtYms;
	}
}
