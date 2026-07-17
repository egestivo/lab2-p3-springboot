package ec.edu.espe.lab2ci.service;

import ec.edu.espe.lab2ci.dto.WalletResponse;
import ec.edu.espe.lab2ci.model.Wallet;
import ec.edu.espe.lab2ci.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.AssertionErrors;

import java.util.Optional;

public class WalletServiceTest {
    private WalletService walletService;
    private RiskClient riskClient;
    private WalletRepository walletRepository;

    // arrange común de cada prueba
    @BeforeEach
    public void setUp(){
        walletRepository = Mockito.mock(WalletRepository.class);
        riskClient = Mockito.mock(RiskClient.class);
        walletService = new WalletService(walletRepository, riskClient);
    }

    // crear wallet valida y devolver respuesta
    @Test
    void createWalletWithValidDataShouldSaveAndReturnResponse(){
        // Arrange
        String email = "estiven.ona@espe.edu.ec";
        double balance = 150.00;

        Mockito.when(riskClient.isBlocked(email)).thenReturn(false);
        Mockito.when(walletRepository.existsByOwnerEmail(email)).thenReturn(false);
        Mockito.when(walletRepository.save(ArgumentMatchers.any(Wallet.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        //Act
        WalletResponse response = walletService.createWallet(email, balance);

        //Assert
        AssertionErrors.assertNotNull("Id Wallet no Debe Ser Null",response.getWalletId());
        Assertions.assertEquals(balance, response.getBalance());

        Mockito.verify(riskClient).isBlocked(email);
        Mockito.verify(walletRepository).existsByOwnerEmail(email);
        Mockito.verify(walletRepository).save(ArgumentMatchers.any(Wallet.class));
    }

    @Test
    void createWalletWithInvalidDataShouldThrowExceptionAndNotCallDependencies(){
        // Arrange
        String invalid = "estiven.ona-espe.edu.ec";
        double balance = 15.00;

        // Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            walletService.createWallet(invalid, balance));

        //Verificar no interacción
        Mockito.verifyNoInteractions(riskClient, walletRepository);
    }

    @Test
    void depositWalletNotFoundShouldThrowException(){
        // Arrange
        String walletId = "no-existe";

        Mockito.when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Act + Assert
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () ->
                walletService.deposit(walletId, 10.00));
        Assertions.assertEquals("Wallet not found", ex.getMessage());
        Mockito.verify(walletRepository).findById(walletId);
        Mockito.verify(walletRepository, Mockito.never()).save(ArgumentMatchers.any(Wallet.class));
    }

    @Test
    void depositShouldUpdateBalanceAndSaveUsingCaptor(){
        // Arrange
        Wallet wallet = new Wallet("estiven.ona@espe.edu.ec", 150.00);
        String walletId = wallet.getId();

        Mockito.when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        Mockito.when(walletRepository.save(ArgumentMatchers.any(Wallet.class))).thenAnswer(i ->
                i.getArguments()[0]);
        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);

        // Act
        double newBalance = walletService.deposit(walletId, 30.00);

        Mockito.verify(walletRepository).save(captor.capture());
        Wallet saved = captor.getValue();
        Assertions.assertEquals(newBalance, saved.getBalance());
    }

//    @Test
//    void () {
//
//    }

    @Test
    void withdrawWithInsufficientFundsShouldThrowExceptionAndNotSave(){
        // Arrange
        Wallet wallet = new Wallet("estiven.ona@espe.edu.ec", 150.00);
        String walletId = wallet.getId();

        Mockito.when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Act + Assert
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, () ->
                walletService.withdraw(walletId, 1000.00));

        Assertions.assertEquals("Insufficient funds", ex.getMessage());
        Mockito.verify(walletRepository, Mockito.never()).save(ArgumentMatchers.any(Wallet.class));
    }
}
