import android.content.Context;
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context){
        super(context,"130149R",null,1);
    }
    public void onCreate(SQLiteDatabase db) {
        db.exec("CREATE TABLE account(accountNo VARCHAR(12) PRIMARY KEY,bankName VARCHAR(10),accountHolderName VARCHAR(25),balance DECIMAL(12,2))");
        db.exec("CREATE TABLE transactions(date DATE,accountNo VARCHAR(12),expenseType INT(1),amount DECIMAL(12,2),FOREIGN KEY(accountNo) REFERENCES account(accountNo))");
    }//expenseType=1 (Expense) ____ expenseType=0 (Income)
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS account");
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }
}
