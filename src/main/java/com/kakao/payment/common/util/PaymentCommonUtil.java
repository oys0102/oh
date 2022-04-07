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
	 * 입력항목 초기 검증 및 세팅(부가가치세)
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
	 * 카드번호 마스킹처리
	 * @param cardNo
	 * @return
	 * @throws Exception
	 */
	public String maskCardNo(String cardNo) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		sb.append(cardNo.substring(0,6)) 															// 카드번호 앞 6자리
		  .append(cardNo.substring(6,cardNo.length()-3).replaceAll(".", PaymentConsts.MASK_CHAR)) 	// 카드번호 중간 마스킹처리
		  .append(cardNo.substring(cardNo.length()-3, cardNo.length())); 							// 카드번호 뒤 3자리
		
		return sb.toString();
	}
	
	/**
	 * 카드사 전송용 결제전문 생성
	 * @param requestEntity
	 * @return
	 */
	public String generatePaymentMsg(PaymentInquiryResponseDto paymentDto, String funcDvn) {
		
		String resultMsg = "";
		String commonHeaderMsg = "";
		String dataMsg = "";
		
		/* 전문생성 */
		
		//공통헤더부문
		commonHeaderMsg = _genCommonHeaderMsg(funcDvn, paymentDto.getTranNo());
		
		//데이터부문
		dataMsg = _genDataMsg(funcDvn, paymentDto);
		
		//공통헤더부문+데이터부문 (자릿수 제외)
		resultMsg = strConcat(commonHeaderMsg, dataMsg);
		
		log.info("전문 자릿수 :: {}" , resultMsg.length());
		
		resultMsg = strConcat(StringUtils.leftPad(String.valueOf(resultMsg.length()), 4, PaymentConsts.NUM_PAD), resultMsg);
		
		log.info("전문 출력 :: {}" , resultMsg);
		
		return resultMsg;
	}
	
	/**
	 * 전문 공통부 생성
	 * @param requestEntity
	 * @return
	 */
	private String _genCommonHeaderMsg(String funcDvn, String tranNo) {
		
		/* 공통헤더부 전문생성 */
		
		
		String commonHeaderMsg = strConcat(
																						// 0.데이터 길이 	-> 데이터부 조립 후 세팅
					 StringUtils.rightPad(funcDvn, 10, PaymentConsts.CHAR_PAD)			// 1.데이터 구분	[문자 10]
					,StringUtils.rightPad(tranNo,  20, PaymentConsts.CHAR_PAD)			// 2.관리번호		[문자 20]
				);
		
		return commonHeaderMsg;
	}
	
	/**
	 * 전문 데이터부 생성
	 * @param requestEntity
	 * @return
	 */
	private String _genDataMsg(String funcDvn, PaymentInquiryResponseDto requestDto) {
		
		// 기능구분에 따른 결제/취소금액 세팅
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
		
		/* 전문생성 */
		// 데이터부
		String commonHeaderMsg = strConcat(
					 StringUtils.rightPad(requestDto.getCardNo()			, 20, PaymentConsts.NUM_PAD)		// 0. 카드번호 			[숫자(L) 20]
					,StringUtils.leftPad (requestDto.getInstallmentMonth()	, 2	, PaymentConsts.NUM_ZERO_PAD)	// 1. 할부개월수 		[숫자(0)  2]
					,StringUtils.rightPad(requestDto.getCardExp()			, 4	, PaymentConsts.NUM_PAD)	 	// 2. 카드유효기간 		[숫자(L)  4]
					,StringUtils.rightPad(requestDto.getCardCVC()			, 3	, PaymentConsts.NUM_PAD)	 	// 3. cvc 				[숫자(L)  3]
					,StringUtils.leftPad (tranAmt.toString()				, 10, PaymentConsts.NUM_PAD)	 	// 4. 거래금액 			[숫자    10]
					,StringUtils.leftPad (taxAmt.toString()					, 10, PaymentConsts.NUM_ZERO_PAD)	// 5. 부가가치세 		[숫자(0) 10]
					,StringUtils.rightPad(orgTranNo							, 20, PaymentConsts.CHAR_PAD)	 	// 6. 원거래관리번호	[문자 	 20]
					,StringUtils.rightPad(requestDto.getEncCardInfo()		,300, PaymentConsts.CHAR_PAD)	 	// 7. 암호화된카드정보 	[문자 	300]
					,StringUtils.rightPad(PaymentConsts.EMPTY				, 47, PaymentConsts.CHAR_PAD)	 	// 8. 예비필드 			[문자 	 47]
				);
		
		return commonHeaderMsg;
	}
	
	// 할부개월 세팅
	public String setInstallmentMonth(int installmentMonth) {
		
		if(0 == installmentMonth) {
			return "00";
		}else {
			return String.valueOf(installmentMonth);
		}
	}
	
	// 결제/취소구분 세팅
	public String setStatus(String status) {
		
		//STATUS_READY 		= "00";	// 전송신청 
		//STATUS_PAY 		= "10";	// 결제승인 
		//STATUS_CAN 		= "90";	// 전체취소 
		//STATUS_CAN_PART 	= "91";	// 부분취소 
		//STATUS_CAN_FAIL 	= "99";	// 취소실패 
		
		switch (status) {
			case PaymentConsts.STATUS_READY:
				return "전송신청";
			case PaymentConsts.STATUS_PAY:
				return "결제승인";
			case PaymentConsts.STATUS_CAN:
				return "전체취소";
			case PaymentConsts.STATUS_CAN_PART:
				return "부분취소";
			case PaymentConsts.STATUS_CAN_FAIL:
				return "취소실패";
		}
		
		return status;
	}
	
	// 문자열 병합
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
