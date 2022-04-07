package com.kakao.payment.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentInquiryResponseDto {
	
	// �ŷ���ȣ
	private String tranNo;
	// �����ݾ�
	private BigDecimal paymentAmount;
	// �ΰ���ġ���ݾ�
	private BigDecimal taxAmount;
	// �����ܾ�
	private BigDecimal paymentBalance;
	// �ΰ���ġ���ܾ�
	private BigDecimal taxBalance;
	// ��ȣȭ_ī������
	private String encCardInfo;
	// ī���ȣ
	private String cardNo;
	// ī����ȿ�Ⱓ
	private String cardExp;
	// ī��CVC
	private String cardCVC;
	// �Һΰ���
	private String installmentMonth;
	// �������νð�
	private String payApplyYms;
	// ������ҽð�
	private String payCancelYms;
	// �ŷ� ����
	private String status;
	// Ʈ�����ID
	private String transactionId;
	// ����Ͻ�
	private String regYms;
	// �����Ͻ�
	private String udtYms;
	// �ŷ�����
	private String tranYmd;
	
	// ��Ұ����ݾ�
	private BigDecimal cancelAmount;
	// ��Һΰ���ġ���ݾ�
	private BigDecimal cancelTax;
	// ��ҿ��ŷ���ȣ
	private String orgTranNo;
	
	// ī������޵�����
	private String cardMsg;

	@Override
	public String toString() {
		return "PaymentEntity >>>> �ŷ���ȣ::"+tranNo+"\n"+"�����ݾ�::"+paymentAmount+"\n"+"�ΰ���ġ���ݾ�::"+taxAmount+"\n"+"�����ܾ�::"+paymentBalance+"\n"+"�ΰ���ġ���ܾ�::"+taxBalance+"\n"+"��ȣȭ_ī������::"+encCardInfo+"\n"+"�Һΰ���::"+installmentMonth+"\n"+"�������νð�::"+payApplyYms+"\n"+"������ҽð�::"+payCancelYms+"\n"+"�ŷ� ����::"+status+"\n"+"Ʈ�����ID::"+transactionId+"\n"+"����Ͻ�::"+regYms+"\n"+"�����Ͻ�::"+udtYms;
	}
}
