package com.irs.yelp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model representing Yelp response to a business search request
 */
public class BusinessResponseModel implements Parcelable {
    private String total;
    private BusinessModel[] businesses;
    private RegionModel region;

    protected BusinessResponseModel(Parcel in) {
        total = in.readString();
        businesses = in.createTypedArray(BusinessModel.CREATOR);
    }

    public static final Creator<BusinessResponseModel> CREATOR = new Creator<BusinessResponseModel>() {
        @Override
        public BusinessResponseModel createFromParcel(Parcel in) {
            return new BusinessResponseModel(in);
        }

        @Override
        public BusinessResponseModel[] newArray(int size) {
            return new BusinessResponseModel[size];
        }
    };

    public String total() {
        return total;
    }

    public BusinessModel[] businesses() {
        return businesses;
    }

    public RegionModel region() {
        return region;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeTypedArray(businesses, 0);
        dest.writeString(total);
        dest.writeTypedArray(businesses, flags);
    }
}

