package com.quantum.ra.service;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseFormat;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseRequest;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.data.ClickHouseDataStreamFactory;
import com.clickhouse.data.ClickHouseOutputStream;
import com.clickhouse.data.ClickHouseWriter;
import com.quantum.ra.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickHouseService {

    private final ClickHouseClient clickHouseClient;
    private final ClickHouseNode clickHouseNode;

    /**
     * Сохраняет транзакции в ClickHouse используя бинарный формат
     *
     * @param transactions список транзакций для сохранения
     * @param fileName имя исходного файла
     * @return количество сохраненных записей
     */
    public int saveTransactions(List<Transaction> transactions, String fileName) {
        String sql = "INSERT INTO ra_analytics.transactions FORMAT Binary";

        try (ClickHouseClient client = clickHouseClient) {
            int[] count = {0};  // Используем массив, чтобы изменять в лямбде
            
            ClickHouseResponse response = client.connect(clickHouseNode)
                    .format(ClickHouseFormat.Binary)
                    .query(sql)
                    .execute()
                    .thenApply(resp -> {
                        try (ClickHouseWriter writer = resp.getWriter()) {
                            for (Transaction transaction : transactions) {
                                // Заполняем бинарные данные для вставки в ClickHouse
                                writeTransaction(writer, transaction);
                                count[0]++;
                            }
                            return resp;
                        } catch (IOException e) {
                            log.error("Error writing to ClickHouse: ", e);
                            throw new RuntimeException("Error writing to ClickHouse", e);
                        }
                    }).get();
            
            long rowsWritten = response.getSummary().getWrittenRows();
            log.info("Successfully loaded {} rows into ClickHouse", rowsWritten);
            return count[0];

        } catch (ExecutionException | InterruptedException e) {
            log.error("Error saving data to ClickHouse: ", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error saving data to ClickHouse", e);
        }
    }
    
    /**
     * Записывает данные транзакции в ClickHouse в бинарном формате
     * 
     * @param writer ClickHouse writer
     * @param transaction транзакция для записи
     * @throws IOException в случае ошибки записи
     */
    private void writeTransaction(ClickHouseWriter writer, Transaction transaction) throws IOException {
        // Запись всех полей транзакции в бинарном формате
        writer.writeUuid(transaction.getId());
        writer.writeString(transaction.getSenderMsisdn());
        writer.writeString(transaction.getReceiverMsisdn());
        writer.writeString(transaction.getReceiverUserId());
        writer.writeString(transaction.getSenderUserId());
        writeDecimal(writer, transaction.getTransactionAmount());
        writeDecimal(writer, transaction.getCommissionsPaid());
        writeDecimal(writer, transaction.getCommissionsReceived());
        writeDecimal(writer, transaction.getCommissionsOthers());
        writeDecimal(writer, transaction.getServiceChargeReceived());
        writeDecimal(writer, transaction.getServiceChargePaid());
        writeDecimal(writer, transaction.getTaxes());
        writer.writeString(transaction.getServiceType());
        writer.writeString(transaction.getTransferStatus());
        writeDecimal(writer, transaction.getSenderPreBal());
        writeDecimal(writer, transaction.getSenderPostBal());
        writeDecimal(writer, transaction.getReceiverPreBal());
        writeDecimal(writer, transaction.getReceiverPostBal());
        writer.writeString(transaction.getSenderAccStatus());
        writer.writeString(transaction.getReceiverAccStatus());
        writer.writeString(transaction.getErrorCode());
        writer.writeString(transaction.getErrorDesc());
        writer.writeString(transaction.getReferenceNumber());
        writeDateTime(writer, transaction.getCreatedOn());
        writer.writeString(transaction.getCreatedBy());
        writeDateTime(writer, transaction.getModifiedOn());
        writer.writeString(transaction.getModifiedBy());
        writeDateTime(writer, transaction.getApp1Date());
        writeDateTime(writer, transaction.getApp2Date());
        writer.writeString(transaction.getTransferId());
        writeDateTime(writer, transaction.getTransferDatetime());
        writer.writeString(transaction.getSenderCategoryCode());
        writer.writeString(transaction.getSenderDomainCode());
        writer.writeString(transaction.getSenderGradeName());
        writer.writeString(transaction.getSenderGroupRole());
        writer.writeString(transaction.getSenderDesignation());
        writer.writeString(transaction.getSenderState());
        writer.writeString(transaction.getReceiverCategoryCode());
        writer.writeString(transaction.getReceiverDomainCode());
        writer.writeString(transaction.getReceiverGradeName());
        writer.writeString(transaction.getReceiverGroupRole());
        writer.writeString(transaction.getReceiverDesignation());
        writer.writeString(transaction.getReceiverState());
        writer.writeString(transaction.getSenderCity());
        writer.writeString(transaction.getReceiverCity());
        writer.writeString(transaction.getApp1By());
        writer.writeString(transaction.getApp2By());
        writer.writeString(transaction.getRequestSource());
        writer.writeString(transaction.getGatewayType());
        writer.writeString(transaction.getTransferSubtype());
        writer.writeString(transaction.getPaymentType());
        writer.writeString(transaction.getPaymentNumber());
        writeDateTime(writer, transaction.getPaymentDate());
        writer.writeString(transaction.getRemarks());
        writer.writeString(transaction.getActionType());
        writer.writeString(transaction.getTransactionTag());
        writer.writeString(transaction.getReconciliationBy());
        writer.writeString(transaction.getReconciliationFor());
        writer.writeString(transaction.getExtTxnNumber());
        writer.writeString(transaction.getOriginalRefNumber());
        writer.writeString(transaction.getZebraAmbiguous());
        writer.writeString(transaction.getAttemptStatus());
        writer.writeString(transaction.getOtherMsisdn());
        writer.writeString(transaction.getSenderWalletNumber());
        writer.writeString(transaction.getReceiverWalletNumber());
        writer.writeString(transaction.getSenderUserName());
        writer.writeString(transaction.getReceiverUserName());
        writer.writeString(transaction.getTnoMsisdn());
        writer.writeString(transaction.getTnoId());
        writer.writeString(transaction.getUnregFirstName());
        writer.writeString(transaction.getUnregLastName());
        writeDate(writer, transaction.getUnregDob());
        writer.writeString(transaction.getUnregIdNumber());
        writer.writeString(transaction.getBulkPayoutBatchid());
        writer.writeString(transaction.getIsFinancial());
        writer.writeString(transaction.getTransferDone());
        writer.writeString(transaction.getInitiatorMsisdn());
        writer.writeString(transaction.getValidatorMsisdn());
        writer.writeString(transaction.getInitiatorComments());
        writer.writeString(transaction.getValidatorComments());
        writer.writeString(transaction.getSenderWalletName());
        writer.writeString(transaction.getRecieverWalletName());
        writer.writeString(transaction.getSenderUserType());
        writer.writeString(transaction.getReceiverUserType());
        writer.writeString(transaction.getTxnmode());
        writer.writeString(transaction.getFileName());
        writeDateTime(writer, transaction.getLoadTimestamp());
    }
    
    /**
     * Записывает BigDecimal в бинарный формат ClickHouse
     */
    private void writeDecimal(ClickHouseWriter writer, BigDecimal value) throws IOException {
        if (value == null) {
            writer.writeNull();
        } else {
            writer.writeDecimal(value, value.scale());
        }
    }
    
    /**
     * Записывает LocalDateTime в бинарный формат ClickHouse
     */
    private void writeDateTime(ClickHouseWriter writer, LocalDateTime value) throws IOException {
        if (value == null) {
            writer.writeNull();
        } else {
            writer.writeDateTime64(value, 3); // С микросекундами
        }
    }
    
    /**
     * Записывает LocalDate в бинарный формат ClickHouse
     */
    private void writeDate(ClickHouseWriter writer, LocalDate value) throws IOException {
        if (value == null) {
            writer.writeNull();
        } else {
            writer.writeDate(value);
        }
    }
} 