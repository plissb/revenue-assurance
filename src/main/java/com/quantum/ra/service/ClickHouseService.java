package com.quantum.ra.service;

import com.quantum.ra.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickHouseService {

    @Qualifier("clickHouseDataSource")
    private final DataSource clickHouseDataSource;

    /**
     * Сохраняет транзакции в ClickHouse через JDBC
     *
     * @param transactions список транзакций для сохранения
     * @param fileUploadId идентификатор загружаемого файла
     * @return количество сохраненных записей
     */
    public int saveTransactions(List<Transaction> transactions, UUID fileUploadId) {
        if (transactions.isEmpty()) {
            return 0;
        }

        String sql = buildBatchInsertQuery();
        
        try (Connection connection = clickHouseDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (Transaction transaction : transactions) {
                addTransactionParams(statement, transaction, fileUploadId);
                statement.addBatch();
            }
            
            int[] results = statement.executeBatch();
            int totalRows = 0;
            for (int result : results) {
                if (result > 0) {
                    totalRows += result;
                }
            }
            
            log.info("Successfully loaded {} rows into ClickHouse", totalRows);
            return totalRows;
            
        } catch (SQLException e) {
            log.error("Error saving data to ClickHouse: ", e);
            throw new RuntimeException("Error saving data to ClickHouse: " + e.getMessage(), e);
        }
    }
    
    /**
     * Строит SQL-запрос на массовую вставку данных
     */
    private String buildBatchInsertQuery() {
        return "INSERT INTO ra_analytics.transactions (" +
                "id, sender_msisdn, receiver_msisdn, receiver_user_id, sender_user_id, " +
                "transaction_amount, commissions_paid, commissions_received, commissions_others, " +
                "service_charge_received, service_charge_paid, taxes, service_type, transfer_status, " +
                "sender_pre_bal, sender_post_bal, receiver_pre_bal, receiver_post_bal, " +
                "sender_acc_status, receiver_acc_status, error_code, error_desc, reference_number, " +
                "created_on, created_by, modified_on, modified_by, app_1_date, app_2_date, " +
                "transfer_id, transfer_datetime, sender_category_code, sender_domain_code, " +
                "sender_grade_name, sender_group_role, sender_designation, sender_state, " +
                "receiver_category_code, receiver_domain_code, receiver_grade_name, " +
                "receiver_group_role, receiver_designation, receiver_state, sender_city, " +
                "receiver_city, app_1_by, app_2_by, request_source, gateway_type, " +
                "transfer_subtype, payment_type, payment_number, payment_date, remarks, " +
                "action_type, transaction_tag, reconciliation_by, reconciliation_for, " +
                "ext_txn_number, original_ref_number, zebra_ambiguous, attempt_status, " +
                "other_msisdn, sender_wallet_number, receiver_wallet_number, sender_user_name, " +
                "receiver_user_name, tno_msisdn, tno_id, unreg_first_name, unreg_last_name, " +
                "unreg_dob, unreg_id_number, bulk_payout_batchid, is_financial, transfer_done, " +
                "initiator_msisdn, validator_msisdn, initiator_comments, validator_comments, " +
                "sender_wallet_name, reciever_wallet_name, sender_user_type, receiver_user_type, " +
                "txnmode, file_upload_id, load_timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    /**
     * Добавляет параметры транзакции в SQL-запрос
     */
    private void addTransactionParams(PreparedStatement statement, Transaction transaction, UUID fileUploadId) 
            throws SQLException {
        int index = 1;
        
        // UUID -> String в ClickHouse
        statement.setString(index++, transaction.getId().toString());
        statement.setString(index++, transaction.getSenderMsisdn());
        statement.setString(index++, transaction.getReceiverMsisdn());
        statement.setString(index++, transaction.getReceiverUserId());
        statement.setString(index++, transaction.getSenderUserId());
        setDecimal(statement, index++, transaction.getTransactionAmount());
        setDecimal(statement, index++, transaction.getCommissionsPaid());
        setDecimal(statement, index++, transaction.getCommissionsReceived());
        setDecimal(statement, index++, transaction.getCommissionsOthers());
        setDecimal(statement, index++, transaction.getServiceChargeReceived());
        setDecimal(statement, index++, transaction.getServiceChargePaid());
        setDecimal(statement, index++, transaction.getTaxes());
        statement.setString(index++, transaction.getServiceType());
        statement.setString(index++, transaction.getTransferStatus());
        setDecimal(statement, index++, transaction.getSenderPreBal());
        setDecimal(statement, index++, transaction.getSenderPostBal());
        setDecimal(statement, index++, transaction.getReceiverPreBal());
        setDecimal(statement, index++, transaction.getReceiverPostBal());
        statement.setString(index++, transaction.getSenderAccStatus());
        statement.setString(index++, transaction.getReceiverAccStatus());
        statement.setString(index++, transaction.getErrorCode());
        statement.setString(index++, transaction.getErrorDesc());
        statement.setString(index++, transaction.getReferenceNumber());
        setDateTime(statement, index++, transaction.getCreatedOn());
        statement.setString(index++, transaction.getCreatedBy());
        setDateTime(statement, index++, transaction.getModifiedOn());
        statement.setString(index++, transaction.getModifiedBy());
        setDateTime(statement, index++, transaction.getApp1Date());
        setDateTime(statement, index++, transaction.getApp2Date());
        statement.setString(index++, transaction.getTransferId());
        setDateTime(statement, index++, transaction.getTransferDatetime());
        statement.setString(index++, transaction.getSenderCategoryCode());
        statement.setString(index++, transaction.getSenderDomainCode());
        statement.setString(index++, transaction.getSenderGradeName());
        statement.setString(index++, transaction.getSenderGroupRole());
        statement.setString(index++, transaction.getSenderDesignation());
        statement.setString(index++, transaction.getSenderState());
        statement.setString(index++, transaction.getReceiverCategoryCode());
        statement.setString(index++, transaction.getReceiverDomainCode());
        statement.setString(index++, transaction.getReceiverGradeName());
        statement.setString(index++, transaction.getReceiverGroupRole());
        statement.setString(index++, transaction.getReceiverDesignation());
        statement.setString(index++, transaction.getReceiverState());
        statement.setString(index++, transaction.getSenderCity());
        statement.setString(index++, transaction.getReceiverCity());
        statement.setString(index++, transaction.getApp1By());
        statement.setString(index++, transaction.getApp2By());
        statement.setString(index++, transaction.getRequestSource());
        statement.setString(index++, transaction.getGatewayType());
        statement.setString(index++, transaction.getTransferSubtype());
        statement.setString(index++, transaction.getPaymentType());
        statement.setString(index++, transaction.getPaymentNumber());
        setDateTime(statement, index++, transaction.getPaymentDate());
        statement.setString(index++, transaction.getRemarks());
        statement.setString(index++, transaction.getActionType());
        statement.setString(index++, transaction.getTransactionTag());
        statement.setString(index++, transaction.getReconciliationBy());
        statement.setString(index++, transaction.getReconciliationFor());
        statement.setString(index++, transaction.getExtTxnNumber());
        statement.setString(index++, transaction.getOriginalRefNumber());
        statement.setString(index++, transaction.getZebraAmbiguous());
        statement.setString(index++, transaction.getAttemptStatus());
        statement.setString(index++, transaction.getOtherMsisdn());
        statement.setString(index++, transaction.getSenderWalletNumber());
        statement.setString(index++, transaction.getReceiverWalletNumber());
        statement.setString(index++, transaction.getSenderUserName());
        statement.setString(index++, transaction.getReceiverUserName());
        statement.setString(index++, transaction.getTnoMsisdn());
        statement.setString(index++, transaction.getTnoId());
        statement.setString(index++, transaction.getUnregFirstName());
        statement.setString(index++, transaction.getUnregLastName());
        setDate(statement, index++, transaction.getUnregDob());
        statement.setString(index++, transaction.getUnregIdNumber());
        statement.setString(index++, transaction.getBulkPayoutBatchid());
        statement.setString(index++, transaction.getIsFinancial());
        statement.setString(index++, transaction.getTransferDone());
        statement.setString(index++, transaction.getInitiatorMsisdn());
        statement.setString(index++, transaction.getValidatorMsisdn());
        statement.setString(index++, transaction.getInitiatorComments());
        statement.setString(index++, transaction.getValidatorComments());
        statement.setString(index++, transaction.getSenderWalletName());
        statement.setString(index++, transaction.getRecieverWalletName());
        statement.setString(index++, transaction.getSenderUserType());
        statement.setString(index++, transaction.getReceiverUserType());
        statement.setString(index++, transaction.getTxnmode());
        statement.setString(index++, fileUploadId.toString());
        setDateTime(statement, index, transaction.getLoadTimestamp());
    }
    
    /**
     * Устанавливает BigDecimal в SQL-запрос
     */
    private void setDecimal(PreparedStatement statement, int index, BigDecimal value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.DECIMAL);
        } else {
            statement.setBigDecimal(index, value);
        }
    }
    
    /**
     * Устанавливает LocalDateTime в SQL-запрос
     */
    private void setDateTime(PreparedStatement statement, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(index, Timestamp.valueOf(value));
        }
    }
    
    /**
     * Устанавливает LocalDate в SQL-запрос
     */
    private void setDate(PreparedStatement statement, int index, LocalDate value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.DATE);
        } else {
            statement.setDate(index, java.sql.Date.valueOf(value));
        }
    }
} 