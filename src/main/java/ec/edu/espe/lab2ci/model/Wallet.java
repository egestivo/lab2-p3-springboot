package ec.edu.espe.lab2ci.model;


import lombok.Getter;

import java.util.UUID;

@Getter
public class Wallet {
    private final String id;
    private final String ownerEmail;
    private double balance;

    public Wallet(String ownerEmail, double balance) {
        this.id = UUID.randomUUID().toString();
        this.ownerEmail = ownerEmail;
        this.balance = balance;
    }

    // Deposirtas el dinero en la billetera
    public void deposit(double amount){
        this.balance += amount;
    }

    // Retirar dinero de la billetera
    public void withdraw(double amount){
        this.balance -= amount;
    }
}
