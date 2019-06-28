package com.eryansky.fastweixin.company.message.req;
/**
 *  微信企业号扫码事件
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYScanCodeEvent extends QYMenuEvent {

    private String scanType;
    private String scanResult;

    public QYScanCodeEvent(String eventKey, String scanType, String scanResult) {
        super(eventKey);
        this.scanType = scanType;
        this.scanResult = scanResult;
    }

    public String getScanType() {
        return scanType;
    }

    public QYScanCodeEvent setScanType(String scanType) {
        this.scanType = scanType;
        return this;
    }

    public String getScanResult() {
        return scanResult;
    }

    public QYScanCodeEvent setScanResult(String scanResult) {
        this.scanResult = scanResult;
        return this;
    }

    @Override
    public String toString(){
        return "QYMenuEvent [scanType=" + scanType + ", scanResult=" + scanResult+ ", eventKey=" + getEventKey()
                + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", event=" + event + ", agentId=" + agentId + "]";
    }
}
