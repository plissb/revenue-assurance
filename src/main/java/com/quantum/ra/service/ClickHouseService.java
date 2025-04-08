package com.quantum.ra.service;

import com.clickhouse.jdbc.ClickHouseConnection;
import com.clickhouse.jdbc.ClickHouseDataSource;
import com.quantum.ra.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с ClickHouse
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "clickhouse.enabled", havingValue = "true", matchIfMissing = true)
public class ClickHouseService {

    private final ClickHouseDataSource dataSource;

    @Autowired
    public ClickHouseService(ClickHouseDataSource dataSource) {
        this.dataSource = dataSource;
        log.info("ClickHouseService initialized with JDBC driver");
    }

    /**
     * Сохраняет транзакции в ClickHouse используя JDBC
     *
     * @param transactions список транзакций для сохранения
     * @param fileUploadId идентификатор загружаемого файла
     * @return количество сохраненных записей
     */
    public int saveTransactions(List<Transaction> transactions, UUID fileUploadId) {
        if (transactions.isEmpty()) {
            return 0;
        }

        String sql = "INSERT INTO ra_analytics.transactions (" +
                "id, sender_msisdn, receiver_msisdn, receiver_user_id, sender_user_id, " +
                "transaction_amount, commissions_paid, commissions_received, commissions_others, " +
                "service_charge_received, service_charge_paid, taxes, service_type, transfer_status, " +
                "sender_pre_bal, sender_post_bal, receiver_pre_bal, receiver_post_bal, " +
                "sender_acc_status, receiver_acc_status, error_code, error_desc, reference_number, " +
                "created_on, created_by, modified_on, modified_by, app1_date, app2_date, transfer_id, " +
                "transfer_datetime, sender_category_code, sender_domain_code, sender_grade_name, " +
                "sender_group_role, sender_designation, sender_state, receiver_category_code, " +
                "receiver_domain_code, receiver_grade_name, receiver_group_role, receiver_designation, " +
                "receiver_state, sender_city, receiver_city, app1_by, app2_by, request_source, " +
                "gateway_type, transfer_subtype, payment_type, payment_number, payment_date, " +
                "remarks, action_type, transaction_tag, reconciliation_by, reconciliation_for, " +
                "ext_txn_number, original_ref_number, zebra_ambiguous, attempt_status, other_msisdn, " +
                "sender_wallet_number, receiver_wallet_number, sender_user_name, receiver_user_name, " +
                "tno_msisdn, tno_id, unreg_first_name, unreg_last_name, unreg_dob, unreg_id_number, " +
                "bulk_payout_batchid, is_financial, transfer_done, initiator_msisdn, validator_msisdn, " +
                "initiator_comments, validator_comments, sender_wallet_name, receiver_wallet_name, " +
                "sender_user_type, receiver_user_type, txnmode, file_upload_id, load_timestamp) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (ClickHouseConnection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int batchSize = 0;
            int totalInserted = 0;

            for (Transaction transaction : transactions) {
                int paramIndex = 1;
                
                setParam(ps, paramIndex++, transaction.getId());
                setParam(ps, paramIndex++, transaction.getSenderMsisdn());
                setParam(ps, paramIndex++, transaction.getReceiverMsisdn());
                setParam(ps, paramIndex++, transaction.getReceiverUserId());
                setParam(ps, paramIndex++, transaction.getSenderUserId());
                setParam(ps, paramIndex++, transaction.getTransactionAmount());
                setParam(ps, paramIndex++, transaction.getCommissionsPaid());
                setParam(ps, paramIndex++, transaction.getCommissionsReceived());
                setParam(ps, paramIndex++, transaction.getCommissionsOthers());
                setParam(ps, paramIndex++, transaction.getServiceChargeReceived());
                setParam(ps, paramIndex++, transaction.getServiceChargePaid());
                setParam(ps, paramIndex++, transaction.getTaxes());
                setParam(ps, paramIndex++, transaction.getServiceType());
                setParam(ps, paramIndex++, transaction.getTransferStatus());
                setParam(ps, paramIndex++, transaction.getSenderPreBal());
                setParam(ps, paramIndex++, transaction.getSenderPostBal());
                setParam(ps, paramIndex++, transaction.getReceiverPreBal());
                setParam(ps, paramIndex++, transaction.getReceiverPostBal());
                setParam(ps, paramIndex++, transaction.getSenderAccStatus());
                setParam(ps, paramIndex++, transaction.getReceiverAccStatus());
                setParam(ps, paramIndex++, transaction.getErrorCode());
                setParam(ps, paramIndex++, transaction.getErrorDesc());
                setParam(ps, paramIndex++, transaction.getReferenceNumber());
                setParam(ps, paramIndex++, transaction.getCreatedOn());
                setParam(ps, paramIndex++, transaction.getCreatedBy());
                setParam(ps, paramIndex++, transaction.getModifiedOn());
                setParam(ps, paramIndex++, transaction.getModifiedBy());
                setParam(ps, paramIndex++, transaction.getApp1Date());
                setParam(ps, paramIndex++, transaction.getApp2Date());
                setParam(ps, paramIndex++, transaction.getTransferId());
                setParam(ps, paramIndex++, transaction.getTransferDatetime());
                setParam(ps, paramIndex++, transaction.getSenderCategoryCode());
                setParam(ps, paramIndex++, transaction.getSenderDomainCode());
                setParam(ps, paramIndex++, transaction.getSenderGradeName());
                setParam(ps, paramIndex++, transaction.getSenderGroupRole());
                setParam(ps, paramIndex++, transaction.getSenderDesignation());
                setParam(ps, paramIndex++, transaction.getSenderState());
                setParam(ps, paramIndex++, transaction.getReceiverCategoryCode());
                setParam(ps, paramIndex++, transaction.getReceiverDomainCode());
                setParam(ps, paramIndex++, transaction.getReceiverGradeName());
                setParam(ps, paramIndex++, transaction.getReceiverGroupRole());
                setParam(ps, paramIndex++, transaction.getReceiverDesignation());
                setParam(ps, paramIndex++, transaction.getReceiverState());
                setParam(ps, paramIndex++, transaction.getSenderCity());
                setParam(ps, paramIndex++, transaction.getReceiverCity());
                setParam(ps, paramIndex++, transaction.getApp1By());
                setParam(ps, paramIndex++, transaction.getApp2By());
                setParam(ps, paramIndex++, transaction.getRequestSource());
                setParam(ps, paramIndex++, transaction.getGatewayType());
                setParam(ps, paramIndex++, transaction.getTransferSubtype());
                setParam(ps, paramIndex++, transaction.getPaymentType());
                setParam(ps, paramIndex++, transaction.getPaymentNumber());
                setParam(ps, paramIndex++, transaction.getPaymentDate());
                setParam(ps, paramIndex++, transaction.getRemarks());
                setParam(ps, paramIndex++, transaction.getActionType());
                setParam(ps, paramIndex++, transaction.getTransactionTag());
                setParam(ps, paramIndex++, transaction.getReconciliationBy());
                setParam(ps, paramIndex++, transaction.getReconciliationFor());
                setParam(ps, paramIndex++, transaction.getExtTxnNumber());
                setParam(ps, paramIndex++, transaction.getOriginalRefNumber());
                setParam(ps, paramIndex++, transaction.getZebraAmbiguous());
                setParam(ps, paramIndex++, transaction.getAttemptStatus());
                setParam(ps, paramIndex++, transaction.getOtherMsisdn());
                setParam(ps, paramIndex++, transaction.getSenderWalletNumber());
                setParam(ps, paramIndex++, transaction.getReceiverWalletNumber());
                setParam(ps, paramIndex++, transaction.getSenderUserName());
                setParam(ps, paramIndex++, transaction.getReceiverUserName());
                setParam(ps, paramIndex++, transaction.getTnoMsisdn());
                setParam(ps, paramIndex++, transaction.getTnoId());
                setParam(ps, paramIndex++, transaction.getUnregFirstName());
                setParam(ps, paramIndex++, transaction.getUnregLastName());
                setParam(ps, paramIndex++, transaction.getUnregDob());
                setParam(ps, paramIndex++, transaction.getUnregIdNumber());
                setParam(ps, paramIndex++, transaction.getBulkPayoutBatchid());
                setParam(ps, paramIndex++, transaction.getIsFinancial());
                setParam(ps, paramIndex++, transaction.getTransferDone());
                setParam(ps, paramIndex++, transaction.getInitiatorMsisdn());
                setParam(ps, paramIndex++, transaction.getValidatorMsisdn());
                setParam(ps, paramIndex++, transaction.getInitiatorComments());
                setParam(ps, paramIndex++, transaction.getValidatorComments());
                setParam(ps, paramIndex++, transaction.getSenderWalletName());
                setParam(ps, paramIndex++, transaction.getRecieverWalletName());
                setParam(ps, paramIndex++, transaction.getSenderUserType());
                setParam(ps, paramIndex++, transaction.getReceiverUserType());
                setParam(ps, paramIndex++, transaction.getTxnmode());
                setParam(ps, paramIndex++, fileUploadId);
                setParam(ps, paramIndex++, transaction.getLoadTimestamp());
                
                ps.addBatch();
                batchSize++;
                
                // Выполняем батч каждые 10000 транзакций для оптимизации памяти
                if (batchSize >= 10000) {
                    int[] counts = ps.executeBatch();
                    for (int count : counts) {
                        totalInserted += count;
                    }
                    batchSize = 0;
                }
            }
            
            // Выполняем оставшийся батч
            if (batchSize > 0) {
                int[] counts = ps.executeBatch();
                for (int count : counts) {
                    totalInserted += count;
                }
            }
            
            log.info("Successfully inserted {} rows into ClickHouse", totalInserted);
            return totalInserted;
            
        } catch (SQLException e) {
            log.error("Error executing ClickHouse save operation: ", e);
            throw new RuntimeException("Error saving data to ClickHouse", e);
        }
    }
    
    /**
     * Вспомогательный метод для установки параметров в PreparedStatement с учетом null-значений
     */
    private void setParam(PreparedStatement ps, int index, Object value) throws SQLException {
        switch (value) {
            case null -> ps.setNull(index, java.sql.Types.NULL);
            case UUID uuid -> ps.setString(index, value.toString());
            case LocalDateTime localDateTime -> ps.setTimestamp(index, Timestamp.valueOf(localDateTime));
            case LocalDate localDate -> ps.setDate(index, java.sql.Date.valueOf(localDate));
            default -> ps.setObject(index, value);
        }
    }
}
