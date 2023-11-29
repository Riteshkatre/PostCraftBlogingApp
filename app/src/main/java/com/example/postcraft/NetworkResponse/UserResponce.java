package com.example.postcraft.NetworkResponse;
import java.io.Serializable;
import java.util.Properties;

import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;





    public class UserResponce  implements Serializable, Parcelable
    {

        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("status")
        @Expose
        private String status;
        public final static Creator<UserResponce> CREATOR = new Creator<UserResponce>() {


            public UserResponce createFromParcel(android.os.Parcel in) {
                return new UserResponce(in);
            }

            public UserResponce[] newArray(int size) {
                return (new UserResponce[size]);
            }

        }
                ;
        private final static long serialVersionUID = -7485523805223375767L;

        @SuppressWarnings({
                "unchecked"
        })
        protected UserResponce(android.os.Parcel in) {
            this.message = ((String) in.readValue((String.class.getClassLoader())));
            this.status = ((String) in.readValue((String.class.getClassLoader())));
        }

        public UserResponce() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void writeToParcel(android.os.Parcel dest, int flags) {
            dest.writeValue(message);
            dest.writeValue(status);
        }

        public int describeContents() {
            return  0;
        }
    }


