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

//����Ͻ� ���� ����(BF)
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
	
	// ����
	public PaymentResponseDto cardPayProcess(PaymentRequestDto paymentRequestDto) throws PaymentBaseException {
		PaymentResponseDto responseDto = new PaymentResponseDto();
		PaymentCommonUtil commonUtil = new PaymentCommonUtil(); 
		
		/**
		 *  ��ȿ������ (�ΰ�������)
		 */
		commonUtil.requestPayInitialize(paymentRequestDto);
		
		/**
		 *  ���� ���� ����
		 */
		// �ŷ��Ͻ�
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String strCurrentYMS = sdf.format(new Date());

		// Ʈ�����ID
		String uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
		
		// �Һΰ���
		String strInstallmentMonth = commonUtil.setInstallmentMonth(paymentRequestDto.getInstallmentMonth());
		
		// �ŷ���ȣä��
		SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyyMMdd");
		String strCurrentYMD = sdfYMD.format(new Date());
		String tranNo = "";
		
		// ������ȣ(unique id, 20�ڸ�) ä��
		tranNo = paymentTranMapper.selectPaymentSEQ(strCurrentYMD);
		
		// ī������ ����
		String cardInfo = commonUtil.strConcat(
				 paymentRequestDto.getCardNo()	// ī���ȣ
				,PaymentConsts.PIPE				// ������
				,paymentRequestDto.getCardExp()	// ī����ȿ�Ⱓ
				,PaymentConsts.PIPE				// ������
				,paymentRequestDto.getCardCVC()	// getCardCVC
			);
		
		// ī������ ��ȣȭ
		String encCardInfo = AesUtil.encrypt(cardInfo);
		
		// �������� INSERT
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
		
		//�������� INSERT
		paymentTranMapper.insertPaymentTran(paymentEntity);
		
		/**
		 *  �������� ��ȸ
		 */
		PayInquiryRequestDto requestInquiryDto = new PayInquiryRequestDto();
		PaymentInquiryResponseDto inquiryDto = new PaymentInquiryResponseDto();
		
		requestInquiryDto.setTranNo(tranNo);
		inquiryDto = payInquiryProcess(requestInquiryDto);

		/**
		 *  ī��� ����
		 */
		// ī��� ���� ���� ����
		String requestMsg = commonUtil.generatePaymentMsg(inquiryDto, PaymentConsts.PAYMENT);

		/* ī��� ���� START */
		log.info("ī��� ���� START");
		
		
		try {
			// ����MSG INSERT
			PaymentMsgEntity paymentMsgEntity = PaymentMsgEntity.builder()
														.transactionId(uniqueId)
														.msg(requestMsg)
														.regYms(strCurrentYMS)
														.build();
			
			paymentMsgMapper.insertMsg(paymentMsgEntity);
			
			log.info("ī��� ���� START");
			
			/**
			 * �����ݾ� �� �ΰ���ġ�� ��� ���
			 */
			// �������� UPDATE
			paymentEntity.setStatus(PaymentConsts.STATUS_PAY);	//��������
			
			log.info("request >>>> {}", paymentEntity.toString());
			
			//ibatis Mapper �̿� INSERT
			paymentTranMapper.updatePaymentTran(paymentEntity);
		} catch (Exception e) {
			// �������� UPDATE
			paymentEntity.setStatus(PaymentConsts.STATUS_PAY_FAIL);	//��������
			
			log.info("request >>>> {}", paymentEntity.toString());
			
			//ibatis Mapper �̿� INSERT
			paymentTranMapper.updatePaymentTran(paymentEntity);
			
			throw new PaymentBaseException(ApiErrorResponseType.CARD_ERROR);
		}
		
		// ����׸� ����
		responseDto.setTranNo(tranNo);
		responseDto.setCardMsg(requestMsg);
		
		return responseDto;
	}

	// �������
	public PaymentResponseDto payCancelProcess(PaymentCancelRequestDto paymentCancelRequestDto, String cancelDvn) throws Exception {
		PaymentResponseDto responseDto = new PaymentResponseDto();
		PaymentInquiryResponseDto inquiryDto = new PaymentInquiryResponseDto();
		PaymentCommonUtil commonUtil = new PaymentCommonUtil(); 
		
		/**
		 *  ��ȿ������ (�ΰ�������)
		 */
		commonUtil.requestPayInitialize(paymentCancelRequestDto);
		
		/**
		 *  ���������� ��ȸ
		 */
		PayInquiryRequestDto paymentRequestDto = new PayInquiryRequestDto();
		paymentRequestDto.setTranNo(paymentCancelRequestDto.getTranNo());
		inquiryDto = payInquiryProcess(paymentRequestDto);
		
		/**
		 *  ��ұ��п� ���� ������ұݾ� �� ���¼���
		 */
		BigDecimal cancelAmt = BigDecimal.ZERO;
		BigDecimal cancelTax = BigDecimal.ZERO;
		String status = "";
		
		if(cancelDvn.equals(PaymentConsts.CANCEL_ALL)) {
			//��ü���
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
			//�κ����
			cancelAmt = paymentCancelRequestDto.getTranAmount();
			cancelTax = paymentCancelRequestDto.getTaxAmount();
			status = PaymentConsts.STATUS_CAN_PART;
		}
		
		/**
		 *  ������� ��� ���� ����
		 */
		// �ŷ��Ͻ�
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String strCurrentYMS = sdf.format(new Date());

		// Ʈ�����ID
		String uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
		
		// ��Ұŷ���ȣä��
		SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyyMMdd");
		String strCurrentYMD = sdfYMD.format(new Date());
		String cancelTranNo = "";
		
		// ������ȣ(unique id, 20�ڸ�) ä��
		cancelTranNo = paymentCancelTranMapper.selectPaymentSEQ(strCurrentYMD);
		
		// ����������� INSERT
		PaymentCancelEntity paymentCancelEntity = PaymentCancelEntity.builder()
				.cancelTranNo(cancelTranNo)
				.tranNo(paymentCancelRequestDto.getTranNo())
				.status(PaymentConsts.STATUS_READY)	// ��Ҵ��
				.tranAmount(cancelAmt)
				.taxAmount(cancelTax)
				.transactionId(uniqueId)
				.regYms(strCurrentYMS)
				.udtYms(strCurrentYMS)
		.build();
		
		log.info("request >>>> {}", paymentCancelEntity.toString());
		
		//ibatis Mapper �̿� INSERT
		paymentCancelTranMapper.insertPaymentTran(paymentCancelEntity);
		
		/**
		 *  ����������� ī��� ����
		 */
		try {
			log.info("ī��� ���� START");
			
			inquiryDto.setOrgTranNo(paymentCancelRequestDto.getTranNo());
			inquiryDto.setCancelAmount(cancelAmt);
			inquiryDto.setCancelTax(cancelTax);
			paymentCancelRequestDto.setTranAmount(cancelAmt);
			paymentCancelRequestDto.setTaxAmount(cancelTax);
			
			// ī��� ���� ���� ����
			String requestMsg = commonUtil.generatePaymentMsg(inquiryDto, PaymentConsts.CANCEL);
			
			// ī��� ���� INSERT
			PaymentMsgEntity paymentMsgEntity = PaymentMsgEntity.builder()
														.transactionId(uniqueId)
														.msg(requestMsg)
														.regYms(strCurrentYMS)
														.build();
			
			paymentMsgMapper.insertMsg(paymentMsgEntity);
			
			/**
			 * �����ݾ� �� �ΰ���ġ�� ��� ���
			 */
			// �������� ���� UPDATE
			PaymentEntity paymentEntity = calcCancel(inquiryDto, paymentCancelRequestDto);
			paymentEntity.setStatus(status);		// ��һ���
			paymentTranMapper.updatePaymentCancelTran(paymentEntity);
			
			// ����������� ���� UPDATE
			paymentCancelEntity.setStatus(status);	// ��һ���
			paymentCancelTranMapper.updatePaymentCancelTran(paymentCancelEntity);
			
			//����׸� ����
			responseDto.setCardMsg(requestMsg);
		} catch (Exception e) {
			// �������� UPDATE
			PaymentEntity paymentEntity = calcCancel(inquiryDto, paymentCancelRequestDto);
			paymentEntity.setStatus(PaymentConsts.STATUS_CAN_FAIL);	//��ҽ���
			paymentTranMapper.updatePaymentCancelTran(paymentEntity);
			
			// ����������� ���� UPDATE
			paymentCancelEntity.setStatus(PaymentConsts.STATUS_CAN_FAIL);	// ��һ���
			paymentCancelTranMapper.updatePaymentCancelTran(paymentCancelEntity);
			
			throw new PaymentBaseException(ApiErrorResponseType.CARD_ERROR);
		}
		
		// ����׸� ����
		responseDto.setTranNo(cancelTranNo);
		
		return responseDto;
	}
	
	// �������� ��ȸ
	public PaymentInquiryResponseDto payInquiryProcess(PayInquiryRequestDto payInquiryRequestDto) throws PaymentBaseException {
		PaymentInquiryResponseDto responseDto = new PaymentInquiryResponseDto();
		PaymentCommonUtil commonUtil = new PaymentCommonUtil();
		
		PaymentEntity payInquiry = PaymentEntity.builder()
				.tranNo(payInquiryRequestDto.getTranNo())
				.build();
		
		// �������� ��ȸ
		
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
		 * ī������ ��ȣȭ �� �߶� ��¼���
		 */
		String encCardInfo = responseEntity.getEncCardInfo();
		// ��ȣȭ�� ���� KEY �и�
		String cardInfo = AesUtil.decrypt(encCardInfo);
		String[] arrayCard = cardInfo.split(PaymentConsts.SPLIT_PIPE);

		String cardNo = arrayCard[0];
		String cardExp = arrayCard[1];
		String cardCVC = arrayCard[2];
		
		// ī������ ��¼���
		responseDto.setEncCardInfo(encCardInfo);
		responseDto.setCardNo(cardNo);
		responseDto.setCardExp(cardExp);
		responseDto.setCardCVC(cardCVC);
		
		return responseDto;
	}

	/**
	 * �����ݾ� �� �ΰ���ġ�� ��� ���
	 */
	private PaymentEntity calcCancel(PaymentInquiryResponseDto balanceDto, PaymentCancelRequestDto cancelDto) {
		
		BigDecimal paymentBalance = balanceDto.getPaymentBalance();	// ���� ���ŷ� ���� �ܾ�
		BigDecimal taxBalance = balanceDto.getTaxBalance();			// ���� ���ŷ� �ΰ���ġ�� �ܾ�
		BigDecimal cancelAmt = cancelDto.getTranAmount();			// ������� �����ݾ�
		BigDecimal cancelTax = cancelDto.getTaxAmount();			// ������� �ΰ���ġ��
		
		// �ŷ��Ͻ�
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String strCurrentYMS = sdf.format(new Date());
		
		//TODO VALIDATION
		// ������ ���� ��ü��Ҵ� 1���� �����մϴ�.
		// �ΰ���ġ�� ������ �ѱ��� �ʴ� ���, ������������ �ΰ���ġ�� �ݾ����� ����մϴ�.
		
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
