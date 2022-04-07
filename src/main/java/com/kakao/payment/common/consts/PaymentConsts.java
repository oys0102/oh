package com.kakao.payment.common.consts;

public class PaymentConsts {
	// ����
	public static final String EMPTY = "";

	//ī������ ������ (���Խ� |)
	public static final String PIPE = "|";
	public static final String SPLIT_PIPE = "\\|";
	
	//����ŷ����
	public static final String MASK_CHAR = "*";
	
	/* �������� */
	// �������� ��ɱ��а�
	public static final String PAYMENT = "PAYMENT";	//����
	public static final String CANCEL = "CANCEL";	//�������
	// Padding
	public static final String CHAR_PAD = " ";		// ���ڿ� Padding
	public static final String NUM_PAD = " ";		// ����   Padding
	public static final String NUM_ZERO_PAD = "0";	// ����(0)Padding
	
	/* ����/��ұ��� */
	public static final String STATUS_READY 	= "00";	// ���۽�û
	public static final String STATUS_PAY 		= "10";	// �������� 
	public static final String STATUS_PAY_FAIL	= "19";	// ��������
	public static final String STATUS_CAN 		= "90";	// ��ü���
	public static final String STATUS_CAN_PART 	= "91";	// �κ����
	public static final String STATUS_CAN_FAIL 	= "99";	// ��ҽ���
	
	/* ��ұ��� */
	public static final String CANCEL_ALL 	= "90";	// ��ü���
	public static final String CANCEL_PART 	= "91";	// �κ����
	
}
