/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class PersistentTransactionDAO implements TransactionDAO {
    private final DBHelper transactions;

    public PersistentTransactionDAO(Context context) {
        this.transactions = new DBHelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase db = transactions.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        contentValues.put("accountNo", accountNo);
        switch (expenseType) {
            case EXPENSE:
                contentValues.put("expenseType",1 );
                break;
            case INCOME:
                contentValues.put("expenseType",0 );
                break;
        }
        contentValues.put("amount", amount);
        db.insert("transactions", null, contentValues);
        return true;
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        ArrayList<Transaction> array_list = new ArrayList<Transaction>();
        SQLiteDatabase db = accounts.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from transactions", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            ExpenseType expense;
            switch (res.getInt(2)) {
                case 1:
                    expense= ExpenseType.EXPENSE;
                    break;
                case 0:
                    expense=ExpenseType.INCOME;
                    break;
            }
            Transaction _transaction = new Transaction (res.getString(0),res.getString(1),expense,res.getDouble(3));
            array_list.add(_transaction);
            res.moveToNext();
        }
        if(!res.isClosed()){
            res.close();
        }
        return array_list;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = transactions.getReadableDatabase();
        int size = (int) DatabaseUtils.queryNumEntries(db, "transactions");
        if (size <= limit) {
            return getAllTransactionLogs();
        }
        // return the last <code>limit</code> number of transaction logs


        ArrayList<Transaction> array_list = new ArrayList<Transaction>();
        array_list=getAllTransactionLogs();

        return array_list.subList(size - limit, size);
    }

}
