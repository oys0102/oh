package com.kakao.payment.common.util;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.kakao.payment.common.consts.PaymentConsts;
import com.kakao.payment.common.exception.PaymentBaseException;
import com.kakao.payment.common.type.ApiErrorResponseType;
import com.kakao.payment.dto.CommonRequestDto;
import com.kakao.payment.dto.PaymentInquiryResponseDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentCommonUtil {

	/**
	 * �Է��׸� �ʱ� ���� �� ����(�ΰ���ġ��)
	 * @param commonRequestDto
	 * @throws PaymentBaseException
	 */
	public void requestPayInitialize(CommonRequestDto commonRequestDto) throws PaymentBaseException {

		BigDecimal paymentAmount = commonRequestDto.getTranAmount();

		if (commonRequestDto.getTaxAmount() == null) {
			BigDecimal tax = paymentAmount.divide(new BigDecimal(11), 0, BigDecimal.ROUND_HALF_UP);
			commonRequestDto.setTaxAmount(tax);

		} else if (paymentAmount.compareTo(commonRequestDto.getTaxAmount()) < 0) {
			throw new PaymentBaseException(ApiErrorResponseType.CARD_INIT_ERROR,
					ApiErrorResponseType.CARD_INIT_ERROR.getMessage());
		}

	}
	
	/**
	 * ī���ȣ ����ŷó��
	 * @param cardNo
	 * @return
	 * @throws Exception
	 */
	public String maskCardNo(String cardNo) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		sb.append(cardNo.substring(0,6)) 															// ī���ȣ �� 6�ڸ�
		  .append(cardNo.substring(6,cardNo.length()-3).replaceAll(".", PaymentConsts.MASK_CHAR)) 	// ī���ȣ �߰� ����ŷó��
		  .append(cardNo.substring(cardNo.length()-3, cardNo.length())); 							// ī���ȣ �� 3�ڸ�
		
		return sb.toString();
	}
	
	/**
	 * ī��� ���ۿ� �������� ����
	 * @param requestEntity
	 * @return
	 */
	public String generatePaymentMsg(PaymentInquiryResponseDto paymentDto, String funcDvn) {
		
		String resultMsg = "";
		String commonHeaderMsg = "";
		String dataMsg = "";
		
		/* �������� */
		
		//��������ι�
		commonHeaderMsg = _genCommonHeaderMsg(funcDvn, paymentDto.getTranNo());
		
		//�����ͺι�
		dataMsg = _genDataMsg(funcDvn, paymentDto);
		
		//��������ι�+�����ͺι� (�ڸ��� ����)
		resultMsg = strConcat(commonHeaderMsg, dataMsg);
		
		log.info("���� �ڸ��� :: {}" , resultMsg.length());
		
		resultMsg = strConcat(StringUtils.leftPad(String.valueOf(resultMsg.length()), 4, PaymentConsts.NUM_PAD), resultMsg);
		
		log.info("���� ��� :: {}" , resultMsg);
		
		return resultMsg;
	}
	
	/**
	 * ���� ����� ����
	 * @param requestEntity
	 * @return
	 */
	private String _genCommonHeaderMsg(String funcDvn, String tranNo) {
		
		/* ��������� �������� */
		
		
		String commonHeaderMsg = strConcat(
																						// 0.������ ���� 	-> �����ͺ� ���� �� ����
					 StringUtils.rightPad(funcDvn, 10, PaymentConsts.CHAR_PAD)			// 1.������ ����	[���� 10]
					,StringUtils.rightPad(tranNo,  20, PaymentConsts.CHAR_PAD)			// 2.������ȣ		[���� 20]
				);
		
		return commonHeaderMsg;
	}
	
	/**
	 * ���� �����ͺ� ����
	 * @param requestEntity
	 * @return
	 */
	private String _genDataMsg(String funcDvn, PaymentInquiryResponseDto requestDto) {
		
		// ��ɱ��п� ���� ����/��ұݾ� ����
		BigDecimal 	tranAmt = new BigDecimal(0);
		BigDecimal 	taxAmt = new BigDecimal(0);
		String 		orgTranNo = "";
		
		if(StringUtils.equals(PaymentConsts.PAYMENT, funcDvn)) {
			tranAmt = requestDto.getPaymentAmount();  
			taxAmt = requestDto.getTaxAmount();      
		}else if(StringUtils.equals(PaymentConsts.CANCEL, funcDvn)) {
			tranAmt = requestDto.getCancelAmount();  
			taxAmt = requestDto.getCancelTax();      
			orgTranNo = requestDto.getOrgTranNo();
		}else {
			throw new PaymentBaseException(ApiErrorResponseType.REQUEST_VALIDATION_ERROR);
		}
		
		/* �������� */
		// �����ͺ�
		String commonHeaderMsg = strConcat(
					 StringUtils.rightPad(requestDto.getCardNo()			, 20, PaymentConsts.NUM_PAD)		// 0. ī���ȣ 			[����(L) 20]
					,StringUtils.leftPad (requestDto.getInstallmentMonth()	, 2	, PaymentConsts.NUM_ZERO_PAD)	// 1. �Һΰ����� 		[����(0)  2]
					,StringUtils.rightPad(requestDto.getCardExp()			, 4	, PaymentConsts.NUM_PAD)	 	// 2. ī����ȿ�Ⱓ 		[����(L)  4]
					,StringUtils.rightPad(requestDto.getCardCVC()			, 3	, PaymentConsts.NUM_PAD)	 	// 3. cvc 				[����(L)  3]
					,StringUtils.leftPad (tranAmt.toString()				, 10, PaymentConsts.NUM_PAD)	 	// 4. �ŷ��ݾ� 			[����    10]
					,StringUtils.leftPad (taxAmt.toString()					, 10, PaymentConsts.NUM_ZERO_PAD)	// 5. �ΰ���ġ�� 		[����(0) 10]
					,StringUtils.rightPad(orgTranNo							, 20, PaymentConsts.CHAR_PAD)	 	// 6. ���ŷ�������ȣ	[���� 	 20]
					,StringUtils.rightPad(requestDto.getEncCardInfo()		,300, PaymentConsts.CHAR_PAD)	 	// 7. ��ȣȭ��ī������ 	[���� 	300]
					,StringUtils.rightPad(PaymentConsts.EMPTY				, 47, PaymentConsts.CHAR_PAD)	 	// 8. �����ʵ� 			[���� 	 47]
				);
		
		return commonHeaderMsg;
	}
	
	// �Һΰ��� ����
	public String setInstallmentMonth(int installmentMonth) {
		
		if(0 == installmentMonth) {
			return "00";
		}else {
			return String.valueOf(installmentMonth);
		}
	}
	
	// ����/��ұ��� ����
	public String setStatus(String status) {
		
		//STATUS_READY 		= "00";	// ���۽�û 
		//STATUS_PAY 		= "10";	// �������� 
		//STATUS_CAN 		= "90";	// ��ü��� 
		//STATUS_CAN_PART 	= "91";	// �κ���� 
		//STATUS_CAN_FAIL 	= "99";	// ��ҽ��� 
		
		switch (status) {
			case PaymentConsts.STATUS_READY:
				return "���۽�û";
			case PaymentConsts.STATUS_PAY:
				return "��������";
			case PaymentConsts.STATUS_CAN:
				return "��ü���";
			case PaymentConsts.STATUS_CAN_PART:
				return "�κ����";
			case PaymentConsts.STATUS_CAN_FAIL:
				return "��ҽ���";
		}
		
		return status;
	}
	
	// ���ڿ� ����
	public String strConcat(String... str) {
		
		if(str.length < 1) {
			return PaymentConsts.EMPTY;
		}
		
		StringBuilder sb = new StringBuilder("");

		for(String arg : str) {
			sb.append(arg);
		}
		
		return sb.toString();
	}
}
