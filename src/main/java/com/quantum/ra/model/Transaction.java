package com.quantum.ra.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Класс, представляющий данные транзакции из CSV файла
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private UUID id;
    private String senderMsisdn;
    private String receiverMsisdn;
    private String receiverUserId;
    private String senderUserId;
    private BigDecimal transactionAmount;
    private BigDecimal commissionsPaid;
    private BigDecimal commissionsReceived;
    private BigDecimal commissionsOthers;
    private BigDecimal serviceChargeReceived;
    private BigDecimal serviceChargePaid;
    private BigDecimal taxes;
    private String serviceType;
    private String transferStatus;
    private BigDecimal senderPreBal;
    private BigDecimal senderPostBal;
    private BigDecimal receiverPreBal;
    private BigDecimal receiverPostBal;
    private String senderAccStatus;
    private String receiverAccStatus;
    private String errorCode;
    private String errorDesc;
    private String referenceNumber;
    private LocalDateTime createdOn;
    private String createdBy;
    private LocalDateTime modifiedOn;
    private String modifiedBy;
    private LocalDateTime app1Date;
    private LocalDateTime app2Date;
    private String transferId;
    private LocalDateTime transferDatetime;
    private String senderCategoryCode;
    private String senderDomainCode;
    private String senderGradeName;
    private String senderGroupRole;
    private String senderDesignation;
    private String senderState;
    private String receiverCategoryCode;
    private String receiverDomainCode;
    private String receiverGradeName;
    private String receiverGroupRole;
    private String receiverDesignation;
    private String receiverState;
    private String senderCity;
    private String receiverCity;
    private String app1By;
    private String app2By;
    private String requestSource;
    private String gatewayType;
    private String transferSubtype;
    private String paymentType;
    private String paymentNumber;
    private LocalDateTime paymentDate;
    private String remarks;
    private String actionType;
    private String transactionTag;
    private String reconciliationBy;
    private String reconciliationFor;
    private String extTxnNumber;
    private String originalRefNumber;
    private String zebraAmbiguous;
    private String attemptStatus;
    private String otherMsisdn;
    private String senderWalletNumber;
    private String receiverWalletNumber;
    private String senderUserName;
    private String receiverUserName;
    private String tnoMsisdn;
    private String tnoId;
    private String unregFirstName;
    private String unregLastName;
    private LocalDate unregDob;
    private String unregIdNumber;
    private String bulkPayoutBatchid;
    private String isFinancial;
    private String transferDone;
    private String initiatorMsisdn;
    private String validatorMsisdn;
    private String initiatorComments;
    private String validatorComments;
    private String senderWalletName;
    private String recieverWalletName;
    private String senderUserType;
    private String receiverUserType;
    private String txnmode;
    
    // Метаданные загрузки
    private UUID fileUploadId;
    private LocalDateTime loadTimestamp;
    
    /**
     * Фабричный метод для создания объекта Transaction из строки CSV
     * 
     * @param csvLine строка CSV с данными
     * @param fileUploadId идентификатор загружаемого файла
     * @return объект Transaction
     */
    public static Transaction fromCsvLine(String csvLine, UUID fileUploadId) {
        String[] values = csvLine.split("\\|", -1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        
        // Заполняем поля из CSV
        if (values.length >= 84) {
            transaction.setSenderMsisdn(values[0]);
            transaction.setReceiverMsisdn(values[1]);
            transaction.setReceiverUserId(values[2]);
            transaction.setSenderUserId(values[3]);
            transaction.setTransactionAmount(parseDecimal(values[4]));
            transaction.setCommissionsPaid(parseDecimal(values[5]));
            transaction.setCommissionsReceived(parseDecimal(values[6]));
            transaction.setCommissionsOthers(parseDecimal(values[7]));
            transaction.setServiceChargeReceived(parseDecimal(values[8]));
            transaction.setServiceChargePaid(parseDecimal(values[9]));
            transaction.setTaxes(parseDecimal(values[10]));
            transaction.setServiceType(values[11]);
            transaction.setTransferStatus(values[12]);
            transaction.setSenderPreBal(parseDecimal(values[13]));
            transaction.setSenderPostBal(parseDecimal(values[14]));
            transaction.setReceiverPreBal(parseDecimal(values[15]));
            transaction.setReceiverPostBal(parseDecimal(values[16]));
            transaction.setSenderAccStatus(values[17]);
            transaction.setReceiverAccStatus(values[18]);
            transaction.setErrorCode(values[19]);
            transaction.setErrorDesc(values[20]);
            transaction.setReferenceNumber(values[21]);
            transaction.setCreatedOn(parseDateTime(values[22], dateTimeFormatter));
            transaction.setCreatedBy(values[23]);
            transaction.setModifiedOn(parseDateTime(values[24], dateTimeFormatter));
            transaction.setModifiedBy(values[25]);
            transaction.setApp1Date(parseDateTime(values[26], dateTimeFormatter));
            transaction.setApp2Date(parseDateTime(values[27], dateTimeFormatter));
            transaction.setTransferId(values[28]);
            transaction.setTransferDatetime(parseDateTime(values[29], dateTimeFormatter));
            transaction.setSenderCategoryCode(values[30]);
            transaction.setSenderDomainCode(values[31]);
            transaction.setSenderGradeName(values[32]);
            transaction.setSenderGroupRole(values[33]);
            transaction.setSenderDesignation(values[34]);
            transaction.setSenderState(values[35]);
            transaction.setReceiverCategoryCode(values[36]);
            transaction.setReceiverDomainCode(values[37]);
            transaction.setReceiverGradeName(values[38]);
            transaction.setReceiverGroupRole(values[39]);
            transaction.setReceiverDesignation(values[40]);
            transaction.setReceiverState(values[41]);
            transaction.setSenderCity(values[42]);
            transaction.setReceiverCity(values[43]);
            transaction.setApp1By(values[44]);
            transaction.setApp2By(values[45]);
            transaction.setRequestSource(values[46]);
            transaction.setGatewayType(values[47]);
            transaction.setTransferSubtype(values[48]);
            transaction.setPaymentType(values[49]);
            transaction.setPaymentNumber(values[50]);
            transaction.setPaymentDate(parseDateTime(values[51], dateTimeFormatter));
            transaction.setRemarks(values[52]);
            transaction.setActionType(values[53]);
            transaction.setTransactionTag(values[54]);
            transaction.setReconciliationBy(values[55]);
            transaction.setReconciliationFor(values[56]);
            transaction.setExtTxnNumber(values[57]);
            transaction.setOriginalRefNumber(values[58]);
            transaction.setZebraAmbiguous(values[59]);
            transaction.setAttemptStatus(values[60]);
            transaction.setOtherMsisdn(values[61]);
            transaction.setSenderWalletNumber(values[62]);
            transaction.setReceiverWalletNumber(values[63]);
            transaction.setSenderUserName(values[64]);
            transaction.setReceiverUserName(values[65]);
            transaction.setTnoMsisdn(values[66]);
            transaction.setTnoId(values[67]);
            transaction.setUnregFirstName(values[68]);
            transaction.setUnregLastName(values[69]);
            transaction.setUnregDob(parseDate(values[70], dateFormatter));
            transaction.setUnregIdNumber(values[71]);
            transaction.setBulkPayoutBatchid(values[72]);
            transaction.setIsFinancial(values[73]);
            transaction.setTransferDone(values[74]);
            transaction.setInitiatorMsisdn(values[75]);
            transaction.setValidatorMsisdn(values[76]);
            transaction.setInitiatorComments(values[77]);
            transaction.setValidatorComments(values[78]);
            transaction.setSenderWalletName(values[79]);
            transaction.setRecieverWalletName(values[80]);
            transaction.setSenderUserType(values[81]);
            transaction.setReceiverUserType(values[82]);
            transaction.setTxnmode(values[83]);
        }
        
        // Устанавливаем метаданные загрузки
        transaction.setFileUploadId(fileUploadId);
        transaction.setLoadTimestamp(LocalDateTime.now());
        
        return transaction;
    }
    
    private static BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    private static LocalDateTime parseDateTime(String value, DateTimeFormatter formatter) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim(), formatter);
        } catch (Exception e) {
            return null;
        }
    }
    
    private static LocalDate parseDate(String value, DateTimeFormatter formatter) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), formatter);
        } catch (Exception e) {
            return null;
        }
    }
} 