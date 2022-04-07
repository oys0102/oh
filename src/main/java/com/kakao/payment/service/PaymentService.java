package com.kakao.payment.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.payment.common.consts.PaymentConsts;
import com.kakao.payment.common.exception.PaymentBaseException;
import com.kakao.payment.common.type.ApiErrorResponseType;
import com.kakao.payment.common.util.AesUtil;
import com.kakao.payment.common.util.PaymentCommonUtil;
import com.kakao.payment.dto.PayInquiryRequestDto;
import com.kakao.payment.dto.PaymentCancelRequestDto;
import com.kakao.payment.dto.PaymentInquiryResponseDto;
import com.kakao.payment.dto.PaymentRequestDto;
import com.kakao.payment.dto.PaymentResponseDto;
import com.kakao.payment.entity.PaymentCancelEntity;
import com.kakao.payment.entity.PaymentEntity;
import com.kakao.payment.entity.PaymentMsgEntity;
import com.kakao.payment.repository.PaymentCancelTranMapper;
import com.kakao.payment.repository.PaymentMsgMapper;
import com.kakao.payment.repository.PaymentTranMapper;

import lombok.extern.slf4j.Slf4j;

//비즈니스 로직 구현(BF)
@Slf4j
@Service
public class PaymentService {

	@Autowired
	private PaymentTranMapper paymentTranMapper;
	@Autowired
	private PaymentCancelTranMapper paymentCancelTranMapper;
	@Autowired
	private PaymentMsgMapper paymentMsgMapper;
	ObjectMapper objectMapper = new ObjectMapper();
	
	// 결제
	public PaymentResponseDto cardPayProcess(PaymentRequestDto paymentRequestDto) throws PaymentBaseException {
		PaymentResponseDto responseDto = new PaymentResponseDto();
		PaymentCommonUtil commonUtil = new PaymentCommonUtil(); 
		
		/**
		 *  유효성검증 (부가세관련)
		 */
		commonUtil.requestPayInitialize(paymentRequestDto);
		
		/**
		 *  결제 정보 생성
		 */
		// 거래일시
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String strCurrentYMS = sdf.format(new Date());

		// 트랜잭션ID
		String uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
		
		// 할부개월
		String strInstallmentMonth = commonUtil.setInstallmentMonth(paymentRequestDto.getInstallmentMonth());
		
		// 거래번호채번
		SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyyMMdd");
		String strCurrentYMD = sdfYMD.format(new Date());
		String tranNo = "";
		
		// 관리번호(unique id, 20자리) 채번
		tranNo = paymentTranMapper.selectPaymentSEQ(strCurrentYMD);
		
		// 카드정보 병합
		String cardInfo = commonUtil.strConcat(
				 paymentRequestDto.getCardNo()	// 카드번호
				,PaymentConsts.PIPE				// 구분자
				,paymentRequestDto.getCardExp()	// 카드유효기간
				,PaymentConsts.PIPE				// 구분자
				,paymentRequestDto.getCardCVC()	// getCardCVC
			);
		
		// 카드정보 암호화
		String encCardInfo = AesUtil.encrypt(cardInfo);
		
		// 결제정보 INSERT
		PaymentEntity paymentEntity = PaymentEntity.builder()
													.tranNo(tranNo)
													.paymentAmount(paymentRequestDto.getTranAmount())
													.taxAmount(paymentRequestDto.getTaxAmount())
													.paymentBalance(paymentRequestDto.getTranAmount())
													.taxBalance(paymentRequestDto.getTaxAmount())
													.encCardInfo(encCardInfo)
													.installmentMonth(strInstallmentMonth)
													.payApplyYms(strCurrentYMS)
													.payCancelYms(strCurrentYMS)
													.status(PaymentConsts.STATUS_READY)
													.transactionId(uniqueId)
													.regYms(strCurrentYMS)
													.udtYms(strCurrentYMS)
													.build();
		
		log.info("request >>>> {}", paymentEntity.toString());
		
		//결제정보 INSERT
		paymentTranMapper.insertPaymentTran(paymentEntity);
		
		/**
		 *  결제정보 조회
		 */
		PayInquiryRequestDto requestInquiryDto = new PayInquiryRequestDto();
		PaymentInquiryResponseDto inquiryDto = new PaymentInquiryResponseDto();
		
		requestInquiryDto.setTranNo(tranNo);
		inquiryDto = payInquiryProcess(requestInquiryDto);

		/**
		 *  카드사 전송
		 */
		// 카드사 전송 전문 생성
		String requestMsg = commonUtil.generatePaymentMsg(inquiryDto, PaymentConsts.PAYMENT);

		/* 카드사 전송 START */
		log.info("카드사 전송 START");
		
		
		try {
			// 전송MSG INSERT
			PaymentMsgEntity paymentMsgEntity = PaymentMsgEntity.builder()
														.transactionId(uniqueId)
														.msg(requestMsg)
														.regYms(strCurrentYMS)
														.build();
			
			paymentMsgMapper.insertMsg(paymentMsgEntity);
			
			log.info("카드사 전송 START");
			
			/**
			 * 결제금액 및 부가가치세 취소 계산
			 */
			// 결제정보 UPDATE
			paymentEntity.setStatus(PaymentConsts.STATUS_PAY);	//결제승인
			
			log.info("request >>>> {}", paymentEntity.toString());
			
			//ibatis Mapper 이용 INSERT
			paymentTranMapper.updatePaymentTran(paymentEntity);
		} catch (Exception e) {
			// 결제정보 UPDATE
			paymentEntity.setStatus(PaymentConsts.STATUS_PAY_FAIL);	//결제실패
			
			log.info("request >>>> {}", paymentEntity.toString());
			
			//ibatis Mapper 이용 INSERT
			paymentTranMapper.updatePaymentTran(paymentEntity);
			
			throw new PaymentBaseException(ApiErrorResponseType.CARD_ERROR);
		}
		
		// 출력항목 세팅
		responseDto.setTranNo(tranNo);
		responseDto.setCardMsg(requestMsg);
		
		return responseDto;
	}

	// 결제취소
	public PaymentResponseDto payCancelProcess(PaymentCancelRequestDto paymentCancelRequestDto, String cancelDvn) throws Exception {
		PaymentResponseDto responseDto = new PaymentResponseDto();
		PaymentInquiryResponseDto inquiryDto = new PaymentInquiryResponseDto();
		PaymentCommonUtil commonUtil = new PaymentCommonUtil(); 
		
		/**
		 *  유효성검증 (부가세관련)
		 */
		commonUtil.requestPayInitialize(paymentCancelRequestDto);
		
		/**
		 *  원결제정보 조회
		 */
		PayInquiryRequestDto paymentRequestDto = new PayInquiryRequestDto();
		paymentRequestDto.setTranNo(paymentCancelRequestDto.getTranNo());
		inquiryDto = payInquiryProcess(paymentRequestDto);
		
		/**
		 *  취소구분에 따른 결제취소금액 및 상태세팅
		 */
		BigDecimal cancelAmt = BigDecimal.ZERO;
		BigDecimal cancelTax = BigDecimal.ZERO;
		String status = "";
		
		if(cancelDvn.equals(PaymentConsts.CANCEL_ALL)) {
			//전체취소
			if(PaymentConsts.CANCEL_ALL.equals(inquiryDto.getStatus())) {
				throw new PaymentBaseException(ApiErrorResponseType.CANCEL_CNT_ERROR);
			}
			
			cancelAmt = inquiryDto.getPaymentBalance();
			cancelTax = inquiryDto.getTaxBalance();
			
			if(cancelAmt.compareTo(BigDecimal.ZERO) == 0) {
				throw new PaymentBaseException(ApiErrorResponseType.CANCEL_ZERO_ERROR);
			}
			status = PaymentConsts.STATUS_CAN;
		}else {
			//부분취소
			cancelAmt = paymentCancelRequestDto.getTranAmount();
			cancelTax = paymentCancelRequestDto.getTaxAmount();
			status = PaymentConsts.STATUS_CAN_PART;
		}
		
		/**
		 *  결제취소 등록 정보 세팅
		 */
		// 거래일시
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String strCurrentYMS = sdf.format(new Date());

		// 트랜잭션ID
		String uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
		
		// 취소거래번호채번
		SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyyMMdd");
		String strCurrentYMD = sdfYMD.format(new Date());
		String cancelTranNo = "";
		
		// 관리번호(unique id, 20자리) 채번
		cancelTranNo = paymentCancelTranMapper.selectPaymentSEQ(strCurrentYMD);
		
		// 결제취소정보 INSERT
		PaymentCancelEntity paymentCancelEntity = PaymentCancelEntity.builder()
				.cancelTranNo(cancelTranNo)
				.tranNo(paymentCancelRequestDto.getTranNo())
				.status(PaymentConsts.STATUS_READY)	// 취소대기
				.tranAmount(cancelAmt)
				.taxAmount(cancelTax)
				.transactionId(uniqueId)
				.regYms(strCurrentYMS)
				.udtYms(strCurrentYMS)
		.build();
		
		log.info("request >>>> {}", paymentCancelEntity.toString());
		
		//ibatis Mapper 이용 INSERT
		paymentCancelTranMapper.insertPaymentTran(paymentCancelEntity);
		
		/**
		 *  결제취소정보 카드사 전송
		 */
		try {
			log.info("카드사 전송 START");
			
			inquiryDto.setOrgTranNo(paymentCancelRequestDto.getTranNo());
			inquiryDto.setCancelAmount(cancelAmt);
			inquiryDto.setCancelTax(cancelTax);
			paymentCancelRequestDto.setTranAmount(cancelAmt);
			paymentCancelRequestDto.setTaxAmount(cancelTax);
			
			// 카드사 전송 전문 생성
			String requestMsg = commonUtil.generatePaymentMsg(inquiryDto, PaymentConsts.CANCEL);
			
			// 카드사 전송 INSERT
			PaymentMsgEntity paymentMsgEntity = PaymentMsgEntity.builder()
														.transactionId(uniqueId)
														.msg(requestMsg)
														.regYms(strCurrentYMS)
														.build();
			
			paymentMsgMapper.insertMsg(paymentMsgEntity);
			
			/**
			 * 결제금액 및 부가가치세 취소 계산
			 */
			// 결제정보 상태 UPDATE
			PaymentEntity paymentEntity = calcCancel(inquiryDto, paymentCancelRequestDto);
			paymentEntity.setStatus(status);		// 취소상태
			paymentTranMapper.updatePaymentCancelTran(paymentEntity);
			
			// 결제취소정보 상태 UPDATE
			paymentCancelEntity.setStatus(status);	// 취소상태
			paymentCancelTranMapper.updatePaymentCancelTran(paymentCancelEntity);
			
			//출력항목 세팅
			responseDto.setCardMsg(requestMsg);
		} catch (Exception e) {
			// 결제정보 UPDATE
			PaymentEntity paymentEntity = calcCancel(inquiryDto, paymentCancelRequestDto);
			paymentEntity.setStatus(PaymentConsts.STATUS_CAN_FAIL);	//취소실패
			paymentTranMapper.updatePaymentCancelTran(paymentEntity);
			
			// 결제취소정보 상태 UPDATE
			paymentCancelEntity.setStatus(PaymentConsts.STATUS_CAN_FAIL);	// 취소상태
			paymentCancelTranMapper.updatePaymentCancelTran(paymentCancelEntity);
			
			throw new PaymentBaseException(ApiErrorResponseType.CARD_ERROR);
		}
		
		// 출력항목 세팅
		responseDto.setTranNo(cancelTranNo);
		
		return responseDto;
	}
	
	// 결제정보 조회
	public PaymentInquiryResponseDto payInquiryProcess(PayInquiryRequestDto payInquiryRequestDto) throws PaymentBaseException {
		PaymentInquiryResponseDto responseDto = new PaymentInquiryResponseDto();
		PaymentCommonUtil commonUtil = new PaymentCommonUtil();
		
		PaymentEntity payInquiry = PaymentEntity.builder()
				.tranNo(payInquiryRequestDto.getTranNo())
				.build();
		
		// 결제정보 조회
		
		PaymentEntity responseEntity;
		try {
			responseEntity = paymentTranMapper.selectPaymentTran(payInquiry);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PaymentBaseException(ApiErrorResponseType.DATABASE_ERROR);
		}
		
		if(responseEntity == null) {
			throw new PaymentBaseException(ApiErrorResponseType.DATA_NULL);
		}
		
		responseDto.setTranNo(responseEntity.getTranNo());
		responseDto.setPaymentAmount(responseEntity.getPaymentAmount());
		responseDto.setTaxAmount(responseEntity.getTaxAmount());
		responseDto.setPaymentBalance(responseEntity.getPaymentBalance());
		responseDto.setTaxBalance(responseEntity.getTaxBalance());
		responseDto.setInstallmentMonth(responseEntity.getInstallmentMonth());
		responseDto.setPayApplyYms(responseEntity.getPayApplyYms());
		responseDto.setPayCancelYms(responseEntity.getPayCancelYms());
		responseDto.setStatus(commonUtil.setStatus(responseEntity.getStatus()));
		responseDto.setTransactionId(responseEntity.getTransactionId());
		responseDto.setRegYms(responseEntity.getRegYms());
		responseDto.setUdtYms(responseEntity.getUdtYms());
		
		/**
		 * 카드정보 복호화 및 잘라서 출력세팅
		 */
		String encCardInfo = responseEntity.getEncCardInfo();
		// 복호화를 위한 KEY 분리
		String cardInfo = AesUtil.decrypt(encCardInfo);
		String[] arrayCard = cardInfo.split(PaymentConsts.SPLIT_PIPE);

		String cardNo = arrayCard[0];
		String cardExp = arrayCard[1];
		String cardCVC = arrayCard[2];
		
		// 카드정보 출력세팅
		responseDto.setEncCardInfo(encCardInfo);
		responseDto.setCardNo(cardNo);
		responseDto.setCardExp(cardExp);
		responseDto.setCardCVC(cardCVC);
		
		return responseDto;
	}

	/**
	 * 결제금액 및 부가가치세 취소 계산
	 */
	private PaymentEntity calcCancel(PaymentInquiryResponseDto balanceDto, PaymentCancelRequestDto cancelDto) {
		
		BigDecimal paymentBalance = balanceDto.getPaymentBalance();	// 결제 원거래 결제 잔액
		BigDecimal taxBalance = balanceDto.getTaxBalance();			// 결제 원거래 부가가치세 잔액
		BigDecimal cancelAmt = cancelDto.getTranAmount();			// 결제취소 결제금액
		BigDecimal cancelTax = cancelDto.getTaxAmount();			// 결제취소 부가가치세
		
		// 거래일시
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String strCurrentYMS = sdf.format(new Date());
		
		//TODO VALIDATION
		// 결제에 대한 전체취소는 1번만 가능합니다.
		// 부가가치세 정보를 넘기지 않는 경우, 결제데이터의 부가가치세 금액으로 취소합니다.
		
		PaymentEntity payInquiry = PaymentEntity.builder()
				.tranNo(balanceDto.getTranNo())
				.paymentBalance(paymentBalance.subtract(cancelAmt))
				.taxBalance(taxBalance.subtract(cancelTax))
				.payCancelYms(strCurrentYMS)
				.udtYms(strCurrentYMS)
				.build();
		
		return payInquiry;
	}
	
}
