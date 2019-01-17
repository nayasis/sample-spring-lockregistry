package io.nayasis.sample.registrylock.redis.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name="TB_BALANCE")
@ToString
public class AccountEntity {

    @Id
    @GeneratedValue
    @Column(name="account_id")
    private Long accountId;

    @Column(name="balance",columnDefinition="VARCHAR(80)")
    private BigDecimal balance;

    public void subtract( BigDecimal amount ) {
        this.balance = this.balance.subtract( amount );
    }

    public void add( BigDecimal amount ) {
        this.balance = this.balance.add( amount );
    }

}
