package ddog.domain.payment.port;

public interface PaymentTimeOutMessageListen<T> {
    void listen(T message);
}
