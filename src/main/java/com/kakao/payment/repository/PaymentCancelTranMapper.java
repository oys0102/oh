package com.kakao.payment.repository;

import org.apache.ibatis.annotations.Mapper;

import com.kakao.payment.entity.PaymentCancelEntity;
import com.kakao.payment.entity.PaymentEntity;

@Mapper
public interface PaymentCancelTranMapper {
	int insertPaymentTran(PaymentCancelEntity paymentCancelEntity);
	PaymentEntity selectPaymentCancelTran(PaymentCancelEntity paymentCancelEntity);
	String selectPaymentSEQ(String tranYmd);
	int updatePaymentCancelTran(PaymentCancelEntity paymentCancelEntity);
}
