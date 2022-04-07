package com.kakao.payment.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakao.payment.common.consts.PaymentConsts;
import com.kakao.payment.common.util.PaymentCommonUtil;
import com.kakao.payment.dto.PayInquiryRequestDto;
import com.kakao.payment.dto.PaymentCancelRequestDto;
import com.kakao.payment.dto.PaymentInquiryResponseDto;
import com.kakao.payment.dto.PaymentRequestDto;
import com.kakao.payment.dto.PaymentResponseDto;
import com.kakao.payment.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("card")
public class PaymentController {
	
	@Autowired
	PaymentService payService;

	@PostMapping("/payment")
	public PaymentResponseDto payment(@RequestBody @Valid PaymentRequestDto request) throws Exception {
		PaymentResponseDto response = new PaymentResponseDto();
		log.info("payment tran request >>>>> {}", request.toString());
		
		// 결제 API
		response = payService.cardPayProcess(request);
		
		return response;
	}
	
	@PostMapping("/cancel")
	public PaymentResponseDto cancel(@RequestBody @Valid PaymentCancelRequestDto request) throws Exception {
		PaymentResponseDto response = new PaymentResponseDto();
		log.info("payment cancel request >>>>> {}", request.toString());
		
		// 결제 데이터조회 API
		response = payService.payCancelProcess(request, PaymentConsts.CANCEL_ALL);
		
		return response;
	}
	
	@PostMapping("/cancelPart")
	public PaymentResponseDto cancelPart(@RequestBody @Valid PaymentCancelRequestDto request) throws Exception {
		PaymentResponseDto response = new PaymentResponseDto();
		log.info("payment cancel request >>>>> {}", request.toString());
		
		// 결제 데이터조회 API
		response = payService.payCancelProcess(request, PaymentConsts.CANCEL_PART);
		
		return response;
	}
	
	@GetMapping("/inquiry")
	public PaymentInquiryResponseDto inquiry(@RequestBody @Valid PayInquiryRequestDto request) throws Exception {
		PaymentInquiryResponseDto response = new PaymentInquiryResponseDto();
		PaymentCommonUtil commonUtil = new PaymentCommonUtil();
		log.info("payment inquiry request >>>>> {}", request.toString());
		
		// 결제 데이터조회 API
		response = payService.payInquiryProcess(request);
		
		// 카드번호 마스킹
		response.setCardNo(commonUtil.maskCardNo(response.getCardNo()));
		
		return response;
	}

}
