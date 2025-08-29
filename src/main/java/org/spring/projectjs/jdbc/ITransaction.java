package org.spring.projectjs.jdbc;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ITransaction {

    public int transaction_write(TransactionDTO transactionDTO);
    public int transaction_totalCount(ParameterDTO parameterDTO);
    public ArrayList<TransactionDTO> transaction_listPage(ParameterDTO parameterDTO);
    public TransactionDTO transaction_view(TransactionDTO transactionDTO);
    public int transaction_deleteFileOne(String transaction_idx, String imgCount);
    public int transaction_updateFile(TransactionDTO transactionDTO);
    public int updateTitleContentPrice(String transaction_price, String transaction_title,
                                       String transaction_content, String transaction_idx);
    public int transaction_DeleteFileAll(String idx);
    public int transaction_delete(String idx);
    public int transaction_purchase(String idx);



}
