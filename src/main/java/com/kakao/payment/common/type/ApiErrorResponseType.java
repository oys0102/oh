/*
 * Copyright (c) 2022 J Bank. All rights reserved.
 * J Bank PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.kakao.payment.common.type;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import com.kakao.payment.common.consts.PaymentConsts;

import lombok.Getter;

@Getter
public enum ApiErrorResponseType {

	// E2EE
	E2EE_ENCRTPYION_ERROR("E000", "E2EE data encryption error."),
	E2EE_KEY_NOT_FOUND("E001", "E2EE Key is not found."),
	
	// Business Error
	CARD_INIT_ERROR("B001", "tax(�ΰ���ġ��)�� ���� �ݾ� ���� Ŭ �� �����ϴ�."),
	CANCEL_CNT_ERROR("B002", "��ü��Ҵ� 1���� �����մϴ�."),
	CANCEL_ZERO_ERROR("B003", "��Ұ����� �ݾ��� �����ϴ�."),
	DATA_NULL("B004", "��ȸ����� �����ϴ�."),

	// DataBase Error
	DATABASE_ERROR("D001", "DataBase error"),
	
	REQUEST_VALIDATION_ERROR("C001", "The request is invalid."),
	CARD_ERROR("C002", "Card server error"),
	INTERNAL_SERVER_ERROR("C999", "Internal server error");

	private final String code;
	private final String message;
	private final String formatMessage;
	private final HttpStatus status;

	ApiErrorResponseType(String code) {
		this(code, PaymentConsts.EMPTY);
	}

	ApiErrorResponseType(String code, String message) {
		this(code, message, PaymentConsts.EMPTY);
	}

	ApiErrorResponseType(String code, String message, String formatMessage) {
		this(code, message, formatMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	ApiErrorResponseType(String code, String message, String formatMessage, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.formatMessage = formatMessage;
		this.status = status;
	}

	private static final Map<String, ApiErrorResponseType> cache = Arrays.stream(values())
			.collect(Collectors.toMap(ApiErrorResponseType::getCode, Function.identity()));

	public static ApiErrorResponseType getByCode(String code) {
		return cache.get(code);
	}

	public String format(Object... values) {
		return String.format(formatMessage, values);
	}
}
