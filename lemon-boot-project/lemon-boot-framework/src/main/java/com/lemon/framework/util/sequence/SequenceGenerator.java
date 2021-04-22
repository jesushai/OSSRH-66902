package com.lemon.framework.util.sequence;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

/**
 * Distributed Sequence Generator.
 * Inspired by Twitter snowflake: https://github.com/twitter/snowflake/tree/snowflake-2010
 * <p>
 * This class should be used as a Singleton.
 * Make sure that you create and reuse a Single instance of SequenceGenerator per node in your distributed system cluster.
 */
@SuppressWarnings("unused")
public class SequenceGenerator {
    private static final int UNUSED_BITS = 1; // Sign bit, Unused (always set to 0)
    private static final int EPOCH_BITS = 41;
    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    /**
     * 最大节点ID，数值是1023
     */
    private static final int maxNodeId = (int) (Math.pow(2, NODE_ID_BITS) - 1);
    /**
     * 最大序列掩码，数值是4095
     */
    private static final int maxSequence = (int) (Math.pow(2, SEQUENCE_BITS) - 1);

    /**
     * UTC: 2020-01-01 00:00:00.0<br/>
     * UTC+8（北京时间）：2020-01-01 08:00:00.0
     */
    private static final long CUSTOM_EPOCH = 1577836800000L;

    /**
     * 当前节点ID
     */
    private final int nodeId;

    private volatile long lastTimestamp = -1L;
    /**
     * 同一毫秒内的序列，0~4095(maxSequence)
     */
    private volatile long sequence = 0L;

    // Create SequenceGenerator with a nodeId
    public SequenceGenerator(int nodeId) {
        if (nodeId < 0 || nodeId > maxNodeId) {
            throw new IllegalArgumentException(String.format("NodeId must be between %d and %d", 0, maxNodeId));
        }
        this.nodeId = nodeId;
    }

    // Let SequenceGenerator generate a nodeId
    public SequenceGenerator() {
        this.nodeId = createNodeId();
    }

    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }

        if (currentTimestamp == lastTimestamp) {
            // 毫秒内序列
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // 序列耗尽，等待下一个毫秒
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // 下一毫秒重置序列为0
            // reset sequence to start with zero for the next millisecond
            sequence = 0;
        }
        // 保存最后时间戳
        lastTimestamp = currentTimestamp;

        long id = currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS);
        id |= (nodeId << SEQUENCE_BITS);
        id |= sequence;
        return id;
    }


    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private static long timestamp() {
        return Instant.now().toEpochMilli() - CUSTOM_EPOCH;
    }

    // Block and wait till next millisecond
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    /**
     * @return 根据MAC地址生成 NodeId
     */
    private int createNodeId() {
        int nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }
            nodeId = sb.toString().hashCode();
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
        }
        nodeId = nodeId & maxNodeId;
        return nodeId;
    }

}