package com.kakao.payment.common.consts;

public class PaymentConsts {
	// 공백
	public static final String EMPTY = "";

	//카드정보 구분자 (정규식 |)
	public static final String PIPE = "|";
	public static final String SPLIT_PIPE = "\\|";
	
	//마스킹문자
	public static final String MASK_CHAR = "*";
	
	/* 전문관련 */
	// 결제전문 기능구분값
	public static final String PAYMENT = "PAYMENT";	//결제
	public static final String CANCEL = "CANCEL";	//결제취소
	// Padding
	public static final String CHAR_PAD = " ";		// 문자열 Padding
	public static final String NUM_PAD = " ";		// 숫자   Padding
	public static final String NUM_ZERO_PAD = "0";	// 숫자(0)Padding
	
	/* 결제/취소구분 */
	public static final String STATUS_READY 	= "00";	// 전송신청
	public static final String STATUS_PAY 		= "10";	// 결제승인 
	public static final String STATUS_PAY_FAIL	= "19";	// 결제실패
	public static final String STATUS_CAN 		= "90";	// 전체취소
	public static final String STATUS_CAN_PART 	= "91";	// 부분취소
	public static final String STATUS_CAN_FAIL 	= "99";	// 취소실패
	
	/* 취소구분 */
	public static final String CANCEL_ALL 	= "90";	// 전체취소
	public static final String CANCEL_PART 	= "91";	// 부분취소
	
}
