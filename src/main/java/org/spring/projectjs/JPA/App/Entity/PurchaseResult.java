package org.spring.projectjs.JPA.App.Entity;

public class PurchaseResult {
    private boolean success;
    private String message;
    private int remainingGold;

    public PurchaseResult(boolean success, String message, int remainingGold) {
        this.success = success;
        this.message = message;
        this.remainingGold = remainingGold;
    }

    // getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getRemainingGold() { return remainingGold; }
    public void setRemainingGold(int remainingGold) { this.remainingGold = remainingGold; }
}