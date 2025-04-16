package com.zishanshu.client.ClientProxyImpl;
import com.zishanshu.client.RPCClient;
import lombok.AllArgsConstructor;
import com.zishanshu.common.RPCResponse;
import com.zishanshu.common.RPCRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
@Slf4j
@AllArgsConstructor
public class Retry {
    private RPCClient PRCClient;
    
    public RPCResponse sendServiceWithRetry( RPCRequest rpcRequest ) {
        RetryConfig config = new RetryConfig.Builder()
                .maxAttempts(3)
                .waitTime(2, TimeUnit.SECONDS)
                .retryOnException(true)
                .retryOnResult(response -> Objects.equals(response.getCode(), 500))
                .listener(new RetryListener() {
                    @Override
                    public void onRetry(int attemptNumber, boolean success) {
                        log.debug("RetryListener: 第" + attemptNumber + "次调用");
                    }
                })
                .build();

        return new RetryTemplate(config).execute(() -> PRCClient.sendRequest(rpcRequest));
    }
    
    public static class RetryConfig {
        private final int maxAttempts;
        private final long waitTime;
        private final TimeUnit timeUnit;
        private final boolean retryOnException;
        private final ResultPredicate<RPCResponse> resultPredicate;
        private final RetryListener listener;

        private RetryConfig(Builder builder) {
            this.maxAttempts = builder.maxAttempts;
            this.waitTime = builder.waitTime;
            this.timeUnit = builder.timeUnit;
            this.retryOnException = builder.retryOnException;
            this.resultPredicate = builder.resultPredicate;
            this.listener = builder.listener;
        }

        public static class Builder {
            private int maxAttempts = 3;
            private long waitTime = 2;
            private TimeUnit timeUnit = TimeUnit.SECONDS;
            private boolean retryOnException = true;
            private ResultPredicate<RPCResponse> resultPredicate;
            private RetryListener listener;

            public Builder maxAttempts(int maxAttempts) {
                this.maxAttempts = maxAttempts;
                return this;
            }

            public Builder waitTime(long waitTime, TimeUnit timeUnit) {
                this.waitTime = waitTime;
                this.timeUnit = timeUnit;
                return this;
            }

            public Builder retryOnException(boolean retryOnException) {
                this.retryOnException = retryOnException;
                return this;
            }

            public Builder retryOnResult(ResultPredicate<RPCResponse> predicate) {
                this.resultPredicate = predicate;
                return this;
            }

            public Builder listener(RetryListener listener) {
                this.listener = listener;
                return this;
            }

            public RetryConfig build() {
                return new RetryConfig(this);
            }
        }
    }

    // 结果判断接口
    public interface ResultPredicate<T> {
        boolean shouldRetry(T result);
    }

    // 重试监听器接口
    public interface RetryListener {
        void onRetry(int attemptNumber, boolean success);
    }

    // 重试模板类
    public static class RetryTemplate {
        private final RetryConfig config;

        public RetryTemplate(RetryConfig config) {
            this.config = config;
        }

        public RPCResponse execute(Retryable<RPCResponse> retryable) {
            int attempt = 0;
            while (true) {
                attempt++;
                try {
                    RPCResponse response = retryable.call();
                    if (config.resultPredicate != null && config.resultPredicate.shouldRetry(response)) {
                        notifyListener(attempt, false);
                        if (shouldRetry(attempt)) {
                            waitBeforeRetry();
                            continue;
                        }
                        return response;
                    }
                    notifyListener(attempt, true);
                    return response;
                } catch (Exception e) {
                    if (!config.retryOnException || !shouldRetry(attempt)) {
                        e.printStackTrace();
                        return RPCResponse.fail();
                    }
                    notifyListener(attempt, false);
                    waitBeforeRetry();
                }
            }
        }

        private boolean shouldRetry(int attempt) {
            return attempt < config.maxAttempts;
        }

        private void waitBeforeRetry() {
            try {
                config.timeUnit.sleep(config.waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void notifyListener(int attemptNumber, boolean success) {
            if (config.listener != null) {
                config.listener.onRetry(attemptNumber, success);
            }
        }
    }

    // 可重试操作接口
    public interface Retryable<V> {
        V call() throws Exception;
    }
}
