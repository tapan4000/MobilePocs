package com.example.tapanj.mapsdemo.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.example.tapanj.mapsdemo.common.Utility.Utility;

public class WorkflowContext implements Parcelable {
    private String workflowId;
    private String sourceId;
    private WorkflowSourceType sourceType;
    private boolean isWorkflowLogged = false;

    public WorkflowContext(String workflowId, String sourceId, WorkflowSourceType sourceType){
        this.workflowId = workflowId;
        this.sourceId = sourceId;
        this.sourceType = sourceType;
    }

    public WorkflowContext(String sourceId, WorkflowSourceType sourceType){
        this.workflowId = Utility.GetUniqueWorkflowId();
        this.sourceId = sourceId;
        this.sourceType = sourceType;
    }

    protected WorkflowContext(Parcel in) {
        workflowId = in.readString();
        sourceId = in.readString();
        sourceType = WorkflowSourceType.valueOf(in.readString());
        isWorkflowLogged = in.readByte() != 0;
    }

    public static final Creator<WorkflowContext> CREATOR = new Creator<WorkflowContext>() {
        @Override
        public WorkflowContext createFromParcel(Parcel in) {
            return new WorkflowContext(in);
        }

        @Override
        public WorkflowContext[] newArray(int size) {
            return new WorkflowContext[size];
        }
    };

    public String getWorkflowId(){
        return this.workflowId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.workflowId);
        dest.writeString(this.sourceId);
        dest.writeString(this.sourceType.name());
        dest.writeByte((byte) (this.isWorkflowLogged ? 1 : 0));
    }
}
