package ch.hftm.entity;

public class StorageInfo {

    private long usedBytes;
    private long quotaBytes;
    private int fileCount;

    public StorageInfo(long usedBytes, long quotaBytes, int fileCount) {
        this.usedBytes = usedBytes;
        this.quotaBytes = quotaBytes;
        this.fileCount = fileCount;
    }

    public long getUsedBytes() {
        return usedBytes;
    }

    public long getQuotaBytes() {
        return quotaBytes;
    }

    public int getFileCount() {
        return fileCount;
    }

    public long getRemainingBytes() {
        return quotaBytes - usedBytes;
    }

    public double getUsagePercent() {
        if (quotaBytes == 0) {
            return 0;
        }
        return Math.round(((double) usedBytes / quotaBytes) * 10000.0) / 100.0;
    }
}