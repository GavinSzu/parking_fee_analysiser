package vo;

/**
 * 银联数据VO
 */
public class YinlianVo {

    /**
     * 发往银行流水
     */
    private String flowNum;

    /**
     * 无感平台订单号
     */
    private String senselessPFNum;

    /**
     * 车牌号码
     */
    private String carNum;

    /**
     * 金额
     */
    private String money;

    /**
     * 交易时间
     */
    private String tradeTime;

    /**
     * 入场时间
     */
    private String carInTime;

    /**
     * 出场时间
     */
    private String carOutTime;

    public String getFlowNum() {
        return flowNum;
    }

    public void setFlowNum(String flowNum) {
        this.flowNum = flowNum;
    }

    public String getSenselessPFNum() {
        return senselessPFNum;
    }

    public void setSenselessPFNum(String senselessPFNum) {
        this.senselessPFNum = senselessPFNum;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getCarInTime() {
        return carInTime;
    }

    public void setCarInTime(String carInTime) {
        this.carInTime = carInTime;
    }

    public String getCarOutTime() {
        return carOutTime;
    }

    public void setCarOutTime(String carOutTime) {
        this.carOutTime = carOutTime;
    }

}
