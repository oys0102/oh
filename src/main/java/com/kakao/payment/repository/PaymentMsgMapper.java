package com.kakao.payment.repository;

import org.apache.ibatis.annotations.Mapper;

import com.kakao.payment.entity.PaymentMsgEntity;

@Mapper
public interface PaymentMsgMapper {
	int insertMsg(PaymentMsgEntity paymentMsgEntity);
}
