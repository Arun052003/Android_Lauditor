package com.digicoffer.lauditor.email;

import java.util.List;

public class MessageModel {
    String msgId;
    String subject;
    String from;
    String to;
    List<AttachmentModel> attachments;
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject
    ){
        this.subject =subject;
    }
    public String getFrom() {
        return from;
    }

    public void setFrom(String from
    ){
        this.from =from;
    }
    public String getTo() {
        return to;
    }

    public void setTo(String to
    ){
        this.to =to;
    }

    public void setAttachments(List<AttachmentModel> attachments) {
        this.attachments = attachments;
    }



    public int getMessage() {
        return 0;
    }

    public boolean isAttachment() {
        return this.attachments.isEmpty();
    }

    public List<AttachmentModel> getAttachment() {
        return this.attachments;
    }
}
