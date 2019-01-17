package io.nayasis.sample.registrylock.redis.sample.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReqTransfer {

    private Long       from;
    private Long       to;
    private BigDecimal amount;

}
