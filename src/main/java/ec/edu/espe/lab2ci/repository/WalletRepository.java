package ec.edu.espe.lab2ci.repository;

import ec.edu.espe.lab2ci.model.Wallet;

import java.util.Optional;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(String id);
    boolean existsByOwnerEmail(String ownerEmail);
}
