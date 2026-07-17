package ec.edu.espe.lab2ci.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WalletResponse {
    private final String walletId;
    private final double balance;
}
