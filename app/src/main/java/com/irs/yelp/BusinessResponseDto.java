package com.irs.yelp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model representing Yelp response to a business search request
 */
public class BusinessResponseDTO implements Parcelable {
    private String total;
    private BusinessDTO[] businesses;
    private RegionDTO region;

    protected BusinessResponseDTO(Parcel in) {
        total = in.readString();
        businesses = in.createTypedArray(BusinessDTO.CREATOR);
    }

    public static final Creator<BusinessResponseDTO> CREATOR = new Creator<BusinessResponseDTO>() {
        @Override
        public BusinessResponseDTO createFromParcel(Parcel in) {
            return new BusinessResponseDTO(in);
        }

        @Override
        public BusinessResponseDTO[] newArray(int size) {
            return new BusinessResponseDTO[size];
        }
    };

    public String total() {
        return total;
    }

    public BusinessDTO[] businesses() {
        return businesses;
    }

    public RegionDTO region() {
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

