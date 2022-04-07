/*
 * Copyright (c) 2022 J Bank. All rights reserved.
 * J Bank PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.kakao.payment.common.exception;


import com.kakao.payment.common.type.ApiErrorResponseType;

import lombok.Getter;

/**
 * BankBaseException
 */
@Getter
public class PaymentBaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final ApiErrorResponseType apiErrorResponseType;

	public PaymentBaseException() {
		this(ApiErrorResponseType.INTERNAL_SERVER_ERROR);
	}

	public PaymentBaseException(ApiErrorResponseType apiErrorResponseType) {
		this(apiErrorResponseType, apiErrorResponseType.getMessage());
	}

	public PaymentBaseException(String message) {
		this(ApiErrorResponseType.INTERNAL_SERVER_ERROR, message);
	}

	public PaymentBaseException(ApiErrorResponseType apiErrorResponseType, String message) {
		super(message);
		this.apiErrorResponseType = apiErrorResponseType;
	}

	public PaymentBaseException(ApiErrorResponseType apiErrorResponseType, String message, Throwable cause) {
		super(message, cause);
		this.apiErrorResponseType = apiErrorResponseType;
	}
}
