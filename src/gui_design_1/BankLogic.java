/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui_design_1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Befkadu Degefa
 */
public class BankLogic
{

    static List<Customer> allCustomersArrayList;
    private static BankLogic instance; //Step 2 declare the instance variabel
    static List<String> removedCustomerList = new ArrayList<>();

    private BankLogic() //Step 1 change this constructor to private
    {
        allCustomersArrayList = new ArrayList<>();
    }

    public static BankLogic getInstance() //Step 3 write getInstance method
    {
        if (instance == null)
        {
            instance = new BankLogic();
        }
        return instance;
    }

    /**
     * Returns all allCustomersArrayList of the bank(Personal number and name)
     *
     * @return
     */
    public List<String> getCustomers()
    {
        List<String> stringListCustomer = new ArrayList<>();
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {
            stringListCustomer.add(allCustomersArrayList.get(i).toString2());

        }
        
        //To print all customer lists to a text file
        try
        {
            FileWriter out = new FileWriter("allCustomersArrayList.txt");
            BufferedWriter bw = new BufferedWriter(out);
            bw.write(stringListCustomer.toString());
            bw.close();
        } catch (IOException ex)
        {
            Logger.getLogger(BankLogic.class.getName()).log(Level.SEVERE, null, ex);
        }

        return stringListCustomer;
    }

    /**
     * Adding the allCustomersArrayList, if not exist in the system. Returns
     * true if the allCustomersArrayList created
     *
     * @param name
     * @param pNr
     * @return
     */
    public boolean addCustomer(String name, long pNr)
    {
        boolean check = true;
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {
            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {
                check = false;
                break;
            }
        }

        //if the allCustomersArrayList doesn't exist in the database, he/she will be added here
        if (check == true)
        {
            allCustomersArrayList.add(new Customer(name, pNr));//
        }
        return check;
    }

    /**
     * To get the information about the specific allCustomersArrayList after
     * entering the personal number, returns name and pNr
     *
     * @param pNr
     * @return
     */
    public List<String> getCustomer(long pNr)
    {
        //Creating empty ArrayList to put all the customerAccountList
        List<String> searchCustomer = new ArrayList<>();
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {
            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {
                searchCustomer.add(allCustomersArrayList.get(i).toString2());
//                for (int j = 0; j < allCustomersArrayList.get(i).getCustumerAccountsList().size(); j++)
//                {
//                    searchCustomer.add(allCustomersArrayList.get(i).getCustumerAccountsList().get(j).toString());
//                    System.out.println("trial1 " + searchCustomer);
//                    //searchCustomer.add(allCustomersArrayList.get(i).toString2());
//
//                }
                break;
            }

        }
        return searchCustomer;
    }

    /**
     * Change name for the guy having the same personal number return true if
     * the name changes
     *
     * @param name
     * @param pNr
     * @return
     */
    public boolean changeCustomerName(String name, long pNr)
    {
        boolean changeCustomerName = false;
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {

            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {
                //allCustomersArrayList.remove(allCustomersArrayList.get(i));
                allCustomersArrayList.get(i).setCustomerName(name);
                changeCustomerName = true;
                break;
            }

        }

    
        return changeCustomerName;
    }

    /**
     * Deleting the allCustomersArrayList with pNr and the result will be
     * returned The return list will have the information about the last
     * balance, interest
     *
     * @param pNr
     * @return
     */
    public List<String> removeCustomer(long pNr)
    {

        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {

            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {

                removedCustomerList.add(allCustomersArrayList.get(i).toString1());

                //After removing allCustomersArrayList, the allCustomersArrayList will be removed
                allCustomersArrayList.remove(allCustomersArrayList.get(i));

                break;
            }

        }

        return removedCustomerList;
    }

    /**
     * create an account for a allCustomersArrayList with personal number, that
     * returns or return -1 if not created
     *
     * @param pNr
     * @return
     */
    public int addSavingsAccount(long pNr)
    {
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {
            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {
                allCustomersArrayList.get(i).getCustumerAccountsList().add(new SavingsAccount("Saving Account", 2));
                return allCustomersArrayList.get(i).custumerAccountsList.get(allCustomersArrayList
                        .get(i).custumerAccountsList.size() - 1).getAccountID();

            }

        }

        return -1;
    }

    public String getAccount(long pNr, int accountId)
    {
        String getAccountReturnString = null;
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {
            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {
                for (int j = 0; j < allCustomersArrayList.get(i).getCustumerAccountsList().size(); j++)
                {
                    if (allCustomersArrayList.get(i).getCustumerAccountsList().get(j).getAccountID() == accountId)
                    {
                        getAccountReturnString = allCustomersArrayList.get(i).getCustumerAccountsList().get(j).toString();
                    }

                }
            }
        }
        return getAccountReturnString;
    }

    public boolean deposit(long pNr, int accountId, double amount)
    {
        boolean depositMade = false;
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {
            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {
                for (int j = 0; j < allCustomersArrayList.get(i).custumerAccountsList.size(); j++)
                {
                    //if condition edited, checked the account type- for Savings
                    if (allCustomersArrayList.get(i).getCustumerAccountsList().get(j).getAccountID() == accountId)
                    {
                        allCustomersArrayList.get(i).getCustumerAccountsList().get(j).deposit(amount);
                        System.out.println("Balance becomes in side BankLigic class in deposit method " + i + " " + allCustomersArrayList.get(i).custumerAccountsList.get(j).getBalance()
                        );
                        //transaktionsArrayList.add(new Transaktions(accountId, allCustomersArrayList.get(i).custumerAccountsList.get(i).getAccountType(), amount, allCustomersArrayList.get(i).custumerAccountsList.get(i).getBalance()));
                        depositMade = true;
                    }

                }
            }

        }
        return depositMade;
    }

    public boolean withdraw(long pNr, int accountId, double amount)
    {
        boolean withdrawMade = false;

        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {
            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {
                for (int j = 0; j < allCustomersArrayList.get(i).custumerAccountsList.size(); j++)
                {
                    if (allCustomersArrayList.get(i).custumerAccountsList.get(j).getAccountID() == accountId)
                    {
                        //withdrawRate (7%) of withdraw amount plus withdraw amount should be less than -5000
                        //-4672 * 7% - 4672 = -5000
                        if (allCustomersArrayList.get(i).custumerAccountsList.get(j).getBalance() <= -5000 &&
                                allCustomersArrayList.get(i).custumerAccountsList.get(j).getAccountType().equals("Credit Account"))
                        {
                            withdrawMade = false;
                        } 
                        else
                        {
                            allCustomersArrayList.get(i).custumerAccountsList.get(j).withdraw(amount);
                            System.out.println("Balance becomes in side BankLigic class in withdraw method "
                                    + allCustomersArrayList.get(i).custumerAccountsList.get(j).getBalance());
                            withdrawMade = true;
                        }
                    }
                }
            }
        }
        return withdrawMade;
    }

    public String closeAccount(long pNr, int accountId)
    {
        String closedAccount = null;
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {
            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {
                for (int j = 0; j < allCustomersArrayList.get(i).custumerAccountsList.size(); j++)
                {
                    if (allCustomersArrayList.get(i).custumerAccountsList.get(j).getAccountID() == accountId)
                    {
                        closedAccount = allCustomersArrayList.get(i).custumerAccountsList.get(j).toStringClose();

                        //Printing the customer information as a text file when closing his account
                        try
                        {
                            FileWriter out = new FileWriter("Closed account" + j + ".txt");
                            BufferedWriter bw = new BufferedWriter(out);
                            bw.write(closedAccount); //closedAccount String type to be printed on the text file
                            bw.close();
                        } catch (IOException ex)
                        {
                            Logger.getLogger(BankLogic.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        allCustomersArrayList.get(i).custumerAccountsList.remove(allCustomersArrayList.get(i).custumerAccountsList.get(j));
                        System.out.print("Personal number " + pNr + ", ");

                    }
                }
            }

        }
        return closedAccount;
    }

    /**
     * create an account for a allCustomersArrayList with personal number, that
     * returns or return -1 if not created
     *
     * @param pNr
     * @return
     */
    public int addCreditAccount(long pNr)
    {
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {
            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {
                allCustomersArrayList.get(i).getCustumerAccountsList().add(new CreditAccount("Credit Account", 0.5, 0));
                return allCustomersArrayList.get(i).custumerAccountsList.get(allCustomersArrayList
                        .get(i).custumerAccountsList.size() - 1).getAccountID();
            }

        }

        return -1;
    }

    public List<String> getTransaktions(long pNr, int accountId)
    {
        List<String> transactionList = null;
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {

            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)

            {
                for (int j = 0; j < allCustomersArrayList.get(i).getCustumerAccountsList().size(); j++)
                {
                    if (allCustomersArrayList.get(i).getCustumerAccountsList().get(j).getAccountID() == accountId)
                    {
                        for (int t = 0; t < allCustomersArrayList.get(i).getCustumerAccountsList().get(j).
                                custumerAccountsTransaktionsList.size(); t++)

                        {

                            System.out.println(allCustomersArrayList.get(i).getCustumerAccountsList().get(j).custumerAccountsTransaktionsList.toString()); //Test
                            transactionList.add(allCustomersArrayList.get(i).getCustumerAccountsList().get(j).custumerAccountsTransaktionsList.get(t).toString());
                            return transactionList;
                        }
                    }
                }
            }
        }

        return null;
    }
    
    public List<String> getAllAccount(long pNr, int accountId)
    {
        List<String> getAccountReturnString = new ArrayList<>();
        for (int i = 0; i < allCustomersArrayList.size(); i++)
        {
            if (allCustomersArrayList.get(i).getPersonalNumber() == pNr)
            {
                for (int j = 0; j < allCustomersArrayList.get(i).getCustumerAccountsList().size(); j++)
                {
//                    if (allCustomersArrayList.get(i).getCustumerAccountsList().get(j).getAccountID() == accountId)
//                    {
                        getAccountReturnString.add(allCustomersArrayList.get(i).getCustumerAccountsList().get(j).toString());
//                    }

                }
            }
        }
        return getAccountReturnString;
    }
}
