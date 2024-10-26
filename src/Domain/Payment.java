package Domain;

import java.math.BigDecimal;
import java.util.Date;

public class Payment {
    public Integer id = null;
    public int reservationId;
    public BigDecimal amount;
    public String paymentDate;
    public String status = PaymentStatus.Pending.toString() ;
}
