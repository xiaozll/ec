package com.eryansky.fastweixin.company.message.req;

/**
 *  微信企业号图片消息事件
 *
 * @author eryan
 * @date 2016-03-15
 */
public class QYImageReqMsg extends QYBaseReqMsg {

    private final String picUrl;
    private final String mediaId;

    public QYImageReqMsg(String picUrl, String mediaId) {
        super();
        this.picUrl = picUrl;
        this.mediaId = mediaId;
        setMsgType(QYReqType.IMAGE);
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getMediaId() {
        return mediaId;
    }

    @Override
    public String toString() {
        return "QYImageReqMsg [picUrl=" + picUrl + ", mediaId=" + mediaId
                + ", toUserName=" + toUserName + ", fromUserName="
                + fromUserName + ", createTime=" + createTime + ", msgType="
                + msgType + ", msgId=" + msgId + ", agentId=" + agentId + "]";
    }
}
