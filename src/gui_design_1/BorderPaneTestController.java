/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui_design_1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import repository.Repository;

/**
 * FXML Controller class
 *
 * @author SYSJM2 GRUPP 3
 */
public class BorderPaneTestController implements Initializable
{

    @FXML
    private Label nameDisplayLabel;
    @FXML
    private Label pNrDisplayLabel;
    @FXML
    private TextField nameChange;
    @FXML
    private TextField searchTextField;
    @FXML
    private Label returnMessageToOperator; // varningar och felmedelanden    
    @FXML
    private ListView custumersListView; // den första, översta listan i GUI
    @FXML
    private ListView accountsListView; // ListView to see the accounts of a specific person
    @FXML
    private ListView transactionsListView;// ListView to see the transaction of one specific account
    @FXML
    private TextField nameTextField; // name-input från användare
    @FXML
    private TextField pNrTextField; // pnr-input från användare

    @FXML
    private TextField depositWithDrawAmountField; // name-input från användare

    private String selectedCustomerString; // To select a customer, the event handler of the listView's MOUSE_CLICKED event
    private String selectedAccountString; //The event handler of the listView's MOUSE_CLICKED event for one account

    private BankLogic bankLogic = BankLogic.getInstance(); // singleton 

    public ObservableList<String> obListAllCustumers = FXCollections.observableArrayList(); // alla kunder som ska visas i custumersListView

    public ObservableList<String> obListFoundCustumers = FXCollections.observableArrayList(); // kunder som hittas och visas i custumersListView

    public ObservableList<String> obListCreateAccount = FXCollections.observableArrayList(); // kunder som skapas och visas i accountsListView

    public ObservableList<String> obListtransaktion = FXCollections.observableArrayList();
    Repository repo = new Repository();

    //To print out bank statement or transactions for one account
    @FXML
    private void kontoUtdrag(ActionEvent event) throws Exception
    {
        
        //A message to the user if the account is not selected
        if (selectedAccountString == null || selectedCustomerString == null)
        {
            returnMessageToOperator.setText("Välj specifik kund och ett konto.");
        } else
        {

          //the event handler of the listView's MOUSE_CLICKED event for one account
            selectedAccountString = (String) accountsListView.getSelectionModel().getSelectedItem();
            Long pNr = Long.parseLong(pNrDisplayLabel.getText());
            int accountID;

            for (int i = 0; i < bankLogic.getAllAccount(pNr).size(); i++)  //Loops to all accounts of a customer
            {
                //To get one account selected if the person has several accounts
                if (bankLogic.getAllAccount(pNr).get(i).toString2().equals(selectedAccountString))
                {
                    accountID = bankLogic.getAllAccount(pNr).get(i).getAccountID();
                    returnMessageToOperator.setText("Listan har skrivits till en textfil.");

                    try
                    {
                        //To print out the transactions and a file name for the file is "Transaktioner.txt"
                        FileWriter out = new FileWriter("Transaktioner.txt");
                        BufferedWriter bw = new BufferedWriter(out);
                        PrintWriter pw = new PrintWriter(bw);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        pw.println("Listan skapad:" + "\t" + dateFormat.format(date));
                        
                        //Printing every line of the transaction
                        for (int j = 0; j < bankLogic.getAllTransactionsArrayList(accountID).size(); j++)
                        {

                            bw.write(bankLogic.getAllTransactionsArrayList(accountID).get(j).toString2());
                            bw.newLine();  //to make a new line after every transaction
                        }
                        bw.close();
                    } catch (IOException ex)
                    {
                        returnMessageToOperator.setText("Filen korrupt eller skrivskyddad! ");
                    }
                }
            }
        }
    }

    //To select one customer in the custumersListView
    @FXML
    public final void getOnMouseClickedCustListView()
    {
        transactionsListView.getItems().clear();  // to make empty the transaction ListView
        
        //One customer is getting selected using "getSelectionModel().getSelectedItem()"
        selectedCustomerString = (String) custumersListView.getSelectionModel().getSelectedItem();
        // To reset the return message
        returnMessageToOperator.setText(" ");
        
        //Looping to all customers
        for (int i = 0; i < bankLogic.getAllCustomersArrayList().size(); i++)
        {
            
            //Comparing or searching for the selected customer
            if (bankLogic.getAllCustomersArrayList().get(i).toString2().equals(selectedCustomerString))
            {
                //To display the name and personal number on nameDisplayLabel and pNrDisplayLabel
                nameDisplayLabel.setText(bankLogic.getAllCustomersArrayList().get(i).getCustomerName());
                pNrDisplayLabel.setText(Long.toString(bankLogic.getAllCustomersArrayList().get(i).getPersonalNumber()));
                obListCreateAccount.clear(); //clears the account Listview and will be filled by the selected customer's accounts

                //To get all accounts of the selected customer above and will be shown on the obListCreateAccount ListView
                for (int j = 0; j < bankLogic.getAllAccount(bankLogic.getAllCustomersArrayList().get(i).getPersonalNumber()).size(); j++)
                {
                    long pNr = bankLogic.getAllCustomersArrayList().get(i).getPersonalNumber();
                    obListCreateAccount.add(bankLogic.getAllAccount(pNr).get(j).toString2());

                }

            }
        }
        accountsListView.setItems(obListCreateAccount); //accountsListView will be filled by the selected customer's account

    }

    /* To select only one account for selected customer in the custumersListView, transactions will be visible on the 
    transaction ListView, "transactionsListView"
    */
    @FXML
    public final void getOnMouseClickedAccoutListView()
    {
        // clears transactions window
        transactionsListView.getItems().clear();

        // gets the toString-text of customer from customer list view
        selectedAccountString = (String) accountsListView.getSelectionModel().getSelectedItem();

        Long pNr = Long.parseLong(pNrDisplayLabel.getText());

        int accountID;
        //To reset the return message
        returnMessageToOperator.setText(" ");
        
        //Loop to go through all accounts of the selected customer
        for (int i = 0; i < bankLogic.getAllAccount(pNr).size(); i++)
        {
            /*if statement begins when the selected account,"selectedAccountString" matches with one accounts 
            (if the customer has more than one account) of the selected customer
            */
            if (bankLogic.getAllAccount(pNr).get(i).toString2().equals(selectedAccountString))
            {

                accountID = bankLogic.getAllAccount(pNr).get(i).getAccountID();//gets the account number from the database
                obListtransaktion.clear(); // 1st clears the transactions ListView

                //Fills the transaction ObservableList and transaction ListView by the selected account
                for (int j = 0; j < bankLogic.getAllTransactionsArrayList(accountID).size(); j++)
                {
                    obListtransaktion.add(bankLogic.getAllTransactionsArrayList(accountID).get(j).toString2());

                }

            }

        }
        transactionsListView.setItems(obListtransaktion);

    }

    //To add a new customer
    @FXML
    private void addCustomerButton(ActionEvent event) throws Exception
    {

        if (nameTextField.getText().isEmpty() || pNrTextField.getText().isEmpty()) // om användare inte fyllt i båda fälten, komplettera med instanceOf?
        {
            returnMessageToOperator.setText("Du måste fylla i båda fälten!");
        } 
        //The personal number size should be 12
        else if (pNrTextField.getText().length() > 12 || pNrTextField.getText().length() < 12)
        {
            returnMessageToOperator.setText("Du måste fylla i 12 siffror!");
        } 
        // The user might write these characters by mistake,  -+.!^:,*/?, protection to not crash the program
        
//        else if (nameTextField.getText().matches("[.!^:,*/?]"))
//        {
//            returnMessageToOperator.setText("Namn får endast bestå av bokstäver1!");
//        } 
        //The allowed characters are only A-zåäöÅÄÖ-
        else if (!nameTextField.getText().matches("^[A-zåäöÅÄÖ-]+$"))
        {
            returnMessageToOperator.setText("Namn får endast bestå av bokstäver2!");
        } else
        {
            //Verification of the personal number
            //ta bort eventuella bindesträck så att både ååååmmdd-xxxx och ååååmmddxxxx fungerar
            String personNummer = pNrTextField.getText();
            personNummer = personNummer.replaceAll("-", "").trim();
            //anropa personnummerkollen i klassen KorrektPersonNummer
            if (!KorrektPersonNummer.nummerValidering1(personNummer))
            {
                returnMessageToOperator.setText(KorrektPersonNummer.nummerValidering2(pNrTextField.getText()));
            } else if (!KorrektPersonNummer.datumValidering1(personNummer))
            {
                returnMessageToOperator.setText(KorrektPersonNummer.datumValidering2(pNrTextField.getText()));
            } else if (!KorrektPersonNummer.checkNumber1(personNummer))
            {
                returnMessageToOperator.setText("Personnummer stämmer inte med kontrollsiffran");
            } else
            {
                //lägga till kund
                try
                {
                    //.replace method is used to replace especial characters, for example if the user 
                    //writes "Tomas[" in the name box,"[" character will be removed
                    boolean add = bankLogic.addCustomer(nameTextField.getText().replace("[", "").replace("]", "").trim(), Long.parseLong(pNrTextField.getText()));

                    if (add)
                    {
                        obListAllCustumers.clear();
                        obListAllCustumers.addAll(bankLogic.getCustomers());
                        custumersListView.setItems(obListAllCustumers);
                        returnMessageToOperator.setText("Kund lades till!");
                    } else
                    {
                        returnMessageToOperator.setText("Kund lades inte till!");
                    }
                } catch (NumberFormatException nfe)
                {
                    returnMessageToOperator.setText("Ange ett giltigt personnummer.");
                }

                nameTextField.clear();
                pNrTextField.clear();
                returnMessageToOperator.setText("");

            }
        }
    }

    //To search for a customer
    @FXML
    private void findCustumerButton(ActionEvent event) throws Exception
    {
        if (searchTextField.getText().isEmpty()) 
        {
            obListAllCustumers.clear(); //clears the customer ListView, custumersListView
            obListAllCustumers.addAll(bankLogic.getCustomers());
            custumersListView.setItems(obListAllCustumers);
            returnMessageToOperator.setText("Visar alla befintliga kunder");
        } else
        {
            obListFoundCustumers.clear();
            custumersListView.getItems().clear();

            //Searching the customer from the database
            for (int i = 0; i < bankLogic.getAllCustomersArrayList().size(); i++)
            {
                if (bankLogic.getAllCustomersArrayList().get(i).toString2().toLowerCase().contains(searchTextField.getText().toLowerCase()))
                {

                    obListFoundCustumers.add(bankLogic.getAllCustomersArrayList().get(i).toString2());
                    custumersListView.setItems(obListFoundCustumers);

                }
            }
        }
    }

    //Changing the customer name
    @FXML
    private void changeCustumerNameButton(ActionEvent event) throws Exception
    {
        //If the bank person writes "[" and "]" by mistake, it will be removed
        String name = nameChange.getText().replace("[", "").replace("]", "").trim();

        Long personalNumber;
        try
        {
            if (nameChange.getText().isEmpty())
            {
                returnMessageToOperator.setText("Välj kund och ange nytt namn.");
            } 
            //The program only accepts, "^[A-zåäöÅÄÖ-]+$", otherwise a return message will be displayed
            else if (!nameChange.getText().matches("^[A-zåäöÅÄÖ-]+$"))
            {
                returnMessageToOperator.setText("Namn får endast bestå av bokstäver!");
            } else
            {
                personalNumber = Long.valueOf(pNrDisplayLabel.getText());// To get a personal number

                if (bankLogic.changeCustomerName(name, personalNumber))
                {
                    obListAllCustumers.clear();

                    seeAllCustomersButton(event); //to show the customer ListView

                    returnMessageToOperator.setText("Kundens namn ändrades till: " + name);
                    nameChange.clear();
                    nameDisplayLabel.setText(name);
                    returnMessageToOperator.setText("");

                }
            }
        } catch (NumberFormatException nfe)
        {
            returnMessageToOperator.setText("Skriv namn och personnummer.");
        }

    }

    //Removing the customer
    @FXML
    private void removeCustomersButton(ActionEvent event) throws Exception
    {
        //Selecting a customer to be removed
        String removeCustomerString = (String) custumersListView.getSelectionModel().getSelectedItem();
        nameTextField.clear();
        pNrTextField.clear();
        if (!pNrTextField.getText().isEmpty())
        {
            returnMessageToOperator.setText("Välj kund att ta bort!");
        } else if (removeCustomerString == null)  // a message if the customer is not selected to be removed
        {
            nameTextField.clear();
            pNrTextField.clear();
            returnMessageToOperator.setText("Välj kund att ta bort!");
        } else
        {
            for (int j = 0; j < bankLogic.getAllCustomersArrayList().size(); j++)
            {
                nameTextField.clear();
                pNrTextField.clear();

                //To get the selected customer (to be removed) in the database
                if (removeCustomerString.equals(bankLogic.getAllCustomersArrayList().get(j).toString2()))
                {
                    //If found, a message will be displayed
                    returnMessageToOperator.setText(bankLogic.getAllCustomersArrayList().get(j).getCustomerName() + " borttagen");

                    //To clear the observable list in the Account ListView
                    obListCreateAccount.clear();
                    obListCreateAccount.add("Följande konto avslutas");

                    //Adding all the customers' removed account int account ListView
                    Long personalNumber = Long.parseLong(pNrDisplayLabel.getText());// To get a personal number
                    obListCreateAccount.addAll(bankLogic.removeCustomer(personalNumber));
                    accountsListView.setItems(obListCreateAccount);

                    obListAllCustumers.addAll((List) BankLogic.getAllCustomersArrayList());
                    custumersListView.setItems(obListAllCustumers);
                    obListtransaktion.clear();//To make obListtransaktion empty
                    break;
                }
            }
        }
        obListAllCustumers.clear();
        obListAllCustumers.addAll(bankLogic.getCustomers());
        custumersListView.setItems(obListAllCustumers);
    }

    //To print all customer lists to a text file
    @FXML
    private void printAllCustomersButton(ActionEvent event) throws Exception
    {
        List<String> stringListCustomer = bankLogic.getCustomers();
        try
        {
            
            //To print all the customers, and the file name will be Kundlista.txt
            FileWriter out = new FileWriter("Kundlista.txt");
            BufferedWriter bw = new BufferedWriter(out);
            PrintWriter pw = new PrintWriter(bw);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            pw.println("Listan skapad:" + "\t" + dateFormat.format(date));
            for (String list : stringListCustomer)
            {

                bw.write(list);
                bw.newLine();
            }
            bw.close();
        } catch (IOException ex)
        {
            returnMessageToOperator.setText("Filen korrupt eller skrivskyddad! ");
        }
    }

    //To see all the customers of the bank, it may be usefull after the bank person searches a specific customer
    @FXML
    private void seeAllCustomersButton(ActionEvent event) throws Exception
    {
        obListAllCustumers.clear();
        obListAllCustumers.addAll(bankLogic.getCustomers());
        custumersListView.setItems(obListAllCustumers);
    }

    //To withdraw money from the specific account, credit and/or saving account
    @FXML
    private void withDrawButton(ActionEvent event) throws Exception
    {
        selectedCustomerString = (String) custumersListView.getSelectionModel().getSelectedItem();
        selectedAccountString = (String) accountsListView.getSelectionModel().getSelectedItem();

        long pNr = 0;
        double amount = 0;
        if (selectedAccountString == null || selectedCustomerString == null)
        {
            returnMessageToOperator.setText("Vänligen välj konto först.");

        } else if (depositWithDrawAmountField.getText().isEmpty())
        {
            returnMessageToOperator.setText("Vänligen ange en summa!");
        } else if (!depositWithDrawAmountField.getText().matches(".*[0-9].*"))
        {
            returnMessageToOperator.setText("Vänligen ange endast siffror!");

        }
        else if (depositWithDrawAmountField.getText().matches(".*[+,*].*"))  //to protect for example "+100"
        {
            returnMessageToOperator.setText("Vänligen ange endast siffror!");

        }
        else
        {
            try
            {
                amount = Double.parseDouble(depositWithDrawAmountField.getText());
                if (amount <= 0)
                {
                    returnMessageToOperator.setText("Värdet är för lågt eller felaktigt.");

                } else if (amount > 1000000)
                {
                    returnMessageToOperator.setText("Värdet för högt!");
                }
                else
                {
                int accountID1 = 1;
                for (int i = 0; i < bankLogic.getAllCustomersArrayList().size(); i++)
                {
                    if (selectedCustomerString.equals(bankLogic.getAllCustomersArrayList().get(i).toString2()))
                    {
                        pNr = bankLogic.getAllCustomersArrayList().get(i).getPersonalNumber();
                    }
                }
                for (int i = 0; i < bankLogic.getAllAccount(pNr).size(); i++)
                {
                    if (bankLogic.getAllAccount(pNr).get(i).toString2().equals(selectedAccountString))
                    {

                        accountID1 = bankLogic.getAllAccount(pNr).get(i).getAccountID();
                        
                       // boolean check = repo.withdraw(accountID1, amount);
   
                        if (repo.withdraw(accountID1, amount))
                        {
                            getOnMouseClickedCustListView();
                            getOnMouseClickedAccoutListView();

                            //To show the specific account's transaction in the transaction ListView
                            for (int k = 0; k < repo.getAllTransactions(accountID1).size(); k++)
                            {
                                obListtransaktion.add(repo.getAllTransactions(accountID1).get(k).toString2());

                            }
                            break;

                        } 
                        else //if(repo.withdraw(accountID1, amount) == false)
                        {
                            getOnMouseClickedCustListView();
                            getOnMouseClickedAccoutListView();
                            if (bankLogic.getAllAccount(pNr).get(i).getAccountType().matches("Credit"))
                            {

                                returnMessageToOperator.setText("Överskriden kreditgräns!");
                            }
                            else if (bankLogic.getAllAccount(pNr).get(i).getAccountType().matches("Savings"))
                            {

                                returnMessageToOperator.setText("Du har inte tillräckligt med pengar!");

                            }
                            

                            //To show the specific account's transaction in the transaction ListView
                            for (int k = 0; k < repo.getAllTransactions(accountID1).size(); k++)
                            {
                                obListtransaktion.add(repo.getAllTransactions(accountID1).get(k).toString2());

                            }
                        }
                    }

                }
                }
            } catch (Exception e)
            {
                returnMessageToOperator.setText("Vänligen ange ett giltigt nummer.");
            }

            transactionsListView.setItems(obListtransaktion);
        }

        depositWithDrawAmountField.clear();

    }

    @FXML
    private void closeAccountButton(ActionEvent event) throws Exception
    {
        selectedAccountString = (String) accountsListView.getSelectionModel().getSelectedItem();
        if (selectedAccountString == null)
        {
            returnMessageToOperator.setText("Välj specifikt konto som ska avslutas.");
        } else
        {

            for (int j = 0; j < BankLogic.getAllAccountsArrayList(Long.parseLong(pNrDisplayLabel.getText())).size(); j++)
            {
                if (selectedAccountString.equals(BankLogic.getAllAccountsArrayList(Long.parseLong(pNrDisplayLabel.getText())).get(j).toString2()))
                {
                    Long personalNumber = Long.parseLong(pNrDisplayLabel.getText());// To get a personal number
                    int accountID = BankLogic.getAllAccountsArrayList(personalNumber).get(j).getAccountID();//To get accountID   
                    //To show the deleted Account on the transaction window, the result would be printed
                    //as a text form too, the printer code is written in BankLogic.closeAccount(personalNumber, accountID)
                    obListCreateAccount.clear();
                    transactionsListView.getItems().clear();
                    obListCreateAccount.add("Följande konto avslutas: ");
                    obListCreateAccount.add(bankLogic.closeAccount(personalNumber, accountID));
                    accountsListView.setItems(obListCreateAccount);

                }
            }

        }
    }

    @FXML
    private void depositButton(ActionEvent event) throws Exception
    {
        selectedCustomerString = (String) custumersListView.getSelectionModel().getSelectedItem();
        selectedAccountString = (String) accountsListView.getSelectionModel().getSelectedItem();
        long pNr = 0;
        double amount = 0;
        if (selectedAccountString == null || selectedCustomerString == null)
        {
            returnMessageToOperator.setText("Vänligen välj konto först.");

        } else if (depositWithDrawAmountField.getText().isEmpty())
        {
            returnMessageToOperator.setText("Vänligen ange ett belopp.");
        } else if (!depositWithDrawAmountField.getText().matches(".*[0-9].*"))
        {
            returnMessageToOperator.setText("Vänligen ange endast siffror!");

        } 
        else if (depositWithDrawAmountField.getText().matches(".*[+,*].*"))  //to protect for example "+100"
        {
            returnMessageToOperator.setText("Vänligen ange endast siffror!");

        }          
        else
        {
            try
            {
                amount = Double.parseDouble(depositWithDrawAmountField.getText());
            } catch (Exception e)
            {
                returnMessageToOperator.setText("Vänligen ange ett giltligt belopp.");
            }
            int accountID1 = 1;
            for (int i = 0; i < bankLogic.getAllCustomersArrayList().size(); i++)
            {
                if (selectedCustomerString.equals(bankLogic.getAllCustomersArrayList().get(i).toString2()))
                {
                    pNr = bankLogic.getAllCustomersArrayList().get(i).getPersonalNumber();
                }
            }
            for (int i = 0; i < bankLogic.getAllAccount(pNr).size(); i++)
            {
                if (bankLogic.getAllAccount(pNr).get(i).toString2().equals(selectedAccountString))
                {

                    accountID1 = bankLogic.getAllAccount(pNr).get(i).getAccountID();
                    if (amount <= 0)
                    {
                        returnMessageToOperator.setText("Felaktigt värde!");

                    } else if (amount > 1000000)
                    {
                        returnMessageToOperator.setText("Felaktigt belopp!");
                    } else if (bankLogic.deposit(accountID1, amount) == true)
                    {
                        getOnMouseClickedCustListView();
                        getOnMouseClickedAccoutListView();

                        //To show the transaction in the transaction ListView
                        for (int k = 0; k < repo.getAllTransactions(accountID1).size(); k++)
                        {
                            obListtransaktion.add(repo.getAllTransactions(accountID1).get(k).toString2());
                        }
                    }

                }

            }
            transactionsListView.setItems(obListtransaktion);
        }

        depositWithDrawAmountField.clear();

    }

    @FXML
    private void createNewCreditAccountButton(ActionEvent event) throws Exception
    {

        selectedCustomerString = (String) custumersListView.getSelectionModel().getSelectedItem();
        selectedAccountString = (String) accountsListView.getSelectionModel().getSelectedItem();
        String getAccountInformation;
        Long personalNumber;
        try
        {

            if (selectedCustomerString == null && pNrTextField.getText().isEmpty())
            {
                returnMessageToOperator.setText("Välj kund från kundlistan.");
            } else if (!pNrTextField.getText().isEmpty())
            {
                personalNumber = Long.valueOf(pNrTextField.getText());
                getAccountInformation = bankLogic.getAccount(personalNumber, bankLogic.addCreditAccount(Long.valueOf(pNrTextField.getText())));
                nameTextField.clear();
                pNrTextField.clear();
                obListCreateAccount.add(getAccountInformation);
                accountsListView.setItems(obListCreateAccount);
            } else
            {
                for (int i = 0; i < bankLogic.getAllCustomersArrayList().size(); i++)
                {
                    nameTextField.clear();
                    pNrTextField.clear();
                    if (selectedCustomerString.equals(bankLogic.getAllCustomersArrayList().get(i).toString2()))
                    {
                        personalNumber = bankLogic.getAllCustomersArrayList().get(i).getPersonalNumber();// Test to get a personal number
                        int accountID = bankLogic.addCreditAccount(personalNumber);//To get accountID                   
                        getAccountInformation = bankLogic.getAccount(personalNumber, accountID);

                        obListCreateAccount.add(getAccountInformation);
                        accountsListView.setItems(obListCreateAccount);
                        getOnMouseClickedCustListView();
                    }

                }

            }
        } catch (NumberFormatException nfe)
        {
            returnMessageToOperator.setText("Ange ett giltigt personnummer");
        }

    }

    @FXML
    private void createNewSavingsAccountButton(ActionEvent event) throws Exception
    {

        selectedCustomerString = (String) custumersListView.getSelectionModel().getSelectedItem();
        selectedAccountString = (String) accountsListView.getSelectionModel().getSelectedItem();
        String getAccountInformation;
        Long personalNumber;
        try
        {
            if (selectedCustomerString == null && pNrTextField.getText().isEmpty())
            {
                returnMessageToOperator.setText("Välj en kund från kundlistan");
            } else if (!pNrTextField.getText().isEmpty())
            {
                getAccountInformation = bankLogic.getAccount(Long.valueOf(pNrTextField.getText()), bankLogic.addSavingsAccount(Long.valueOf(pNrTextField.getText())));
                obListCreateAccount.add(getAccountInformation);
                accountsListView.setItems(obListCreateAccount);
                nameTextField.clear();
                pNrTextField.clear();
            } else
            {
                for (int i = 0; i < bankLogic.getAllCustomersArrayList().size(); i++)
                {

                    nameTextField.clear();
                    pNrTextField.clear();
                    if (selectedCustomerString.equals(bankLogic.getAllCustomersArrayList().get(i).toString2()))
                    {

                        personalNumber = bankLogic.getAllCustomersArrayList().get(i).getPersonalNumber();// Test to get a personal number
                        int accountID = bankLogic.addSavingsAccount(personalNumber);//To get accountID                   
                        getAccountInformation = bankLogic.getAccount(personalNumber, accountID);

                        obListCreateAccount.add(getAccountInformation);
                        accountsListView.setItems(obListCreateAccount);
                        getOnMouseClickedCustListView();

                    }

                }

            }
        } catch (NumberFormatException nfe)
        {
            returnMessageToOperator.setText("Ange giltigt personnummer.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

        obListAllCustumers.clear();
        obListAllCustumers.addAll(bankLogic.getCustomers());
        custumersListView.setItems(obListAllCustumers);

        accountsListView.setItems(obListCreateAccount);
    }

}
