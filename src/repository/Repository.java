/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repository;

import gui_design_1.Account;
import gui_design_1.CreditAccount;
import gui_design_1.Customer;
import gui_design_1.SavingsAccount;
import gui_design_1.Transaktions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author SYSJM2 GRUPP 3
 */
public class Repository
{

    Connection connection;
    Statement statement;
    List<Customer> repositCustList;
    ArrayList<Account> getAccountArrayList;
    private ArrayList<Account> repositAccountList;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    Date now = new Date();
    public List<Transaktions> repositTransList;
    String date1 = dateFormat2.format(now);
    private final double WITHDRAWRATESAVINGSACCOUNT = 2;
    String inOut = "null";

    // URL 
    String url = "jdbc:mysql://127.0.0.1:3306/banksystem?user=root&password=root";

    public Repository()
    {

        try
        {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();

        } catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
            System.out.println("Fel i Connection till Databas");

        }
    }

    /**
     * Returns all allCustomersArrayList of the bank(Personal number and name)
     *
     * @return
     */
    public List<String> getCustomers()
    {
        List<String> stringListCustomer = new ArrayList<>();
        try
        {

            ResultSet result = statement.executeQuery("SELECT * FROM customers");
            while (result.next())
            {
                String s1 = result.getString("customerName");
                String s2 = result.getString("personalNumber");
                Customer c = new Customer(s1, Long.parseLong(s2));

                stringListCustomer.add(c.toString2());

            }

        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return stringListCustomer;
    }

    //returns list of customer, ersätter allcustomerarraylist
    public List<Customer> getAllCustomers()
    {
        repositCustList = new ArrayList<>();
        try
        {
            //step 3. execute sql query

            ResultSet result = statement.executeQuery("SELECT * FROM customers");
            while (result.next())
            {
                String s1 = result.getString("customerName");
                String s2 = result.getString("personalNumber");
                Customer c = new Customer(s1, Long.parseLong(s2));

                repositCustList.add(c);

            }
        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return repositCustList;

    }

    public List<Transaktions> getAllTransactions(int accountID)
    {
        repositTransList = new ArrayList<>();
        try
        {
            //step 3. execute sql query

            ResultSet result = statement.executeQuery("SELECT * FROM transactions WHERE account_accounts_accountID = " + accountID);
            while (result.next())
            {

                int transID = result.getInt("account_accounts_accountID");
                String date = result.getString("date");
                double amount = result.getDouble("amount");
                int accounts_accountID = result.getInt("account_accounts_accountID");
                double balance_after = result.getDouble("balance_after_tranaction");
                inOut = result.getString("inout_text");

                Transaktions t = new Transaktions(date, accounts_accountID, amount, Math.round(balance_after * 100.0) / 100.0, inOut);

                repositTransList.add(t);

            }
        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return repositTransList;
    }

    public String getAccount(long pNr)
    {
        String getAccountReturnString = null;
        int accountCounter = 0;
        try
        {
            ResultSet result1 = statement.executeQuery("SELECT max(accounts_accountID) FROM accounts");
            while (result1.next())
            // hittar högsta kontonummer
            {
                accountCounter = result1.getInt("max(accounts_accountID)") + 1001;
            }
            ResultSet result = statement.executeQuery("SELECT * FROM accounts WHERE customers_personalNumber like " + pNr);
            while (result.next())
            {

                if (result.getString("account_type").equals("Credit"))
                {
                    CreditAccount ca = new CreditAccount("Credit");
                    ca.setAccountID(accountCounter);
                    getAccountReturnString = ca.toString2();

                } else
                {
                    SavingsAccount sa = new SavingsAccount("Savings");
                    sa.setAccountID(accountCounter);
                    getAccountReturnString = sa.toString2();

                }
            }

        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return getAccountReturnString;
    }

//For the customer's specific account
    public ArrayList<Account> getAllAccountArrayList(long pNr)
    {
        //repositAccountList = new ArrayList<>();
        getAccountArrayList = new ArrayList<>();

        try
        {

            ResultSet result = statement.executeQuery("SELECT * FROM accounts WHERE customers_personalNumber like " + pNr);
            while (result.next())
            {

                if (result.getString("account_type").equals("Credit"))
                {
                    CreditAccount ca = new CreditAccount("Credit");
                    ca.setBalance(result.getDouble("balance"));
                    ca.setAccountID(result.getInt("accounts_accountID"));
                    getAccountArrayList.add(ca);

                } else
                {
                    SavingsAccount sa = new SavingsAccount("Savings");
                    sa.setBalance(result.getDouble("balance"));
                    sa.setAccountID(result.getInt("accounts_accountID"));
                    getAccountArrayList.add(sa);
                }

            }
        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return getAccountArrayList;
    }

    public boolean addCustomer(String name, long pNr) throws Exception
    {
        boolean added = true;

        try
        {

            statement.executeUpdate("INSERT INTO customers (customerName, personalNumber) VALUES ('" + name + "', " + pNr + ")");

        } catch (SQLException ex)
        {
            added = false;
            return added;
        }
        return added;
    }

    public int addCreditAccount(long pNr)
    {
        int accountCounter = 0;
        String insertSqlAddCreditAcc = null;
        try
        {
            ResultSet result = statement.executeQuery("SELECT max(accounts_accountID) FROM accounts");
            while (result.next())
            // hittar högsta kontonummer
            {
                accountCounter = result.getInt("max(accounts_accountID)") + 1;
            }
            if (accountCounter < 1001)
            {
                accountCounter = 1001;
            }

            insertSqlAddCreditAcc = " insert into accounts (accounts_accountID, account_type, customers_personalNumber)"
                    + " values (" + (accountCounter) + ", 'Credit', " + (double) pNr + " )";

            statement.executeUpdate(insertSqlAddCreditAcc);

        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return accountCounter + 1;

    }

    public int addSavingsAccount(long pNr)
    {
        int accountCounter = 0;
        String insertSqlAddSavingAcc = null;

        try
        {
            ResultSet result = statement.executeQuery("SELECT max(accounts_accountID) FROM accounts");
            while (result.next())
            // hittar högsta kontonummer
            {
                accountCounter = result.getInt("max(accounts_accountID)") + 1;
            }
            if (accountCounter < 1001)
            {
                accountCounter = 1001;
            }

            insertSqlAddSavingAcc = " insert into accounts (accounts_accountID, account_type, customers_personalNumber)"
                    + " values (" + (accountCounter) + ", 'Savings', " + (double) pNr + " )";

            statement.executeUpdate(insertSqlAddSavingAcc);

        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return accountCounter + 1;

    }

    public boolean closeAccount(long pNr, int accountId) throws SQLException
    {

        boolean closed = false;

        try
        {
            PreparedStatement closeAccount = connection.prepareStatement("DELETE FROM Accounts WHERE accounts_accountID LIKE ? AND customers_personalNumber LIKE ?");
            closeAccount.setInt(1, accountId);
            closeAccount.setLong(2, pNr);
            closeAccount.executeUpdate();

            closed = true;

        } catch (Exception e)
        {

            e.getMessage();

            closed = false;
        }

        return closed;
    }

    public boolean deposit(int accountID, double amount)
    {
        double currentBalance = 0;
        double newBalance = 0;
        boolean depositMade = false;
        boolean check = false;
        int transactionCounter = 0;

        try
        {

            ResultSet result1 = statement.executeQuery("SELECT * FROM accounts WHERE accounts_accountID = " + accountID);

            while (result1.next())
            {
                currentBalance = result1.getDouble("balance");
                newBalance = currentBalance + amount;
                check = true;
            }

            statement.executeUpdate("UPDATE accounts SET balance = " + newBalance + " WHERE accounts_accountID = " + accountID);

            //ResultSet resultTrans = statement.executeQuery("SELECT max(transaction_Id) FROM transactions WHERE account_accounts_accountID = " + accountID);
            ResultSet resultTrans = statement.executeQuery("SELECT max(transaction_Id) FROM transactions ");
            if (check)
            {
                while (resultTrans.next())
                {
                    transactionCounter = resultTrans.getInt("max(transaction_Id)") + 1;
                    dateFormat2.format(now);

                }

                statement.executeUpdate("insert into transactions (transaction_Id,date,amount,account_accounts_accountID, balance_after_tranaction, inout_text) values ("
                        + transactionCounter + ",'" + date1 + "'," + amount + "," + accountID + "," + newBalance + ",'in')");

            }

        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return depositMade;
    }

    public boolean withdraw(int accountID, double amount) throws SQLException
    {

        double currentBalance = 0;
        double newBalance = 0;
        boolean depositMade = false;
        boolean checkSaving = false;
        boolean checkCredit = false;
        int transactionCounter = 0;

        try
        {

            ResultSet result1 = statement.executeQuery("SELECT * FROM accounts WHERE accounts_accountID = " + accountID);

            while (result1.next())
            {
                int freeOrNot = result1.getInt("firstFreeWithDrawDone"); // Check the value in column "firstFreeWithDrawDone" from the database 
                currentBalance = result1.getDouble("balance");
                if (result1.getString("account_type").equals("Savings"))
                {

                    //if the customer withdraws money for the first time
                    if (freeOrNot == 0)
                    {
                        //balance is 0 at the beginning and can't withdraw money for the savings account
                        if (currentBalance == 0)
                        {
                            newBalance = currentBalance;
                            checkSaving = false;
                        } else
                        {
                            newBalance = currentBalance - amount;
                            checkSaving = true;

                        }
                    } else if (freeOrNot > 0)
                    {
                        //To protect the saving account above 0, withdrawal interest rate 2 %
                        if ((amount + (amount * WITHDRAWRATESAVINGSACCOUNT / 100)) > currentBalance)
                        {
                            newBalance = currentBalance;
                            checkSaving = false;
                            depositMade = false;

                        } else
                        {
                            newBalance = currentBalance - amount - (amount * WITHDRAWRATESAVINGSACCOUNT / 100);
                            checkSaving = true;
                        }

                    }
                } //credit account
                else
                {
                    int creditLimit = -5000;
                    newBalance = currentBalance - amount;

                    //if the sum is between -5000 and 0
                    if (newBalance >= creditLimit)
                    {

                        checkCredit = true;
                    } //if the sum is above 0
//                    else
//                    {
//                        checkCredit = false;
//                        depositMade = false;
////                        System.out.println("Kreditgräns -5000");
//                    }

                }

            }


            ResultSet resultTrans = statement.executeQuery("SELECT max(transaction_Id) FROM transactions ");

            //Updating the transaction in the transaktion and accounts tables in the database for the savingsaccount
            if (checkSaving)
            {

                while (resultTrans.next())
                {
                    transactionCounter = resultTrans.getInt("max(transaction_Id)") + 1;
                    dateFormat2.format(now);
                    depositMade = true;

                }

                inOut = "ut";

                statement.executeUpdate("UPDATE accounts SET balance = " + newBalance + " WHERE accounts_accountID = " + accountID);
                statement.executeUpdate("insert into transactions (transaction_Id,date,amount,account_accounts_accountID, balance_after_tranaction, inout_text) values ("
                        + transactionCounter + ",'" + date1 + "'," + amount + "," + accountID + "," + newBalance + " ,'ut')");

                statement.executeUpdate("UPDATE accounts SET firstFreeWithDrawDone = 1 " + " WHERE accounts_accountID = " + accountID); // Updates the "firstFreeWithDrawDone" after the first withdraw is done.

            } else
            {
                System.out.println("hi");
                depositMade = false;
            }

            //Updating the transaction in the the transaktion and accounts tables in the database for the credit account
            if (checkCredit)
            {
                while (resultTrans.next())
                {
                    transactionCounter = resultTrans.getInt("max(transaction_Id)") + 1;

                }
                // String date1 = dateFormat2.format(now);
                statement.executeUpdate("UPDATE accounts SET balance = " + newBalance + " WHERE accounts_accountID = " + accountID);
                statement.executeUpdate("insert into transactions (transaction_Id,date,amount,account_accounts_accountID, balance_after_tranaction, inout_text) values ("
                        + transactionCounter + ",'" + date1 + "'," + amount + "," + accountID + "," + newBalance + " ,'out')");
                getAllTransactions(accountID).add(new Transaktions(date1, accountID, -Math.round(amount * 100.0) / 100.0, newBalance, "ut"));

            } else
            {
                depositMade = false;
            }

        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
        }

        

        return depositMade;
    }

    public boolean removeCustomer(Long pNr)
    {
        boolean deleted = false;
        try
        {

            String deleteCust = "DELETE FROM customers WHERE personalNumber LIKE ?";
            String deleteCustAcc = "DELETE  FROM accounts WHERE customers_personalNumber LIKE ?";
            PreparedStatement deleteCustAccStm = connection.prepareCall(deleteCustAcc);
            deleteCustAccStm.setLong(1, pNr);
            deleteCustAccStm.executeUpdate();

            PreparedStatement deleteCustStm = connection.prepareStatement(deleteCust);
            deleteCustStm.setLong(1, pNr);
            int delCheck = deleteCustStm.executeUpdate();

            if (delCheck > 0)
            {
                deleted = true;
            }

        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
            deleted = false;
        }
        return deleted;
    }

    public boolean changeCustomerName(String name, long pNr)
    {
        Boolean changedName = false;
        try
        {
            String updateCustomer = "UPDATE customers SET customerName ='" + name + "' WHERE personalNumber =" + pNr;
            statement.executeUpdate(updateCustomer);
            changedName = true;

        } catch (SQLException ex)
        {
            Logger.getLogger(Repository.class.getName()).log(Level.SEVERE, null, ex);
            changedName = false;
        }
        return changedName;
    }
}
