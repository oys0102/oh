package com.kakao.payment.entity;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentCancelEntity {

	// ��Ұŷ���ȣ
	private String cancelTranNo;
	// ���ŷ���ȣ
	private String tranNo;
	// ����
	private String status;
	// �ŷ��ݾ�
	private BigDecimal tranAmount;
	// �ΰ���ġ���ݾ�
	private BigDecimal taxAmount;
	// Ʈ�����ID
	private String transactionId;
	// ����Ͻ�
	private String regYms;
	// �����Ͻ�
	private String udtYms;

	@Override
	public String toString() {
		return "CancelEntity >>>> ��Ұŷ���ȣ::"+cancelTranNo+
								"\n"+"���ŷ���ȣ::"+tranNo+
								"\n"+"����::"+status+
								"\n"+"�ŷ��ݾ�::"+tranAmount+
								"\n"+"�ΰ���ġ���ݾ�::"+taxAmount+
								"\n"+"Ʈ�����ID::"+transactionId+
								"\n"+"����Ͻ�::"+regYms+
								"\n"+"�����Ͻ�::"+udtYms;
	}
}
