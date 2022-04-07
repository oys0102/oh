package com.kakao.payment.common.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.kakao.payment.common.exception.PaymentBaseException;
import com.kakao.payment.common.type.ApiErrorResponseType;

@Component
public class AesUtil {
	private static final int GCM_IV_LENGTH = 12;
	private static final int GCM_TAG_LENGTH = 128;
	private static final int AES_KEY_SIZE = 32;
	private static final String AES_ALGORITHM = "AES";
	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static String aesKey;

	AesUtil(@Value("${e2ee.aes.key}") String key) {
		aesKey = key;
	}

	public static String encrypt(String plainText) throws PaymentBaseException {
		try {
			byte[] iv = new byte[GCM_IV_LENGTH];
			SecureRandom random = new SecureRandom();
			random.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), gcmParameterSpec);

			byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			return Base64Utils.encodeToUrlSafeString(ArrayUtils.addAll(iv, encrypted));
		} catch (Exception e) {
			throw new PaymentBaseException(ApiErrorResponseType.E2EE_ENCRTPYION_ERROR,
					ApiErrorResponseType.E2EE_ENCRTPYION_ERROR.getMessage(), e);
		}
	}

	public static String decrypt(String cryptogram) throws PaymentBaseException {
		try {
			// Decryption results in strings of the same formula as IV + Cryptogram
			byte[] buffer = Base64Utils.decodeFromUrlSafeString(cryptogram);
			byte[] iv = ArrayUtils.subarray(buffer, 0, GCM_IV_LENGTH);
			byte[] encrypted = ArrayUtils.subarray(buffer, GCM_IV_LENGTH, buffer.length);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), gcmParameterSpec);

			byte[] plainBytes = cipher.doFinal(encrypted);
			return new String(plainBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new PaymentBaseException(ApiErrorResponseType.E2EE_ENCRTPYION_ERROR,
					ApiErrorResponseType.E2EE_ENCRTPYION_ERROR.getMessage(), e);
		}
	}

	private static SecretKey getSecretKey() throws PaymentBaseException {
		byte[] key = Base64Utils.decodeFromUrlSafeString(aesKey);

		if (key.length != AES_KEY_SIZE) {
			throw new PaymentBaseException(ApiErrorResponseType.E2EE_KEY_NOT_FOUND);
		}

		return new SecretKeySpec(key, AES_ALGORITHM);
	}
}
