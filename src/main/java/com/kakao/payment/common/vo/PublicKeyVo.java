package com.kakao.payment.common.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PublicKeyVo {
	private String keyId;
	private String publicKey;
}