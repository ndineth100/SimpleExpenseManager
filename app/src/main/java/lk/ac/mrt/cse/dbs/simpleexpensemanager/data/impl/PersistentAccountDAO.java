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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class PersistentAccountDAO implements AccountDAO {
    private final DBHelper accounts;

    public PersistentAccountDAO(Context context) {
        this.accounts = new DBHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = accounts.getReadableDatabase();
        Cursor res =  db.rawQuery( "select accountNo from account", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("accountNo")));
            res.moveToNext();
        }
        if(!res.isClosed()){
            res.close();
        }
        return array_list;
    }

    @Override
    public List<Account> getAccountsList() {
        ArrayList<Account> array_list = new ArrayList<Account>();
        SQLiteDatabase db = accounts.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Account account = new Account(res.getString(0),res.getString(1),res.getString(2)),res.getDouble(3));
            array_list.add(account);
            res.moveToNext();
        }
        if(!res.isClosed()){
            res.close();
        }
        return array_list;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = accounts.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from account WHERE accountNo='"+accountNo+"'", null );
        res.moveToFirst();

        if(res.isAfterLast() == false){
            Account account = new Account(res.getString(0),res.getString(1),res.getString(2)),res.getDouble(3));
            if(!res.isClosed()){
                res.close();
            }
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = accounts.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", account.getAccountNumber());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("bankHolderName", account.getBankHolderName());
        contentValues.put("balance", account.getBalance());
        db.insert("account", null, contentValues);
        return true;
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        getAccount(accountNo);
        return db.delete("account",
                "accountNo = '"+accountNo+"' ",
                new String[] { Integer.toString(id) });
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        SQLiteDatabase db = this.getWritableDatabase();
        Account account = getAccount(accountNo);
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", accountNo);
        contentValues.put("bankName", account.getBankName());
        switch (expenseType) {
            case EXPENSE:
                contentValues.put("balance", account.getBalance() - amount);
                break;
            case INCOME:
                contentValues.put("balance", account.getBalance() + amount);
                break;
        }
        db.update("account", contentValues, "accountNo = '"+accountNo+"' ", new String[] { Integer.toString(id) } );
        return true;
    }
}
