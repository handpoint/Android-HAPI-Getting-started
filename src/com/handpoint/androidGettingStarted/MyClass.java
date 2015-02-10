/**
 * Created by Handpoint on 09/02/15.
 * Copyright (c) 2014 Handpoint. All rights reserved.
 */
package com.handpoint.androidGettingStarted;

import android.content.Context;
import com.handpoint.api.*;
import java.math.BigInteger;
import java.util.List;

// MyClass implements the Events.Required and Events.Status so we can capture transaction and status notification events
public class MyClass implements Events.Required, Events.Status {

    Hapi api;
    Device device;
    MyActivity UIClass;

    public MyClass(MyActivity activity) {
        initApi((Context) activity);
        UIClass = activity;
    }

    //An Android Context is required to be able to handle bluetooth
    public void initApi(Context context) {
        // If using a Handpoint integration card reader update the following string with the supplied shared secret
        String sharedSecret = "0102030405060708091011121314151617181920212223242526272829303132";
        this.api = HapiFactory.getAsyncInterface(this, context).defaultSharedSecret(sharedSecret);
        // The shared secret is a unique string shared between the card reader and your mobile application.
        // It prevents other people to connect to your card reader.

        // Subscribe to to status notifications
        this.api.addStatusNotificationEventHandler(this);
    }

    public void discoverDevices(){
        // Manually update the status
        UIClass.updateStatusDisplay("Searching for card readers");
        // This triggers the search for all the bluetooth devices around.
        this.api.listDevices(ConnectionMethod.BLUETOOTH);
    }

    @Override
    public void deviceDiscoveryFinished(List<Device> devices) {
        // Manually update the status
        UIClass.updateStatusDisplay("Card reader discovery finished");
        // Only needed when using a payment terminal, here you get a list of Bluetooth devices discovered around your smartphone
        for (Device device : devices) {
            if (device.getName() != null){
                if (device.getName().equals("PP0513900544")) {
                    // Please put the BT name of your device, you can find it by doing C then up arrow on your card reader keypad
                    // or displayed on the card reader screen when idle
                    this.device = device;
                    this.api.useDevice(this.device);
                }
            }
        }
    }

    public void connectToSimulator() {
        // Disconnect from previous connection
        this.disconnect();
        // Manually update the status
        UIClass.updateStatusDisplay("Connecting to simulator");
        // Initiates a connection to the card reader simulator
        Device device = new Device("name", "address", "port", ConnectionMethod.SIMULATOR);
        String simulatorSharedSecret = "0102030405060708091011121314151617181920212223242526272829303132";
        device.setSharedSecret(simulatorSharedSecret);
        this.api.useDevice(device);
    }

    public void connectToDevice() {
        // Manually update the status
        UIClass.updateStatusDisplay("Starting discovery");
        // Starts a discovery for real card readers
        this.discoverDevices();
    }

    public boolean payWithPinAuthorized() {
        // Starts a sale transaction for $110, which will result in a pin authorized tranasction if using the simulator
        return this.api.sale(new BigInteger("11000"), Currency.USD);
        // amount X10XX where X represents an integer [0;9] --> Pin authorized
    }

    @Override
    public void signatureRequired(SignatureRequest signatureRequest, Device device) {
        // You'll be notified here if a sale process needs a signature verification
        // A signature verification is only needed if the cardholder uses a magnetic stripe card or a chip and signature card for the payment
        // This method will not be invoked if a transaction is made with a Chip & Pin card
        // At this step, you are supposed to display the merchant receipt to the cardholder on the smartphone
        // the cardholder must have the possibility to accept or decline the transaction (for example : if he disagrees with the amount on the receipt)
        // If the cardholder clicks on decline, the transaction is VOID
        // If the cardholder clicks on accept he is then asked to sign electronically the receipt
        this.api.signatureResult(true);
        // This line means that the cardholder ALWAYS accepts to sign the receipt, for this sample app we are not going to implement the whole signature process
    }


    @Override
    public void endOfTransaction(TransactionResult transactionResult, Device device) {
        // The object TransactionResult stores a lot of information, for example, it holds the merchant receipt as well as the cardholder receipt
        // Other information can be accessed through this object like the transaction ID, the currency, the amount...
        // Displaying the customer receipt
        UIClass.callReceiptDialog(transactionResult.getCustomerReceipt());
        // Updating the status label on screen
        UIClass.updateStatusDisplay(transactionResult.getStatusMessage());
    }

    @Override
    public void connectionStatusChanged(ConnectionStatus connectionStatus, Device device) {
        // Updating the status label on screen
        UIClass.updateStatusDisplay(connectionStatus.toString());
    }

    @Override
    public void currentTransactionStatus(StatusInfo statusInfo, Device device) {
        // Updating the status label on screen
        UIClass.updateStatusDisplay(statusInfo.getMessage());
    }

    public void disconnect(){
        //This disconnects from the card reader
        this.api.disconnect();
    }

}
