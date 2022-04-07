package com.kakao.payment.repository;

import org.apache.ibatis.annotations.Mapper;

import com.kakao.payment.entity.PaymentEntity;

@Mapper
public interface PaymentTranMapper {
	int insertPaymentTran(PaymentEntity paymentEntity);
	int updatePaymentTran(PaymentEntity paymentEntity);
	int updatePaymentCancelTran(PaymentEntity paymentEntity);
	PaymentEntity selectPaymentTran(PaymentEntity paymentEntity);
	String selectPaymentSEQ(String tranYmd);
}
