package vo;

/**
 * 捷顺数据VO
 */
public class JieShunVo {

    /**
     * 所属项目
     */
    private String projectName;

    /**
     * 车牌号码
     */
    private String carNum;

    /**
     * 入场时间
     */
    private String carInTime;

    /**
     * 收款时间
     */
    private String collectedTime;

    /**
     * 应收金额(元)
     */
    private String receivableMoney;

    /**
     * 实收金额(元)
     */
    private String receiptMoney;

    /**
     * 付款方式
     */
    private String payMethod;


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getCarInTime() {
        return carInTime;
    }

    public void setCarInTime(String carInTime) {
        this.carInTime = carInTime;
    }

    public String getCollectedTime() {
        return collectedTime;
    }

    public void setCollectedTime(String collectedTime) {
        this.collectedTime = collectedTime;
    }

    public String getReceivableMoney() {
        return receivableMoney;
    }

    public void setReceivableMoney(String receivableMoney) {
        this.receivableMoney = receivableMoney;
    }

    public String getReceiptMoney() {
        return receiptMoney;
    }

    public void setReceiptMoney(String receiptMoney) {
        this.receiptMoney = receiptMoney;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

}
